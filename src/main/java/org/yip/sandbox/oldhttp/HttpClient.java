package org.yip.sandbox.oldhttp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * HttpClient.
 * HttpClientBuilder で HttpClientインスタンスを生成する。
 *  <PRE>
 * 要求先 URL パス、要求の種類(POST/GET)、要求するパラメータは、HttpClientBuilder で指定する。
 * HttpClientBuilder を使用することで送信するパラメータ値のエンコードは保証される。
 *
 * HttpClient client = HttpClientBuilder.of("http://xxx/xxx", "POST")
 *                    .add("a", "1")
 *                    .add("b", "2")
 *                    .add("c", "{\"name\":\"apple\",\"price\":150}")
 *                    .build();
 * String result = client.execute((t, m)->{
 *    logger.debug("# ContentType = " + m );
 *    ((Map&lt;String, List&lt;String&gt;&gt;&gt;)m).entrySet().stream().forEach(e->{
 *    logger.info("headerName = " + e.getKey());
 *    List&lt;String&gt; l = e.getValue();
 *    if (l != null){
 *       l.stream().forEach(s->{
 *          logger.info(s);
 *       });
 *    }
 * });
 *または、
 * public void execute(BiConsumer&lt;String, Map&lt;String, List&lt;String&gt;&gt;&gt; headconsumer, Consumer&lt;InputStream&gt; readconsumer)
 *
 * client.execute((type, m)->{
 *    System.out.println("type = " + type);
 *    m.entrySet().stream().forEach(e->{
 *       System.out.println(e.getKey() + " → " + e.getValue());
 *    });
 * }, in->{
 *    try(OutputStream out = new FileOutputStream(new File("result"))){
 *       byte[] buf = new byte[1024];
 *       int n;
 *       while((n = in.read(buf)) >= 0){
 *          out.write(buf, 0, n);
 *       }
 *    }catch(IOException e){
 *       e.printStackTrace();
 *    }
 * });
 *
 * 注意すべきは、１つのインスタンスで併用はできない。
 *  </PRE>
 */
public class HttpClient{
	private URL url;
	private String method;
	private Map<String, String> parameters;
	private int httpresponsecode;
	/**
	 * コンストラクタ.
	 * @param url HTTP先URL
	 * @param method HTTPメソッド
	 * @param map パラメータMap 値は、URLエンコードされてなければならない。HttpClientBuilder を使用することで送信するパラメータ値のエンコードは保証される。
	 */
	protected HttpClient(URL url, String method, Map<String, String> map){
		this.url = url;
		this.method = method;
		this.parameters = map;
	}
	/**
	 * HTTP要求送受信.
	 * @param consumer HTTP通信結果、受け取ったHTTP ContentType、HTTPヘッダを読取り処理する BiConsumer
	 * @return 要求した結果を受信したテキスト文字列。受信失敗は、null を返す。
	 */
	public String execute(BiConsumer<String, Map<String, List<String>>> consumer){
		String result = null;
		try{
			HttpURLConnection uc = (HttpURLConnection)url.openConnection();
			uc = (HttpURLConnection)url.openConnection();
			/* HTTPリクエストヘッダの設定 */
			uc.setDoOutput(true);              // こちらからのデータ送信を可能とする
			uc.setReadTimeout(0);              // 読み取りタイムアウト値をミリ秒単位で設定(0は無限)
			uc.setRequestMethod(method);       // URL 要求のメソッドを設定
			String sendstring = parameters.entrySet().stream().map(e->e.getKey() + "=" + e.getValue()).collect(Collectors.joining("&"));
			uc.setRequestProperty("Content-Length", Integer.toString(sendstring.getBytes("utf8").length));
			// コネクション確立→送信
			uc.connect();
			OutputStreamWriter osw = new OutputStreamWriter(uc.getOutputStream(), "utf8");
			osw.write(sendstring);
			osw.flush();
			httpresponsecode = uc.getResponseCode();
			if (httpresponsecode != 200){
				return null;
			}
			// 戻り値取得
			consumer.accept(uc.getContentType(), uc.getHeaderFields());
			// 応答読込
			try(BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream(), "utf8"))){
				StringBuffer out = new StringBuffer();
				char[] buf = new char[1024];
				int n;
				while((n = in.read(buf)) >= 0){
					out.append(buf, 0, n);
				}
				result = out.toString();
			}catch(IOException e){
				throw new RuntimeException(e);
			}
		}catch(Exception e){
		   throw new RuntimeException(e);
		}
		return result;
	}

	/**
	 * Stream受信（HTTP要求→ダウンロード）.
	 * @param headconsumer HTTP通信結果、受け取ったHTTP ContentType、HTTPヘッダを読取り処理する BiConsumer
	 * @param readconsumer 要求した結果の受け取り InputStream を指定する Consumer
	 */
	public void execute(BiConsumer<String, Map<String, List<String>>> headconsumer, Consumer<InputStream> readconsumer){
		try{
			HttpURLConnection uc = (HttpURLConnection)url.openConnection();
			uc = (HttpURLConnection)url.openConnection();
			/* HTTPリクエストヘッダの設定 */
			uc.setDoOutput(true);              // こちらからのデータ送信を可能とする
			uc.setReadTimeout(0);              // 読み取りタイムアウト値をミリ秒単位で設定(0は無限)
			uc.setRequestMethod(method);       // URL 要求のメソッドを設定
			String sendstring = parameters.entrySet().stream().map(e->e.getKey() + "=" + e.getValue()).collect(Collectors.joining("&"));
			uc.setRequestProperty("Content-Length", Integer.toString(sendstring.getBytes("utf8").length));
			// コネクション確立→送信
			uc.connect();
			OutputStreamWriter osw = new OutputStreamWriter(uc.getOutputStream(), "utf8");
			osw.write(sendstring);
			osw.flush();
			httpresponsecode = uc.getResponseCode();
			if (httpresponsecode != 200){
				throw new RuntimeException("HTTP response " + httpresponsecode);
			}
			// 戻り値取得
			headconsumer.accept(uc.getContentType(), uc.getHeaderFields());
			// 応答読込
			readconsumer.accept(uc.getInputStream());
		}catch(Exception e){
		   throw new RuntimeException(e);
		}
	}

	/**
	 * HTTP通信時のレスポンスコードを返す.
	 * @return httpresponsecode HTTP通信時のレスポンスコード、正常時は 200
	 */
	public int getHttpResponsecode(){
		return httpresponsecode;
	}
}
