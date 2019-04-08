package org.yip.sandbox;

import java.util.regex.Pattern;

/**
 * B64Util : Base64 判定を提供
 */
public final class B64Util{
	private static final byte[] DECODE_TABLE = {
	//   0   1   2   3   4   5   6   7   8   9   A   B   C   D   E   F
	    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 00-0f
	    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 10-1f
	    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, 62, -1, 63, // 20-2f + - /
	    52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, // 30-3f 0-9
	    -1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, // 40-4f A-O
	    15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63, // 50-5f P-Z _
	    -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, // 60-6f a-o
	    41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51                      // 70-7a p-z
	};
	private static final Pattern b64pattern = Pattern.compile("^[A-Za-z0-9/+=]+={1,2}$");
	private B64Util(){}
	/**
	 * byte[] Base64 判定.
	 * @param array 対象 byte[]
	 * @return true=Base64 の byte[]
	 */
	public static boolean isBase64(byte[] array){
		for(int i=0; i < array.length; i++){
			if (!isBase64(array[i]) && !isWhiteSpace(array[i])){
				return false;
			}
		}
		return true;
	}
	/**
	 * String Base64 判定.
	 * @param str 対象 String
	 * @return true=Base64文字列
	 */
	public static boolean isBase64(String str){
		if (str==null) return false;
		if (!b64pattern.matcher(str).matches()) return false;
		return isBase64(str.getBytes());
	}
	private static boolean isBase64(byte octet){
		return octet == '=' || (octet >= 0 && octet < DECODE_TABLE.length && DECODE_TABLE[octet] != -1);
	}
	private static boolean isWhiteSpace(byte byteToCheck){
		switch(byteToCheck){
			case ' ' :
			case '\n' :
			case '\r' :
			case '\t' :
				return true;
			default :
				return false;
		}
	}
}
