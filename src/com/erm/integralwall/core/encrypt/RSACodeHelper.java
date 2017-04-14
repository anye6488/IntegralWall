package com.erm.integralwall.core.encrypt;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

import javax.crypto.Cipher;

import android.util.Base64;

/**
 * 加密类
 * 作者：liemng on 2017/3/31
 * 邮箱：859686819@qq.com
 *
 */
public class RSACodeHelper {
	
	private static final String TAG = RSACodeHelper.class.getSimpleName();
	
	private static final String RSA_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCoXbk+id3B5ko6/NrNPCE6Cs9h+GETvxGp+jSUWXPARXQhXYbeJzo2w8dwFDrnkNHpFare/ad+VcL3eEWQC9wdF9oAkHmZeJN7l8D1swhEZTuZ0cyXBMtzcm92K2NJUYjWdssw+GqhfcI7uhTGMQ4bzYxZZJE/WoT3siwTMlQgiQIDAQAB";
	private static final String RSA_PRIVATE_KEY = "";
	
	/**
	 * 加密
	 * @param str
	 * @return
	 */
	public static String encrypt(String str){
        RSAPublicKey pubKey;
        byte[] cipherText;
        Cipher cipher;
		try {
			cipher = Cipher.getInstance("RSA");          
			pubKey = (RSAPublicKey) getPublicKey();
			
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);  
			cipherText = cipher.doFinal(str.getBytes());  
			//加密后的东西  
	        return new String(cipherText);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;  
	}
	
	/**
	 * 解密
	 * @param cipherText
	 * @return
	 */
	public static String decrypt(byte[] cipherText){
		RSAPrivateKey privKey;
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("RSA");          
			privKey = (RSAPrivateKey) getPrivateKey();
			//开始解密  
			cipher.init(Cipher.DECRYPT_MODE, privKey);   
			byte[] plainText = cipher.doFinal(cipherText);  
			return new String(plainText);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;  
	}
	
	/**
	 *生成私钥  公钥 
	 */
	public static void generation(){
		KeyPairGenerator keyPairGenerator;
		try {
			keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			SecureRandom secureRandom = new SecureRandom(new Date().toString().getBytes());  
	        keyPairGenerator.initialize(1024, secureRandom);  
	        KeyPair keyPair = keyPairGenerator.genKeyPair();  
	        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();  
	        byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();  
	        /**公钥*/
	        String publicKey = Base64.encodeToString(publicKeyBytes, Base64.DEFAULT);
	        /**私钥*/
	        String privateKey = Base64.encodeToString(privateKeyBytes, Base64.DEFAULT);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
	}
	
	/**
	 * 获取公钥
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public static PublicKey getPublicKey() throws Exception {  
		byte[] keyBytes = Base64.decode(RSA_PUBLIC_KEY, Base64.DEFAULT);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);  
        KeyFactory kf = KeyFactory.getInstance("RSA");   
        return kf.generatePublic(spec);  
    }  
	
	/**
     * 获取私钥
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public static PrivateKey getPrivateKey()throws Exception {  
		byte[] keyBytes = Base64.decode(RSA_PRIVATE_KEY, Base64.DEFAULT);
        PKCS8EncodedKeySpec spec =new PKCS8EncodedKeySpec(keyBytes);  
        KeyFactory kf = KeyFactory.getInstance("RSA");  
        return kf.generatePrivate(spec);  
      }  
}