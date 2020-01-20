package org.yip.sandbox.json;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * JsonClientBuilder.java
 */
public class JsonClientBuilder{
	private URL url;
	private String jsonstr;

	private JsonClientBuilder(String path, String jsonstr){
		this.jsonstr = jsonstr;
		try{
			url = new URL(path);
		}catch(MalformedURLException e){
			throw new RuntimeException(e);
		}
	}
	public static JsonClientBuilder of(String path, String jsonstr){
		return new JsonClientBuilder(path, jsonstr);
	}
	public JsonClient build(){
		return new JsonClient(url, jsonstr);
	}
}
