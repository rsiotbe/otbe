package com.rsi.rvia.rest.tool;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RviaConnectCipher
{
    public static String  RVIA_CONNECT_KEY  = "rsi12345RSI12345";                              // 128 bit key
    private static String ENCRYPT_KEY_RVIA  = "AES";
    private static String ENCRYPT_MODE_RVIA = "AES/CBC/PKCS5Padding";
    private static Logger pLog              = LoggerFactory.getLogger(RviaConnectCipher.class);

    /**
     * Cifra un texto en funcion de una clave que se le pasa
     * 
     * @param strText
     *            Texto sin cifrar
     * @param strSecretKey
     *            Clave para cifrar el texto
     * @return String texto cifrado en base a la clave.
     */
    public static String symmetricEncrypt(String strText, String strSecretKey)
    {
        byte[] bRaw;
        String strEncryptedString;
        SecretKeySpec pSkeySpec;
        byte[] bEncryptText = strText.getBytes();
        Cipher pCipher;
        byte[] bIvByte;
        IvParameterSpec pIvParameterSpec;
        try
        {
            bRaw = strSecretKey.getBytes();
            pSkeySpec = new SecretKeySpec(bRaw, ENCRYPT_KEY_RVIA);
            pCipher = Cipher.getInstance(ENCRYPT_MODE_RVIA);
            bIvByte = new byte[pCipher.getBlockSize()];
            pIvParameterSpec = new IvParameterSpec(bIvByte);
            pCipher.init(Cipher.ENCRYPT_MODE, pSkeySpec, pIvParameterSpec);
            strEncryptedString = Base64.encodeBase64String(pCipher.doFinal(bEncryptText));
            pLog.trace("Se encripta el texto: " + strText + "  ->  " + strEncryptedString);
        }
        catch (Exception e)
        {
            pLog.error("Error en el proceso de encriptado: " + e);
            return "Error";
        }
        return strEncryptedString;
    }

    /**
     * Descifra una cadena de texto en funcion de una clave
     * 
     * @param strText
     *            String cifrado
     * @param strSecretKey
     *            String con la clave
     * @return String con la cadena de texto descifrada
     */
    public static String symmetricDecrypt(String strText, String strSecretKey)
    {
        Cipher pCipher;
        String strEncryptedString;
        byte[] bEncryptText = null;
        byte[] bRaw;
        byte[] bIvByte;
        SecretKeySpec pSkeySpec;
        IvParameterSpec pIvParameterSpec;
        try
        {
            bRaw = strSecretKey.getBytes();
            pSkeySpec = new SecretKeySpec(bRaw, ENCRYPT_KEY_RVIA);
            bEncryptText = Base64.decodeBase64(strText);
            pCipher = Cipher.getInstance(ENCRYPT_MODE_RVIA);
            bIvByte = new byte[pCipher.getBlockSize()];
            pIvParameterSpec = new IvParameterSpec(bIvByte);
            pCipher.init(Cipher.DECRYPT_MODE, pSkeySpec, pIvParameterSpec);
            strEncryptedString = new String(pCipher.doFinal(bEncryptText));
            pLog.trace("Se desencripta el texto: " + strText + "  ->  " + strEncryptedString);
        }
        catch (Exception e)
        {
            pLog.error("Error en el proceso de desencriptado: " + e);
            return "Error";
        }
        return strEncryptedString;
    }
}
