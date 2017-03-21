package com.rsi.rvia.rest.client;

import javax.ws.rs.core.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.conector.RestConnector;
import com.rsi.rvia.rest.conector.RestRviaConnector;
import com.rsi.rvia.rest.conector.RestWSConnector;
import com.rsi.rvia.rest.operation.MiqQuests;
import com.rsi.rvia.rest.operation.MiqQuests.CompomentType;
import com.rsi.rvia.rest.response.RviaRestResponse;
import com.rsi.rvia.rest.response.RviaRestResponse.Type;
import com.rsi.rvia.rest.response.RviaRestResponseErrorItem;
import com.rsi.rvia.rest.session.RequestConfig;
import com.rsi.rvia.rest.tool.Utils;

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
        JSONObject pJsonData;
        RviaRestResponse pRviaRestResponse;
        String strResponseData = pResponseConnector.readEntity(String.class);
        /* se comprueba si el contenido de la respuesta es un JSON u otra cosa */
        if (!Utils.isDataAJson(strResponseData))
        {
            /* no es un JSON, viene html, se evalua por si es un error web de ruralvia */
            if (RestRviaConnector.isRuralviaWebError(strResponseData))
            {
                /* se evalua el html para construir un error JSOn con los datos obtenidos */
                RviaRestResponseErrorItem pRviaRestResponseErrorItem = new RviaRestResponseErrorItem("999999", "Error no controlado de ruralvia");
                pRviaRestResponse = new RviaRestResponse(RviaRestResponse.Type.ERROR, "{}", pRviaRestResponseErrorItem);
            }
            else if (RestRviaConnector.isRuralviaSessionTimeoutError(strResponseData))
            {
                /* se evalua el html para construir un error JSOn con los datos obtenidos */
                RviaRestResponseErrorItem pRviaRestResponseErrorItem = new RviaRestResponseErrorItem("999999", "Error de timeout");
                pRviaRestResponse = new RviaRestResponse(Type.ERROR, "{}", pRviaRestResponseErrorItem);
            }
            else if (pMiqQuests.getComponentType() == CompomentType.COORD)
            {
                /* se evalua el html para construir un error JSOn con los datos obtenidos */
                strResponseData = SignExtractor.extraerCoordenada(strResponseData);
                pRviaRestResponse = new RviaRestResponse(Type.OK, strResponseData, null);
            }
            else
            {
                /*
                 * se procede a generar un error generico ya que si el valor no es un JSON y no es de ruralvia no
                 * debería producirse
                 */
                /* se evalua el html para construir un error JSOn con los datos obtenidos */
                RviaRestResponseErrorItem pRviaRestResponseErrorItem = new RviaRestResponseErrorItem("999999", "Error no controlado al procesar la petición");
                pRviaRestResponse = new RviaRestResponse(Type.ERROR, "{}", pRviaRestResponseErrorItem);
            }
        }
        else
        {
            /* se crea el objeto JSON para ser manejado */
            pJsonData = new JSONObject(strResponseData);
            /* se comprueba si el json contiene un error, si es así se genera una excepción lógica */
            /* se formatea la respuesta para estandarizarla y eliminar información que el usuario final no necesita */
            pRviaRestResponse = formatResponse(pJsonData, pMiqQuests, pRestConnector, pResponseConnector);
        }
        return pRviaRestResponse.toString();
    }

    /**
     * Formatea la respuesta recibida por el conector para ajustarla al estadar definido por la aplciación
     * 
     * @param pJsonData
     *            Objeto que contiene la información JSON
     * @return
     * @throws Exception
     */
    private static RviaRestResponse formatResponse(JSONObject pJsonData, MiqQuests pMiqQuests,
            RestConnector pRestConnector, Response pResponseConnector) throws Exception
    {
        RviaRestResponse pRviaRestResponse;
        /* se comprueba si el mensaje pertenece as un WS */
        if (RestWSConnector.isWSJson(pJsonData))
        {
            if (RestWSConnector.isWSError(pJsonData))
            {
                RviaRestResponseErrorItem pErrorItem = RestWSConnector.generateRviaRestErrorItem(pJsonData);
                int nHttpCode = pResponseConnector.getStatus();
                if (nHttpCode != Response.Status.OK.getStatusCode())
                    pRviaRestResponse = new RviaRestResponse(Type.ERROR, nHttpCode, "{}", pErrorItem);
                else
                    pRviaRestResponse = new RviaRestResponse(Type.ERROR, "{}", pErrorItem);
            }
            else
            {
                JSONObject pData = RestWSConnector.getRespuesta(pJsonData);
                int nHttpCode = pResponseConnector.getStatus();
                pRviaRestResponse = new RviaRestResponse(Type.OK, nHttpCode, pData);
            }
        }
        else if (RestRviaConnector.isRviaJson(pJsonData))
        {
            RviaRestResponse.Type pType = RestRviaConnector.getResponseType(pJsonData, pMiqQuests.getIdMiq());
            switch (pType)
            {
                case ERROR:
                    RviaRestResponseErrorItem pErrorItem = RestRviaConnector.generateRviaRestErrorItem(pJsonData);
                    pRviaRestResponse = new RviaRestResponse(Type.ERROR, "{}", pErrorItem);
                    break;
                case WARNING:
                    RviaRestResponseErrorItem pWarningItem = RestRviaConnector.generateRviaRestErrorItem(pJsonData);
                    pRviaRestResponse = new RviaRestResponse(Type.WARNING, RestRviaConnector.getRespuesta(pJsonData), pWarningItem);
                    break;
                default:
                    pRviaRestResponse = new RviaRestResponse(Type.OK, RestRviaConnector.getRespuesta(pJsonData));
                    break;
            }
        }
        else
        {
            JSONObject pData = RestWSConnector.getRespuesta(pJsonData);
            int nHttpCode = pResponseConnector.getStatus();
            pRviaRestResponse = new RviaRestResponse(Type.OK, nHttpCode, pData);
        }
        /* si la ejecución ha llegado aqui es que todo parece correcto, se continua */
        pLog.info("Se ha generado una respuesta. Respuesta:" + pRviaRestResponse);
        pRviaRestResponse = filterResponseFields(pRviaRestResponse, pMiqQuests, pRestConnector);
        return pRviaRestResponse;
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
                pResponseObjectData.put("data", pJsonData.getJSONObject(strPrimaryKey).getJSONObject(RestWSConnector.RAMA_RESPUESTA));
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

    private static RviaRestResponse filterResponseFields(RviaRestResponse pRviaRestResponse, MiqQuests pMiqQuests,
            RestConnector pRestConnector) throws Exception
    {
        /* Cargamos en el modelo los parámetros de salida */
        SaveExitHierarchy.process(pRviaRestResponse.getJsonObject(), pMiqQuests.getIdMiq(), pRestConnector.getMethod());
        // TODO: aqui ira el filtrado de campos de salida
        return pRviaRestResponse;
    }
}
