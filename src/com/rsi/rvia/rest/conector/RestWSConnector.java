package com.rsi.rvia.rest.conector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;
import com.rsi.rvia.rest.client.RviaRestHttpClient;
import com.rsi.rvia.rest.error.exceptions.LogicalErrorException;
import com.rsi.rvia.rest.operation.MiqQuests;
import com.rsi.rvia.rest.operation.info.InterrogateRvia;
import com.rsi.rvia.rest.response.RviaRestResponseErrorItem;
import com.rsi.rvia.rest.session.RequestConfig;
import com.rsi.rvia.rest.session.RequestConfigRvia;
import com.rsi.rvia.rest.tool.GettersRequestParams;
import com.rsi.rvia.rest.tool.Utils;

/** Clase que gestiona la conexión y comunicaciñon con el proveedor de datos (Ruralvia o WS) */
public class RestWSConnector
{
    private static Logger      pLog           = LoggerFactory.getLogger(RestWSConnector.class);
    public static final String RAMA_RESPUESTA = "Respuesta";
    public static final String RAMA_RETORNO   = "codigoRetorno";
    public static final String RAMA_ERROR     = "Errores";
    public static final String RAMA_COD_ERROR = "codigoMostrar";
    public static final String RAMA_MSG_ERROR = "mensajeMostrar";
    public static final String RAMA_SOL_ERROR = "solucion";

    /**
     * Realiza una petición de tipo get restFull al proveedor de datos (Ruralvia o WS dependiendo de la configuración)
     * 
     * @param pRequest
     *            petición del cliente
     * @param strPathRest
     *            path de la petición
     * @param pMiqQuests
     *            Objeto MiqQuests con la información de la operativa
     * @param strJsonData
     *            Datos a enviar
     * @param strEndPoint
     *            Endpoint del proveedor de datos
     * @param pPathParams
     *            Parámetros asociados al path
     * @return Respuesta del proveedor de datos
     * @throws Exception
     */
    public static Response get(HttpServletRequest pRequest, MiqQuests pMiqQuests, String strJsonData,
            MultivaluedMap<String, String> pPathParams, HashMap<String, String> pParamsToInject) throws Exception
    {
        Client pClient = RviaRestHttpClient.getClient();
        String strQueryParams = ((pRequest.getQueryString() == null) ? "" : pRequest.getQueryString());
        String JWT = pRequest.getHeader("Authorization");
        /* se obtienen lso header necesarios para realizar la petición al WS */
        String strCODSecEnt = GettersRequestParams.getCODSecEnt(pRequest);
        String strCODSecUser = GettersRequestParams.getCODSecUser(pRequest);
        String strCODSecTrans = GettersRequestParams.getCODSecTrans(pRequest);
        String strCODTerminal = GettersRequestParams.getCODTerminal(pRequest);
        String strCODApl = GettersRequestParams.getCODApl(pRequest);
        String strCODCanal = GettersRequestParams.getCODCanal(pRequest);
        String strCODSecIp = GettersRequestParams.getCODSecIp(pRequest);
        String pathQueryParams = "";
        pathQueryParams = Utils.multiValuedMapToQueryString(pPathParams);
        if (!pathQueryParams.isEmpty() && !pathQueryParams.endsWith("&"))
            pathQueryParams += "&";
        pathQueryParams += Utils.hashMapToQueryString(pParamsToInject);
        if (!pathQueryParams.isEmpty() && !pathQueryParams.endsWith("&"))
            pathQueryParams += "&";
        pathQueryParams += Utils.simpleJsonToQueryString(strJsonData);
        String urlQueryString = strQueryParams;
        if (!urlQueryString.isEmpty() && !urlQueryString.endsWith("&"))
            urlQueryString += "&";
        urlQueryString += "idMiq=" + pMiqQuests.getIdMiq() + "&" + pathQueryParams;
        String strUrlTotal = pMiqQuests.getBaseWSEndPoint(pRequest) + "?" + urlQueryString;
        WebTarget pTarget = pClient.target(strUrlTotal);
        pLog.info("Url final de petición de datos: " + strUrlTotal);
        Response pReturn = pTarget.request().header("Authorization", JWT).header("CODSecEnt", strCODSecEnt).header("CODSecUser", strCODSecUser).header("CODSecTrans", strCODSecTrans).header("CODTerminal", strCODTerminal).header("CODApl", strCODApl).header("CODCanal", strCODCanal).header("CODSecIp", strCODSecIp).accept(MediaType.APPLICATION_JSON).get();
        // Evitar logueo de campos de login
        logWithFilter(pReturn);
        return pReturn;
    }

    /**
     * Realiza una petición de tipo post restFull al proveedor de datos (Ruralvia o WS dependiendo de la configuración)
     * 
     * @param pRequest
     *            petición del cliente
     * @param strPathRest
     *            path de la petición
     * @param strJsonData
     *            Datos a enviar
     * @param pMiqQuests
     *            Objeto MiqQuests con la información de la operativa
     * @param pPathParams
     *            Parámetros asociados al path
     * @return Respuesta del proveedor de datos
     * @throws Exception
     */
    public static Response post(@Context HttpServletRequest pRequest, MiqQuests pMiqQuests,
            RequestConfig pRequestConfig, String strJsonData, MultivaluedMap<String, String> pPathParams,
            HashMap<String, String> pParamsToInject) throws Exception
    {
        Hashtable<String, String> htDatesParameters = new Hashtable<String, String>();
        Client pClient = RviaRestHttpClient.getClient();
        // Headers
        String JWT = pRequest.getHeader("Authorization");
        String strCODSecEnt = GettersRequestParams.getCODSecEnt(pRequest);
        String strCODSecUser = GettersRequestParams.getCODSecUser(pRequest);
        String strCODSecTrans = GettersRequestParams.getCODSecTrans(pRequest);
        String strCODTerminal = GettersRequestParams.getCODTerminal(pRequest);
        String strCODApl = GettersRequestParams.getCODApl(pRequest);
        String strCODCanal = GettersRequestParams.getCODCanal(pRequest);
        String strCODSecIp = GettersRequestParams.getCODSecIp(pRequest);
        String strParameters = getDDBBOperationParameters(pMiqQuests.getPathRest(), "paramname");
        pLog.info("Query Params: " + strParameters);
        if (!strParameters.isEmpty())
        {
            htDatesParameters = InterrogateRvia.getParameterFromSession(strParameters, (RequestConfigRvia) pRequestConfig);
            htDatesParameters = checkSessionValues(pRequest, htDatesParameters);
        }
        ObjectMapper pMapper = new ObjectMapper();
        ObjectNode pJson = (ObjectNode) pMapper.readTree(strJsonData);
        Enumeration<String> pEnum = htDatesParameters.keys();
        while (pEnum.hasMoreElements())
        {
            String strTableKey = (String) pEnum.nextElement();
            pJson.put(strTableKey, (String) htDatesParameters.get(strTableKey).toString());
        }
        Iterator<String> pIterator = pPathParams.keySet().iterator();
        while (pIterator.hasNext())
        {
            String strKey = (String) pIterator.next();
            pJson.put(strKey, (String) pPathParams.get(strKey).toString());
        }
        if (pParamsToInject != null)
        {
            pIterator = pParamsToInject.keySet().iterator();
            while (pIterator.hasNext())
            {
                String strKey = (String) pIterator.next();
                pJson.put(strKey, (String) pPathParams.get(strKey).toString());
            }
        }
        JSONObject opciones = pMiqQuests.getJsonOpciones();
        if (opciones == null || opciones.optBoolean(MiqQuests.OPTION_PARAM_PROPAGATE_ID_MIQ, true))
        {
            pJson.put("idMiq", pMiqQuests.getIdMiq());
        }
        strJsonData = pJson.toString();
        pLog.info("Url final de petición de datos: " + pMiqQuests.getBaseWSEndPoint(pRequest));
        WebTarget pTarget = pClient.target(pMiqQuests.getBaseWSEndPoint(pRequest));
        Response pReturn = pTarget.request().header("Authorization", JWT).header("CODSecEnt", strCODSecEnt).header("CODSecUser", strCODSecUser).header("CODSecTrans", strCODSecTrans).header("CODTerminal", strCODTerminal).header("CODApl", strCODApl).header("CODCanal", strCODCanal).header("CODSecIp", strCODSecIp).accept(MediaType.APPLICATION_JSON).post(Entity.json(strJsonData));
        logWithFilter(pReturn);
        return pReturn;
    }

    /**
     * Realiza una petición de tipo put restFull al proveedor de datos (Ruralvia o WS dependiendo de la configuración)
     * 
     * @param pRequest
     *            petición del cliente
     * @param strPathRest
     *            path de la petición
     * @param pRequestConfig
     *            Datos de la petición recibida desde ruralvia
     * @param strJsonData
     *            Datos a enviar
     * @param pMiqQuests
     *            Objeto MiqQuests con la información de la operativa
     * @param pPathParams
     *            Parámetros asociados al path
     * @return Respuesta del proveedor de datos
     * @throws Exception
     */
    public static Response put(@Context HttpServletRequest pRequest, MiqQuests pMiqQuests,
            RequestConfig pRequestConfig, String strJsonData, MultivaluedMap<String, String> pPathParams,
            HashMap<String, String> pParamsToInject) throws Exception
    {
        /*
         * se reutiliza la petición post puesto que es similar, en caso de una implementación diferente, es necesario
         * definir este método desde cero
         */
        pLog.warn("Se recibe un método PUT, pero se trata como si fuera un POST");
        return post(pRequest, pMiqQuests, pRequestConfig, strJsonData, pPathParams, pParamsToInject);
    }

    /**
     * Realiza una petición de tipo delete restFull al proveedor de datos (Ruralvia o WS dependiendo de la
     * configuración)
     * 
     * @param pRequest
     *            petición del cliente
     * @return Respuesta del proveedor de datos
     * @throws Exception
     */
    public static Response delete(@Context HttpServletRequest pRequest) throws Exception
    {
        // /??? falta por implementar el método delete
        pLog.error("El método delete no está implementado");
        throw new Exception("Se ha recibido una petición de tipo DELETE y no existe ningún método que implemente este tipo de peticiones");
    }

    /**
     * Obtiene los parámetros necesarios para poder ejecutar una operación. Los datos se leen desde la base de datos de
     * configuración.
     * 
     * @param strPathRest
     *            Path rest de la opreación a realizar
     * @param strCampo
     *            campo a consultar de la base de datos
     * @return Cadena que contiene los campos separados por el carácter ';'
     * @throws Exception
     */
    private static String getDDBBOperationParameters(String strPathRest, String strCampo) throws Exception
    {
        String strReturn = "";
        Connection pConnection = null;
        PreparedStatement pPreparedStatement = null;
        ResultSet pResultSet = null;
        try
        {
            String strQuery = "select c." + strCampo + " campo from " + " BEL.BDPTB222_MIQ_QUESTS a, "
                    + " BEL.BDPTB226_MIQ_QUEST_RL_SESSION b, " + " BEL.BDPTB225_MIQ_SESSION_PARAMS c "
                    + " where a.id_miq=b.id_miq " + " and b.ID_MIQ_PARAM=c.ID_MIQ_PARAM " + " and a.path_rest='"
                    + strPathRest + "' order by c.ID_MIQ_PARAM";
            pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
            pPreparedStatement = pConnection.prepareStatement(strQuery);
            pResultSet = pPreparedStatement.executeQuery();
            pLog.trace("Query BBDD Params bien ejecutada");
            while (pResultSet.next())
            {
                String strInputName = (String) pResultSet.getString("campo");
                if (!strReturn.isEmpty())
                {
                    strReturn += ";";
                }
                strReturn += strInputName;
            }
        }
        catch (Exception ex)
        {
            pLog.error("Error al recuperar los nombres de parametros Path_Rest(" + strPathRest + "): " + ex);
            strReturn = "";
        }
        finally
        {
            DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
        }
        return strReturn;
    }

    /**
     * Comprueba si el contenido del JSON es de tipo WS
     * 
     * @param pJsonData
     *            Objeto que contiene la información JSON
     * @return
     */
    public static boolean isWSJson(JSONObject pJsonData)
    {
        boolean fReturn = false;
        String strStatusResponse = getRetorno(pJsonData);
        if (strStatusResponse != null && strStatusResponse.trim().length() > 0)
        {
            fReturn = true;
        }
        return fReturn;
    }

    /**
     * Comprueba si el contenido del JSON es un error generado por WS
     * 
     * @param pJsonData
     *            Objeto que contiene la información JSON
     * @return true si codigoRetorno es cero
     */
    public static boolean isWSError(JSONObject pJsonData)
    {
        String strStatusResponse = getRetorno(pJsonData);
        if (strStatusResponse != null && !strStatusResponse.equals(""))
        {
            if (!strStatusResponse.equals("1"))
                return true;
        }
        return false;
    }

    public static JSONObject getRespuesta(JSONObject pJsonData)
    {
        JSONObject pJson = null;
        String strPrimaryKey = Utils.getPrimaryKeyFromJson(pJsonData);
        try
        {
            if (strPrimaryKey != null && !strPrimaryKey.trim().isEmpty())
            {
                if (pJsonData.getJSONObject(strPrimaryKey).has(RAMA_RESPUESTA))
                    pJson = (JSONObject) pJsonData.getJSONObject(strPrimaryKey).getJSONObject(RAMA_RESPUESTA);
                else
                    pJson = null;
            }
        }
        catch (JSONException e)
        {
            pLog.error("RestWSConnector.getRespuesta:No tiene el componente Respuesta");
            pJson = null;
        }
        return pJson;
    }

    public static JSONObject getErrores(JSONObject pJsonData)
    {
        JSONObject pJson = null;
        String strPrimaryKey = Utils.getPrimaryKeyFromJson(pJsonData);
        try
        {
            if (strPrimaryKey != null && !strPrimaryKey.trim().isEmpty())
            {
                if (pJsonData.getJSONObject(strPrimaryKey).has(RAMA_ERROR))
                    pJson = (JSONObject) pJsonData.getJSONObject(strPrimaryKey).getJSONObject(RAMA_ERROR);
                else
                    pJson = null;
            }
        }
        catch (JSONException e)
        {
            pLog.error("RestWSConnector.getRespuesta:No tiene el componente Respuesta");
            pJson = null;
        }
        return pJson;
    }

    private static String getRetorno(JSONObject pJsonData)
    {
        String strPrimaryKey = "";
        String strStatusResponse = "";
        try
        {
            strPrimaryKey = Utils.getPrimaryKeyFromJson(pJsonData);
            if (strPrimaryKey != null && !strPrimaryKey.trim().isEmpty())
            {
                strStatusResponse = pJsonData.getJSONObject(strPrimaryKey).getString(RAMA_RETORNO);
            }
        }
        catch (Exception ex)
        {
            pLog.warn("No es un JSON de WS");
            strStatusResponse = "";
        }
        return strStatusResponse;
    }

    /**
     * genera unaexceción de tipo lógico a partir del mensaje de error de una respuesta WS
     * 
     * @param nHttpErrorCode
     *            Codigo de error obtenido en la cabecera de la respuesta
     * @param pJsonData
     *            Objeto que contiene la información JSON
     * @return Objeto JSON que contiene el cuerpo
     * @throws LogicalErrorException
     */
    public static boolean throwWSError(Integer nHttpErrorCode, JSONObject pJsonData) throws LogicalErrorException
    {
        Integer nCode = null;
        String strMessage = null;
        String strDescription = null;
        boolean fProcessed = false;
        try
        {
            String strPrimaryKey = Utils.getPrimaryKeyFromJson(pJsonData);
            if (strPrimaryKey != null && !strPrimaryKey.trim().isEmpty())
            {
                JSONObject pJsonContent = pJsonData.getJSONObject(strPrimaryKey).getJSONObject(RAMA_ERROR);
                if (pJsonContent == null)
                    pLog.error("No se ha encontrado el nodo 'Errores' dentro del contenido del JSON devuelto por el WS");
                else
                {
                    nCode = Integer.parseInt(pJsonContent.getString(RAMA_COD_ERROR));
                    strMessage = pJsonContent.getString(RAMA_MSG_ERROR);
                    strDescription = pJsonContent.getString(RAMA_SOL_ERROR);
                    fProcessed = true;
                }
            }
        }
        catch (Exception ex)
        {
            pLog.error("Error al obtener el cuerpo del mensaje de error de una respuesta WS", ex);
        }
        if (fProcessed)
            throw new LogicalErrorException(nHttpErrorCode, nCode, strMessage, strDescription, null);
        return true;
    }

    public static RviaRestResponseErrorItem generateRviaRestErrorItem(JSONObject pJsonData) throws JSONException
    {
        RviaRestResponseErrorItem pReturn;
        JSONObject pError = RestWSConnector.getErrores(pJsonData);
        String strCodeError = pError.getString(RAMA_COD_ERROR);
        String strTextError = pError.getString(RAMA_MSG_ERROR);
        pReturn = new RviaRestResponseErrorItem(strCodeError, strTextError);
        return pReturn;
    }

    /**
     * Revisa si algun parametro recuperado esta en sesion, si lo está coge el de sesion, sino lo añade a esta
     * 
     * @param pRequest
     * @param pParameters
     * @return
     */
    public static Hashtable<String, String> checkSessionValues(@Context HttpServletRequest pRequest,
            Hashtable<String, String> pParameters)
    {
        if (pParameters != null)
        {
            HttpSession pSession = pRequest.getSession(false);
            Iterator<String> pIterator = pParameters.keySet().iterator();
            while (pIterator.hasNext())
            {
                String strKey = (String) pIterator.next();
                String strSessionValue = (String) pSession.getAttribute(strKey);
                if (strSessionValue != null)
                {
                    pParameters.remove(strKey);
                    pParameters.put(strKey, strSessionValue);
                }
                else
                {
                    pSession.setAttribute(strKey, pParameters.get(strKey));
                }
            }
        }
        return pParameters;
    }

    /**
     * Filtra datos sensibles del request en la escritura de trazas
     * 
     * @param pRequest
     * @return
     */
    private static void logWithFilter(Response pReturn)
    {
        if (pReturn.toString().indexOf("documento") == -1) // Campo devuelto por el servicio de login
        {
            pLog.info("GET: " + pReturn.toString());
        }
    }
}
