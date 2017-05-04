package com.rsi.rvia.rest.userCommunication;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class CommunicationUtils {
	
	public static String digiereSHA1(String cadena) {

			try{
				String algoritmo="SHA1";
				byte[] inputData = cadena.getBytes();
				MessageDigest md = MessageDigest.getInstance(algoritmo);
				md.update(inputData);
				byte[] digest = md.digest();
				String aux = new String(digest);
				//System.out.println("El digOut vale: "+digOut(digest)+".");
				
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

	private static long getfechaRSI() {
		return new Date().getTime();
	}
}
