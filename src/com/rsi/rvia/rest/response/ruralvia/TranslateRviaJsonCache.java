package com.rsi.rvia.rest.response.ruralvia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.Constants;
import com.rsi.Constants.Languages;
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
    private static Logger                                     pLog                 = LoggerFactory.getLogger(
            TranslateRviaJsonCache.class);
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
     * 
     * @param strErrorCode
     * @param nIdMiq
     * @param languaje
     * @return
     * @throws ApplicationException
     */
    private static TranslateRviaJsonObject loadDataFromDDBB(String strErrorCode, int nIdMiq, String languaje)
            throws ApplicationException
    {
        boolean fIsError = false;
        TranslateRviaJsonObject pTRJO = null;
        String lang = languaje == null ? Constants.DEFAULT_LANGUAGE : languaje;
        if (!strErrorCode.isEmpty())
        {
            Connection pConnection = null;
            PreparedStatement pPreparedStatement = null;
            ResultSet pResultSet = null;
            try
            {
                String strQuery = "select s.tiporesp, "
                        + "(select i.traduccion from BEL.BDPTB079_IDIOMA i where i.idioma = ? and codigo = s.TEXTERROR) as error, "
                        + "(select i.comentario from BEL.BDPTB079_IDIOMA i where i.idioma = ? and codigo = s.TEXTERROR) as descripcion "
                        + "from BEL.BDPTB282_ERR_RVIA s where s.CODERR = ? AND s.ID_MIQ = ?";
                pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
                pLog.trace("pConnection:" + pConnection);
                pPreparedStatement = pConnection.prepareStatement(strQuery);
                pLog.trace("pPreparedStatement:" + pPreparedStatement);
                pLog.trace("strErrorCode:" + strErrorCode);
                pPreparedStatement.setString(1, lang);
                pPreparedStatement.setString(2, lang);
                pPreparedStatement.setString(3, strErrorCode);
                pPreparedStatement.setInt(4, nIdMiq);
                pResultSet = pPreparedStatement.executeQuery();
                while (pResultSet.next())
                {
                    pTRJO = new TranslateRviaJsonObject();
                    String strType = pResultSet.getString("tiporesp");
                    String strTextError = pResultSet.getString("error");
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
                    throw new ApplicationException(500, 999994,
                            "Error al procesar la información de respuesta de error de RVIA", "Error al acceder a BBDD",
                            null);
            }
        }
        return pTRJO;
    }

    private static int putDataInDDBB(String strErrorCode, String strErrorText, int nIdMiq) throws ApplicationException
    {
        final int THREE_OK = 6; // Se corresponde con tres ejecuciones de query OK.
        final String DEFAULT_COMMENT = "Error generado automáticamente";
        final String DEFAULT_LEVEL = RviaRestResponse.Type.ERROR.name();
        int nResult = 0;
        boolean fIsError = false;
        Connection pConnection1 = null;
        PreparedStatement pPreparedStatement1 = null;
        Connection pConnection2 = null;
        PreparedStatement pPreparedStatement2 = null;
        Connection pConnection3 = null;
        PreparedStatement pPreparedStatement3 = null;
        if (!strErrorCode.isEmpty())
        {
            try
            {
                String code = DEFAULT_LEVEL + "_" + nIdMiq + "_" + strErrorCode; // Formato: ERROR_IDMIQ_CODERROR;
                //
                // Insertar en BDPTB282_ERR_RVIA
                //
                String strQuery1 = "INSERT INTO bel.BDPTB282_ERR_RVIA (CODERR, TIPORESP, TEXTERROR, ID_MIQ, DESCRIPCION) ";
                strQuery1 += "VALUES('" + code + "','" + DEFAULT_LEVEL + "','" + strErrorText + "','" + nIdMiq + "','"
                        + DEFAULT_COMMENT + "')";
                pConnection1 = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
                pLog.trace("pConnection1:" + pConnection1);
                pPreparedStatement1 = pConnection1.prepareStatement(strQuery1);
                pLog.trace("pPreparedStatement1:" + pPreparedStatement1);
                pLog.trace("strErrorCode:" + strErrorCode);
                pLog.trace("nIdMiq:" + nIdMiq);
                nResult += pPreparedStatement1.executeUpdate(strQuery1);
                //
                // Insertar en BDPTB079_IDIOMA
                //
                String strQuery2 = "";
                for (Languages lang : Languages.values())
                {
                    strQuery2 += "INSERT INTO bel.BDPTB079_IDIOMA (IDIOMA, CODIGO, TRADUCCION, COMENTARIO) ";
                    strQuery2 += "VALUES('" + lang + "','" + code + "','" + strErrorText + "','" + "','"
                            + DEFAULT_COMMENT + "');";
                }
                pConnection2 = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
                pLog.trace("pConnection2:" + pConnection2);
                pPreparedStatement2 = pConnection2.prepareStatement(strQuery2);
                pLog.trace("pPreparedStatement2:" + pPreparedStatement2);
                nResult += pPreparedStatement2.executeUpdate(strQuery2);
                //
                // Insertar en BDPTB079_IDIOMA_APLICATIVO
                // APLICATIVO='AUTO' (Autocensado)
                //
                String strQuery3 = "INSERT INTO bel.BDPTB079_IDIOMA_APLICATIVO (CODIGO, APLICATIVO, OPCIONES) ";
                strQuery3 += "VALUES('" + code + "','" + "AUTO" + "','" + nIdMiq + "')";
                pConnection3 = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
                pLog.trace("pConnection3:" + pConnection3);
                pPreparedStatement3 = pConnection3.prepareStatement(strQuery3);
                pLog.trace("pPreparedStatement3:" + pPreparedStatement3);
                nResult += pPreparedStatement3.executeUpdate(strQuery3);
                if (nResult == THREE_OK)
                {
                    pConnection1.commit();
                    pConnection2.commit();
                    pConnection3.commit();
                }
            }
            catch (Exception ex)
            {
                pLog.error("Error al realizar la consulta a la BBDD", ex);
                fIsError = true;
            }
            finally
            {
                DDBBPoolFactory.closeDDBBObjects(pLog, null, pPreparedStatement1, pConnection1);
                DDBBPoolFactory.closeDDBBObjects(pLog, null, pPreparedStatement2, pConnection2);
                DDBBPoolFactory.closeDDBBObjects(pLog, null, pPreparedStatement3, pConnection3);
                if (fIsError)
                    throw new ApplicationException(500, 999993,
                            "Error al procesar insertar respues ta de error de ruralvia", "Error al acceder a BBDD",
                            null);
            }
        }
        return nResult;
    }

    public static RviaRestResponse.Type isErrorCode(String strErrorCode, String strTextError, int nIdMiq,
            String languaje) throws ApplicationException
    {
        RviaRestResponse.Type pReturn;
        TranslateRviaJsonObject pTRJO;
        // ver tabla BDPTB090_ERRORES y BELTS105
        pReturn = RviaRestResponse.Type.OK;
        // Se busca el error en la caché de errores por clave IDMIQ_ERRORCODE
        pTRJO = getError(nIdMiq, strErrorCode);
        /* si no se encuentra se intenta cargar de bbdd */
        if (pTRJO == null)
        {
            pTRJO = loadDataFromDDBB(strErrorCode, nIdMiq, languaje);
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
            putError(nIdMiq, strErrorCode, pTRJO);
        }
        pReturn = pTRJO.getTipo();
        return pReturn;
    }

    public static TranslateRviaJsonObject getError(int nIdMiq, String strErrorCode)
    {
        String key = createKey(nIdMiq, strErrorCode);
        return htTranslateCacheData.get(key);
    }

    public static TranslateRviaJsonObject putError(int nIdMiq, String strErrorCode, TranslateRviaJsonObject pTRJO)
    {
        String key = createKey(nIdMiq, strErrorCode);
        return htTranslateCacheData.put(key, pTRJO);
    }

    private static String createKey(int nIdMiq, String strErrorCode)
    {
        return nIdMiq + "_" + strErrorCode;
    }
}
