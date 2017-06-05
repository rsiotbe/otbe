package com.rsi.rvia.rest.conector;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.operation.MiqQuests;
import com.rsi.rvia.rest.session.RequestConfig;
import com.rsi.rvia.rest.session.RequestConfigRvia;

/** Clase que gestiona la conexión y comunicaciñon con el proveedor de datos (Ruralvia o WS) */
public class RestConnector
{
    private static Logger      pLog                   = LoggerFactory.getLogger(RestConnector.class);
    private String             _requestMethod         = "";
    private HttpServletRequest _privateContextRequest = null;

    /**
     * Devuelve el método asociado a la petición
     * 
     * @return String método del request
     */
    public String getMethod()
    {
        return this._requestMethod;
    }

    /**
     * Devuelve el objeto Request
     * 
     * @return HttpServletRequest
     */
    public HttpServletRequest getRequest() throws Exception
    {
        return _privateContextRequest;
    }

    /**
     * Realiza la llamada al proveedor de datos para obtener el resultado de la operación
     * 
     * @param pRequest
     *            petición del cliente
     * @param strData
     *            datos a enviar al proveedor
     * @param pRequestConfig
     *            datos de la petición recibida desde ruralvia
     * @param strPrimaryPath
     *            path original de la petición
     * @param pPathParams
     *            parámetros asociados al path
     * @return Respuesta del proveedor de datos
     * @throws Exception
     */
    public Response getData(HttpServletRequest pRequest, String strData, RequestConfig pRequestConfig,
            MiqQuests pMiqQuests, MultivaluedMap<String, String> pPathParams, HashMap<String, String> pParamsToInject)
            throws Exception
    {
        Response pReturn = null;
        String strMethod = pRequest.getMethod();
        this._requestMethod = strMethod;
        this._privateContextRequest = pRequest;
        /* se obtiene la configuración de la operativa desde base de datos */
        pLog.info("Se obtiene la configuración de la base de datos. MiqQuest: " + pMiqQuests);
        pLog.info("Se recibe una petición con tipo de metodo : " + strMethod + " a "
                + pMiqQuests.getComponentType().name());
        switch (pMiqQuests.getComponentType())
        {
            case RVIA:
                pReturn = RestRviaConnector.doConnection(pRequest, pMiqQuests, (RequestConfigRvia) pRequestConfig,
                        strData, pPathParams, pParamsToInject);
                break;
            case COORD:
                pReturn = RestRviaConnector.doDirectConnectionToJsp(pRequest, pMiqQuests,
                        (RequestConfigRvia) pRequestConfig, strData, pPathParams, pParamsToInject);
                break;
            case WS:
            case API:
            case SIMULATOR:
                switch (strMethod)
                {
                    case "GET":
                        pReturn = RestWSConnector.get(pRequest, pMiqQuests, pRequestConfig, strData, pPathParams,
                                pParamsToInject);
                        break;
                    case "POST":
                        pReturn = RestWSConnector.post(pRequest, pMiqQuests, pRequestConfig, strData, pPathParams,
                                pParamsToInject);
                        break;
                    case "PUT":
                        pReturn = RestWSConnector.put(pRequest, pMiqQuests, pRequestConfig, strData, pPathParams,
                                pParamsToInject);
                        break;
                    case "PATCH":
                        pLog.warn("No existe ninguna acción para este método");
                        break;
                    case "DELETE":
                        pReturn = RestWSConnector.delete(pRequest);
                        break;
                    default:
                        pLog.warn(
                                "No existe tipo de componente definido para esta petición, se devuelve una respuesta ok vacía");
                        pReturn = Response.ok("{}").build();
                        break;
                }
                break;
            case LITE:
                throw new Exception("Tipo de componente sin desarrollar");
            default:
                throw new Exception("No se ha determinado un tipo de componente válido para la petición");
        }
        return pReturn;
    }
}
