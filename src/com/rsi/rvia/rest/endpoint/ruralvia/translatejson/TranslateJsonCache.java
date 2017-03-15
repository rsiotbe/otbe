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
import com.rsi.rvia.rest.error.exceptions.ApplicationException;
import com.rsi.rvia.rest.session.RequestConfigRvia;
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
     * Devuelve los datos de la cache.
     * 
     * @return Contenido de la caché
     * @throws Exception
     *             the exception
     */
    public static Hashtable<String, TranslateJsonObject> getCache() throws Exception
    {
        return htTranslateCacheData;
    }

    /**
     * Load data from DDBB.
     */
    private static boolean loadDataFromDDBB(String strCode, RequestConfigRvia pSessionRviaData, String clave_pagina)
            throws ApplicationException
    {
        boolean existe = false;
        boolean error = false;
        if (!strCode.isEmpty())
        {
            Connection pConnection = null;
            PreparedStatement pPreparedStatement = null;
            ResultSet pResultSet = null;
            try
            {
                String strQuery = "SELECT tiporesp,desc_coderr, descripcion FROM bdptb282_ERR_RVIA where id_coderr = ? and idiomaerr = ? and clave_pagina=?";
                pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
                System.out.println("loadDataFromDDBB:" + pConnection);
                pPreparedStatement = pConnection.prepareStatement(strQuery);
                System.out.println("loadDataFromDDBB:" + pPreparedStatement);
                System.out.println("loadDataFromDDBB:" + strCode);
                pPreparedStatement.setString(1, strCode);
                pPreparedStatement.setString(2, pSessionRviaData.getLanguage());
                pPreparedStatement.setString(3, clave_pagina);
                pResultSet = pPreparedStatement.executeQuery();
                while (pResultSet.next())
                {
                    TranslateJsonObject jsonO = new TranslateJsonObject();
                    String tipo = (String) pResultSet.getString("tiporesp");
                    String desc_coderr = (String) pResultSet.getString("desc_coderr");
                    String descripcion = (String) pResultSet.getString("descripcion");
                    jsonO.setStrCode(strCode);
                    jsonO.setStrDesc(desc_coderr);
                    jsonO.setStrTipo(tipo);
                    jsonO.setDescripcion(descripcion);
                    htTranslateCacheData.put(strCode, jsonO);
                    existe = true;
                }
            }
            catch (SQLException sex)
            {
                sex.printStackTrace();
                error = true;
            }
            catch (Exception ex)
            {
                pLog.error("Error al realizar la consulta a la BBDD", ex);
                error = true;
            }
            finally
            {
                DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
                if (error)
                    throw new ApplicationException(500, 999994, "Error al procesar la información", "Error al acceder a BBDD", null);
            }
        }
        return existe;
    }

    /**
     * Load data from DDBB.
     */
    private static int putDataInDDBB(String strCode, String strDesc, RequestConfigRvia pSessionRviaData,
            String clave_pagina) throws ApplicationException
    {
        int ok = -1;
        boolean error = false;
        if (!strCode.isEmpty())
        {
            Connection pConnection = null;
            PreparedStatement pPreparedStatement = null;
            try
            {
                String strQuery = "INSERT INTO BDPTB282_ERR_RVIA (ID_CODERR, TIPORESP, DESC_CODERR, IDIOMAERR, CLAVE_PAGINA, DESCRIPCION) ";
                strQuery += "VALUES(" + strCode + ",'" + "02" + "','" + strDesc + "','"
                        + pSessionRviaData.getLanguage() + "','" + clave_pagina + "','" + strDesc + "')";
                pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
                System.out.println("putDataInDDBB:" + pConnection);
                pPreparedStatement = pConnection.prepareStatement(strQuery);
                System.out.println("putDataInDDBB:" + pPreparedStatement);
                System.out.println("putDataInDDBB:" + strCode + ":" + pSessionRviaData.getLanguage());
                System.out.println("putDataInDDBB:" + strDesc + ":" + clave_pagina + ":" + strDesc);
                // pPreparedStatement.setString(1, strCode);
                // pPreparedStatement.setInt(1, (new Integer(strCode)).intValue());
                /*
                 * pPreparedStatement.setString(2, "02"); // por defecto es un error pPreparedStatement.setString(3,
                 * strDesc);
                 */
                /*
                 * pPreparedStatement.setString(4, pSessionRviaData.getLanguage()); pPreparedStatement.setString(5,
                 * clave_pagina); pPreparedStatement.setString(6, strDesc);
                 */
                ok = pPreparedStatement.executeUpdate(strQuery);
                if (ok > 0)
                    pConnection.commit();
            }
            catch (SQLException sex)
            {
                sex.printStackTrace();
                error = true;
            }
            catch (Exception ex)
            {
                pLog.error("Error al realizar la consulta a la BBDD", ex);
                error = true;
            }
            finally
            {
                DDBBPoolFactory.closeDDBBObjects(pLog, null, pPreparedStatement, pConnection);
                if (error)
                    throw new ApplicationException(500, 999993, "Error al procesar la información", "Error al acceder a BBDD", null);
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
    public static String isErrorCode(String strCode, String strDesc, RequestConfigRvia pSessionRviaData,
            String clave_pagina) throws ApplicationException
    {
        // ver tabla BDPTB090_ERRORES y BELTS105
        boolean isError = false;
        String tipoErr = "03";
        if (!htTranslateCacheData.containsKey(strCode))
        {
            if (!loadDataFromDDBB(strCode, pSessionRviaData, clave_pagina))
                putDataInDDBB(strCode, strDesc, pSessionRviaData, clave_pagina);
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
