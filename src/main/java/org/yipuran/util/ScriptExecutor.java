package org.yipuran.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * ラムダ・スクリプト実行.
 * <PRE>
 * プロセスとして実行するスクリプトをラムダ式、Supplier によるスクリプト実行から
 *  BiConsumer で結果標準出力と標準エラー出力を処理する。
 * 例外捕捉は全て標準エラー出力として受け取る。
 * java.lang.Process の exitValue() 結果をメソッドの戻り値とする。
 *
 * BiConsumer は、標準出力、標準エラー出力を String で受け取る実行方法と
 * InputStream(byte単位のストリーム）で受け取る実行方法の２通りのメソッドがある。
 *
 * BiConsumer のラムダが実行された後に、Process 実行結果を得る＝すなわちメソッドが返ってくる。
 *
 * （Linux での Pythonスクリプト 実行例）
 *   int sts = ScriptExecutor.run(()-&gt;"python /usr/local/app/myapp.py", (t, e)-&gt;{
 *         System.out.println("stdout : " + t );
 *         System.out.println("stderr : " + e );
 *   });
 *
 * （Windows 環境での Pythonスクリプト 実行例）
 *   int sts = ScriptExecutor.run(()-&gt;"cmd.exe /C python c:/Userr/app/myapp.py", (t, e)-&gt;{
 *         System.out.println("stdout : " + t );
 *         System.out.println("stderr : " + e );
 *   });
 *
 * </PRE>
 */
public final class ScriptExecutor{

	private ScriptExecutor(){}

	/**
	 * ラムダ・スクリプト実行（String → BiConsumer）
	 * @param scriptSupplier 実行するスクリプト
	 * @param consumer BiConsumer&lt;String, String&gt; = &lt;stdout, stderr&gt;
	 * @return java.lang.Process の exitValue() 結果、例外発生時は 1 を返す。
	 */
	public static int run(Supplier<String> scriptSupplier, BiConsumer<String, String> consumer){
		int rtn = 0;
		String stdout;
		String stderr;
		try{
			Process p = Runtime.getRuntime().exec(scriptSupplier.get());
			_processStreamReader p_stderr = new _processStreamReader(p.getErrorStream());
			_processStreamReader p_stdout = new _processStreamReader(p.getInputStream());
			p_stderr.start();
			p_stdout.start();
			p_stderr.join();
			p_stdout.join();
			p.waitFor();
			rtn = p.exitValue();
			stdout = p_stdout.getString();
			stderr = p_stderr.getString();
		}catch(Exception ex){
			rtn = 1;
			stdout = "";
			StringBuilder sb = new StringBuilder();
			sb.append(ex.getMessage());
			sb.append("\n");
			sb.append(Arrays.stream(ex.getStackTrace()).map(t->t.toString()).collect(Collectors.joining("\n\t")));
			Optional.ofNullable(ex.getCause()).ifPresent(x->{
				sb.append("\n");
				sb.append("Caused by: ");
				sb.append(x.getMessage());
				sb.append("\n");
			sb.append(Arrays.stream(x.getStackTrace()).map(t->t.toString()).collect(Collectors.joining("\n\t")));
			});
			stderr = sb.toString();
		}
		consumer.accept(stdout, stderr);
		return rtn;
	}
	/**
	 * ラムダ・スクリプト実行（InputStream → BiConsumer）
	 * @param scriptSupplier 実行するスクリプト
	 * @param consumer BiConsumer&lt;InputStream, InputStream&gt; = &lt;stdout の InputStream, stderr InputStream&gt;
	 * @return java.lang.Process の exitValue() 結果、例外発生時は 1 を返す。
	 */
	public static int runStream(Supplier<String> scriptSupplier, BiConsumer<InputStream, InputStream> consumer){
		int rtn = 0;
		try{
			Process p = Runtime.getRuntime().exec(scriptSupplier.get());
			p.waitFor();
			consumer.accept(p.getInputStream(), p.getErrorStream());
			return p.exitValue();
		}catch(Exception ex){
			rtn = 1;
			StringBuilder sb = new StringBuilder();
			sb.append(ex.getMessage());
			sb.append("\n");
			sb.append(Arrays.stream(ex.getStackTrace()).map(t->t.toString()).collect(Collectors.joining("\n\t")));
			Optional.ofNullable(ex.getCause()).ifPresent(x->{
				sb.append("\n");
				sb.append("Caused by: ");
				sb.append(x.getMessage());
				sb.append("\n");
			sb.append(Arrays.stream(x.getStackTrace()).map(t->t.toString()).collect(Collectors.joining("\n\t")));
			});
			try{
				consumer.accept(new ByteArrayInputStream("".getBytes()), new ByteArrayInputStream(sb.toString().getBytes("UTF-8")));
			}catch(UnsupportedEncodingException e){
				e.printStackTrace();
			}
		}
		return rtn;
	}
	//---------------
	static class _processStreamReader extends Thread{
		StringBuffer        sb;
		InputStreamReader   inredaer;
		public _processStreamReader(InputStream in){
			super();
			this.inredaer = new InputStreamReader(in);
			this.sb = new StringBuffer();
		}
		@Override
		public void run(){
			try{
				int i;
				int BUFFER_SIZE = 1024;
				char[] c = new char[BUFFER_SIZE];
				while((i = this.inredaer.read(c,0,BUFFER_SIZE - 1)) > 0){
					this.sb.append(c,0,i);
					if (i < BUFFER_SIZE - 1){ break; }
				}
				this.inredaer.close();
			}catch(IOException e){}
		}
		public String getString(){
			return this.sb.toString();
		}
	}
}
