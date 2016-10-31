package com.rsi.rvia.rest.tool;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.collections4.MapUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils
{
    private static Logger pLog = LoggerFactory.getLogger(Utils.class);

    /**
     * Dado el uriInfo, compone el path original (con el nombre del parametro, no el valor) Ejemplo de salida:
     * /cards/{id}
     * 
     * @param pUriInfo
     * @return
     */
    public static String getPrimaryPath(UriInfo pUriInfo)
    {
        String strPath = pUriInfo.getPath();
        MultivaluedMap<String, String> pListParameters = pUriInfo.getPathParameters();
        Iterator<String> pIterator = pListParameters.keySet().iterator();
        while (pIterator.hasNext())
        {
            String strKeyName = (String) pIterator.next();
            String strValue = pListParameters.get(strKeyName).get(0);
            strPath = strPath.replaceFirst(strValue, "{" + strKeyName + "}");
        }
        pLog.debug("StrPath: " + strPath);
        return ("/" + strPath);
    }

    /**
     * Convierte un ResultSet a un Json bien formado
     * 
     * @param pResultSet
     * @return
     * @throws SQLException
     * @throws JSONException
     */
    public static JSONArray convertResultSetToJSON(ResultSet pResultSet) throws SQLException, JSONException
    {
        JSONArray pJson = new JSONArray();
        ResultSetMetaData rsmd = pResultSet.getMetaData();
        while (pResultSet.next())
        {
            int numColumns = rsmd.getColumnCount();
            JSONObject pJsonObj = new JSONObject();
            for (int i = 1; i < numColumns + 1; i++)
            {
                String column_name = rsmd.getColumnName(i);
                if (column_name.indexOf("_NOPRINT") != -1)
                {
                    continue;
                }
                if (rsmd.getColumnType(i) == java.sql.Types.ARRAY)
                {
                    pJsonObj.put(column_name, pResultSet.getArray(column_name));
                }
                else if (rsmd.getColumnType(i) == java.sql.Types.BIGINT)
                {
                    pJsonObj.put(column_name, pResultSet.getInt(column_name));
                }
                else if (rsmd.getColumnType(i) == java.sql.Types.BOOLEAN)
                {
                    pJsonObj.put(column_name, pResultSet.getBoolean(column_name));
                }
                else if (rsmd.getColumnType(i) == java.sql.Types.BLOB)
                {
                    pJsonObj.put(column_name, pResultSet.getBlob(column_name));
                }
                else if (rsmd.getColumnType(i) == java.sql.Types.DOUBLE)
                {
                    pJsonObj.put(column_name, pResultSet.getDouble(column_name));
                }
                else if (rsmd.getColumnType(i) == java.sql.Types.FLOAT)
                {
                    pJsonObj.put(column_name, pResultSet.getFloat(column_name));
                }
                else if (rsmd.getColumnType(i) == java.sql.Types.INTEGER)
                {
                    pJsonObj.put(column_name, pResultSet.getInt(column_name));
                }
                else if (rsmd.getColumnType(i) == java.sql.Types.NVARCHAR)
                {
                    pJsonObj.put(column_name, pResultSet.getNString(column_name));
                }
                else if (rsmd.getColumnType(i) == java.sql.Types.VARCHAR)
                {
                    pJsonObj.put(column_name, pResultSet.getString(column_name));
                }
                else if (rsmd.getColumnType(i) == java.sql.Types.TINYINT)
                {
                    pJsonObj.put(column_name, pResultSet.getInt(column_name));
                }
                else if (rsmd.getColumnType(i) == java.sql.Types.SMALLINT)
                {
                    pJsonObj.put(column_name, pResultSet.getInt(column_name));
                }
                else if (rsmd.getColumnType(i) == java.sql.Types.DATE)
                {
                    pJsonObj.put(column_name, pResultSet.getDate(column_name));
                }
                else if (rsmd.getColumnType(i) == java.sql.Types.TIMESTAMP)
                {
                    pJsonObj.put(column_name, pResultSet.getTimestamp(column_name));
                }
                else
                {
                    pJsonObj.put(column_name, pResultSet.getObject(column_name));
                }
            }
            pJson.put(pJsonObj);
        }
        return pJson;
    }

    /**
     * Convierte un ResultSet a un Json bien formado
     * 
     * @param pResultSet
     * @return
     * @throws SQLException
     * @throws JSONException
     */
    public static JSONObject convertResultSetToJSONWithTotalRegCount(ResultSet pResultSet)
            throws SQLException, JSONException
    {
        JSONObject pJsonRetorno = new JSONObject();
        JSONArray pJson = new JSONArray();
        ResultSetMetaData rsmd = pResultSet.getMetaData();
        int nTotalRecordCount = 0;
        while (pResultSet.next())
        {
            int numColumns = rsmd.getColumnCount();
            JSONObject pJsonObj = new JSONObject();
            for (int i = 1; i < numColumns + 1; i++)
            {
                String column_name = rsmd.getColumnName(i);
                if (column_name.indexOf("_NOPRINT") != -1)
                {
                    if ("C_REG_NOPRINT".equals(column_name))
                    {
                        nTotalRecordCount = pResultSet.getInt(column_name);
                    }
                    continue;
                }
                if (rsmd.getColumnType(i) == java.sql.Types.ARRAY)
                {
                    pJsonObj.put(column_name, pResultSet.getArray(column_name));
                }
                else if (rsmd.getColumnType(i) == java.sql.Types.BIGINT)
                {
                    pJsonObj.put(column_name, pResultSet.getInt(column_name));
                }
                else if (rsmd.getColumnType(i) == java.sql.Types.BOOLEAN)
                {
                    pJsonObj.put(column_name, pResultSet.getBoolean(column_name));
                }
                else if (rsmd.getColumnType(i) == java.sql.Types.BLOB)
                {
                    pJsonObj.put(column_name, pResultSet.getBlob(column_name));
                }
                else if (rsmd.getColumnType(i) == java.sql.Types.DOUBLE)
                {
                    pJsonObj.put(column_name, pResultSet.getDouble(column_name));
                }
                else if (rsmd.getColumnType(i) == java.sql.Types.FLOAT)
                {
                    pJsonObj.put(column_name, pResultSet.getFloat(column_name));
                }
                else if (rsmd.getColumnType(i) == java.sql.Types.INTEGER)
                {
                    pJsonObj.put(column_name, pResultSet.getInt(column_name));
                }
                else if (rsmd.getColumnType(i) == java.sql.Types.NVARCHAR)
                {
                    pJsonObj.put(column_name, pResultSet.getNString(column_name));
                }
                else if (rsmd.getColumnType(i) == java.sql.Types.VARCHAR)
                {
                    pJsonObj.put(column_name, pResultSet.getString(column_name));
                }
                else if (rsmd.getColumnType(i) == java.sql.Types.TINYINT)
                {
                    pJsonObj.put(column_name, pResultSet.getInt(column_name));
                }
                else if (rsmd.getColumnType(i) == java.sql.Types.SMALLINT)
                {
                    pJsonObj.put(column_name, pResultSet.getInt(column_name));
                }
                else if (rsmd.getColumnType(i) == java.sql.Types.DATE)
                {
                    pJsonObj.put(column_name, pResultSet.getDate(column_name));
                }
                else if (rsmd.getColumnType(i) == java.sql.Types.TIMESTAMP)
                {
                    pJsonObj.put(column_name, pResultSet.getTimestamp(column_name));
                }
                else
                {
                    pJsonObj.put(column_name, pResultSet.getObject(column_name));
                }
            }
            pJson.put(pJsonObj);
        }
        pJsonRetorno.put("records", pJson);
        pJsonRetorno.put("totalrecordcount", nTotalRecordCount);
        return pJsonRetorno;
    }

    /**
     * Devuelve un Map con los parametros del path (key -> value)
     * 
     * @param pUriInfo
     *            UriInfo con la información de path
     * @return MultivaluedMap con el key y el valor de cada elemento del path dinámico
     */
    public static MultivaluedMap<String, String> getParamByPath(UriInfo pUriInfo)
    {
        MultivaluedMap<String, String> pListParameters = pUriInfo.getPathParameters();
        return pListParameters;
    }

    /**
     * Convierte un MultivaluedMap a un queryString.
     * 
     * @param pMap
     *            MultivaluedMap a convertir
     * @return String en formato queryString del tipo "&key=value"
     */
    public static String multiValuedMapToQueryString(MultivaluedMap<String, String> pMap)
    {
        String strReturn = "";
        Iterator<String> pIterator = pMap.keySet().iterator();
        while (pIterator.hasNext())
        {
            String strKey = (String) pIterator.next();
            if (pMap.get(strKey) != null)
            {
                if (!strReturn.isEmpty())
                    strReturn += "&";
                strReturn += strKey + "=" + pMap.getFirst(strKey);
            }
        }
        return strReturn;
    }

    public static String hashMapToQueryString(HashMap<String, String> pMap)
    {
        String strReturn = "";
        if (pMap != null)
        {
            Iterator<String> pIterator = pMap.keySet().iterator();
            while (pIterator.hasNext())
            {
                String strKey = (String) pIterator.next();
                if (pMap.get(strKey) != null)
                {
                    if (!strReturn.isEmpty())
                        strReturn += "&";
                    strReturn += strKey + "=" + pMap.get(strKey);
                }
            }
        }
        return strReturn;
    }

    public static String simpleJsonToQueryString(String strJSON) throws Exception
    {
        String strReturn = "";
        if (strJSON != null && !strJSON.trim().isEmpty())
        {
            JSONObject pJSONObject = new JSONObject(strJSON);
            Iterator<?> pKeys = pJSONObject.keys();
            while (pKeys.hasNext())
            {
                String strKey = (String) pKeys.next();
                if (!strReturn.isEmpty())
                    strReturn += "&";
                strReturn += strKey + "=" + pJSONObject.get(strKey).toString();
            }
        }
        return strReturn;
    }

    /**
     * Obtiene un String a traves de un InputStream de un recuerso compilado. Ejemplo de uso: lectura de los templates
     * xhtml.
     * 
     * @param is
     *            InputStream del recurso
     * @return String con del recurso
     * @throws Exception
     */
    public static String getStringFromInputStream(InputStream is) throws Exception
    {
        BufferedReader pBufferedReader = null;
        StringBuilder pStringBuilder = new StringBuilder();
        String strLine;
        try
        {
            pBufferedReader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            while ((strLine = pBufferedReader.readLine()) != null)
            {
                pStringBuilder.append(strLine).append("\r\n");
            }
        }
        catch (Exception ex)
        {
            pLog.error("No es posible leer el contenido del InputStreamReader", ex);
            throw ex;
        }
        finally
        {
            if (pBufferedReader != null)
            {
                try
                {
                    pBufferedReader.close();
                }
                catch (Exception ex)
                {
                    pLog.error("No es posible cerrar el StringBuilder", ex);
                }
            }
        }
        return pStringBuilder.toString();
    }

    /**
     * Convierte un Hashtable a un JSON (Hace uso de la librería Jackson)
     * 
     * @param htData
     *            Hashtable a convertir.
     * @return String json que contiene los valores del hashtable.
     * @throws JSONException
     */
    public static String hashTableToJson(Hashtable<String, String> htData) throws JSONException
    {
        String strReturn = "{}";
        JSONObject pJson = new JSONObject();
        Iterator<Entry<String, String>> pIterator = htData.entrySet().iterator();
        while (pIterator.hasNext())
        {
            Map.Entry<String, String> pPair = (Entry<String, String>) pIterator.next();
            pJson.put(pPair.getKey(), pPair.getValue());
        }
        strReturn = pJson.toString();
        return strReturn;
    }

    /**
     * Obtiene la cadena que contiene el stacktrace de una excepción
     * 
     * @param ex
     *            Excepcion a escribir
     * @return Cadena que contiene la pila
     */
    public static String getExceptionStackTrace(Exception ex)
    {
        StringWriter errors = new StringWriter();
        ex.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }

    public static String hastablePrettyPrint(Hashtable<?, ?> pHashtable, String strTitle)
    {
        String strReturn = "";
        ByteArrayOutputStream pBaos;
        PrintStream pPs;
        try
        {
            pBaos = new ByteArrayOutputStream();
            pPs = new PrintStream(pBaos);
            MapUtils.debugPrint(pPs, strTitle, pHashtable);
            strReturn = new String(pBaos.toByteArray(), StandardCharsets.UTF_8);
            pPs.close();
            pBaos.close();
        }
        catch (IOException e)
        {
            pLog.error("Error al convertir un hashtable a String", e);
            strReturn = "<<Error al generar la información, ver log>>";
        }
        return strReturn;
    }

    /**
     * Devuelve un string con los datos de un hastable en formato cadena
     * 
     * @param pHashtable
     *            Hashtable con los datos
     * @return
     */
    public static String hastablePrettyPrintHtml(Hashtable<?, ?> pHashtable)
    {
        String strHtml = "";
        for (Enumeration<?> e = (Enumeration<?>) pHashtable.keys(); e.hasMoreElements();)
        {
            Object objKey = e.nextElement();
            String strKey = objKey.toString();
            strHtml += "<div class=\"hastableElement\">";
            strHtml += "<div class=\"hastableKey\"><span class=\"hastableKeySpan\">" + strKey + "</span></div>";
            strHtml += "<div class=\"hastableValue\"><span class=\"hastableValueSpan\">" + pHashtable.get(strKey)
                    + "</span></div>";
            strHtml += "</div>";
        }
        return strHtml;
    }
}
