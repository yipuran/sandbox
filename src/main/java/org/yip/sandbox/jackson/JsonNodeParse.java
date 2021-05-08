package org.yip.sandbox.jackson;

import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;

/**
 * JsonNodeParse
 */
public class JsonNodeParse{
	private ObjectMapper mapper;
	private Map<String, Function<JsonNode, Object>> dMap;
	public JsonNodeParse() {
		dMap = new HashMap<>();
		mapper = new ObjectMapper();
	}
	public JsonNodeParse addDeserilaize(Pattern ptn, Function<JsonNode, Object> deserial) {
		dMap.put(ptn.pattern(), deserial);
		return this;
	}

	public void readJson(String jsontxt, BiConsumer<String, Object> con){
		try{
			parseJson(mapper.readTree(jsontxt), "", con);
		}catch (JsonProcessingException e){
			throw new RuntimeException(e);
		}
	}

	public void readJson(InputStream in, BiConsumer<String, Object> con){
		try{
			parseJson(mapper.readTree(in), "", con);
		}catch (JsonProcessingException e){
			throw new RuntimeException(e);
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}

	public void readJson(JsonNode node, BiConsumer<String, Object> con){
		parseJson(node, "", con);
	}

	private void parseJson(JsonNode node, String path, BiConsumer<String, Object> con){
		String p = path.length() > 0 ? path.substring(1) : path;
		Function<JsonNode, Object> nodeparser;
		if (dMap.size() > 0 && (nodeparser = dMap.entrySet().stream()
				.filter(e->Pattern.compile(e.getKey()).matcher(p).find())
				.findAny().map(e->e.getValue()).orElse(null)) != null) {
			con.accept(p, nodeparser.apply(node));
		}else{
			if (node.getNodeType().equals(JsonNodeType.OBJECT)) {
				for(Iterator<Entry<String, JsonNode>> it=node.fields(); it.hasNext();) {
					Entry<String, JsonNode> entry = it.next();
					parseJson(entry.getValue(), path + "." + entry.getKey(), con);
				}
			}else if(node.getNodeType().equals(JsonNodeType.ARRAY)){
				if (node.size() > 0){
					int x=0;
					for(Iterator<JsonNode> it=node.iterator(); it.hasNext();x++){
						parseJson(it.next(), path + "[" + x + "]", con);
					}
				}else{
					con.accept(p, new ArrayList<Object>());
				}
			}else if(node.getNodeType().equals(JsonNodeType.NULL)){
				con.accept(p, null);
			}else if(node.getNodeType().equals(JsonNodeType.NUMBER)){
				if (node.isDouble()){
					con.accept(p, node.asDouble());
				}else if(node.isLong()){
					con.accept(p, node.asLong());
				}else{
					con.accept(p, node.asInt());
				}
			}else if(node.getNodeType().equals(JsonNodeType.BOOLEAN)){
				con.accept(p, node.asBoolean());
			}else if(node.getNodeType().equals(JsonNodeType.STRING)){
				con.accept(p, node.asText());
			}
		}
	}

	public Stream<Entry<String, Object>> jsonStream(String jsontxt){
		Stream.Builder<Entry<String, Object>> builder = Stream.builder();
		ObjectMapper mapper = new ObjectMapper();
		try{
			parseJson(mapper.readTree(jsontxt), "", builder);
		}catch (JsonProcessingException e){
			throw new RuntimeException(e);
		}
		return  builder.build();
	}

	public Stream<Entry<String, Object>> jsonStream(InputStream in){
		Stream.Builder<Entry<String, Object>> builder = Stream.builder();
		ObjectMapper mapper = new ObjectMapper();
		try{
			parseJson(mapper.readTree(in), "", builder);
		}catch (JsonProcessingException e){
			throw new RuntimeException(e);
		}catch(IOException e){
			throw new RuntimeException(e);
		}
		return  builder.build();
	}

	public Stream<Entry<String, Object>> jsonStream(JsonNode node){
		Stream.Builder<Entry<String, Object>> builder = Stream.builder();
		parseJson(node, "", builder);
		return  builder.build();
	}

	private void parseJson(JsonNode node, String path, Stream.Builder<Entry<String, Object>> builder){
		String p = path.length() > 0 ? path.substring(1) : path;
		Function<JsonNode, Object> nodeparser;
		if (dMap.size() > 0 && (nodeparser = dMap.entrySet().stream()
				.filter(e->Pattern.compile(e.getKey()).matcher(p).find())
				.findAny().map(e->e.getValue()).orElse(null)) != null) {
			builder.add(new SimpleEntry<String, Object>(p, nodeparser.apply(node)));
		}else{
			if (node.getNodeType().equals(JsonNodeType.OBJECT)) {
				for(Iterator<Entry<String, JsonNode>> it=node.fields(); it.hasNext();) {
					Entry<String, JsonNode> entry = it.next();
					parseJson(entry.getValue(), path + "." + entry.getKey(), builder);
				}
			}else if(node.getNodeType().equals(JsonNodeType.ARRAY)){
				if (node.size() > 0){
		         int x=0;
		         for(Iterator<JsonNode> it=node.iterator(); it.hasNext();x++){
		            parseJson(it.next(), path + "[" + x + "]", builder);
		         }
		      }else{
		         builder.add(new SimpleEntry<String, Object>(p, new ArrayList<Object>()));
		      }
			}else if(node.getNodeType().equals(JsonNodeType.NULL)){
				builder.add(new SimpleEntry<String, Object>(p, null));
			}else if(node.getNodeType().equals(JsonNodeType.NUMBER)){
				if (node.isDouble()){
					builder.add(new SimpleEntry<String, Object>(p, node.asDouble()));
				}else if(node.isLong()){
					builder.add(new SimpleEntry<String, Object>(p, node.asLong()));
				}else{
					builder.add(new SimpleEntry<String, Object>(p, node.asInt()));
				}
			}else if(node.getNodeType().equals(JsonNodeType.BOOLEAN)){
				builder.add(new SimpleEntry<String, Object>(p, node.asBoolean()));
			}else if(node.getNodeType().equals(JsonNodeType.STRING)){
				builder.add(new SimpleEntry<String, Object>(p, node.asText()));
			}
		}
	}
	public static Function<JsonNode, Object> toFunction(Class<?> cls){
		return t->{
			try{
				return new ObjectMapper().readerFor(cls).readValue(t);
			}catch(Throwable ex){
				throw new RuntimeException(ex);
			}
		};
	}
}
