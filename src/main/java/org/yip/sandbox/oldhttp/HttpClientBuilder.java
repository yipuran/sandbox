package org.yip.sandbox.oldhttp;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * HttpClientを生成するビルダー.
 */
public final class HttpClientBuilder{
	private URL url;
	private String method;
	private Map<String, String> map;
	/**
	 * private constructor.
	 * @param path URL path
	 * @param method HTTPメソッド、"POST" か "GET" を指定
	 */
	private HttpClientBuilder(String path, String method){
		map = new HashMap<>();
		this.method = method.toUpperCase();
		try{
			url = new URL(path);
		}catch(MalformedURLException e){
			throw new RuntimeException(e);
		}
	}
	/**
	 * HttpClientBuilder生成.
	 * @param path HTTP要求するAPI要求先のURL
	 * @param method HTTPメソッド、"POST" か "GET" を指定
	 * @return HttpClientBuilder
	 */
	public static HttpClientBuilder of(String path, String method){
		return new HttpClientBuilder(path, method);
	}
	/**
	 * HTTPパラメータ追加.
	 * valueは本メソッド内で ＵＲＬエンコードされるのでエンコードして呼びだしてはいけない。
	 * @param key キー
	 * @param value 値 エンコードして呼びだしてはいけない。
	 * @return HttpClientBuilder
	 */
	public HttpClientBuilder add(String key, String value){
		if (key != null){
			try{
				map.put(key, value==null ? null : URLEncoder.encode(value, "UTF-8"));
			}catch(UnsupportedEncodingException e){
			}
		}
		return this;
	}
	/**
	 * HttpClient生成.
	 * @return HttpClient
	 */
	public HttpClient build(){
		return new HttpClient(url, method, map);
	}
}
