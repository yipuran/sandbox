package org.yip.sandbox.unicode;

import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import org.yipuran.regex.MatcherStream;

/**
 * Unicodes
 */
public final class Unicodes{
	private static Pattern ptn2;
	private static Pattern ptn4;
	private Unicodes(){
		ptn2 = Pattern.compile("\\\\u[0-9a-fA-F]{4}");
		ptn4 = Pattern.compile("\\\\\\\\u[0-9a-fA-F]{4}");
	}
	public static Unicodes of(){
		return new Unicodes();
	}
	/**
	 * Unicode文字列に変換する("あ" → "\u3042")
	 */
	public static String encode(String string){
		if (string == null || string.isEmpty()) return "";
		StringBuilder sb = new StringBuilder();
		for(int i=0; i < string.length(); i++){
			sb.append(String.format("\\u%04X", Character.codePointAt(string, i)));
		}
		return sb.toString();
	}
	/**
	 * Unicode文字列→UTF-8
	 */
	public static String decode(String unicode){
		if (unicode == null || unicode.isEmpty()) return "";
		String[] cs = unicode.split("\\\\u");
		int[] cp = new int[cs.length - 1];
		for(int i=0;i < cp.length; i++){
			cp[i] = Integer.parseInt(cs[i + 1], 16);
		}
		return new String(cp, 0, cp.length);
	}
	/**
	 * １文字だけのUnicode文字→UTF-8
	 */
	public String character(String unicode){
      if (unicode==null || unicode.isEmpty()) return "";
      if (ptn4.matcher(unicode).matches()){
      	return new String(new int[]{ Integer.parseInt(unicode.substring(3), 16) }, 0, 1);
      }else if(ptn2.matcher(unicode).matches()){
      	return new String(new int[]{ Integer.parseInt(unicode.substring(2), 16) }, 0, 1);
      }else{
      	return unicode;
      }
   }

	/**
	 * Unicode表現＋Unicode以外表現の混在文字列→UTF-8
	 */
	public String parse(String str){
		if (str==null || str.isEmpty()) return "";
		/* for Java 9  *
		String s = ptn4.matcher(str).replaceAll(m->
			new String(new int[]{ Integer.parseInt(m.group().substring(3), 16) }, 0, 1)
		);
		return ptn2.matcher(s).replaceAll(m->
			new String(new int[]{ Integer.parseInt(m.group().substring(2), 16) }, 0, 1)
		);
		/****************/
		AtomicReference<String> r = new AtomicReference<>(str);
		MatcherStream.findMatches(ptn4, str).forEach(e->{
			r.set(r.get().replace(e.group(), new String(new int[]{ Integer.parseInt(e.group().substring(3), 16) }, 0, 1)));
		});
		MatcherStream.findMatches(ptn2, str).forEach(e->{
			r.set(r.get().replace(e.group(), new String(new int[]{ Integer.parseInt(e.group().substring(2), 16) }, 0, 1)));
		});
		return r.get();
	}
}
