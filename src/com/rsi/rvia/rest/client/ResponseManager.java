package com.rsi.rvia.rest.client;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.conector.RestConnector;
import com.rsi.rvia.rest.conector.RestRviaConnector;
import com.rsi.rvia.rest.conector.RestWSConnector;
import com.rsi.rvia.rest.error.exceptions.ApplicationException;
import com.rsi.rvia.rest.error.exceptions.RestConnectorException;
import com.rsi.rvia.rest.operation.MiqQuests;
import com.rsi.rvia.rest.operation.MiqQuests.CompomentType;
import com.rsi.rvia.rest.session.RequestConfig;
import com.rsi.rvia.rest.session.RequestConfigRvia;

/**
 * Clase para manejar la respuesta del RestConnector. Mira si es un error o si no lo es y compone una respuesta JSON
 * Siguiendo la siguiente estructura: { "error": 0, ó 1 (si tiene o si no tiene) "response" : {...} (respuesta en JSON,
 * puede ser el error o la respuesta ya formada) }
 */
public class ResponseManager
{
    private static Logger pLog = LoggerFactory.getLogger(ResponseManager.class);

    /**
     * Procesa una respuesta recibida desde el conector para evaluar si es un error y formatear su contenido
     * 
     * @param pRequestConfig
     *            Datos de sesión del usuario ruralvia
     * @param pRestConnector
     *            Conector al origen de datos
     * @param pResponseConnector
     *            Objeto respuesta obtenida del conector
     * @return
     * @throws Exception
     */
    /**
     * @param pRequestConfig
     * @param pRestConnector
     * @param pResponseConnector
     * @param pMiqQuests
     * @return
     * @throws Exception
     */
    public static String processResponseConnector(RequestConfig pRequestConfig, RestConnector pRestConnector,
            Response pResponseConnector, MiqQuests pMiqQuests) throws Exception
    {
        String strJsonData;
        strJsonData = pResponseConnector.readEntity(String.class);
        JSONObject pJsonData;
        /* se comprueba si el contenido de la respuesta es un JSON u otra cosa */
        if (!isDataAJson(strJsonData))
        {
            /* no es un JSON, se evalua por si es un error web de ruralvia */
            if (RestRviaConnector.isRuralviaWebError(strJsonData))
            {
                /* se procede a obtener la infomración del error del interior del html devuelto por ruralvia */
                throw RestRviaConnector.generateLogicalErrorException(strJsonData);
            }
            else if (RestRviaConnector.isRuralviaSessionTimeoutError(strJsonData))
            {
                /* se procede a obtener la infomración del error del interior del html devuelto por ruralvia */
                throw RestRviaConnector.generateLogicalErrorException(strJsonData);
            }
            else if (pMiqQuests.getComponentType() == CompomentType.COORD)
            {
                // Procesar html para extraer la coordenada
                strJsonData = SignExtractor.extraerCoordenada(strJsonData);
            }
            else if (pResponseConnector.getStatus() != 200)
            {
                /*
                 * se comprueba si al respuesta contiene un codigo de error http para genera la respuesta con el mismo
                 * tipo
                 */
                throw new RestConnectorException(pResponseConnector.getStatus(), 99999, "Error al procesar la petición", pResponseConnector.getStatusInfo().getReasonPhrase(), null);
            }
            else
            {
                /*
                 * se procede a generar un error generico ya que si el valor no es un JSON y no es de ruralvia no
                 * debería producirse
                 */
                throw new ApplicationException(500, 99999, "Error al procesar la petición", "Se ha recibido unos datos no válidos", null);
            }
        }
        /* se crea el objeto JSON para ser manejado */
        pJsonData = new JSONObject(strJsonData);
        /* se comprueba si el json contiene un error, si es así se genera una excepción lógica */
        checkLogicalError(pRequestConfig, pMiqQuests, pResponseConnector, pJsonData);
        /* se formatea la respuesta para estandarizarla y eliminar información que el usuario final no necesita */
        pJsonData = formatResponse(pJsonData, pMiqQuests, pRestConnector);
        return pJsonData.toString();
    }

    /**
     * Comprueba si los adtos obtenidos contienen un error lógico y genera a excepción en dicho caso *
     * 
     * @param pRequestConfig
     *            Datos de sesión del usuario ruralvia
     * @param pRestConnector
     *            Conector al origen de datos
     * @param pResponse
     *            Objeto respuesta obtenida del conector
     * @param pJsonData
     *            Objeto que contiene la información JSON
     * @throws ApplicationException
     */
    private static void checkLogicalError(RequestConfig pRequestConfig, MiqQuests pMiqQuests, Response pResponse,
            JSONObject pJsonData) throws ApplicationException
    {
        Integer nHttpErrorCode = null;
        /* se comprueba si la respuesta tiene un codigo http de error para utilizarlo */
        if (pResponse.getStatus() != Status.OK.getStatusCode())
        {
            nHttpErrorCode = pResponse.getStatus();
        }
        /* se comprueba si el mensaje contiene un error generado por el conector WS */
        if (RestWSConnector.isWSError(pJsonData))
        {
            /* se lanza la excepción de tipo lócigo, en caso de no lanzarse se genera una exceción de tipo general */
            if (!RestWSConnector.throwWSError(nHttpErrorCode, pJsonData))
                throw new ApplicationException(500, 999999, "Error al procesar la información", "Error al acceder al contenido de un error de tipo ws", null);
        }
        else if (RestRviaConnector.isRVIAError(pJsonData))
        {
            /* se lanza la excepción de tipo lócigo, en caso de no lanzarse se genera una exceción de tipo general */
            if (!RestRviaConnector.throwRVIAError((RequestConfigRvia) pRequestConfig, pMiqQuests, pJsonData))
                throw new ApplicationException(500, 999999, "Error al procesar la información", "Error al acceder al contenido de un error de tipo ws", null);
        }
        /* si la ejecución ha llegado aqui es que todo parece correcto, se continua */
        pLog.info("No se han detectado errores en el json de respuesta, se continua la ejecución normal");
    }

    /**
     * Formatea la respuesta recibida por el conector para ajustarla al estadar definido por la aplciación
     * 
     * @param pJsonData
     *            Objeto que contiene la información JSON
     * @return
     * @throws Exception
     */
    private static JSONObject formatResponse(JSONObject pJsonData, MiqQuests pMiqQuests, RestConnector pRestConnector)
            throws Exception
    {
        /* se comprueba si el json pertenece a WS */
        if (RestWSConnector.isWSJson(pJsonData))
        {
            pJsonData = adjustWSJson(pJsonData);
        }
        pJsonData = filterResponseFields(pJsonData, pMiqQuests, pRestConnector);
        return pJsonData;
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

    /**
     * Formatea el contenido de la respuesta para que comience por el token 'response' y despues el contenido
     * 
     * @param pJsonData
     *            Objeto que contiene la información JSON
     * @return Objeto que contiene la información JSON
     * @throws Exception
     */
    private static JSONObject adjustWSJson(JSONObject pJsonData) throws Exception
    {
        JSONObject pResponseObjectData;
        JSONObject pResponseObject;
        try
        {
            String strPrimaryKey = "";
            if (pJsonData.keys().hasNext())
            {
                strPrimaryKey = (String) pJsonData.keys().next();
            }
            if (!strPrimaryKey.trim().isEmpty())
            {
                pResponseObjectData = new JSONObject();
                pResponseObject = new JSONObject();
                pResponseObjectData.put("data", pJsonData.getJSONObject(strPrimaryKey).getJSONObject("Respuesta"));
                pResponseObject.put("response", pResponseObjectData);
            }
            else
                throw new Exception("No se ha encontrado la raiz del json de WS");
        }
        catch (Exception ex)
        {
            pLog.error("Error al obtener el cuerpo del mensaje de una respuesta WS", ex);
            throw ex;
        }
        return pResponseObject;
    }

    private static JSONObject filterResponseFields(JSONObject pJsonData, MiqQuests pMiqQuests,
            RestConnector pRestConnector) throws Exception
    {
        // Estaría bien procesar los json estáticos aquí mismo
        HttpServletRequest pRequest = pRestConnector.getRequest();
        /* Cargamos en el modelo los parámetros de salida */
        SaveExitHierarchy.process(pJsonData, pMiqQuests.getIdMiq(), pRestConnector.getMethod());
        // TODO: aqui ira el filtrado de campos de salida
        return pJsonData;
    }
}
