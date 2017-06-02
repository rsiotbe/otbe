package com.rsi.rvia.rest.userCommunication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.Date;
import java.util.Properties;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.Constants;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;

public class CommunicationUtils
{
    public static HttpURLConnection sendCommunication(String strEndpoint, String urlParameters, String strUserAgent,
            String strRviaServer) throws IOException
    {
        Properties pProperties = new Properties();
        pProperties.load(CommunicationUtils.class.getResourceAsStream("/communications.properties"));
        String strServerUrl = (String) pProperties.get(strRviaServer);
        URL pUrl = new URL(strServerUrl + strEndpoint);
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

    public static String getRsiSign(String strUser, String strNrbe, String strOffice, String strUsrType,
            String strLanguage) throws UnsupportedEncodingException
    {
        String digest = strUser.concat(strNrbe).concat(strOffice).concat(strUsrType).concat(strLanguage);
        return URLEncoder.encode(digiereSHA1(digest), Constants.UTF8);
    }

    public static String digiereSHA1(String cadena)
    {
        try
        {
            String algoritmo = "SHA1";
            byte[] inputData = cadena.getBytes();
            MessageDigest md = MessageDigest.getInstance(algoritmo);
            md.update(inputData);
            byte[] digest = md.digest();
            return digOut(digest);
        }
        catch (NoSuchAlgorithmException nsae)
        {
            return null;
        }
    }

    private static String digOut(byte[] digestBits)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < digestBits.length; i++)
        {
            char c1, c2;
            c1 = (char) ((digestBits[i] >>> 4) & 0xf);
            c2 = (char) (digestBits[i] & 0xf);
            c1 = (char) ((c1 > 9) ? 'A' + (c1 - 10) : '0' + c1);
            c2 = (char) ((c2 > 9) ? 'A' + (c2 - 10) : '0' + c2);
            sb.append(c1);
            sb.append(c2);
            if (((i + 1) % 4) == 0)
                sb.append(' ');
        }
        return sb.toString();
    }

    public static long getRsiDate()
    {
        return new Date().getTime();
    }

    public static String convertInputStream(InputStream pInput)
    {
        StringBuilder pResult = new StringBuilder();
        BufferedReader pReader = null;
        try
        {
            pReader = new BufferedReader(new InputStreamReader(pInput));
            String line;
            while ((line = pReader.readLine()) != null)
            {
                pResult.append(line);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (pReader != null)
            {
                try
                {
                    pReader.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return pResult.toString();
    }

    public static int getHistoryNumber(String strHistoryCod, String strMailCod) throws Exception
    {
        Logger pLog = LoggerFactory.getLogger("CommunicationUtils");
        pLog.info("Messages ::: MessageDetails ::: Start ");
        Connection pConnection = null;
        JSONArray pJsongetNewsResponse = null;
        String strQuery = "{? = call BEL.PK_CONSULTA_BUZON_MOVIL.getHistoryNumber(?,?)}";
        int iHistoryNumber = 0;
        try
        {
            pLog.debug("Messages ::: MessageDetails ::: DDBBProvider ");
            pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
            pConnection.setAutoCommit(false);
        }
        catch (Exception ex)
        {
            pLog.error("Messages ::: getNews ::: DDBBProvider Exception " + ex.getMessage());
        }
        CallableStatement pCallableStatement = null;
        try
        {
            pLog.info("Messages ::: MessageDetails ::: pCallableStatement ");
            pCallableStatement = pConnection.prepareCall(strQuery);
            pCallableStatement.registerOutParameter(1, Types.INTEGER);
            pCallableStatement.setString(2, strHistoryCod);
            pCallableStatement.setString(3, strMailCod);
            pCallableStatement.executeUpdate();
            iHistoryNumber = pCallableStatement.getInt(1);
            pLog.debug("Messages ::: MessageDetails ::: pJsongetNewsResponse " + pJsongetNewsResponse);
        }
        catch (Exception e)
        {
            pLog.error("Messages ::: MessageDetails ::: pCallableStatement Exception " + e.getMessage());
        }
        finally
        {
            try
            {
                pCallableStatement.close();
                pConnection.close();
            }
            catch (Exception e)
            {
                pLog.error("Messages ::: MessageDetails ::: pCallableStatement Close Exception " + e.getMessage());
            }
        }
        return iHistoryNumber;
    }
}
