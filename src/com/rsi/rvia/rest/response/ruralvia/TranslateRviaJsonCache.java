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
    /*
     * private static boolean loadDataFromDDBB(String strCode, RequestConfigRvia pSessionRviaData, String clave_pagina)
     * throws ApplicationException { boolean fExist = false; boolean fIsError = false; if (!strCode.isEmpty()) {
     * Connection pConnection = null; PreparedStatement pPreparedStatement = null; ResultSet pResultSet = null; try {
     * String strQuery =
     * "SELECT tiporesp,desc_coderr, descripcion FROM bdptb282_ERR_RVIA where id_coderr = ? and idiomaerr = ? and clave_pagina=?"
     * ; pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca); pLog.trace("pConnection:" + pConnection);
     * pPreparedStatement = pConnection.prepareStatement(strQuery); pLog.trace("pPreparedStatement:" +
     * pPreparedStatement); pLog.trace("strCode:" + strCode); pPreparedStatement.setString(1, strCode);
     * pPreparedStatement.setString(2, pSessionRviaData.getLanguage()); pPreparedStatement.setString(3, clave_pagina);
     * pResultSet = pPreparedStatement.executeQuery(); while (pResultSet.next()) { TranslateRviaJsonObject pTRJO = new
     * TranslateRviaJsonObject(); String tipo = (String) pResultSet.getString("tiporesp"); String desc_coderr = (String)
     * pResultSet.getString("desc_coderr"); String descripcion = (String) pResultSet.getString("descripcion");
     * pTRJO.setCode(strCode); pTRJO.setDesc(desc_coderr); pTRJO.setType(RviaRestResponse.Type.valueOf(tipo));
     * pTRJO.setDescription(descripcion); htTranslateCacheData.put(strCode, pTRJO); fExist = true; } } catch (Exception
     * ex) { pLog.error("Error al realizar la consulta a la BBDD", ex); fIsError = true; } finally {
     * DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection); if (fIsError) throw new
     * ApplicationException(500, 999994, "Error al procesar la información", "Error al acceder a BBDD", null); } }
     * return fExist; }
     */
    /**
     * Load data from DDBB.
     */
    private static TranslateRviaJsonObject loadDataFromDDBB(String strErrorCode, String strClavePagina)
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
                String strQuery = "SELECT * FROM bdptb282_ERR_RVIA where coderr = ? and clave_pagina=?";
                pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
                pLog.trace("pConnection:" + pConnection);
                pPreparedStatement = pConnection.prepareStatement(strQuery);
                pLog.trace("pPreparedStatement:" + pPreparedStatement);
                pLog.trace("strErrorCode:" + strErrorCode);
                pPreparedStatement.setString(1, strErrorCode);
                pPreparedStatement.setString(2, strClavePagina);
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
                    pTRJO.setClavePagina(strClavePagina);
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

    /**
     * Load data from DDBB.
     */
    /*
     * private static int putDataInDDBB(String strCode, String strDesc, RequestConfigRvia pSessionRviaData, String
     * clave_pagina) throws ApplicationException { int nResult = -1; boolean fIsError = false; if (!strCode.isEmpty()) {
     * Connection pConnection = null; PreparedStatement pPreparedStatement = null; try { String strQuery =
     * "INSERT INTO BDPTB282_ERR_RVIA (ID_CODERR, TIPORESP, DESC_CODERR, IDIOMAERR, CLAVE_PAGINA, DESCRIPCION) ";
     * strQuery += "VALUES(" + strCode + ",'" + "02" + "','" + strDesc + "','" + pSessionRviaData.getLanguage() + "','"
     * + clave_pagina + "','" + strDesc + "')"; pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
     * pLog.trace("pConnection:" + pConnection); pPreparedStatement = pConnection.prepareStatement(strQuery);
     * pLog.trace("pPreparedStatement:" + pPreparedStatement); pLog.trace(strCode + ":" +
     * pSessionRviaData.getLanguage()); pLog.trace(strDesc + ":" + clave_pagina + ":" + strDesc); //
     * pPreparedStatement.setString(1, strCode); // pPreparedStatement.setInt(1, (new Integer(strCode)).intValue()); /*
     * pPreparedStatement.setString(2, "02"); // por defecto es un error pPreparedStatement.setString(3, strDesc);
     */
    /*
     * pPreparedStatement.setString(4, pSessionRviaData.getLanguage()); pPreparedStatement.setString(5, clave_pagina);
     * pPreparedStatement.setString(6, strDesc);
     */
    /*
     * nResult = pPreparedStatement.executeUpdate(strQuery); if (nResult > 0) pConnection.commit(); } catch (Exception
     * ex) { pLog.error("Error al realizar la consulta a la BBDD", ex); fIsError = true; } finally {
     * DDBBPoolFactory.closeDDBBObjects(pLog, null, pPreparedStatement, pConnection); if (fIsError) throw new
     * ApplicationException(500, 999993, "Error al procesar la información", "Error al acceder a BBDD", null); } }
     * return nResult; }
     */
    private static int putDataInDDBB(String strErrorCode, String strErrorText, String strClavePagina)
            throws ApplicationException
    {
        int nResult = -1;
        boolean fIsError = false;
        if (!strErrorCode.isEmpty())
        {
            Connection pConnection = null;
            PreparedStatement pPreparedStatement = null;
            try
            {
                String strQuery = "INSERT INTO BDPTB282_ERR_RVIA (CODERR, TIPORESP, TEXTERROR, CLAVE_PAGINA, DESCRIPCION) ";
                strQuery += "VALUES('" + strErrorCode + "','" + "ERROR" + "','" + strErrorText + "','" + strClavePagina
                        + "','Error generado automáticamente')";
                pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
                pLog.trace("pConnection:" + pConnection);
                pPreparedStatement = pConnection.prepareStatement(strQuery);
                pLog.trace("pPreparedStatement:" + pPreparedStatement);
                pLog.trace("strErrorCode:" + strErrorCode);
                pLog.trace("strClavePagina:" + strClavePagina);
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

    /**
     * Checks if is error code.
     * 
     * @param strCode
     *            the str code
     * @param strDesc
     *            the str desc
     * @return true, if is error code
     */
    /*
     * public static String isErrorCode(String strCode, String strDesc, RequestConfigRvia pSessionRviaData, String
     * clave_pagina) throws ApplicationException { // ver tabla BDPTB090_ERRORES y BELTS105 boolean isError = false;
     * String tipoErr = "03"; if (!htTranslateCacheData.containsKey(strCode)) { if (!loadDataFromDDBB(strCode,
     * pSessionRviaData, clave_pagina)) putDataInDDBB(strCode, strDesc, pSessionRviaData, clave_pagina); } if
     * (htTranslateCacheData.containsKey(strCode)) { TranslateRviaJsonObject pTRJO = (TranslateRviaJsonObject)
     * htTranslateCacheData.get(strCode); if (pTRJO.getDesc().equals(strDesc)) tipoErr = pTRJO.getTipo().name(); }
     * return tipoErr; }
     */
    public static RviaRestResponse.Type isErrorCode(String strErrorCode, String strTextError, String strClavePagina)
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
            pTRJO = loadDataFromDDBB(strErrorCode, strClavePagina);
            /* si no se ha podido cargar de bbdd por que no se encuentra, se inserta la info y se genera el objeto */
            if (pTRJO == null)
            {
                putDataInDDBB(strErrorCode, strTextError, strClavePagina);
                pTRJO = new TranslateRviaJsonObject();
                pTRJO.setCode(strErrorCode);
                pTRJO.setTexterror(strTextError);
                pTRJO.setClavePagina(strClavePagina);
                pTRJO.setType(RviaRestResponse.Type.ERROR);
            }
            htTranslateCacheData.put(strErrorCode, pTRJO);
        }
        pReturn = pTRJO.getTipo();
        return pReturn;
    }
}
