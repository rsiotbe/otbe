package com.rsi.rvia.rest.tool;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class RviaConnectCipher
{
	public static String		RVIA_CONNECT_KEY	= "rsi12345RSI12345";	// 128 bit key
	private static String	ENCRYPT_MODE_RVIA	= "AES";

	public static String symmetricEncrypt(String strText, String strSecretKey)
	{
		byte[] bRaw;
		String strEncryptedString;
		SecretKeySpec pSkeySpec;
		byte[] bEncryptText = strText.getBytes();
		Cipher pCipher;
		try
		{
			bRaw = strSecretKey.getBytes();
			pSkeySpec = new SecretKeySpec(bRaw, ENCRYPT_MODE_RVIA);
			pCipher = Cipher.getInstance(ENCRYPT_MODE_RVIA);
			pCipher.init(Cipher.ENCRYPT_MODE, pSkeySpec);
			strEncryptedString = Base64.encodeBase64String(pCipher.doFinal(bEncryptText));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "Error";
		}
		return strEncryptedString;
	}

	public static String symmetricDecrypt(String strText, String strSecretKey)
	{
		Cipher pCipher;
		String pEncryptedString;
		byte[] bEncryptText = null;
		byte[] bRaw;
		SecretKeySpec pSkeySpec;
		try
		{
			bRaw = strSecretKey.getBytes();
			pSkeySpec = new SecretKeySpec(bRaw, ENCRYPT_MODE_RVIA);
			bEncryptText = Base64.decodeBase64(strText);
			pCipher = Cipher.getInstance(ENCRYPT_MODE_RVIA);
			pCipher.init(Cipher.DECRYPT_MODE, pSkeySpec);
			pEncryptedString = new String(pCipher.doFinal(bEncryptText));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "Error";
		}
		return pEncryptedString;
	}

	public static String cipherUserData(String strData) throws Exception
	{
		String strReturn;
		Key pAesKey = new SecretKeySpec(RVIA_CONNECT_KEY.getBytes(), ENCRYPT_MODE_RVIA);
		Cipher pCipher = Cipher.getInstance(ENCRYPT_MODE_RVIA);
		pCipher.init(Cipher.ENCRYPT_MODE, pAesKey);
		byte[] bEncrypted = pCipher.doFinal(strData.getBytes());
		strReturn = new String(Base64.encodeBase64(bEncrypted));
		return strReturn;
	}

	public static String decipherUserData(String strData) throws Exception
	{
		String strReturn;
		Key pAesKey = new SecretKeySpec(RVIA_CONNECT_KEY.getBytes(), ENCRYPT_MODE_RVIA);
		Cipher pCipher = Cipher.getInstance(ENCRYPT_MODE_RVIA);
		pCipher.init(Cipher.DECRYPT_MODE, pAesKey);
		strReturn = new String(pCipher.doFinal(Base64.decodeBase64(strData)));
		return strReturn;
	}
}
