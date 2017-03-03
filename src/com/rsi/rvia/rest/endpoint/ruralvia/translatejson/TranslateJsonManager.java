package com.rsi.rvia.rest.endpoint.ruralvia.translatejson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.codec.CharEncoding;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.isum.IsumValidation;
import com.rsi.rvia.rest.conector.RestWSConnector;
import com.rsi.rvia.rest.error.ErrorManager;
import com.rsi.rvia.rest.error.ErrorResponse;
import com.rsi.rvia.rest.error.exceptions.ISUMException;
import com.rsi.rvia.rest.session.RequestConfigRvia;

/**
 * The Class TranslateJsonManager.
 */
public class TranslateJsonManager
{
    /** The log. */
    private static Logger       pLog               = LoggerFactory.getLogger(TranslateJsonManager.class);
    /** The Constant ISUM_ERROR_CODE_EX. */
    private static final int    ISUM_ERROR_CODE_EX = 401;
    /** The Constant RESPONSE_OK. */
    private static final String RESPONSE_OK        = "{\"response\":\"0\"}";
    /** The Constant RESPONSE_WR. */
    private static final String RESPONSE_WR        = "{\"response\":\"1\"}";
    /** The Constant RESPONSE_KO. */
    private static final String RESPONSE_KO        = "{\"response\":\"2\"}";
    /** The Constant RESPONSE_NW. */
    private static final String RESPONSE_NW        = "{\"response\":\"3\"}";
    /** The session. */
    private static HttpSession  pSession;

    /**
     * Process data.
     * 
     * @param pRequest
     *            the request
     * @param pUriInfo
     *            the uri info
     * @param pJsonData
     *            the json data
     * @return the response
     * @throws ISUMException
     *             the ISUM exception
     */
    public static Response processData(HttpServletRequest pRequest, UriInfo pUriInfo, JSONObject pJsonData,
            boolean swsession) throws ISUMException
    {
        Response response = null;
        ErrorResponse pErrorCaptured = null;
        String strCode = null;
        String strDesc = null;
        int isError = 0;
        try
        {
            RequestConfigRvia pRequestConfigRvia = null;
            if (swsession)
            {
                pLog.debug("TranslateJSONManager:translateJSON:request:" + pRequest.getRequestURL().toString() + "::"
                        + pRequest.getRequestURI() + "::" + pRequest.getRemoteHost() + "::" + pRequest.getRemoteAddr());
                /*
                 * pRequestConfigRvia = getValidateSession(pRequest); pSession = pRequest.getSession(true); // Se
                 * comprueba si el servicio de isum está permitido. if
                 * (!IsumValidation.IsValidService(pRequestConfigRvia)) { throw new ISUMException(ISUM_ERROR_CODE_EX,
                 * null, "Servicio no permitido",
                 * "El servicio solicitado de ISUM no está permitido para el perfil de este usuario.", null); }
                 */
                System.out.println("pJsonData:" + pJsonData.toString());
                if (RestWSConnector.isWSError(pJsonData))
                {
                    response = Response.status(HttpStatus.SC_OK).entity(RESPONSE_KO).encoding(CharEncoding.UTF_8).build();
                    // if (!RestWSConnector.throwWSError(nHttpErrorCode, pJsonData))
                    // throw new ApplicationException(500, 999999, "Error al procesar la información",
                    // "Error al acceder al contenido de un error de tipo ws", null);
                }
            }
            pJsonData = adjustWSJson(pJsonData);
            System.out.println("pJsonData:" + pJsonData.toString());
            if (pJsonData.has("CODERR") && pJsonData.has("TXTERR"))
            {
                strCode = pJsonData.getString("CODERR");
                strDesc = pJsonData.getString("TXTERR");
                isError = Integer.parseInt(TranslateJsonCache.isErrorCode(strCode, strDesc));
            }
            switch (isError)
            {
                case 0:
                    response = Response.status(HttpStatus.SC_OK).entity(RESPONSE_OK).encoding(CharEncoding.UTF_8).build();
                    pLog.info("El json no contiene código de error");
                    break;
                case 1:
                    response = Response.status(HttpStatus.SC_OK).entity(RESPONSE_WR).encoding(CharEncoding.UTF_8).build();
                    pLog.info("El json contiene código de warning");
                    break;
                case 2:
                    response = Response.status(HttpStatus.SC_OK).entity(RESPONSE_KO).encoding(CharEncoding.UTF_8).build();
                    pLog.info("El json contiene código de error");
                    break;
                case 3:
                    response = Response.status(HttpStatus.SC_OK).entity(RESPONSE_NW).encoding(CharEncoding.UTF_8).build();
                    pLog.info("El json no contiene código de error");
                    break;
            }
            /*
             * if (RestRviaConnector.isRVIAError(pJsonData)) { response =
             * Response.status(HttpStatus.SC_OK).entity(RESPONSE_KO).encoding(CharEncoding.UTF_8).build(); // if
             * (!RestRviaConnector.throwRVIAError((RequestConfigRvia) pRequestConfig, pMiqQuests, pJsonData)) // throw
             * new ApplicationException(500, 999999, "Error al procesar la información", //
             * "Error al acceder al contenido de un error de tipo ws", null); }
             */
        }
        catch (Exception ex)
        {
            pLog.error("Error en comprobación de json", ex);
            pErrorCaptured = ErrorManager.getErrorResponseObject(ex);
            response = (Response.status(pErrorCaptured.getHttpCode()).entity(pErrorCaptured.getJsonError()).encoding(CharEncoding.UTF_8).build());
        }
        return response;
    }

    /**
     * Process data.
     * 
     * @param pRequest
     *            the request
     * @param pUriInfo
     *            the uri info
     * @param strCode
     *            the str code
     * @param strDesc
     *            the str desc
     * @return the response
     * @throws ISUMException
     *             the ISUM exception
     */
    public static Response processData(HttpServletRequest pRequest, UriInfo pUriInfo, String strCode, String strDesc)
            throws ISUMException
    {
        Response response = null;
        ErrorResponse pErrorCaptured = null;
        String isError = null;
        try
        {
            RequestConfigRvia pRequestConfigRvia = null;
            pRequestConfigRvia = getValidateSession(pRequest);
            pSession = pRequest.getSession(true);
            // Se comprueba si el servicio de isum está permitido.
            if (!IsumValidation.IsValidService(pRequestConfigRvia))
            {
                throw new ISUMException(ISUM_ERROR_CODE_EX, null, "Servicio no permitido", "El servicio solicitado de ISUM no está permitido para el perfil de este usuario.", null);
            }
            isError = TranslateJsonCache.isErrorCode(strCode, strDesc);
            if (isError.equals("00"))
            {
                response = Response.status(HttpStatus.SC_OK).entity(RESPONSE_OK).encoding(CharEncoding.UTF_8).build();
                pLog.info("El json no contiene código de error");
            }
            else
            {
                response = Response.status(HttpStatus.SC_OK).entity(RESPONSE_KO).encoding(CharEncoding.UTF_8).build();
                pLog.info("El jsoncontiene código de error");
            }
        }
        catch (Exception ex)
        {
            pLog.error("Error en comprobación de json", ex);
            pErrorCaptured = ErrorManager.getErrorResponseObject(ex);
            response = (Response.status(pErrorCaptured.getHttpCode()).entity(pErrorCaptured.getJsonError()).encoding(CharEncoding.UTF_8).build());
        }
        return response;
    }

    /**
     * Crea el objeto RequestConfigRvia validando la sesión contra ISUM (Recibe el token).
     * 
     * @param pRequest
     *            the request
     * @return RequestConfigRvia con todos los datos cargados del token
     * @throws Exception
     *             the exception
     */
    public static RequestConfigRvia getValidateSession(HttpServletRequest pRequest) throws Exception
    {
        RequestConfigRvia pRequestConfigRvia = null;
        // Se obtiene los datos asociados a la petición de ruralvia.
        pRequestConfigRvia = new RequestConfigRvia(pRequest);
        // Se establece el token de datos recibido desde ruralvia como dato de sesión.
        pSession.setAttribute("token", pRequestConfigRvia.getToken());
        // Se comprueba si el servicio de isum está permitido.
        if (!IsumValidation.IsValidService(pRequestConfigRvia))
        {
            throw new ISUMException(ISUM_ERROR_CODE_EX, null, "Servicio no permitido", "El servicio solicitado de ISUM no está permitido para le perfil de este usuario.", null);
        }
        return pRequestConfigRvia;
    }

    /**
     * Adjust WS json.
     * 
     * @param pJsonData
     *            the json data
     * @return the JSON object
     * @throws Exception
     *             the exception
     */
    private static JSONObject adjustWSJson(JSONObject pJsonData) throws Exception
    {
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
                pResponseObject = (JSONObject) pJsonData.getJSONObject(strPrimaryKey).getJSONObject("Respuesta");
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
}
