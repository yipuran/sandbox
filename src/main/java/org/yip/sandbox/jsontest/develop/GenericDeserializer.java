package org.yip.sandbox.jsontest.develop;

import java.lang.reflect.Type;
import java.util.function.Supplier;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * GenericDeserializer
 */
public class GenericDeserializer<T> implements JsonDeserializer<T>{
	private JsonDeserializeFunction<JsonElement, T, JsonDeserializationContext, T> function;
	private Supplier<T> supplier;

	public GenericDeserializer(Supplier<T> supplier, JsonDeserializeFunction<JsonElement, T, JsonDeserializationContext, T> function){
		this.supplier = supplier;
		this.function = function;
	}
	@Override
	public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException{
		T t = supplier.get();
		if (!json.isJsonNull()){
			if (json.isJsonArray()){
				for(JsonElement je:json.getAsJsonArray()){
					t = function.apply(je, t, context);
				}
			}else if(json.isJsonObject()){
				t = function.apply(json, t, context);
			}
		}
		return t;
	}
}
