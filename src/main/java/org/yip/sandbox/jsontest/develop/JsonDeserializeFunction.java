package org.yip.sandbox.jsontest.develop;

/**
 * JsonDeserializeFunction :
 */
@FunctionalInterface
public interface JsonDeserializeFunction<JsonElement, T, JsonDeserializationContext, R>{
	R apply(JsonElement je, T t, JsonDeserializationContext v);
}
