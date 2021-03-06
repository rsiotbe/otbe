package com.rsi.rvia.rest.client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.Constants;
import com.rsi.isum.IsumValidation;
import com.rsi.rvia.rest.conector.RestConnector;
import com.rsi.rvia.rest.error.ErrorManager;
import com.rsi.rvia.rest.error.ErrorResponse;
import com.rsi.rvia.rest.error.exceptions.ApplicationException;
import com.rsi.rvia.rest.error.exceptions.ISUMException;
import com.rsi.rvia.rest.operation.MiqQuests;
import com.rsi.rvia.rest.response.RviaRestResponse;
import com.rsi.rvia.rest.security.IdentityProvider;
import com.rsi.rvia.rest.security.IdentityProviderFactory;
import com.rsi.rvia.rest.security.IdentityProviderRVIASession;
import com.rsi.rvia.rest.security.IdentityProviderTRUSTED;
import com.rsi.rvia.rest.session.RequestConfig;
import com.rsi.rvia.rest.session.RequestConfigRvia;
import com.rsi.rvia.rest.template.TemplateManager;
import com.rsi.rvia.rest.tool.AppConfiguration;
import com.rsi.rvia.rest.tool.ServiceHelper;
import com.rsi.rvia.rest.tool.Utils;

// import org.apache.http.impl.client.DefaultHttpClient;
/** Clase que gestiona cualquier petición que llega a la apliación RviaRest */
public class OperationManager
{
    private static final String ENCODING_UTF8      = "UTF-8";
    private static final int    ISUM_ERROR_CODE_EX = 401;
    private static final int    HTTP_CODE_OK       = 200;
    private static Logger       pLog               = LoggerFactory.getLogger(OperationManager.class);

    /**
     * Se procesa una petición que llega desde la antigua apliación de ruralvia
     * 
     * @param pRequest
     *            Objeto petición original
     * @param pUriInfo
     *            Uri asociada a la petición
     * @param strData
     *            Datos asociados a la petición
     * @param pMediaType
     *            Tipo de mediatype que debe cumplir la petición
     * @return Objeto respuesta de Jersey
     */
    public static Response processDataFromRvia(HttpServletRequest pRequest, UriInfo pUriInfo, String strData,
            MediaType pMediaType)
    {
        MiqQuests pMiqQuests = null;
        ErrorResponse pErrorCaptured = null;
        Response pResponseConnector;
        RviaRestResponse pRviaRestResponse = null;
        RequestConfigRvia pRequestConfigRvia = null;
        IdentityProvider pIdentityProvider = null;
        try
        {
            // Se obtienen los datos necesario para realizar la petición al proveedor.
            pMiqQuests = MiqQuests.getMiqQuests(pUriInfo);
            /* se comprueba la validaez de la petición */
            pIdentityProvider = IdentityProviderFactory.getIdentityProvider(pRequest, pMiqQuests);
            pIdentityProvider.process();
            pRequestConfigRvia = ((IdentityProviderRVIASession) pIdentityProvider).getRequestConfigRvia();
            // se comrpeuba el permiso de acceso de isum para esta petición */
            checkIsumPermission(pRequestConfigRvia);
            // Se instancia el conector y se solicitan los datos.
            pRviaRestResponse = doRestConector(pUriInfo, pRequest, pRequestConfigRvia, pMiqQuests, strData);
            pLog.info("Respuesta correcta. Datos finales obtenidos: " + pRviaRestResponse.toJsonString());
        }
        catch (Exception ex)
        {
            pLog.error("Se captura un error. Se procede a evaluar que tipo de error es para generar la respuesta adecuada");
            pErrorCaptured = ErrorManager.getErrorResponseObject(ex);
        }
        try
        {
            // Se construye la respuesta ya sea error, o correcta, json o template.
            pResponseConnector = buildResponse(pErrorCaptured, pMediaType, pMiqQuests, pRviaRestResponse, pRequestConfigRvia, pIdentityProvider);
        }
        catch (Exception ex)
        {
            pLog.error("Se ha generado un error al procesar la respuesta final", ex);
            pErrorCaptured = ErrorManager.getErrorResponseObject(ex);
            if (pIdentityProvider == null)
            {
                pIdentityProvider = new IdentityProviderTRUSTED(pRequest, pMiqQuests);
            }
            pResponseConnector = Response.serverError().encoding(ENCODING_UTF8).header("Authorization", pIdentityProvider.getJWT()).build();
        }
        pLog.trace("Se devuelve el objeto respuesta de la petición: " + pResponseConnector);
        return pResponseConnector;
    }

    /**
     * Se procesa una petición que llega desde la antigua apliación de ruralvia
     * 
     * @param pRequest
     *            Objeto petición original
     * @param pUriInfo
     *            Uri asociada a la petición
     * @param strData
     *            Datos asociados a la petición
     * @param pMediaType
     *            Tipo de mediatype que debe cumplir la petición
     * @return Objeto respuesta de Jersey
     */
    public static Response processDownload(HttpServletRequest pRequest, UriInfo pUriInfo, String strData)
    {
        MiqQuests pMiqQuests = null;
        Response pResponseConnector = null;
        RequestConfigRvia pRequestConfigRvia = null;
        IdentityProvider pIdentityProvider = null;
        try
        {
            // Se obtienen los datos necesario para realizar la petición al proveedor.
            pMiqQuests = MiqQuests.getMiqQuests(pUriInfo);
            /* se comprueba la validaez de la petición */
            pIdentityProvider = IdentityProviderFactory.getIdentityProvider(pRequest, pMiqQuests);
            pIdentityProvider.process();
            if (pMiqQuests.getComponentType() == MiqQuests.CompomentType.RVIA)
            {
                pRequestConfigRvia = ((IdentityProviderRVIASession) pIdentityProvider).getRequestConfigRvia();
                // se comrpeuba el permiso de acceso de isum para esta petición */
                checkIsumPermission(pRequestConfigRvia);
                // Se instancia el conector y se solicitan los datos.
                pResponseConnector = doDownloadConector(pUriInfo, pRequest, pRequestConfigRvia, pMiqQuests, strData);
                pLog.info("Respuesta correcta desde el servidor de rvia");
            }
            /* se genera una nueva respuesta con el contenido descargado */
            Object objFile = pResponseConnector.getEntity();
            ResponseBuilder responseBuilder = Response.ok(objFile);
            String strFileName = pRequest.getParameter("filename");
            if (strFileName == null)
            {
                pLog.info("No existe nombre de la descarga definido desde parámetro, se intenta conseguir del origen");
                try
                {
                    strFileName = pResponseConnector.getHeaderString("Content-disposition").split("filename=")[1].trim().replace(";", "");
                    pLog.info("Nombre leido:" + strFileName);
                }
                catch (Exception ex)
                {
                    pLog.warn("Error al recuperar el nombre desde la petición original", ex);
                    strFileName = "descarga";
                    try
                    {
                        strFileName += pResponseConnector.getHeaderString("Content-type").split("/")[1];
                    }
                    catch (Exception ex2)
                    {
                        pLog.warn("Error al recuperar la extensión de fichero descargado", ex2);
                    }
                    pLog.info("Imposible lerr el nombre, se asigna uno por defecto: " + strFileName);
                }
            }
            responseBuilder.header("Content-Disposition", "attachment; filename=\"" + strFileName + "\"");
            return responseBuilder.build();
        }
        catch (Exception ex)
        {
            pLog.error("Se ha generado un error al procesar la respuesta final", ex);
        }
        return pResponseConnector;
    }

    private static Response doDownloadConector(UriInfo pUriInfo, HttpServletRequest pRequest,
            RequestConfig pRequestConfig, MiqQuests pMiqQuests, String strData) throws Exception
    {
        RestConnector pRestConnector = null;
        Response pResponseConnector = null;
        MultivaluedMap<String, String> pListParams = Utils.getParamByPath(pUriInfo);
        MultivaluedMap<String, String> pAllParams = new MultivaluedHashMap<String, String>();
        pAllParams.putAll(pListParams);
        // Se instancia el conector y se solicitan los datos.
        pRestConnector = new RestConnector();
        pResponseConnector = pRestConnector.getData(pRequest, strData, pRequestConfig, pMiqQuests, pAllParams, null);
        pLog.info("Respuesta recuperada del conector, se procede a devolver al cliente");
        return pResponseConnector;
    }

    /**
     * Se procesa una petición para consumo http, la cual puede ser ajena a Ruralvía
     * 
     * @param pRequest
     *            Objeto petición original
     * @param pUriInfo
     *            Uri asociada a la petición
     * @param strData
     *            Datos asociados a la petición
     * @param pMediaType
     *            Tipo de mediatype que debe cumplir la petición
     * @return Objeto respuesta de Jersey
     * @throws Exception
     * @throws JoseException
     * @throws IOException
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    public static Response processForAPI(HttpServletRequest pRequest, UriInfo pUriInfo, String strData,
            MediaType pMediaType)
    {
        ErrorResponse pErrorCaptured = null;
        RestConnector pRestConnector;
        String strJsonData = "";
        int nReturnHttpCode = 200;
        String strTemplate = "";
        Response pResponseConnector;
        String strPrimaryPath = "";
        String JWT = "";
        MiqQuests pMiqQuests = null;
        try
        {
            strPrimaryPath = Utils.getPrimaryPath(pUriInfo);
            // Si existe el parámetro help, invocamos a la ayuda y escapamos
            if (pRequest.getParameter("help") != null)
            {
                // JSONObject jsonHelp = ServiceHelperL.getHelp(strPrimaryPath);
                String strJsonHelp = ServiceHelper.getHelp(strPrimaryPath);
                pLog.info("Invocando proceso help para " + strPrimaryPath);
                return Response.ok(strJsonHelp).build();
            }
            pMiqQuests = MiqQuests.getMiqQuests(strPrimaryPath);
            MultivaluedMap<String, String> pListParams = Utils.getParamByPath(pUriInfo);
            // Se instancia el conector y se solicitan los datos.
            pRestConnector = new RestConnector();
            // BEGIN: Gestión de login y token.
            // Si los servicios que se invocan son de rsiapi
            IdentityProvider pIdentityProvider = IdentityProviderFactory.getIdentityProvider(pRequest, pMiqQuests);
            pIdentityProvider.process();
            // Si estamos invocando a login tendremos los campos resueltos o el error
            JWT = pIdentityProvider.getJWT();
            HashMap<String, String> pParamsToInject = pIdentityProvider.getClaims();
            pResponseConnector = pRestConnector.getData(pRequest, strData, null, pMiqQuests, pListParams, pParamsToInject);
            int nHttpCode = pResponseConnector.getStatus();
            if (nHttpCode != 200)
            {
                throw new ApplicationException(nHttpCode, 99999, "Respuesta errónea desde end point", "No se ha podido recuperar la información de la operación", new Exception());
            }
            strJsonData = pResponseConnector.readEntity(String.class);
            pLog.info("Respuesta correcta. Datos finales obtenidos: " + strJsonData);
            try
            {
                SaveExitHierarchy.process(strJsonData, pMiqQuests.getIdMiq(), pRestConnector.getMethod());
            }
            catch (Exception ex)
            {
                pLog.error("Error al grabar la jerarquía de salida.");
            }
        }
        catch (Exception ex)
        {
            pLog.error("Se captura un error. Se procede a evaluar que tipo de error es para generar la respuesta adecuada");
            pErrorCaptured = ErrorManager.getErrorResponseObject(ex);
        }
        try
        {
            /* Se comprueba si ha habido algun error para generar la respuesta adecuada */
            if (pErrorCaptured != null)
            {
                pLog.info("Se procede a gestionar el error");
                /* si la apliación debe responder un XHTML */
                if (pMediaType == MediaType.APPLICATION_XHTML_XML_TYPE)
                    strTemplate = ErrorManager.ERROR_TEMPLATE;
                strJsonData = pErrorCaptured.getJsonError();
                nReturnHttpCode = pErrorCaptured.getHttpCode();
                pLog.info("Se obtiene el JSON de error, modifica la cabecera de retrono y la plantilla si es necesario");
            }
            if (pMediaType == MediaType.APPLICATION_XHTML_XML_TYPE)
            {
                pLog.info("La petición utiliza plantilla XHTML");
                strJsonData = TemplateManager.processTemplate(pMiqQuests, strTemplate, null, strJsonData);
            }
            pResponseConnector = Response.status(nReturnHttpCode).entity(strJsonData).header("Authorization", JWT).build();
        }
        catch (Exception ex)
        {
            pLog.error("Se ha generado un error al procesar la respuesta final", ex);
            pErrorCaptured = ErrorManager.getErrorResponseObject(ex);
            pResponseConnector = Response.serverError().header("Authorization", JWT).build();
        }
        // Insertar siempre JWT en el response
        return pResponseConnector;
    }

    /**
     * Se procesa la petición sin validar sesion en ISUM para devolver un template.
     * 
     * @param pRequest
     *            Objeto petición original
     * @param pUriInfo
     *            Uri asociada a la petición
     * @param strData
     *            Datos asociados a la petición
     * @param pMediaType
     *            Tipo de mediatype que debe cumplir la petición
     * @return Objeto respuesta de Jersey
     */
    public static Response processGenericAPP(HttpServletRequest pRequest, UriInfo pUriInfo, String strJsonData,
            MediaType pMediaType)
    {
        MiqQuests pMiqQuests = null;
        ErrorResponse pErrorCaptured = null;
        Response pResponseConnector;
        RequestConfig pRequestConfig = null;
        RviaRestResponse pRviaRestResponse = null;
        IdentityProvider pIdentityProvider = null;
        try
        {
            /*
             * Se crea el objeto RequestConfig con los solo con los datos del lang y css para aplicarlos en el template
             */
            pRequestConfig = RequestConfig.getRequestConfig(pRequest, strJsonData);
            /* se obtienen los datos necesario para realizar la petición al proveedor */
            pMiqQuests = MiqQuests.getMiqQuests(pUriInfo);
            pIdentityProvider = IdentityProviderFactory.getIdentityProvider(pRequest, pMiqQuests);
            pIdentityProvider.process();
            /* se procesa el resultado del conector paa evaluar y adaptar su contenido */
            pRviaRestResponse = doRestConector(pUriInfo, pRequest, pRequestConfig, pMiqQuests, strJsonData);
            pLog.info("Respuesta correcta. Datos finales obtenidos: " + pRviaRestResponse.toJsonString());
        }
        catch (Exception ex)
        {
            pLog.error("Se captura un error. Se procede a evaluar que tipo de error es para generar la respuesta adecuada", ex);
            pErrorCaptured = ErrorManager.getErrorResponseObject(ex);
        }
        try
        {
            pResponseConnector = buildResponse(pErrorCaptured, pMediaType, pMiqQuests, pRviaRestResponse, pRequestConfig, pIdentityProvider);
        }
        catch (Exception ex)
        {
            pLog.error("Se ha generado un error al procesar la respuesta final", ex);
            pErrorCaptured = ErrorManager.getErrorResponseObject(ex);
            /* Se construye la respuesta ya sea error, o correcta, json o template */
            if (pIdentityProvider == null)
            {
                pIdentityProvider = new IdentityProviderTRUSTED(pRequest, pMiqQuests);
            }
            pResponseConnector = Response.serverError().encoding(ENCODING_UTF8).header("Authorization", pIdentityProvider.getJWT()).build();
        }
        pLog.trace("Se devuelve el objeto respuesta de la petición: " + pResponseConnector);
        return pResponseConnector;
    }

    /**
     * Se procesa la petición sin validar sesion en ISUM para devolver un template.
     * 
     * @param pRequest
     *            Objeto petición original
     * @param pUriInfo
     *            Uri asociada a la petición
     * @param fValidateTokenRvia
     *            Indica si lapetición viene de ruralvia para validar los datos de sesión y token
     * @return Objeto respuesta de Jersey
     */
    public static Response processTemplate(HttpServletRequest pRequest, UriInfo pUriInfo, boolean fValidateTokenRvia)
    {
        MiqQuests pMiqQuests = null;
        ErrorResponse pErrorCaptured = null;
        Response pResponseConnector;
        RequestConfig pRequestConfig = null;
        IdentityProvider pIdentityProvider = null;
        try
        {
            pMiqQuests = MiqQuests.getMiqQuests(pUriInfo);
            pLog.debug("MiqQuest a procesar para obtener el template: " + pMiqQuests);
            pIdentityProvider = IdentityProviderFactory.getIdentityProvider(pRequest, pMiqQuests);
            pIdentityProvider.process();
            /* se obtienen los datos necesarios para realizar la petición al proveedor */
            if (fValidateTokenRvia)
            {
                RequestConfigRvia pRequestConfigRvia = ((IdentityProviderRVIASession) pIdentityProvider).getRequestConfigRvia();
                checkIsumPermission(pRequestConfigRvia);
                pRequestConfig = pRequestConfigRvia;
            }
            else
            {
                pRequestConfig = RequestConfig.getRequestConfig(pRequest, null);
            }
        }
        catch (Exception ex)
        {
            pLog.error("Se captura un error. Se procede a evaluar que tipo de error es para generar la respuesta adecuada");
            pErrorCaptured = ErrorManager.getErrorResponseObject(ex);
        }
        try
        {
            /* Se construye la respuesta ya sea error, o correcta, json o template */
            pResponseConnector = buildResponse(pErrorCaptured, MediaType.APPLICATION_XHTML_XML_TYPE, pMiqQuests, null, pRequestConfig, pIdentityProvider);
        }
        catch (Exception ex)
        {
            pLog.error("Se ha generado un error al procesar la respuesta final", ex);
            pErrorCaptured = ErrorManager.getErrorResponseObject(ex);
            /* Se construye la respuesta ya sea error, o correcta, json o template */
            if (pIdentityProvider == null)
            {
                pIdentityProvider = new IdentityProviderTRUSTED(pRequest, pMiqQuests);
            }
            pResponseConnector = Response.serverError().encoding(ENCODING_UTF8).header("Authorization", pIdentityProvider.getJWT()).build();
        }
        pLog.trace("Se devuelve el objeto respuesta de la petición: " + pResponseConnector);
        return pResponseConnector;
    }

    /**
     * Devuelve el MediaType de la request o establece por defecto MediaType.TEXT_HTML_TYPE.
     * 
     * @param pRequest
     * @return MediaType
     */
    public static MediaType getMediaType(HttpServletRequest pRequest)
    {
        MediaType mediaType = MediaType.TEXT_HTML_TYPE;
        String headerType = pRequest.getHeader("content-type");
        if (headerType != null)
        {
            mediaType = MediaType.valueOf(headerType);
        }
        return mediaType;
    }

    /**
     * Último paso en el procesado de la respuesta, el resultado sera en forma de XHTML o JSON en funcion de el
     * MediaType que se le pase. Encapsula la respuesta en un objeto Response.
     * 
     * @param pErrorCaptured
     *            Capturador de errores previos al procesado del Template.
     * @param pMediaType
     *            MediaType del objeto respuesta.
     * @param strTemplate
     *            Ruta del template a aplicar. (Solo se aplicara con el mediatype de XHTML)
     * @param strJsonData
     *            Datos en formato JSON para añadir en el template o devolver directamente
     * @param pRequestConfig
     *            Contiene información básica tanto del logueo como de la navegación (lang, id, etc)
     * @return Objeto Response con el resultado ya procesado
     * @throws Exception
     */
    private static Response buildResponse(ErrorResponse pErrorCaptured, MediaType pMediaType, MiqQuests pMiqQuests,
            RviaRestResponse pRviaRestResponse, RequestConfig pRequestConfig, IdentityProvider pIdentityProvider)
            throws Exception
    {
        int nReturnHttpCode = HTTP_CODE_OK;
        String strTemplate = "";
        String strJsonData = "{}";
        /* Se comprueba si ha habido algun error para generar la respuesta adecuada */
        if (pErrorCaptured != null)
        {
            pLog.info("Se procede a gestionar el error");
            /* si la aplicación debe responder un XHTML */
            if (pMediaType == MediaType.APPLICATION_XHTML_XML_TYPE || pMediaType == MediaType.TEXT_HTML_TYPE)
                strTemplate = ErrorManager.ERROR_TEMPLATE;
            strJsonData = pErrorCaptured.getJsonError();
            nReturnHttpCode = pErrorCaptured.getHttpCode();
            pLog.info("Se obtiene el JSON de error, modifica la cabecera de retrono y la plantilla si es necesario");
        }
        else
        {
            /* se obtiene la plantilla destino si es que existe */
            strTemplate = pMiqQuests.getTemplate();
            if (pRviaRestResponse != null)
            {
                strJsonData = pRviaRestResponse.toJsonString();
                nReturnHttpCode = pRviaRestResponse.getHttpCode();
            }
        }
        if (pMediaType == MediaType.APPLICATION_XHTML_XML_TYPE || pMediaType == MediaType.TEXT_HTML_TYPE)
        {
            pLog.info("La petición utiliza plantilla XHTML o HTML");
            strJsonData = TemplateManager.processTemplate(pMiqQuests, strTemplate, pRequestConfig, strJsonData);
        }
        return (Response.status(nReturnHttpCode).entity(strJsonData).encoding(ENCODING_UTF8).header("Authorization", pIdentityProvider.getJWT()).build());
    }

    /**
     * Crea el objeto RequestConfigRvia validando la sesión contra ISUM (Recibe el token)
     * 
     * @param pRequest
     * @return RequestConfigRvia con todos los datos cargados del token
     * @throws Exception
     */
    public static void checkIsumPermission(RequestConfigRvia pRequestConfigRvia) throws Exception
    {
        // Se comprueba si el servicio de isum está permitido.
        if (!IsumValidation.IsValidService(pRequestConfigRvia))
        {
            throw new ISUMException(ISUM_ERROR_CODE_EX, null, "Servicio no permitido", "El servicio solicitado de ISUM no está permitido para le perfil de este usuario.", null);
        }
    }

    /**
     * Realiza una petición al rest conector que devolvera los datos de un end point en formato JSON
     * 
     * @param pUriInfo
     *            Necesario para sacar los parametros del path
     * @param pRequest
     * @param pRequestConfig
     * @param pMiqQuests
     * @param strJsonData
     * @return String en formato JSON con la información recuperada del endpoint
     * @throws Exception
     */
    private static RviaRestResponse doRestConector(UriInfo pUriInfo, HttpServletRequest pRequest,
            RequestConfig pRequestConfig, MiqQuests pMiqQuests, String strJsonData) throws Exception
    {
        RestConnector pRestConnector = null;
        Response pResponseConnector = null;
        MultivaluedMap<String, String> pListParams = Utils.getParamByPath(pUriInfo);
        MultivaluedMap<String, String> pAllParams = new MultivaluedHashMap<String, String>();
        pAllParams.putAll(pListParams);
        // Se instancia el conector y se solicitan los datos.
        pRestConnector = new RestConnector();
        pResponseConnector = pRestConnector.getData(pRequest, strJsonData, pRequestConfig, pMiqQuests, pAllParams, null);
        pLog.info("Respuesta recuperada del conector, se procede a procesar su contenido");
        // Se procesa el resultado del conector paa evaluar y adaptar su contenido.
        RviaRestResponse pRespuesta = ResponseManager.processResponseConnector(pRequestConfig, pRestConnector, pResponseConnector, pMiqQuests);
        String entorno = AppConfiguration.getInstance().getProperty(Constants.ENVIRONMENT);
        if (Constants.Environment.TEST.name().equals(entorno))
        {
            // Utils.writeMock(pRequest, pUriInfo, pMiqQuests, strRespuesta);
        }
        return pRespuesta;
    }
}
