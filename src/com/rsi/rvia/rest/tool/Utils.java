package com.rsi.rvia.rest.tool;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.collections4.MapUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.Constants;
import com.rsi.rvia.rest.error.exceptions.LogicalErrorException;
import com.rsi.rvia.rest.operation.MiqQuests;

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
            strPath = strPath.replaceFirst("/" + strValue + "/", "/{" + strKeyName + "}/");
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
    public static JSONObject convertResultSetToJSONWithTotalRegCount(ResultSet pResultSet) throws SQLException,
            JSONException
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
     * Devuelve un Map con los parametros del QueryString (key -> value)
     * 
     * @param pUriInfo
     *            UriInfo con la información de path
     * @return MultivaluedMap con el key y el valor de cada elemento del path dinámico
     */
    public static MultivaluedMap<String, String> queryStringToMultivaluedMap(UriInfo pUriInfo)
    {
        MultivaluedMap<String, String> pListParameters = pUriInfo.getQueryParameters();
        return pListParameters;
    }

    /**
     * Convierte un MultivaluedMap a un queryString.
     * 
     * @param pMap
     *            MultivaluedMap a convertir
     * @return String en formato queryString del tipo "&key=value"
     * @throws UnsupportedEncodingException
     */
    public static String multiValuedMapToQueryString(MultivaluedMap<String, String> pMap)
            throws UnsupportedEncodingException
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
                strReturn += strKey + "=" + URLEncoder.encode(pMap.getFirst(strKey), "ISO-8859-1");
            }
        }
        return strReturn;
    }

    public static String hashMapToQueryString(HashMap<String, String> pMap) throws UnsupportedEncodingException
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
                    strReturn += strKey + "=" + URLEncoder.encode(pMap.get(strKey), "ISO-8859-1");
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
                strReturn += strKey + "=" + URLEncoder.encode(pJSONObject.get(strKey).toString(), "ISO-8859-1");
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
        if (pHashtable != null)
        {
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
        }
        return strHtml;
    }

    /**
     * En el entorno de test, grabará ficheros json con la estructura del path que se pide
     * 
     * @param pUriInfo
     *            Necesario para sacar los parametros del path
     * @param pRequest
     * @param pMiqQuests
     * @param strJsonData
     * @throws Exception
     */
    public static void writeMock(HttpServletRequest pRequest, UriInfo pUriInfo, MiqQuests pMiqQuests, String strJsonData)
            throws Exception
    {
        int i;
        String strTargetMockRootDir = AppConfiguration.getInstance().getProperty(Constants.TARGET_MOCK_DIRECTORY);
        String strPartes[] = pUriInfo.getPath().split("/");
        String strTestPath = strTargetMockRootDir;
        for (i = 0; i < strPartes.length - 1; i++)
        {
            strTestPath = strTestPath + "/" + strPartes[i];
        }
        File directorio = new File(strTestPath);
        if (!directorio.exists())
        {
            try
            {
                directorio.mkdirs();
            }
            catch (Exception e)
            {
                throw new LogicalErrorException(500, 9999, "Error al intentar crear directorio", "Fallo al crear directorio para mocks: "
                        + strTestPath, null);
            }
        }
        FileWriter fichero = null;
        File fileTest = null;
        try
        {
            fileTest = new File(strTestPath + "/__" + strPartes[i]);
            if (fileTest.exists())
            {
                fileTest.delete();
            }
            fichero = new FileWriter(strTestPath + "/__" + strPartes[i]);
            fichero.write(strJsonData);
        }
        catch (Exception e)
        {
            throw new LogicalErrorException(500, 9999, "Error manejo de ficheros", "Fallo al crear fichero para mocks: "
                    + strTestPath + "/__" + strPartes[i], null);
        }
        finally
        {
            try
            {
                if (null != fichero)
                    fichero.close();
            }
            catch (Exception e2)
            {
                e2.printStackTrace();
            }
        }
    }

    /**
     * Obtiene el flujo de bytes de un fichero existente en una URL
     * 
     * @param strFileUrl
     *            Url al fichero
     * @return Objeto que contiene los bytes del fichero leido
     * @throws MalformedURLException
     * @throws IOException
     */
    public static ByteArrayOutputStream getFileFromUrl(String strFileUrl)
    {
        InputStream pInputStream = null;
        ByteArrayOutputStream pByteArrayOutputStream = null;
        byte[] abBuffer = null;
        try
        {
            pInputStream = new URL(strFileUrl).openStream();
            pByteArrayOutputStream = new ByteArrayOutputStream();
            abBuffer = new byte[4096];
            for (int nCnt; (nCnt = pInputStream.read(abBuffer)) != -1;)
            {
                pByteArrayOutputStream.write(abBuffer, 0, nCnt);
            }
        }
        catch (Exception ex)
        {
            pLog.error("Error al obtener el fichero desde la URL (" + strFileUrl + "). Error:" + ex);
            pByteArrayOutputStream = null;
        }
        finally
        {
            if (pByteArrayOutputStream != null)
            {
                try
                {
                    pByteArrayOutputStream.close();
                }
                catch (IOException e)
                {
                    pLog.error("Error al cerrar el objeto pByteArrayOutputStream. Error:" + e);
                }
            }
            if (pInputStream != null)
            {
                try
                {
                    pInputStream.close();
                }
                catch (IOException e)
                {
                    pLog.error("Error al cerrar el objeto pInputStream. Error:" + e);
                }
            }
        }
        return pByteArrayOutputStream;
    }

    /**
     * Recibe un objeto y devuelve si es o no una coleccion iterable
     * 
     * @param obj
     *            Objeto del que se quiere saber si es una coleccion iterable
     * @return boolean con el resultado de la comparación
     */
    public static boolean isArrayObject(Object obj)
    {
        boolean fReturn = false;
        if (obj instanceof java.util.List)
        {
            fReturn = true;
        }
        else if (obj.getClass().isArray())
        {
            fReturn = true;
        }
        return fReturn;
    }

    /**
     * Evalua en profundidad, de forma recursiva de un objeto para sacar todos sus valores. Los subOjetos que encuentra
     * los guarda dentro de la estructura como "key" - "Json con el interior del hashtable"
     * 
     * @param oObject
     *            Objeto para poder hacer llamadas recursivas.
     * @return Texto json generado
     */
    public static String objectToJson(Object oObject)
    {
        StringBuffer pSB = new StringBuffer();
        String strObjectType = oObject.getClass().getName();
        if ("java.util.Hashtable".equals(strObjectType))
        {
            Hashtable<?, ?> pHashtable = (Hashtable<?, ?>) oObject;
            boolean fFirstElement = true;
            pSB.append("{");
            Enumeration<?> attributeNames = pHashtable.keys();
            while (attributeNames.hasMoreElements())
            {
                String strName = (String) attributeNames.nextElement();
                Object oInnerObject = pHashtable.get(strName);
                String strValue = objectToJson(oInnerObject);
                if (fFirstElement)
                {
                    fFirstElement = false;
                }
                else
                {
                    pSB.append(",");
                }
                pSB.append("\"" + strName + "\":" + strValue);
            }
            pSB.append("}");
            return pSB.toString();
        }
        else if (isArrayObject(oObject))
        {
            boolean fFirstElement = true;
            pSB.append("[");
            for (Object oItem : (List<?>) oObject)
            {
                String strValue = objectToJson(oItem);
                if (fFirstElement)
                {
                    fFirstElement = false;
                }
                else
                {
                    pSB.append(",");
                }
                pSB.append(strValue);
            }
            pSB.append("]");
            return pSB.toString();
        }
        else if ("java.lang.Short".equals(strObjectType) || "java.lang.Integer".equals(strObjectType)
                || "java.lang.Long".equals(strObjectType) || "java.lang.Double".equals(strObjectType)
                || "java.lang.Decimal".equals(strObjectType) || "java.lang.Float".equals(strObjectType)
                || "java.lang.Boolean".equals(strObjectType))
        {
            pSB.append(replaceIlegalCharactersInJSON(oObject.toString()));
        }
        else
        {
            pSB.append("\"" + replaceIlegalCharactersInJSON(oObject.toString()) + "\"");
        }
        return pSB.toString();
    }

    public static String replaceIlegalCharactersInJSON(String strInput)
    {
        return strInput.replaceAll("\"", "\\\"").replaceAll("\n", " ").replaceAll("\t", " ").replaceAll("\r", "");
    }

    public static String generateWSResponseJsonError(String strServiceName, int strErrorCode, String strErrorTxt)
    {
        return "{" + "\"" + strServiceName + "\": { \"codigoRetorno\": \"0\", \"Errores\": { \"codigoMostrar\": \""
                + strErrorCode + "\",\"mensajeMostrar\": \"" + strErrorTxt
                + "\",\"solucion\":\"REVISE LA ESPECIFICACION DEL SERVICIO\"}}}}";
    }

    public static String generateWSResponseJsonOk(String strServiceName, String strData)
    {
        return "{" + "\"" + strServiceName + "\": { \"codigoRetorno\": \"1\", \"Respuesta\": " + strData + "}}";
    }

    public static String readContentFromRequestBody(HttpServletRequest pRequest) throws Exception
    {
        StringBuffer pSB = new StringBuffer();
        String line = null;
        pRequest.setCharacterEncoding("UTF-8");
        BufferedReader reader = pRequest.getReader();
        while ((line = reader.readLine()) != null)
            pSB.append(line);
        return pSB.toString();
    }

    /**
     * Indica si la cadena que recibe es un objeto json o no
     * 
     * @param strData
     * @return
     */
    public static boolean isDataAJson(String strData)
    {
        try
        {
            new JSONObject(strData);
        }
        catch (JSONException ex)
        {
            try
            {
                new JSONArray(strData);
            }
            catch (JSONException ex1)
            {
                return false;
            }
        }
        return true;
    }

    public static String getPrimaryKeyFromJson(JSONObject pJsonData)
    {
        String strPrimaryKey = "";
        if (pJsonData.keys().hasNext())
        {
            strPrimaryKey = (String) pJsonData.keys().next();
        }
        return strPrimaryKey;
    }

    public static String formatString(String message, List<String> params)
    {
        try
        {
            return MessageFormat.format(message, params.toArray());
        }
        catch (MissingResourceException e)
        {
            return "";
        }
    }
}
