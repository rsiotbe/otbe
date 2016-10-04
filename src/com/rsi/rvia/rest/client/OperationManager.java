package com.rsi.rvia.rest.client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.isum.IsumValidation;
import com.rsi.rvia.rest.conector.RestConnector;
import com.rsi.rvia.rest.error.ErrorManager;
import com.rsi.rvia.rest.error.ErrorResponse;
import com.rsi.rvia.rest.error.exceptions.ISUMException;
import com.rsi.rvia.rest.error.exceptions.LogicalErrorException;
import com.rsi.rvia.rest.operation.MiqQuests;
import com.rsi.rvia.rest.session.SessionRviaData;
import com.rsi.rvia.rest.template.TemplateManager;
import com.rsi.rvia.rest.tool.Utils;

// import org.apache.http.impl.client.DefaultHttpClient;
/** Clase que gestiona cualquier petición que llega a la apliación RviaRest */
public class OperationManager
{
   private static final String ENCODING_UTF8      = "UTF-8";
   private static final int    ISUM_ERROR_CODE_EX = 401;
   private static final int    HTTP_CODE_OK       = 200;
   private static HttpSession  pSession;
   private static Logger       pLog               = LoggerFactory.getLogger(OperationManager.class);

   /**
    * Se procesa una petición que llega desde la antigua apliación de ruralvia
    * 
    * @param pRequest
    *           Objeto petición original
    * @param pUriInfo
    *           Uri asociada a la petición
    * @param strData
    *           Datos asociados a la petición
    * @param pMediaType
    *           Tipo de mediatype que debe cumplir la petición
    * @return Objeto respuesta de Jersey
    */
   public static Response processDataFromRvia(HttpServletRequest pRequest, UriInfo pUriInfo, String strData,
         MediaType pMediaType)
   {
      MiqQuests pMiqQuests = null;
      ErrorResponse pErrorCaptured = null;
      String strJsonData = "";
      Response pResponseConnector;
      SessionRviaData pSessionRviaData = null;
      pSession = pRequest.getSession(true);
      try
      {
         // Se obtiene los datos asociados a la petición de ruralvia y valida contra ISUM.
         pSessionRviaData = getValidateSession(pRequest);
         // Se obtienen los datos necesario para realizar la petición al proveedor.
         pMiqQuests = createMiqQuests(pUriInfo);
         // Se instancia el conector y se solicitan los datos.
         strJsonData = doRestConector(pUriInfo, pRequest, pSessionRviaData, pMiqQuests, strData);
         pLog.info("Respuesta correcta. Datos finales obtenidos: " + strJsonData);
      }
      catch (Exception ex)
      {
         pLog.error("Se captura un error. Se procede a evaluar que tipo de error es para generar la respuesta adecuada");
         pErrorCaptured = ErrorManager.getErrorResponseObject(ex);
      }
      try
      {
         // Se construye la respuesta ya sea error, o correcta, json o template.
         pResponseConnector = buildResponse(pErrorCaptured, pMediaType, pMiqQuests, strJsonData, pSessionRviaData);
      }
      catch (Exception ex)
      {
         pLog.error("Se ha generado un error al procesar la respuesta final", ex);
         pErrorCaptured = ErrorManager.getErrorResponseObject(ex);
         pResponseConnector = Response.serverError().encoding(ENCODING_UTF8).build();
      }
      pLog.trace("Se devuelve el objeto respuesta de la petición: " + pResponseConnector);
      return pResponseConnector;
   }

   /**
    * Se procesa una petición que solo pinta una página sin datos de una petición que llega desde la antigua apliación
    * de ruralvia
    * 
    * @param pRequest
    *           Objeto petición original
    * @param pUriInfo
    *           Uri asociada a la petición
    * @param strData
    *           Datos asociados a la petición
    * @param pMediaType
    *           Tipo de mediatype que debe cumplir la petición
    * @return Objeto respuesta de Jersey
    */
   public static Response processTemplateFromRvia(HttpServletRequest pRequest, UriInfo pUriInfo, String strJsonData)
   {
      MiqQuests pMiqQuests = null;
      ErrorResponse pErrorCaptured = null;
      Response pResponseConnector;
      SessionRviaData pSessionRviaData = null;
      pSession = pRequest.getSession(true);
      try
      {
         // Se obtiene los datos asociados a la petición de ruralvia y valida contra ISUM.
         pSessionRviaData = getValidateSession(pRequest);
         // Se obtienen los datos necesario para realizar la petición al proveedor.
         String strPrimaryPath = Utils.getPrimaryPath(pUriInfo);
         pLog.debug("Path en el que se recibne la petición: " + strPrimaryPath);
         pMiqQuests = MiqQuests.getMiqQuests(strPrimaryPath);
         pLog.debug("MiqQuest a procesar: " + pMiqQuests);
      }
      catch (Exception ex)
      {
         pLog.error("Se captura un error. Se procede a evaluar que tipo de error es para generar la respuesta adecuada");
         pErrorCaptured = ErrorManager.getErrorResponseObject(ex);
      }
      try
      {
         /* Se construye la respuesta ya sea error, o correcta, json o template */
         pResponseConnector = buildResponse(pErrorCaptured, MediaType.APPLICATION_XHTML_XML_TYPE, pMiqQuests, strJsonData, pSessionRviaData);
      }
      catch (Exception ex)
      {
         pLog.error("Se ha generado un error al procesar la respuesta final", ex);
         pErrorCaptured = ErrorManager.getErrorResponseObject(ex);
         pResponseConnector = Response.serverError().encoding(ENCODING_UTF8).build();
      }
      pLog.trace("Se devuelve el objeto respuesta de la petición: " + pResponseConnector);
      return pResponseConnector;
   }

   /**
    * Se procesa una petición para consumo http, la cual puede ser ajena a Ruralvía
    * 
    * @param pRequest
    *           Objeto petición original
    * @param pUriInfo
    *           Uri asociada a la petición
    * @param strData
    *           Datos asociados a la petición
    * @param pMediaType
    *           Tipo de mediatype que debe cumplir la petición
    * @return Objeto respuesta de Jersey
    * @throws JoseException
    * @throws IOException
    * @throws InvalidKeySpecException
    * @throws NoSuchAlgorithmException
    */
   public static Response processForAPI(HttpServletRequest pRequest, UriInfo pUriInfo, String strData,
         MediaType pMediaType) throws JoseException, IOException, NoSuchAlgorithmException, InvalidKeySpecException
   {
      ErrorResponse pErrorCaptured = null;
      RestConnector pRestConnector;
      String strJsonData = "";
      int nReturnHttpCode = 200;
      String strTemplate = "";
      Response pResponseConnector;
      SessionRviaData pSessionRviaData = null;
      String strPrimaryPath = "";
      String JWT = "";
      MiqQuests pMiqQuests;
      pSession = pRequest.getSession(true);
      try
      {
         // Se obtiene los datos asociados a la petición de ruralvia.
         // pSessionRviaData = new SessionRviaData(pRequest);
         // if (pSessionRviaData != null)
         // {
         // Se establece el token de datos recibido desde ruralvia como dato de sesión.
         // pSession.setAttribute("token", pSessionRviaData.getToken());
         // Se comprueba si el servicio de isum está permitido.
         // if (!IsumValidation.IsValidService(pSessionRviaData))
         // throw new ISUMException(401, null, "Servicio no permitido",
         // "El servicio solicitado de ISUM no está permitido para le perfil de este usuario.", null);
         // Se obtienen los datos necesario para realizar la petición al proveedor.
         strPrimaryPath = Utils.getPrimaryPath(pUriInfo);
         pMiqQuests = MiqQuests.getMiqQuests(strPrimaryPath);
         MultivaluedMap<String, String> pListParams = Utils.getParam4Path(pUriInfo);
         // Se instancia el conector y se solicitan los datos.
         pRestConnector = new RestConnector();
         // BEGIN: Gestión de login y token.
         // Cuando exista un login rest hay que cambiar todos esto.
         // Si estamos invocando a login tendremos los campos resueltos o el error
         if (strPrimaryPath.indexOf("/login") != -1)
         {
            // Si es login generamos JWT
            HashMap<String, String> claims;
            claims = doLogin();
            if (claims != null)
               JWT = ManageJWToken.generateJWT(claims);
            else
            {
               // Login fallido
               throw new LogicalErrorException(403, 9999, "Login failed", "Suministre credenciales válidas para iniciar sesión", new Exception());
            }
         }
         else
         {
            // Else verificamos JWT
            JWT = pRequest.getHeader("Authorization");
         }
         HashMap<String, String> pParamsToInject = ManageJWToken.validateJWT(JWT);
         if (pParamsToInject == null)
         {
            throw new LogicalErrorException(401, 9999, "Unauthorized", "Sesión no válida", new Exception());
         }
         // else{
         // Forzamos regeneración de token tras una verificación correcta.
         // JWT = ManageJWToken.generateJWT(pParamsToInject);
         // }
         // pRestConnector.setParamsToInject( pParamsToInject );
         /* END: Gestión de login y token */
         pResponseConnector = pRestConnector.getData(pRequest, strData, pSessionRviaData, pMiqQuests, pListParams, pParamsToInject);
         pLog.info("Respuesta recuperada del conector, se procede a procesar su contenido");
         /* se procesa el resultado del conector paa evaluar y adaptar su contenido */
         strJsonData = ResponseManager.processResponseConnector(pSessionRviaData, pRestConnector, pResponseConnector, pMiqQuests);
         pLog.info("Respuesta correcta. Datos finales obtenidos: " + strJsonData);
         /* se obtiene la plantilla destino si es que existe */
         strTemplate = pMiqQuests.getTemplate();
         // }
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
            strJsonData = TemplateManager.processTemplate(strTemplate, pSessionRviaData, strJsonData);
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

   private static HashMap<String, String> doLogin() throws JoseException, IOException
   {
      String strBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" "
            + "xmlns:ee=\"http://www.ruralserviciosinformaticos.com/empresa/EE_AutenticarUsuario/\">" + "<soap:Header>"
            + "<ee:RSISecCampo1>03054906</ee:RSISecCampo1>" + "<ee:RSISecCampo2>50456061H</ee:RSISecCampo2>"
            + "<ee:RSISecCampo3>20141217155327</ee:RSISecCampo3>" + "<ee:RSISecCampo4></ee:RSISecCampo4>"
            + "<ee:RSISecCampo5>0f0262740a3f50d9</ee:RSISecCampo5>" + "</soap:Header><soap:Body>"
            + "<ee:EE_I_AutenticarUsuario>" + "<ee:usuario>" + "03052445" + "</ee:usuario>" + "<ee:password>"
            + "03052445" + "</ee:password>" + "<ee:documento>" + "33334444S" + "</ee:documento>"
            + "</ee:EE_I_AutenticarUsuario>" + "</soap:Body>" + "</soap:Envelope>";
      /*
       * strBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
       * "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
       * "xmlns:ee=\"http://www.ruralserviciosinformaticos.com/empresa/EE_AutenticarUsuario/\">" + "<soap:Header>" +
       * "<ee:RSISecCampo1>03054906</ee:RSISecCampo1>" + "<ee:RSISecCampo2>50456061H</ee:RSISecCampo2>" +
       * "<ee:RSISecCampo3>20141217155327</ee:RSISecCampo3>" + "<ee:RSISecCampo4></ee:RSISecCampo4>" +
       * "<ee:RSISecCampo5>0f0262740a3f50d9</ee:RSISecCampo5>" + "</soap:Header><soap:Body>" +
       * "<ee:EE_I_AutenticarUsuario>" + "<ee:usuario>03052885</ee:usuario>" + "<ee:password>03052445</ee:password>" +
       * "<ee:documento>33334444S</ee:documento>" + "</ee:EE_I_AutenticarUsuario>" + "</soap:Body>" + "</soap:Envelope>"
       * ;
       */
      // Create a StringEntity for the SOAP XML.
      StringEntity stringEntity = new StringEntity(strBody, "UTF-8");
      stringEntity.setChunked(true);
      // Request parameters and other properties.
      HttpPost httpPost = new HttpPost("http://soa02.risa/SOA_Wallet/Empresa/PS/SE_WAL_AutenticarUsuario");
      httpPost.setEntity(stringEntity);
      httpPost.addHeader("Accept", "text/xml");
      httpPost.addHeader("SOAPAction", "");
      // Execute and get the response.
      HttpClient httpClient = new DefaultHttpClient();
      // HttpClient httpClient = new HttpClient();
      HttpResponse response = httpClient.execute(httpPost);
      HttpEntity entity = response.getEntity();
      String strResponse = null;
      if (entity != null)
      {
         strResponse = EntityUtils.toString(entity);
      }
      strResponse = strResponse.replace("\n", "");
      String codRetorno = strResponse.replaceAll("^.*<ee:codigoRetorno>([^<]*)</ee:codigoRetorno>.*$", "$1");
      if (Integer.parseInt(codRetorno) == 0)
      {
         // pLog.warning("->>>>>>>>>>>>>>>>>>>> Error en el servicio de login");
         return null;
      }
      else
      {
         HashMap<String, String> fields = new HashMap<String, String>();
         String codEntidad = strResponse.replaceAll("^.*<ee:entidad>([^<]*)</ee:entidad>.*$", "$1");
         String idInternoPe = strResponse.replaceAll("^.*<ee:idInternoPe>([^<]*)</ee:idInternoPe>.*$", "$1");
         String nTarjeta = strResponse.replaceAll("^.*<ee:numeroTarjeta>([^<]*)</ee:numeroTarjeta>.*$", "$1");
         fields.put("codEntidad", codEntidad.replace(" ", ""));
         fields.put("idInternoPe", idInternoPe.replace(" ", ""));
         fields.put("nTarjeta", nTarjeta.replace(" ", ""));
         /*
          * fields.put("codEntidad", entidad); fields.put("idInternoPe", idInternoPe); fields.put("nTarjeta", nTarjeta);
          */
         return fields;
      }
   }

   /**
    * Se procesa la petición sin validar sesion en ISUM para devolver un template.
    * 
    * @param pRequest
    *           Objeto petición original
    * @param pUriInfo
    *           Uri asociada a la petición
    * @param strData
    *           Datos asociados a la petición
    * @param pMediaType
    *           Tipo de mediatype que debe cumplir la petición
    * @return Objeto respuesta de Jersey
    */
   public static Response processTemplate(HttpServletRequest pRequest, UriInfo pUriInfo, String strJsonData)
   {
      MiqQuests pMiqQuests = null;
      ErrorResponse pErrorCaptured = null;
      Response pResponseConnector;
      SessionRviaData pSessionRviaData = null;
      pSession = pRequest.getSession(true);
      try
      {
         /*
          * Se crea el objeto SessionRviaData con los solo con los datos del lang y css para aplicarlos en el template
          */
         pSessionRviaData = getNoValidateSession(pRequest);
         /* se obtienen los datos necesario para realizar la petición al proveedor */
         String strPrimaryPath = Utils.getPrimaryPath(pUriInfo);
         pLog.debug("Path en el que se recibne la petición: " + strPrimaryPath);
         pMiqQuests = MiqQuests.getMiqQuests(strPrimaryPath);
         pLog.debug("MiqQuest a procesar: " + pMiqQuests);
      }
      catch (Exception ex)
      {
         pLog.error("Se captura un error. Se procede a evaluar que tipo de error es para generar la respuesta adecuada");
         pErrorCaptured = ErrorManager.getErrorResponseObject(ex);
      }
      try
      {
         /* Se construye la respuesta ya sea error, o correcta, json o template */
         pResponseConnector = buildResponse(pErrorCaptured, MediaType.APPLICATION_XHTML_XML_TYPE, pMiqQuests, strJsonData, pSessionRviaData);
      }
      catch (Exception ex)
      {
         pLog.error("Se ha generado un error al procesar la respuesta final", ex);
         pErrorCaptured = ErrorManager.getErrorResponseObject(ex);
         pResponseConnector = Response.serverError().encoding(ENCODING_UTF8).build();
      }
      pLog.trace("Se devuelve el objeto respuesta de la petición: " + pResponseConnector);
      return pResponseConnector;
   }

   /**
    * Se procesa la petición sin validar sesion en ISUM para devolver un template.
    * 
    * @param pRequest
    *           Objeto petición original
    * @param pUriInfo
    *           Uri asociada a la petición
    * @param strData
    *           Datos asociados a la petición
    * @param pMediaType
    *           Tipo de mediatype que debe cumplir la petición
    * @return Objeto respuesta de Jersey
    */
   public static Response processGenericAPP(HttpServletRequest pRequest, UriInfo pUriInfo, String strJsonData,
         MediaType pMediaType)
   {
      MiqQuests pMiqQuests = null;
      ErrorResponse pErrorCaptured = null;
      Response pResponseConnector;
      SessionRviaData pSessionRviaData = null;
      pSession = pRequest.getSession(true);
      try
      {
         /*
          * Se crea el objeto SessionRviaData con los solo con los datos del lang y css para aplicarlos en el template
          */
         pSessionRviaData = getNoValidateSession(pRequest);
         /* se obtienen los datos necesario para realizar la petición al proveedor */
         pMiqQuests = createMiqQuests(pUriInfo);
         /* se procesa el resultado del conector paa evaluar y adaptar su contenido */
         strJsonData = doRestConector(pUriInfo, pRequest, pSessionRviaData, pMiqQuests, strJsonData);
         pLog.info("Respuesta correcta. Datos finales obtenidos: " + strJsonData);
      }
      catch (Exception ex)
      {
         pLog.error("Se captura un error. Se procede a evaluar que tipo de error es para generar la respuesta adecuada");
         pErrorCaptured = ErrorManager.getErrorResponseObject(ex);
      }
      try
      {
         /* Se construye la respuesta ya sea error, o correcta, json o template */
         pResponseConnector = buildResponse(pErrorCaptured, pMediaType, pMiqQuests, strJsonData, pSessionRviaData);
      }
      catch (Exception ex)
      {
         pLog.error("Se ha generado un error al procesar la respuesta final", ex);
         pErrorCaptured = ErrorManager.getErrorResponseObject(ex);
         pResponseConnector = Response.serverError().encoding(ENCODING_UTF8).build();
      }
      pLog.trace("Se devuelve el objeto respuesta de la petición: " + pResponseConnector);
      return pResponseConnector;
   }

   /**
    * Último paso en el procesado de la respuesta, el resultado sera en forma de XHTML o JSON en funcion de el MediaType
    * que se le pase. Encapsula la respuesta en un objeto Response.
    * 
    * @param pErrorCaptured
    *           Capturador de errores previos al procesado del Template.
    * @param pMediaType
    *           MediaType del objeto respuesta.
    * @param strTemplate
    *           Ruta del template a aplicar. (Solo se aplicara con el mediatype de XHTML)
    * @param strJsonData
    *           Datos en formato JSON para añadir en el template o devolver directamente
    * @param pSessionRviaData
    *           Contiene información básica tanto del logueo como de la navegación (lang, id, etc)
    * @return Objeto Response con el resultado ya procesado
    * @throws Exception
    */
   private static Response buildResponse(ErrorResponse pErrorCaptured, MediaType pMediaType, MiqQuests pMiqQuests,
         String strJsonData, SessionRviaData pSessionRviaData) throws Exception
   {
      int nReturnHttpCode = HTTP_CODE_OK;
      String strTemplate = "";
      if (pMiqQuests != null)
      {
         /* se obtiene la plantilla destino si es que existe */
         strTemplate = pMiqQuests.getTemplate();
      }
      else
      {
         /* se inicializa si es necesario pErrorCaptured para que se genere un error controlado */
         if (pErrorCaptured == null)
         {
            pErrorCaptured = new ErrorResponse();
         }
      }
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
         strJsonData = TemplateManager.processTemplate(strTemplate, pSessionRviaData, strJsonData);
      }
      return (Response.status(nReturnHttpCode).entity(strJsonData).encoding(ENCODING_UTF8).build());
   }

   /**
    * Crea el objeto SessionRviaData validando la sesión contra ISUM (Recibe el token)
    * 
    * @param pRequest
    * @return SessionRviaData con todos los datos cargados del token
    * @throws Exception
    */
   private static SessionRviaData getValidateSession(HttpServletRequest pRequest) throws Exception
   {
      SessionRviaData pSessionRviaData = null;
      // Se obtiene los datos asociados a la petición de ruralvia.
      pSessionRviaData = new SessionRviaData(pRequest);
      // Se establece el token de datos recibido desde ruralvia como dato de sesión.
      pSession.setAttribute("token", pSessionRviaData.getToken());
      // Se comprueba si el servicio de isum está permitido.
      if (!IsumValidation.IsValidService(pSessionRviaData))
      {
         throw new ISUMException(ISUM_ERROR_CODE_EX, null, "Servicio no permitido", "El servicio solicitado de ISUM no está permitido para le perfil de este usuario.", null);
      }
      return pSessionRviaData;
   }

   /**
    * Crea el objeto SessionRviaData sin validando la sesión contra ISUM (no recibe el token)
    * 
    * @param pRequest
    * @return SessionRviaData solo con el lang y NRBE
    * @throws Exception
    */
   private static SessionRviaData getNoValidateSession(HttpServletRequest pRequest)
   {
      SessionRviaData pSessionRviaData = null;
      // Se recuperan los valores lang y NRBE por si vinieran por parametros.
      String strLang = (String) pRequest.getParameter("lang");
      String strNRBE = (String) pRequest.getParameter("NRBE");
      // Se inicializa SessionRviaData con los parametros minimos, por defecto pone lang a 'es_ES' y NRBE a '0198'.
      pSessionRviaData = new SessionRviaData(strLang, strNRBE);
      return pSessionRviaData;
   }

   /**
    * Crea el objeto miqQuests a raiz de un UriInfo, Si pUriInfo viene a null devuelve MiqQuests a null
    * 
    * @param pUriInfo
    * @return MiqQuests construido
    * @throws Exception
    */
   private static MiqQuests createMiqQuests(UriInfo pUriInfo) throws Exception
   {
      MiqQuests pMiqQuests = null;
      if (pUriInfo != null)
      {
         // Se obtienen los datos necesario para realizar la petición al proveedor.
         String strPrimaryPath = Utils.getPrimaryPath(pUriInfo);
         pLog.debug("Path en el que se recibne la petición: " + strPrimaryPath);
         pMiqQuests = MiqQuests.getMiqQuests(strPrimaryPath);
         pLog.debug("MiqQuest a procesar: " + pMiqQuests);
      }
      return pMiqQuests;
   }

   /**
    * Realiza una peteción al rest conector que devolvera los datos de un end point en formato JSON
    * 
    * @param pUriInfo
    *           Necesario para sacar los parametros del path
    * @param pRequest
    * @param pSessionRviaData
    * @param pMiqQuests
    * @param strJsonData
    * @return String en formato JSON con la información recuperada del endpoint
    * @throws Exception
    */
   private static String doRestConector(UriInfo pUriInfo, HttpServletRequest pRequest, SessionRviaData pSessionRviaData,
         MiqQuests pMiqQuests, String strJsonData) throws Exception
   {
      RestConnector pRestConnector = null;
      Response pResponseConnector = null;
      MultivaluedMap<String, String> pListParams = Utils.getParam4Path(pUriInfo);
      // Se instancia el conector y se solicitan los datos.
      pRestConnector = new RestConnector();
      pResponseConnector = pRestConnector.getData(pRequest, strJsonData, pSessionRviaData, pMiqQuests, pListParams, null);
      pLog.info("Respuesta recuperada del conector, se procede a procesar su contenido");
      // Se procesa el resultado del conector paa evaluar y adaptar su contenido.
      return (ResponseManager.processResponseConnector(pSessionRviaData, pRestConnector, pResponseConnector, pMiqQuests));
   }
}
