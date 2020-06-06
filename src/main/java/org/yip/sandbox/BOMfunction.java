package org.labo.csv;

import java.io.IOException;
import java.io.OutputStream;

/**
 * BOM操作ユーティリティクラス.
 */
public final class BOMfunction{
	/** private constructor. */
	private BOMfunction(){}

	/**
	 * BOMを出力する.
	 * @param out OutputStream
	 * @throws IOException
	 */
	public static void push(OutputStream out) throws IOException{
		out.write(new byte[]{ (byte)0xef,(byte)0xbb, (byte)0xbf });
	}

	/**
	 * BOMが先頭に付いた文字列のBOMを除去する.
	 * @param str 除去前の文字列
	 * @return 除去後の文字列
	 */
	public static String chop(String str){
		byte[] b = str.getBytes();
		if (b.length < 3) return str;
		if (b[0] == -17 && b[1] == -69 && b[2] == -65) {
			byte[] n = new byte[b.length-3];
			for(int i=0,k=3; i < n.length;i++, k++){
				n[i] = b[k];
			}
			return new String(n);
		}else if(b[0] == -2 && b[1] == -1){
			byte[] n = new byte[b.length-2];
			for(int i=0,k=2; i < n.length;i++, k++){
				n[i] = b[k];
			}
			return new String(n);
		}else if(b[0] == -1 && b[1] == -2){
			byte[] n = new byte[b.length-2];
			for(int i=0,k=2; i < n.length;i++, k++){
				n[i] = b[k];
			}
			return new String(n);
		}
		return str;
	}
	/**
	 * 文字列がBOM付き文字であるか返す
	 * @param str 文字列
	 * @return true=BOM付き
	 */
	public static boolean match(String str){
		if (str==null) return false;
		byte[] b = str.getBytes();
		if (b.length < 3) return false;
		if (b[0] == -17 && b[1] == -69 && b[2] == -65) return true;
		// UTF_16BE BOM
		if (b[0] == -2 && b[1] == -1) return true;
		// UTF_16LE BOM
		if (b[0] == -1 && b[1] == -2) return true;
		return false;
	}
}
