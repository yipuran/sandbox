package org.yip.sandbox.json;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * JsonClient.java
 */
public class JsonClient{
	private URL url;
	private String jsonstr;
	private int httpresponsecode;

	protected JsonClient(URL url, String jsonstr){
		this.url = url;
		this.jsonstr = jsonstr;
	}
	public void execute(BiConsumer<String, Map<String, List<String>>> headconsumer, Consumer<String> readconsumer){
		try{
			HttpURLConnection uc = (HttpURLConnection)url.openConnection();
			uc = (HttpURLConnection)url.openConnection();
			/* HTTPリクエストヘッダの設定 */
			uc.setDoOutput(true);              // こちらからのデータ送信を可能とする
			uc.setReadTimeout(0);              // 読み取りタイムアウト値をミリ秒単位で設定(0は無限)
			uc.setRequestMethod("POST");       // URL 要求のメソッドを設定

			uc.setRequestProperty("Content-Type", "application/json");
			uc.setRequestProperty("Content-Length", Integer.toString(jsonstr.getBytes("utf8").length));
			// コネクション確立→送信
			uc.connect();
			OutputStreamWriter osw = new OutputStreamWriter(uc.getOutputStream(), "utf8");
			osw.write(jsonstr);
			osw.flush();
			httpresponsecode = uc.getResponseCode();
			if (httpresponsecode != 200){
				throw new RuntimeException("HTTP response " + httpresponsecode);
			}
			// 戻り値取得
			headconsumer.accept(uc.getContentType(), uc.getHeaderFields());
			// 応答読込
			try(InputStream in = uc.getInputStream();ByteArrayOutputStream bo = new ByteArrayOutputStream()){
				byte[] buf = new byte[1024];
				int n;
				while((n = in.read(buf)) >= 0){
					bo.write(buf, 0, n);
				}
				bo.flush();
				readconsumer.accept(bo.toString());
			}
		}catch(Exception e){
		   throw new RuntimeException(e);
		}
	}
}
