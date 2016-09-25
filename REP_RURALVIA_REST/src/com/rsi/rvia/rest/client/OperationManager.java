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
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.isum.IsumValidation;
import com.rsi.rvia.rest.conector.RestConnector;
import com.rsi.rvia.rest.error.ErrorManager;
import com.rsi.rvia.rest.error.ErrorResponse;
import com.rsi.rvia.rest.error.exceptions.ISUMException;
import com.rsi.rvia.rest.operation.MiqQuests;
import com.rsi.rvia.rest.session.SessionRviaData;
import com.rsi.rvia.rest.template.TemplateManager;
import com.rsi.rvia.rest.tool.Utils;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost; 
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


/** Clase que gestiona cualquier petición que llega a la apliación RviaRest */
public class OperationManager
{
	private static HttpSession	pSession;
	private static Logger		pLog	= LoggerFactory.getLogger(OperationManager.class);

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
	public static Response proccesDataFromRvia(HttpServletRequest pRequest, UriInfo pUriInfo, String strData,
			MediaType pMediaType)
	{
		MiqQuests pMiqQuests;
		ErrorResponse pErrorCaptured = null;
		RestConnector pRestConnector;
		String strJsonData = "";
		int nReturnHttpCode = 200;
		String strTemplate = "";
		Response pResponseConnector;
		SessionRviaData pSessionRviaData = null;
		pSession = pRequest.getSession(true);
		try
		{
			/* se obtiene los datos asociados a la petición de ruralvia */
			pSessionRviaData = new SessionRviaData(pRequest);
			/* se establece el token de datos recibido desde ruralvia como dato de sesión */
			pSession.setAttribute("token", pSessionRviaData.getToken());
			/* se comprueba si el servicio de isum está permitido */
			if (!IsumValidation.IsValidService(pSessionRviaData))
				throw new ISUMException(401, null, "Servicio no permitido", "El servicio solicitado de ISUM no está permitido para le perfil de este usuario.", null);
			/* se obtienen los datos necesario para realizar la petición al proveedor */
			String strPrimaryPath = Utils.getPrimaryPath(pUriInfo);
			pMiqQuests = MiqQuests.getMiqQuests(strPrimaryPath);
			pLog.debug("MiqQuest a procesar: " + pMiqQuests);
			MultivaluedMap<String, String> pListParams = Utils.getParam4Path(pUriInfo);
			/* se instancia el conector y se solicitan los datos */
			pRestConnector = new RestConnector();
			pResponseConnector = pRestConnector.getData(pRequest, strData, pSessionRviaData, pMiqQuests, pListParams, null);
			pLog.info("Respuesta recuperada del conector, se procede a procesar su contenido");
			/* se procesa el resultado del conector paa evaluar y adaptar su contenido */
			strJsonData = ResponseManager.processResponseConnector(pSessionRviaData, pRestConnector, pResponseConnector, pMiqQuests);
			pLog.info("Respuesta correcta. Datos finales obtenidos: " + strJsonData);
			/* se obtiene la plantilla destino si es que existe */
			strTemplate = pMiqQuests.getTemplate();
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
			pResponseConnector = Response.status(nReturnHttpCode).entity(strJsonData).encoding("UTF-8").build();
		}
		catch (Exception ex)
		{
			pLog.error("Se ha generado un error al procesar la respuesta final", ex);
			pErrorCaptured = ErrorManager.getErrorResponseObject(ex);
			pResponseConnector = Response.serverError().encoding("UTF-8").build();
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
		MiqQuests pMiqQuests;
		ErrorResponse pErrorCaptured = null;
		int nReturnHttpCode = 200;
		String strTemplate = "";
		Response pResponseConnector;
		SessionRviaData pSessionRviaData = null;
		pSession = pRequest.getSession(true);
		try
		{
			/* se obtiene los datos asociados a la petición de ruralvia */
			pSessionRviaData = new SessionRviaData(pRequest);
			/* se establece el token de datos recibido desde ruralvia como dato de sesión */
			pSession.setAttribute("token", pSessionRviaData.getToken());
			/* se comprueba si el servicio de isum está permitido */
			if (!IsumValidation.IsValidService(pSessionRviaData))
				throw new ISUMException(401, null, "Servicio no permitido", "El servicio solicitado de ISUM no está permitido para le perfil de este usuario.", null);
			/* se obtienen los datos necesario para realizar la petición al proveedor */
			String strPrimaryPath = Utils.getPrimaryPath(pUriInfo);
			pLog.debug("Path en el que se recibne la petición: " + strPrimaryPath);
			pMiqQuests = MiqQuests.getMiqQuests(strPrimaryPath);
			pLog.debug("MiqQuest a procesar: " + pMiqQuests);
			/* se obtiene la plantilla destino si es que existe */
			strTemplate = pMiqQuests.getTemplate();
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
				strTemplate = ErrorManager.ERROR_TEMPLATE;
				strJsonData = pErrorCaptured.getJsonError();
				nReturnHttpCode = pErrorCaptured.getHttpCode();
				pLog.info("Se obtiene el JSON de error, modifica la cabecera de retrono y la plantilla si es necesario");
			}
			pLog.info("La petición utiliza plantilla XHTML:" + strTemplate);
			strJsonData = TemplateManager.processTemplate(strTemplate, pSessionRviaData, strJsonData);
			pResponseConnector = Response.status(nReturnHttpCode).entity(strJsonData).encoding("UTF-8").build();
		}
		catch (Exception ex)
		{
			pLog.error("Se ha generado un error al procesar la respuesta final", ex);
			pErrorCaptured = ErrorManager.getErrorResponseObject(ex);
			pResponseConnector = Response.serverError().encoding("UTF-8").build();
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
	 */
	public static Response proccesForAPI(HttpServletRequest pRequest, UriInfo pUriInfo, String strData,
			MediaType pMediaType) throws JoseException, IOException, NoSuchAlgorithmException, InvalidKeySpecException
	{
		ErrorResponse pErrorCaptured = null;
		MiqQuests pMiqQuests;
		RestConnector pRestConnector;
		String strJsonData = "";
		int nReturnHttpCode = 200;
		String strTemplate = "";
		Response pResponseConnector;
		SessionRviaData pSessionRviaData = null;
		String strPrimaryPath = "";
		String JWT="";
		pSession = pRequest.getSession(true);
		try
		{
			/* se obtiene los datos asociados a la petición de ruralvia */
			// pSessionRviaData = new SessionRviaData(pRequest);
			// if (pSessionRviaData != null)
			// {
			/* se establece el token de datos recibido desde ruralvia como dato de sesión */
			// pSession.setAttribute("token", pSessionRviaData.getToken());
			/* se comprueba si el servicio de isum está permitido */
			// if (!IsumValidation.IsValidService(pSessionRviaData))
			// throw new ISUMException(401, null, "Servicio no permitido",
			// "El servicio solicitado de ISUM no está permitido para le perfil de este usuario.", null);
			/* se obtienen los datos necesario para realizar la petición al proveedor */
			String strPrimaryPath = Utils.getPrimaryPath(pUriInfo);
			pMiqQuests = MiqQuests.getMiqQuests(strPrimaryPath);
			MultivaluedMap<String, String> pListParams = Utils.getParam4Path(pUriInfo);
			/* se instancia el conector y se solicitan los datos */
			pRestConnector = new RestConnector();
			pResponseConnector = pRestConnector.getData(pRequest, strData, pSessionRviaData, pMiqQuests, pListParams);
				
				/* BEGIN: Gestión de login y token */
				// Cuando exista un login rest hay que cambiar todos esto.
				// Si estamos invocando a login tendremos los campos resueltos o el error
				if(strPrimaryPath.indexOf("/login") != -1){
				// Si es login generamos JWT	
					HashMap<String, String> claims;
					claims = doLogin();
					if(claims != null)					
						JWT = ManageJWToken.generateJWT(claims);
					else{
						// Login fallido
						throw new Exception();
					}
				}
				else {
				// Else verificamos JWT		
					JWT = pRequest.getHeader("token");							
				}
				HashMap<String, String> pParamsToInject = ManageJWToken.validateJWT(JWT);
				if(pParamsToInject == null){
					throw new Exception();
				}
				//else{
					// Forzamos regeneración de token tras una verificación correcta.
				//	JWT = ManageJWToken.generateJWT(pParamsToInject);
				//}
				//pRestConnector.setParamsToInject( pParamsToInject );	
				/* END: Gestión de login y token */
	

				pResponseConnector = pRestConnector.getData(pRequest, strData, pSessionRviaData, strPrimaryPath, pListParams, pParamsToInject);
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
			pResponseConnector = Response.status(nReturnHttpCode).encoding("UTF-8").entity(strJsonData).build();
		}
		catch (Exception ex)
		{
			pLog.error("Se ha generado un error al procesar la respuesta final", ex);
			pErrorCaptured = ErrorManager.getErrorResponseObject(ex);
			pResponseConnector = Response.serverError().encoding("UTF-8").build();
		}
		
	// Insertar siempre JWT en el response		
		return pResponseConnector;
	}
	private static HashMap<String, String> doLogin () throws JoseException, IOException{		
		HashMap<String, String> fields = new HashMap<String, String>();
		String strBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
				"xmlns:ee=\"http://www.ruralserviciosinformaticos.com/empresa/EE_AutenticarUsuario/\">" +
				"<soap:Header>" +
				"<ee:RSISecCampo1>03054906</ee:RSISecCampo1>" +
				"<ee:RSISecCampo2>50456061H</ee:RSISecCampo2>" +
				"<ee:RSISecCampo3>20141217155327</ee:RSISecCampo3>" +
				"<ee:RSISecCampo4></ee:RSISecCampo4>" +
				"<ee:RSISecCampo5>0f0262740a3f50d9</ee:RSISecCampo5>" +
				"</soap:Header><soap:Body>" +
				"<ee:EE_I_AutenticarUsuario>" +
				"<ee:usuario>" + "03052445" + "</ee:usuario>" +
				"<ee:password>" + "03052445" + "</ee:password>" +
				"<ee:documento>" + "33334444S" + "</ee:documento>" +
				"</ee:EE_I_AutenticarUsuario>" +
				"</soap:Body>" +
				"</soap:Envelope>" ;
		
/*
		strBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
				"xmlns:ee=\"http://www.ruralserviciosinformaticos.com/empresa/EE_AutenticarUsuario/\">" +
				"<soap:Header>" +
				"<ee:RSISecCampo1>03054906</ee:RSISecCampo1>" +
				"<ee:RSISecCampo2>50456061H</ee:RSISecCampo2>" +
				"<ee:RSISecCampo3>20141217155327</ee:RSISecCampo3>" +
				"<ee:RSISecCampo4></ee:RSISecCampo4>" +
				"<ee:RSISecCampo5>0f0262740a3f50d9</ee:RSISecCampo5>" +
				"</soap:Header><soap:Body>" +
				"<ee:EE_I_AutenticarUsuario>" +
				"<ee:usuario>03052885</ee:usuario>" +
				"<ee:password>03052445</ee:password>" +
				"<ee:documento>33334444S</ee:documento>" +
				"</ee:EE_I_AutenticarUsuario>" +
				"</soap:Body>" +
				"</soap:Envelope>" ;		
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
	    //HttpClient httpClient = new HttpClient();
	    HttpResponse response = httpClient.execute(httpPost);
	    HttpEntity entity = response.getEntity();
	    }		
	    strResponse = strResponse.replace("\n","");
	    String codRetorno = strResponse.replaceAll("^.*<ee:codigoRetorno>([^<]*)</ee:codigoRetorno>.*$","$1");
	    if(Integer.parseInt(codRetorno) == 0){
	   	 //pLog.warning("->>>>>>>>>>>>>>>>>>>> Error en el servicio de login");
	   	 return null;
	    }
	    else{
		    String codEntidad = strResponse.replaceAll("^.*<ee:entidad>([^<]*)</ee:entidad>.*$","$1");		    
		    String idInternoPe = strResponse.replaceAll("^.*<ee:idInternoPe>([^<]*)</ee:idInternoPe>.*$","$1");
		    String nTarjeta = strResponse.replaceAll("^.*<ee:numeroTarjeta>([^<]*)</ee:numeroTarjeta>.*$","$1");
			fields.put("codEntidad", codEntidad.replace(" ", ""));
			fields.put("idInternoPe", idInternoPe.replace(" ", ""));
			fields.put("nTarjeta", nTarjeta.replace(" ", ""));
