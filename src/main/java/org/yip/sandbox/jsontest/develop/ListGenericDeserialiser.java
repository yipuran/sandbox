package org.yip.sandbox.jsontest.develop;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * ListGenericDeserialiser
 */
public class ListGenericDeserialiser<T> implements JsonDeserializer<List<T>>{
	private Function<JsonElement, T> function;

	public ListGenericDeserialiser(Function<JsonElement, T> function){
		this.function = function;
	}
	@Override
	public List<T> deserialize(JsonElement json, Type typeOfT
	, JsonDeserializationContext context) throws JsonParseException{
		List<T> list = new ArrayList<>();
		if (!json.isJsonNull()){
			if (json.isJsonArray()){
				for(JsonElement je:json.getAsJsonArray()){
					list.add(function.apply(je));
				}
			}
		}
		return list;
	}
}
