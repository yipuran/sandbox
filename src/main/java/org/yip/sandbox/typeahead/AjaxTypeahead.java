package org.yip.sandbox.typeahead;

import java.util.List;
import java.util.Optional;

import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.handler.TextRequestHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Twitter Boostrap Typeahead TextField.
 */
public abstract class AjaxTypeahead<T extends Typeahead> extends TextField<T>{
	private AbstractDefaultAjaxBehavior queryAjaxBehavior;

	public AjaxTypeahead(String id, IModel<T> model){
		super(id, model);
		Gson gson = new GsonBuilder().serializeNulls().create();
		queryAjaxBehavior = new AbstractDefaultAjaxBehavior(){
			@Override
			protected void respond(AjaxRequestTarget target){
				Optional.ofNullable(getRequestCycle().getRequest().getQueryParameters().getParameterValue("query").toString()).ifPresent(s->{
					getComponent().getRequestCycle().replaceAllRequestHandlers(new TextRequestHandler("application/json", "UTF-8", gson.toJson(getChoices(s)) ));
				});
				Optional.ofNullable(getRequestCycle().getRequest().getQueryParameters().getParameterValue("selected").toString()).ifPresent(s->{
					onSelect(target, Optional.ofNullable(getRequestCycle().getRequest().getQueryParameters().getParameterValue("id").toString()).orElse("")
								, Optional.ofNullable(getRequestCycle().getRequest().getQueryParameters().getParameterValue("display").toString()).orElse(""));
				});
				Optional.ofNullable(getRequestCycle().getRequest().getQueryParameters().getParameterValue("change").toString()).ifPresent(s->{
					onChange(target, Optional.ofNullable(getRequestCycle().getRequest().getQueryParameters().getParameterValue("display").toString()).orElse(""));
				});
				getRequestCycle().getResponse().close();
			}
			@Override
		    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
		        super.updateAjaxAttributes(attributes);
		        attributes.setDataType("json");
		        attributes.setWicketAjaxResponse(false);
		    }
		};
		add(queryAjaxBehavior);
	}

	@Override
	protected void onAfterRender(){
		super.onAfterRender();

		getResponse().write("<script type=\"text/javascript\">");
		getResponse().write(" $(\"#" + this.getMarkupId(true) + "\").typeahead({highlight:true,minLength:1},{");
		getResponse().write("displayKey: 'display',");
		getResponse().write("source : new Bloodhound({");
		getResponse().write("datumTokenizer: function(datum){");
		getResponse().write("return Bloodhound.tokenizers.whitespace(datum.display);");
		getResponse().write("},");
		getResponse().write("queryTokenizer: Bloodhound.tokenizers.whitespace,");
		getResponse().write("remote:{");
		getResponse().write("wildcard: '%QUERY',");
		getResponse().write("url: '" + queryAjaxBehavior.getCallbackUrl() + "&query=%QUERY',");
		getResponse().write("transform: function(r){");
		getResponse().write("return $.map(r, function(t){");
		getResponse().write("return { display:t.value, id:t.id };");
		getResponse().write("});}}})");
		getResponse().write("}).on('typeahead:select', function(ev, item){");
		getResponse().write("var url = '" + queryAjaxBehavior.getCallbackUrl() + "' + '&selected=&id=' + item.id + '&display=' + item.display;");
		getResponse().write("Wicket.Ajax.get({ u: url });");
		getResponse().write("}).on('change', function(ev){");
		getResponse().write("var url = '" + queryAjaxBehavior.getCallbackUrl() + "' + '&change=&display=' + $(this).val();");
		getResponse().write("Wicket.Ajax.get({ u: url });");
		getResponse().write("});");
		getResponse().write("</script>");
	}

	/** 入力文字→候補リスト  */
	protected abstract List<T> getChoices(String input);

	/**
	 * 選択イベント捕捉.
	 * @param target AjaxRequestTarget
	 * @param id
	 * @param dislay
	 */
	protected void onSelect(AjaxRequestTarget target, String id, String dislay){
	}
	/** 変更イベント捕捉	 */
	protected void onChange(AjaxRequestTarget target, String dislay){
	}

}