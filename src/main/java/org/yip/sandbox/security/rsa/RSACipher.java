package org.yip.sandbox.security.rsa;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
//import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OpenSSL RSA
 */
public class RSACipher{
	private static RSACipher inst;
	private Key public_key;
	private Key secret_key;
	Logger logger = LoggerFactory.getLogger(this.getClass());

	private RSACipher(){}
	private RSACipher(String publicPath, String privatePath){
		try{
			KeyFactory keyfactory = KeyFactory.getInstance("RSA");
			public_key = keyfactory.generatePublic(new X509EncodedKeySpec(readFile(new File(publicPath).getAbsolutePath())));
			secret_key = keyfactory.generatePrivate(new PKCS8EncodedKeySpec(readFile(new File(privatePath).getAbsolutePath())));
		}catch(Exception ex){
			logger.error(ex.getMessage(), ex);
		}
	}
	/**
	 * Webアプリ用 Singleton インスタンス取得, アプリは、使用前に１回は init()を実行しなければならない。
	 * @return RSACipher
	 */
	public static synchronized RSACipher getInstance(){
		if (inst==null) inst = new RSACipher();
		return inst;
	}
//	/**
//	 * Webアプリ用初期化
//	 * @param context ServletContext
//	 * @param publicName リソースに置いた公開鍵ファイル名  derファイル名
//	 * @param privateName リソースに置いた秘密鍵ファイル名 pk8ファイル名
//	 */
//	public void init(ServletContext context, String publicName, String privateName){
//		String root = context.getRealPath("WEB-INF/classes");
//		try{
//			KeyFactory keyfactory = KeyFactory.getInstance("RSA");
//			public_key = keyfactory.generatePublic(new X509EncodedKeySpec(readFile(new File(root + "/" + publicName).getAbsolutePath())));
//			secret_key = keyfactory.generatePrivate(new PKCS8EncodedKeySpec(readFile(new File(root + "/" + privateName).getAbsolutePath())));
//		}catch(Exception ex){
//			logger.error(ex.getMessage(), ex);
//		}
//	}

	/**
	 * RSA鍵 path指定 インスタンス取得
	 * @param publicPath 公開鍵ファイル名  derファイルPath
	 * @param privatePath 秘密鍵ファイル名 pk8ファイルPath
	 * @return
	 */
	public static RSACipher of(String publicPath, String privatePath){
		return new RSACipher(publicPath, privatePath);
	}

	/**
	 * 暗号化. (Base64 エンコードして返す）
	 * @param planetxt 対象文字列 null 指定は null を返す
	 * @return 暗号化されたBase64エンコード済文字列
	 * @throws Exception
	 */
	public String encrypt(String planetxt){
		if (planetxt==null) return null;
		try{
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, public_key);
			return Base64.getEncoder().encodeToString(cipher.doFinal(planetxt.getBytes()));
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 秘密鍵による暗号化. (Base64 エンコードして返す）
	 * @param planetxt 対象文字列 null 指定は null を返す
	 * @return 暗号化されたBase64エンコード済文字列
	 * @throws Exception
	 */
	public String encryptPrivate(String planetxt) throws Exception{
		if (planetxt==null) return null;
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
		cipher.init(Cipher.ENCRYPT_MODE, secret_key);
		return Base64.getEncoder().encodeToString(cipher.doFinal(planetxt.getBytes()));
	}

	/**
	 * 複合化. (暗号化→Base64 エンコード済を指定）
	 * @param encstring Base64 エンコード済の暗号テキスト
	 * @return 複合文字列
	 * @throws Exception
	 */
	public String decrpt(String encstring) throws Exception{
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
		cipher.init(Cipher.DECRYPT_MODE, secret_key);
		return new String(cipher.doFinal(Base64.getDecoder().decode(encstring)));
	}
	/**
	 * 公開鍵による複合化. (暗号化→Base64 エンコード済を指定）
	 * @param encstring Base64 エンコード済の暗号テキスト
	 * @return 複合文字列
	 * @throws Exception
	 */
	public String decrptPublic(String encstring) throws Exception{
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
		cipher.init(Cipher.DECRYPT_MODE, public_key);
		return new String(cipher.doFinal(Base64.getDecoder().decode(encstring)));
	}
	/**
	 * 公開鍵による複合化(byte[]). (暗号化→Base64 エンコード済を指定）
	 * @param encstring Base64 エンコード済の暗号テキスト
	 * @return 複合文字列
	 * @throws Exception
	 */
	public byte[] decrptPublicByte(String encstring) throws Exception{
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
		cipher.init(Cipher.DECRYPT_MODE, public_key);
		return cipher.doFinal(Base64.getDecoder().decode(encstring));
	}
	/**
	 * RSA公開鍵 Base64エンコード文字列の取得.
	 * @return RSA公開鍵 Base64エンコード文字列
	 */
	public String getPublickeyBase64(){
		return Base64.getEncoder().encodeToString(public_key.getEncoded());
	}

	/**
	 * 署名の作成.
	 * @param str メッセージ
	 * @return Base64 エンコードした署名データ
	 * @throws Exception
	 */
	public String createSignature(String str) throws Exception{
		Signature signer = Signature.getInstance("SHA256withRSA");
		signer.initSign((PrivateKey)secret_key);
		signer.update(str.getBytes("UTF-8"));
		byte[] b = signer.sign();
		return Base64.getEncoder().encodeToString(b);
	}
	/**
	 * byte[] → 署名の作成.
	 * @param data メッセージ byte[]
	 * @return Base64 エンコードした署名データ
	 * @throws Exception
	 */
	public String createSignature(byte[] data) throws Exception{
		Signature signer = Signature.getInstance("SHA256withRSA");
		signer.initSign((PrivateKey)secret_key);
		signer.update(data);
		byte[] b = signer.sign();
		return Base64.getEncoder().encodeToString(b);
	}

	/**
	 * 署名検証 （秘密鍵で暗号化→Base64エンコード文字列 を検証）
	 * @param signstring 秘密鍵で暗号化したBase64エンコード済のデータ
	 * @param origin 署名元のデータ
	 * @return true = OK
	 */
	public boolean verifySignature(String signstring, String origin){
		try{
			byte[] sigdata = Base64.getDecoder().decode(signstring);
			Signature signature = Signature.getInstance("SHA256withRSA");
			signature.initVerify((PublicKey)public_key);
			signature.update(origin.getBytes());
			return signature.verify(sigdata);
		}catch(Exception e){
			return false;
		}
	}

	private byte[] readFile(String path) throws IOException{
		try(InputStream in = new FileInputStream(path)){
			byte[] data = new byte[in.available()];
			in.read(data);
			in.close();
			return data;
		}
	}
}
