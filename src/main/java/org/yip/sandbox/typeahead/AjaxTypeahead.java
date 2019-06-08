package org.yip.sandbox.typeahead;

import java.util.List;
import java.util.Optional;

import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.handler.TextRequestHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Twitter Boostrap Typeahead TextField.
 */
public abstract class AjaxTypeahead<T extends Typeahead> extends TextField<String>{
	private AbstractDefaultAjaxBehavior queryAjaxBehavior;
	private List<Typeahead> choicelist;

	public AjaxTypeahead(String id, IModel<String> model){
		super(id, model);
		Gson gson = new GsonBuilder().serializeNulls().create();
		queryAjaxBehavior = new AbstractDefaultAjaxBehavior(){
			@Override
			protected void respond(AjaxRequestTarget target){
				IRequestParameters p = getRequestCycle().getRequest().getQueryParameters();
				Optional.ofNullable(p.getParameterValue("query").toString()).ifPresent(s->{
					choicelist = getChoices(s.trim());
					getComponent().getRequestCycle().replaceAllRequestHandlers(new TextRequestHandler("application/json", "UTF-8", gson.toJson(choicelist)));
				});
				Optional.ofNullable(p.getParameterValue("selected").toString()).ifPresent(s->{
					String id = p.getParameterValue("id").toString();
					for(Typeahead t:choicelist){
						if (t.id.equals(id)){
							onSelect(target, t);
							break;
						}
					}
				});
				Optional.ofNullable(p.getParameterValue("change").toString()).ifPresent(s->{
					String display = Optional.ofNullable(p.getParameterValue("display").toString()).orElse("").trim();
					onChange(target, display);
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
		getResponse().write("limit: 7,");
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
	protected abstract List<Typeahead> getChoices(String input);

	/**
	 * 選択イベント捕捉.
	 * @param target AjaxRequestTarget
	 * @param id
	 * @param dislay
	 */
	protected void onSelect(AjaxRequestTarget target, Typeahead typeahead){
	}
	/** 変更イベント捕捉	 */
	protected void onChange(AjaxRequestTarget target, String dislay){
	}
}