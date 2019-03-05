package org.yip.sandbox.jsontest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.yip.sandbox.fileio.FileTool;
import org.yip.sandbox.jsontest.develop.GenericDeserializer;
import org.yipuran.gsonhelper.LocalDateAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;;
/**
 * TestJson
 */
public class TestJson {
	public static void main(String[] args) throws JsonSyntaxException, IOException {
		Gson gson = new GsonBuilder().serializeNulls()
		.registerTypeAdapter(LocalDate.class, LocalDateAdapter.create(()->DateTimeFormatter.ofPattern("yyyy/MM/dd")))
//		.registerTypeAdapter(new TypeToken<List<Foo>>(){}.getType(), new ListGenericDeserialiser<Foo>(je->{
//			Foo f = new Foo();
//			je.getAsJsonObject().entrySet().forEach(e->{
//				if (e.getKey().equals("name")){
//					f.name = e.getValue().getAsString();
//				}
//			});
//			return f;
//		}))
//		.registerTypeAdapter(new TypeToken<List<Foo>>(){}.getType(), new GenericDeseroalizer<List<Foo>>(()->new ArrayList<Foo>(), (je, list)->{
//			Foo f = new Foo();
//			je.getAsJsonObject().entrySet().forEach(e->{
//				if (e.getKey().equals("name")){
//					f.name = e.getValue().getAsString();
//				}
//			});
//			list.add(f);
//			return list;
//		}))

		.registerTypeAdapter(new TypeToken<List<Foo>>(){}.getType(), new GenericDeserializer<List<Foo>>(()->new ArrayList<Foo>(), (je, list, ctxt)->{
			Foo f = je.getAsJsonObject().entrySet().stream().filter(e->!e.getValue().isJsonNull()).collect(()->new Foo(), (r, e)->{
				if (e.getKey().equals("name")) r.name = e.getValue().getAsString();
				if (e.getKey().equals("value")) r.value = e.getValue().getAsInt();
				if (e.getKey().equals("date")) r.date = ctxt.deserialize(e.getValue(), new TypeToken<LocalDate>(){}.getType());
			}, (r, t)->{});
			list.add(f);
			return list;
		}))
//		.registerTypeAdapter(new TypeToken<Foo>(){}.getType(), new GenericDeseroalizer<Foo>(()->new Foo(), (je, f)->{
//			je.getAsJsonObject().entrySet().stream().filter(e->!e.getValue().isJsonNull()).forEach(e->{
//				if (e.getKey().equals("name")) f.name = e.getValue().getAsString();
//				if (e.getKey().equals("value")) f.value = e.getValue().getAsInt();
//			});
//			return f;
//		}))
		.registerTypeAdapter(new TypeToken<Foo>(){}.getType()
		, new GenericDeserializer<Foo>(()->new Foo(), (je, f, ctxt)->
			je.getAsJsonObject().entrySet().stream().filter(e->!e.getValue().isJsonNull())
			.collect(()->f, (r, e)->{
				if (e.getKey().equals("name")) f.name = e.getValue().getAsString();
				if (e.getKey().equals("value")) f.value = e.getValue().getAsInt();
				if (e.getKey().equals("date")) f.date = ctxt.deserialize(e.getValue(), new TypeToken<LocalDate>(){}.getType());
			},(r, t)->{})
		))
		.setPrettyPrinting()
		.create();

		String str = FileTool.readText("a.json");

		Fdata data = gson.fromJson(str, Fdata.class);

		System.out.println("data.flist = "+ data.flist );
		data.flist.stream().forEach(e->{
			System.out.println("## e.name = " + e.name + "  value = " + e.value + "  date : "+ e.date );
		});
		System.out.println("=====================");
		System.out.println("data.foot = "+ data.foo.name + "  value = " + data.foo.value + "  date : "+ data.foo.date  );

	}

}
