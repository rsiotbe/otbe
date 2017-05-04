package com.rsi.rvia.rest.userCommunication;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class CommunicationUtils {
	private static final String strServer = "http://lnxntf04:2035/gestionComunicados/";
	public static HttpURLConnection sendCommunication(String strEndpoint, String urlParameters, String strUserAgent) throws IOException{
		URL pUrl = new URL(strServer+strEndpoint);
		HttpURLConnection con = (HttpURLConnection) pUrl.openConnection();
		
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", strUserAgent);
		con.setRequestProperty("Accept-Language", "es-ES,en;q=0.5");
		con.setDoOutput(true);
		
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
		return con;
	}
	
	public static String getRsiSign(String strUser, String strNrbe, String strOffice, String strUsrType, String strLanguage){
		String digest = strUser.concat(strNrbe).concat(strOffice).concat(strUsrType).concat(strLanguage);
		return java.net.URLEncoder.encode(digiereSHA1(digest));
	}
	
	public static String digiereSHA1(String cadena) {

		try{
			String algoritmo="SHA1";
			byte[] inputData = cadena.getBytes();
			MessageDigest md = MessageDigest.getInstance(algoritmo);
			md.update(inputData);
			byte[] digest = md.digest();
			
			return digOut(digest);
		}catch(NoSuchAlgorithmException nsae){
			return null;
		}	
	}

	private static String digOut(byte[] digestBits) {

		StringBuffer sb = new StringBuffer();
        for (int i = 0; i < digestBits.length; i++) {
            char c1, c2;

            c1 = (char) ((digestBits[i] >>> 4) & 0xf);
            c2 = (char) (digestBits[i] & 0xf);
            c1 = (char) ((c1 > 9) ? 'A' + (c1 - 10) : '0' + c1);
            c2 = (char) ((c2 > 9) ? 'A' + (c2 - 10) : '0' + c2);
            sb.append(c1);
            sb.append(c2);
            if (((i+1) % 4) == 0)
                sb.append(' ');
		}
		return sb.toString();
	}

	public static long getRsiDate() {
		return new Date().getTime();
	}
}
