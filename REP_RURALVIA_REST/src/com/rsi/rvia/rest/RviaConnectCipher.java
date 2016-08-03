package com.rsi.rvia.rest;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class RviaConnectCipher
{
	public static String RVIA_CONNECT_KEY = "rsi12345RSI12345"; // 128 bit key
	private static String ENCRYPT_MODE_RVIA = "AES";
	
   public static String symmetricEncrypt(String text, String secretKey) {
      byte[] raw;
      String encryptedString;
      SecretKeySpec skeySpec;
      byte[] encryptText = text.getBytes();
      Cipher cipher;
      try {
          raw = secretKey.getBytes();
          skeySpec = new SecretKeySpec(raw, ENCRYPT_MODE_RVIA);
          cipher = Cipher.getInstance(ENCRYPT_MODE_RVIA);
          cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
          encryptedString = Base64.encodeBase64String(cipher.doFinal(encryptText));
      } 
      catch (Exception e) {
          e.printStackTrace();
          return "Error";
      }
      return encryptedString;
  }

  public static String symmetricDecrypt(String text, String secretKey) {
      Cipher cipher;
      String encryptedString;
      byte[] encryptText = null;
      byte[] raw;
      SecretKeySpec skeySpec;
      try {
          raw = secretKey.getBytes();
          skeySpec = new SecretKeySpec(raw, ENCRYPT_MODE_RVIA);
          encryptText = Base64.decodeBase64(text);
          cipher = Cipher.getInstance(ENCRYPT_MODE_RVIA);
          cipher.init(Cipher.DECRYPT_MODE, skeySpec);
          encryptedString = new String(cipher.doFinal(encryptText));
      } catch (Exception e) {
          e.printStackTrace();
          return "Error";
      }
      return encryptedString;
  }
	
	
	public static String cipherUserData(String strData) throws Exception
	{
		String strReturn;
		Key aesKey = new SecretKeySpec(RVIA_CONNECT_KEY.getBytes(), ENCRYPT_MODE_RVIA);
		Cipher cipher = Cipher.getInstance(ENCRYPT_MODE_RVIA);
		cipher.init(Cipher.ENCRYPT_MODE, aesKey);
		byte[] encrypted = cipher.doFinal(strData.getBytes());
		strReturn = new String(Base64.encodeBase64(encrypted));
	   return strReturn;
	}
	
	public static String decipherUserData(String strData) throws Exception
	{
		String strReturn;
		Key aesKey = new SecretKeySpec(RVIA_CONNECT_KEY.getBytes(), ENCRYPT_MODE_RVIA);
		Cipher cipher = Cipher.getInstance(ENCRYPT_MODE_RVIA);
		cipher.init(Cipher.DECRYPT_MODE, aesKey);
		strReturn = new String(cipher.doFinal(Base64.decodeBase64(strData)));
		return strReturn;		
	}
}
