package org.yip.sandbox.security.aes;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.yip.sandbox.B64Util;

/**
 * AES 256 暗合復号.  mode : CBC     padding : PKCS5Padding
 * iv ← SHA-256 SecretKeySpec byte[] の先頭16byte
 */
public final class AESCipher{
	private SecretKeySpec key;
	private IvParameterSpec iv;

	private AESCipher(String password){
		try{
			byte[] keydata = password.getBytes();
			MessageDigest sha = MessageDigest.getInstance("SHA-256");
			keydata = sha.digest(keydata);
			keydata = Arrays.copyOf(keydata, 32);
			key = new SecretKeySpec(keydata, "AES");
			iv = new IvParameterSpec(Arrays.copyOf(key.getEncoded(), 16));
		}catch(NoSuchAlgorithmException e){
			throw new RuntimeException(e.getMessage());
		}
	}
	/**
	 * AES インスタンス生成
	 * @param keyword 共通鍵
	 * @return AESCipher
	 */
	public static AESCipher of(String keyword){
		return new AESCipher(keyword);
	}
	/**
	 * CBC モードで使用する Initilize Vector
	 * @return 16byte byte[]
	 */
	public byte[] getIV(){
		return iv.getIV();
	}
	/**
	 * 暗合化.
	 * @param message 平文
	 * @return 暗合文byte[]
	 */
	public byte[] encrypt(String message){
		try{
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key, iv);
			return cipher.doFinal(message.getBytes());
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	/**
	 * 復号
	 * @param encbytes 暗合文byte[]
	 * @return 平文
	 */
	public byte[] decrypt(byte[] encbytes){
		try{
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key, iv);
			return cipher.doFinal(encbytes);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	/**
	 * 復号 byte[] → String → Base64 decode → 復元
	 * @param encbytes
	 * @return
	 */
	public String decryptB64(byte[] encbytes){
		String dectxt = new String(decrypt(encbytes), StandardCharsets.UTF_8);
		if (B64Util.isBase64(dectxt)){
			dectxt = new String(Base64.getDecoder().decode(dectxt), StandardCharsets.UTF_8);
		}
		return dectxt;
	}
	/**
	 * ランダム文字列生成
	 * @param len 長さ
	 * @return a-zA-Z0-9 の文字列
	 */
	public static String randomKey(int len){
		SecureRandom secureRandom = new SecureRandom();
		String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder sb = new StringBuilder();
		for(int i=0; i < len; i++){
			sb.append(CHARACTERS.charAt(secureRandom.nextInt(CHARACTERS.length())));
		}
		return sb.toString();
	}
}
