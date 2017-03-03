package com.rsi.rvia.rest.endpoint.ruralvia.translatejson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;
import com.rsi.rvia.rest.tool.Utils;

/**
 * The Class TranslateJsonCache.
 */
public class TranslateJsonCache
{
    /** The p log. */
    private static Logger                                 pLog                 = LoggerFactory.getLogger(TranslateJsonCache.class);
    /** The ht translate cache data. */
    private static Hashtable<String, TranslateJsonObject> htTranslateCacheData = new Hashtable<String, TranslateJsonObject>();

    /**
     * Devuelve el tamaño de la cache.
     * 
     * @return int con el tamaño de la cache
     */
    public static int getCacheSize()
    {
        int nReturn = 0;
        if (htTranslateCacheData != null)
        {
            return htTranslateCacheData.size();
        }
        return nReturn;
    }

    /**
     * Reinicia la Cache.
     */
    public static void resetCache()
    {
        if (htTranslateCacheData != null)
        {
            htTranslateCacheData = new Hashtable<String, TranslateJsonObject>();
        }
    }

    /**
     * Devuelve los datos de la cache en formato texto.
     * 
     * @return Contenido de la caché
     * @throws Exception
     *             the exception
     */
    public static String cacheToString() throws Exception
    {
        String strReturn = "";
        strReturn += Utils.hastablePrettyPrintHtml(htTranslateCacheData);
        return strReturn;
    }

    /**
     * Load data from DDBB.
     */
    private static boolean loadDataFromDDBB(String strCode)
    {
        boolean existe = false;
        if (!strCode.isEmpty())
        {
            Connection pConnection = null;
            PreparedStatement pPreparedStatement = null;
            ResultSet pResultSet = null;
            try
            {
                String strQuery = "SELECT tiporesp,desc_coderr FROM bdptb282_ERR_RVIA where id_coderr = ?";
                pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
                System.out.println("loadDataFromDDBB:" + pConnection);
                pPreparedStatement = pConnection.prepareStatement(strQuery);
                System.out.println("loadDataFromDDBB:" + pPreparedStatement);
                System.out.println("loadDataFromDDBB:" + strCode);
                pPreparedStatement.setString(1, strCode);
                pResultSet = pPreparedStatement.executeQuery();
                while (pResultSet.next())
                {
                    TranslateJsonObject jsonO = new TranslateJsonObject();
                    String tipo = (String) pResultSet.getString("tiporesp");
                    String descripcion = (String) pResultSet.getString("desc_coderr");
                    jsonO.setStrCode(strCode);
                    jsonO.setStrDesc(descripcion);
                    jsonO.setStrTipo(tipo);
                    htTranslateCacheData.put(strCode, jsonO);
                    existe = true;
                }
            }
            catch (SQLException sex)
            {
                sex.printStackTrace();
            }
            catch (Exception ex)
            {
                pLog.error("Error al realizar la consulta a la BBDD", ex);
            }
            finally
            {
                DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
            }
        }
        return existe;
    }

    /**
     * Load data from DDBB.
     */
    private static int putDataInDDBB(String strCode, String strDesc)
    {
        int ok = -1;
        if (!strCode.isEmpty())
        {
            Connection pConnection = null;
            PreparedStatement pPreparedStatement = null;
            try
            {
                String strQuery = "INSERT INTO BDPTB282_ERR_RVIA (ID_CODERR, TIPORESP, DESC_CODERR) VALUE (?, ?, ?)";
                pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
                System.out.println("putDataInDDBB:" + pConnection);
                pPreparedStatement = pConnection.prepareStatement(strQuery);
                System.out.println("putDataInDDBB:" + pPreparedStatement);
                System.out.println("putDataInDDBB:" + strCode);
                pPreparedStatement.setString(1, strCode);
                pPreparedStatement.setString(2, "02"); // por defecto es un error
                pPreparedStatement.setString(3, strDesc);
                ok = pPreparedStatement.executeUpdate(strQuery);
                if (ok > 0)
                    pConnection.commit();
            }
            catch (SQLException sex)
            {
                sex.printStackTrace();
            }
            catch (Exception ex)
            {
                pLog.error("Error al realizar la consulta a la BBDD", ex);
            }
            finally
            {
                DDBBPoolFactory.closeDDBBObjects(pLog, null, pPreparedStatement, pConnection);
            }
        }
        return ok;
    }

    /**
     * Checks if is error code.
     * 
     * @param strCode
     *            the str code
     * @param strDesc
     *            the str desc
     * @return true, if is error code
     */
    public static String isErrorCode(String strCode, String strDesc)
    {
        boolean isError = false;
        String tipoErr = "03";
        if (!htTranslateCacheData.containsKey(strCode))
        {
            if (!loadDataFromDDBB(strCode))
                putDataInDDBB(strCode, strDesc);
        }
        if (htTranslateCacheData.containsKey(strCode))
        {
            TranslateJsonObject jsonO = (TranslateJsonObject) htTranslateCacheData.get(strCode);
            if (jsonO.getStrDesc().equals(strDesc))
                tipoErr = jsonO.getStrTipo();
        }
        return tipoErr;
    }
}
