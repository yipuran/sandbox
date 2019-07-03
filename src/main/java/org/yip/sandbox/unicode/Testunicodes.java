package org.yip.sandbox.unicode;

/**
 * Testunicodes
 */
public class Testunicodes{

	/**
	 * @param args
	 */
	public static void main(String[] args){
		String s0 = "\u3042";
		System.out.println(s0);
		System.out.println(Unicodes.of().character(s0));

		String s1 = "\\u3042";
		System.out.println(s1);
		System.out.println(Unicodes.of().character(s1));

		String s2 = "\\\\u3042";
		System.out.println(s2);
		System.out.println(Unicodes.of().character(s2));

		String ustr = Unicodes.encode("あいうabc123()＋＝％");

		System.out.println(ustr);
		System.out.println(Unicodes.decode(ustr));

		/*  _あ_い_う_＋_＝_あ_い_う_ の文字列 */
		String str = "_\\\\u3042_\\\\u3044_\\\\u3046_\\\\uff0b_\\\\uFF1D_\\u3042_\\u3044_\u3046_";

		System.out.println(str);
		System.out.println(Unicodes.of().parse(str));


	}

}
