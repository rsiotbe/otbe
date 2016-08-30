package com.rsi.rvia.rest.tool;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class RviaConnectCipher
{
	public static String		RVIA_CONNECT_KEY	= "rsi12345RSI12345";	// 128 bit key
	private static String	ENCRYPT_MODE_RVIA	= "AES";

	/** Cifra un texto en funcion de una clave que se le pasa
	 * 
	 * @param strText
	 *           Texto sin cifrar
	 * @param strSecretKey
	 *           Clave para cifrar el texto
	 * @return String texto cifrado en base a la clave. */
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

	/** Descifra una cadena de texto en funcion de una clave
	 * 
	 * @param strText
	 *           String cifrado
	 * @param strSecretKey
	 *           String con la clave
	 * @return String con la cadena de texto descifrada */
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

	/** Cifra una cadena de datos usando el esquema 'AES' y como clave utiliza el valor de la constante 'RVIA_CONNECT_KEY'
	 * 
	 * @param strData
	 *           Cadena de datos inicialmente descifrado
	 * @return String Datos cifrados
	 * @throws Exception */
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

	/** Descifra la cadena de datos usando como clave el contenido de la constante 'RVIA_CONNECT_KEY' y usando el esquema
	 * 'AES'.
	 * 
	 * @param strData
	 *           Cadena de datos inicialmente cifrada
	 * @return String con cadena de texto descifrada
	 * @throws Exception */
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
