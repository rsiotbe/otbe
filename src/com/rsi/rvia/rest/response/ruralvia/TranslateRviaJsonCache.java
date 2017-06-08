package com.rsi.rvia.rest.response.ruralvia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.Constants;
import com.rsi.Constants.Language;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;
import com.rsi.rvia.rest.error.exceptions.ApplicationException;
import com.rsi.rvia.rest.response.RviaRestResponse;
import com.rsi.rvia.rest.response.RviaRestResponse.Type;
import com.rsi.rvia.rest.tool.AppConfiguration;
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
    final static String                                       DEFAULT_LEVEL        = RviaRestResponse.Type.ERROR.name();

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
     * @param strLanguaje
     * @return
     * @throws ApplicationException
     */
    private static TranslateRviaJsonObject loadDataFromDDBB(String strErrorCode, int nIdMiq, Language pLanguage)
            throws ApplicationException
    {
        boolean fIsError = false;
        TranslateRviaJsonObject pTRJO = null;
        if (pLanguage == null)
            pLanguage = Constants.DEFAULT_LANGUAGE;
        if (!strErrorCode.isEmpty())
        {
            Connection pConnection = null;
            PreparedStatement pPreparedStatement = null;
            ResultSet pResultSet = null;
            try
            {
                pLog.trace("Codigo de error a comprobar:" + strErrorCode);
                String strQuery = "select s.tiporesp, " + "(select i.traduccion from "
                        + AppConfiguration.getInstance().getProperty("BELScheme").trim()
                        + ".BDPTB079_IDIOMA i where i.idioma = ? and codigo = s.TEXTERROR) as error, "
                        + "(select i.comentario from " + AppConfiguration.getInstance().getProperty("BELScheme").trim()
                        + ".BDPTB079_IDIOMA i where i.idioma = ? and codigo = s.TEXTERROR) as descripcion " + "from "
                        + AppConfiguration.getInstance().getProperty("BELScheme").trim()
                        + ".BDPTB282_ERR_RVIA s where s.TEXTERROR = ?";
                pLog.info("Query:" + strQuery);
                pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
                pPreparedStatement = pConnection.prepareStatement(strQuery);
                pPreparedStatement.setString(1, pLanguage.getJavaCode());
                pPreparedStatement.setString(2, pLanguage.getJavaCode());
                pPreparedStatement.setString(3, getErrorCode(nIdMiq, strErrorCode));
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
                {
                    throw new ApplicationException(500, 999994,
                            "Error al procesar la información de respuesta de error de RVIA", "Error al acceder a BBDD",
                            null);
                }
            }
        }
        return pTRJO;
    }

    private static synchronized int putDataInDDBB(String strErrorCode, String strErrorText, int nIdMiq)
            throws ApplicationException, SQLException
    {
        int OKS = 0; // Se corresponde con tres ejecuciones de query OK.
        final String DEFAULT_COMMENT = "Error generado automáticamente";
        final String DEFAULT_APP = "AUTO";
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
                OKS++;
                String code = getErrorCode(nIdMiq, strErrorCode);
                //
                // Insertar en BDPTB282_ERR_RVIA
                //
                pLog.trace("Se procede a insertar el codigo de error " + strErrorCode + " para el idMiq " + nIdMiq);
                String strQuery1 = "INSERT INTO " + AppConfiguration.getInstance().getProperty("BELScheme").trim()
                        + ".BDPTB282_ERR_RVIA (CODERR, TIPORESP, TEXTERROR, ID_MIQ, DESCRIPCION) VALUES (?, ?, ?, ?, ?)";
                pConnection1 = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
                pConnection1.setAutoCommit(false);
                pPreparedStatement1 = pConnection1.prepareStatement(strQuery1);
                pPreparedStatement1.setString(1, strErrorCode);
                pPreparedStatement1.setString(2, DEFAULT_LEVEL);
                pPreparedStatement1.setString(3, code);
                pPreparedStatement1.setLong(4, nIdMiq);
                pPreparedStatement1.setString(5, DEFAULT_COMMENT);
                nResult += pPreparedStatement1.executeUpdate();
                //
                // Insertar en BDPTB079_IDIOMA
                //
                String strQuery2 = "INSERT ALL";
                Language[] values = Language.values();
                for (int i = 0; i < values.length; i++)
                {
                    OKS++;
                    strQuery2 += " INTO " + AppConfiguration.getInstance().getProperty("BELScheme").trim()
                            + ".BDPTB079_IDIOMA (IDIOMA, CODIGO, TRADUCCION, COMENTARIO) VALUES (?, ?, ?, ?)";
                }
                strQuery2 += " SELECT * FROM DUAL";
                pLog.trace("Se realiza la inserción en la tabla BDPTB282_ERR_RVIA");
                pConnection2 = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
                pConnection2.setAutoCommit(false);
                pPreparedStatement2 = pConnection2.prepareStatement(strQuery2);
                int count = 1;
                for (Language lang : Language.values())
                {
                    pPreparedStatement2.setString(count++, lang.getJavaCode());
                    pPreparedStatement2.setString(count++, code);
                    pPreparedStatement2.setString(count++, strErrorText);
                    pPreparedStatement2.setString(count++, DEFAULT_COMMENT);
                }
                nResult += pPreparedStatement2.executeUpdate();
                //
                // Insertar en BDPTB079_IDIOMA_APLICATIVO
                // APLICATIVO='AUTO' (Autocensado)
                //
                OKS++;
                pLog.trace("Se realiza la insercción en la tabla BDPTB079_IDIOMA");
                String strQuery3 = "INSERT INTO " + AppConfiguration.getInstance().getProperty("BELScheme").trim()
                        + ".BDPTB079_IDIOMA_APLICATIVO (CODIGO, APLICATIVO, OPCIONES) VALUES (?, ?, ?)";
                pConnection3 = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
                pConnection3.setAutoCommit(false);
                pPreparedStatement3 = pConnection3.prepareStatement(strQuery3);
                pPreparedStatement3.setString(1, code);
                pPreparedStatement3.setString(2, DEFAULT_APP);
                pPreparedStatement3.setString(3, String.valueOf(nIdMiq));
                pLog.trace("pPreparedStatement3:" + pPreparedStatement3);
                nResult += pPreparedStatement3.executeUpdate();
                pLog.trace("Se realiza la insercción en la tabla BDPTB079_IDIOMA_APLICATIVO");
                if (nResult == OKS)
                {
                    pConnection1.commit();
                    pConnection2.commit();
                    pConnection3.commit();
                }
                pLog.trace("Se realiza el commit de las tres inserciones");
            }
            catch (Exception ex)
            {
                fIsError = true;
                if (pConnection1 != null)
                {
                    pConnection1.rollback();
                }
                if (pConnection2 != null)
                {
                    pConnection2.rollback();
                }
                if (pConnection3 != null)
                {
                    pConnection3.rollback();
                }
                pLog.error("Error al realizar la consulta a la BBDD", ex);
            }
            finally
            {
                if (pConnection1 != null)
                {
                    pConnection1.setAutoCommit(true);
                    DDBBPoolFactory.closeDDBBObjects(pLog, null, pPreparedStatement1, pConnection1);
                }
                if (pConnection2 != null)
                {
                    pConnection2.setAutoCommit(true);
                    DDBBPoolFactory.closeDDBBObjects(pLog, null, pPreparedStatement2, pConnection2);
                }
                if (pConnection3 != null)
                {
                    pConnection3.setAutoCommit(true);
                    DDBBPoolFactory.closeDDBBObjects(pLog, null, pPreparedStatement3, pConnection3);
                }
                if (fIsError)
                {
                    throw new ApplicationException(500, 999993, "Error al insertar de error de ruralvia",
                            "Error al acceder a BBDD", null);
                }
            }
        }
        return nResult;
    }

    private static String getErrorCode(int nIdMiq, String strErrorCode)
    {
        return DEFAULT_LEVEL + "_" + nIdMiq + "_" + strErrorCode; // Formato: ERROR_IDMIQ_CODERROR;
    }

    public static RviaRestResponse.Type getRviaResponseType(String strErrorCode, String strTextError, int nIdMiq,
            Language pLanguage) throws ApplicationException, SQLException
    {
        RviaRestResponse.Type pReturn;
        TranslateRviaJsonObject pTRJO;
        // ver tabla BDPTB090_ERRORES y BELTS105
        pTRJO = getRviaTranslation(strErrorCode, strTextError, nIdMiq, pLanguage);
        pReturn = pTRJO.getTipo();
        return pReturn;
    }

    private static TranslateRviaJsonObject getRviaTranslation(String strErrorCode, String strTextError, int nIdMiq,
            Language pLanguage) throws ApplicationException, SQLException
    {
        // Se busca el error en la caché de errores por clave IDMIQ_ERRORCODE
        TranslateRviaJsonObject pTRJO = getError(nIdMiq, strErrorCode);
        /* si no se encuentra se intenta cargar de bbdd */
        if (pTRJO == null)
        {
            pTRJO = loadDataFromDDBB(strErrorCode, nIdMiq, pLanguage);
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
        return pTRJO;
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
