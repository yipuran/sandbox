package org.yip.sandbox.oldhttp;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * HttpsClientを生成するビルダー.
 */
public final class HttpsClientBuilder{
	private URL url;
	private String method;
	private String proxy_server;
	private String proxy_user;
	private String proxy_passwd;
	private Integer proxy_port;
	private Map<String, String> map;
	/**
	 * private constructor.
	 * @param path URL path
	 * @param method HTTPメソッド、"POST" か "GET" を指定
	 */
	private HttpsClientBuilder(String path, String method){
		map = new HashMap<>();
		this.method = method.toUpperCase();
		try{
			url = new URL(path);
		}catch(MalformedURLException e){
			throw new RuntimeException(e);
		}
	}
	/**
	 * HttpsClientBuilder生成.
	 * @param path HTTP要求するAPI要求先のURL
	 * @param method HTTPメソッド、"POST" か "GET" を指定
	 * @return HttpsClientBuilder
	 */
	public static HttpsClientBuilder of(String path, String method){
		return new HttpsClientBuilder(path, method);
	}
	/**
	 * HTTPパラメータ追加.
	 * valueは本メソッド内で ＵＲＬエンコードされるのでエンコードして呼びだしてはいけない。
	 * @param key キー
	 * @param value 値 エンコードして呼びだしてはいけない。
	 * @return HttpsClientBuilder
	 */
	public HttpsClientBuilder add(String key, String value){
		if (key != null){
			try{
				map.put(key, value==null ? null : URLEncoder.encode(value, "UTF-8"));
			}catch(UnsupportedEncodingException e){
			}
		}
		return this;
	}
	/**
	 * Proxy使用指定.
	 * @param proxy_server Proxyサーバ名
	 * @param proxy_user Proxyユーザ名
	 * @param proxy_passwd Proxyパスワード
	 * @param proxy_port Proxyポート番号
	 * @return HttpsClientBuilder
	 */
	public HttpsClientBuilder addProxy(String proxy_server, String proxy_user, String proxy_passwd, Integer proxy_port){
		this.proxy_server = proxy_server;
		this.proxy_user = proxy_user;
		this.proxy_passwd = proxy_passwd;
		this.proxy_port = proxy_port;
		return this;
	}
	/**
	 * HttpsClient生成.
	 * @return HttpsClient
	 */
	public HttpsClient build(){
		if (proxy_server==null) return new HttpsClient(url, method, map);
		return new HttpsClient(url, method, proxy_server, proxy_user, proxy_passwd, proxy_port, map);
	}
}
