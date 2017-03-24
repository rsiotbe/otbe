package com.rsi.rvia.rest.response.ruralvia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;
import com.rsi.rvia.rest.error.exceptions.ApplicationException;
import com.rsi.rvia.rest.response.RviaRestResponse;
import com.rsi.rvia.rest.response.RviaRestResponse.Type;
import com.rsi.rvia.rest.tool.Utils;

/**
 * The Class TranslateJsonCache.
 */
public class TranslateRviaJsonCache
{
    /** The p log. */
    private static Logger                                     pLog                 = LoggerFactory.getLogger(TranslateRviaJsonCache.class);
    /** The ht translate cache data. */
    private static Hashtable<String, TranslateRviaJsonObject> htTranslateCacheData = new Hashtable<String, TranslateRviaJsonObject>();

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
            htTranslateCacheData = new Hashtable<String, TranslateRviaJsonObject>();
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
    public static Hashtable<String, TranslateRviaJsonObject> getCache() throws Exception
    {
        return htTranslateCacheData;
    }

    /**
     * Load data from DDBB.
     */
    private static TranslateRviaJsonObject loadDataFromDDBB(String strErrorCode, int nIdMiq)
            throws ApplicationException
    {
        boolean fIsError = false;
        TranslateRviaJsonObject pTRJO = null;
        if (!strErrorCode.isEmpty())
        {
            Connection pConnection = null;
            PreparedStatement pPreparedStatement = null;
            ResultSet pResultSet = null;
            try
            {
                String strQuery = "SELECT * FROM bel.bdptb282_ERR_RVIA where coderr = ? and Id_Miq=?";
                pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
                pLog.trace("pConnection:" + pConnection);
                pPreparedStatement = pConnection.prepareStatement(strQuery);
                pLog.trace("pPreparedStatement:" + pPreparedStatement);
                pLog.trace("strErrorCode:" + strErrorCode);
                pPreparedStatement.setString(1, strErrorCode);
                pPreparedStatement.setInt(2, nIdMiq);
                pResultSet = pPreparedStatement.executeQuery();
                while (pResultSet.next())
                {
                    pTRJO = new TranslateRviaJsonObject();
                    String strType = pResultSet.getString("tiporesp");
                    String strTextError = pResultSet.getString("TEXTERROR");
                    String strDescription = pResultSet.getString("descripcion");
                    pTRJO.setCode(strErrorCode);
                    pTRJO.setTexterror(strTextError);
                    pTRJO.setType(Type.valueOf(strType));
                    pTRJO.setIdMiq(nIdMiq);
                    pTRJO.setDescription(strDescription);
                }
            }
            catch (Exception ex)
            {
                pLog.error("Error al realizar la consulta a la BBDD", ex);
                fIsError = true;
            }
            finally
            {
                DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
                if (fIsError)
                    throw new ApplicationException(500, 999994, "Error al procesar la información de respuesta de error de RVIA", "Error al acceder a BBDD", null);
            }
        }
        return pTRJO;
    }

    private static int putDataInDDBB(String strErrorCode, String strErrorText, int nIdMiq) throws ApplicationException
    {
        int nResult = -1;
        boolean fIsError = false;
        if (!strErrorCode.isEmpty())
        {
            Connection pConnection = null;
            PreparedStatement pPreparedStatement = null;
            try
            {
                String strQuery = "INSERT INTO bel.BDPTB282_ERR_RVIA (CODERR, TIPORESP, TEXTERROR, ID_MIQ, DESCRIPCION) ";
                strQuery += "VALUES('" + strErrorCode + "','" + "ERROR" + "','" + strErrorText + "','" + nIdMiq
                        + "','Error generado automáticamente')";
                pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
                pLog.trace("pConnection:" + pConnection);
                pPreparedStatement = pConnection.prepareStatement(strQuery);
                pLog.trace("pPreparedStatement:" + pPreparedStatement);
                pLog.trace("strErrorCode:" + strErrorCode);
                pLog.trace("nIdMiq:" + nIdMiq);
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
                nResult = pPreparedStatement.executeUpdate(strQuery);
                if (nResult > 0)
                    pConnection.commit();
            }
            catch (Exception ex)
            {
                pLog.error("Error al realizar la consulta a la BBDD", ex);
                fIsError = true;
            }
            finally
            {
                DDBBPoolFactory.closeDDBBObjects(pLog, null, pPreparedStatement, pConnection);
                if (fIsError)
                    throw new ApplicationException(500, 999993, "Error al procesar insertar respues ta de error de ruralvia", "Error al acceder a BBDD", null);
            }
        }
        return nResult;
    }

    public static RviaRestResponse.Type isErrorCode(String strErrorCode, String strTextError, int nIdMiq)
            throws ApplicationException
    {
        RviaRestResponse.Type pReturn;
        TranslateRviaJsonObject pTRJO;
        // ver tabla BDPTB090_ERRORES y BELTS105
        pReturn = RviaRestResponse.Type.OK;
        /* se busca el error en lac ache de errores */
        pTRJO = (TranslateRviaJsonObject) htTranslateCacheData.get(strErrorCode);
        /* si no se encuentra se intenta cargar de bbdd */
        if (pTRJO == null)
        {
            pTRJO = loadDataFromDDBB(strErrorCode, nIdMiq);
            /* si no se ha podido cargar de bbdd por que no se encuentra, se inserta la info y se genera el objeto */
            if (pTRJO == null)
            {
                putDataInDDBB(strErrorCode, strTextError, nIdMiq);
                pTRJO = new TranslateRviaJsonObject();
                pTRJO.setCode(strErrorCode);
                pTRJO.setTexterror(strTextError);
                pTRJO.setIdMiq(nIdMiq);
                pTRJO.setType(RviaRestResponse.Type.ERROR);
            }
            htTranslateCacheData.put(strErrorCode, pTRJO);
        }
        pReturn = pTRJO.getTipo();
        return pReturn;
    }
}
