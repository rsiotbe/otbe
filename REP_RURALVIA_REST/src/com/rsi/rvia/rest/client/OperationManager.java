package com.rsi.rvia.rest.client;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
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
import com.rsi.rvia.translates.TranslateProcessor;

/** Clase que gestiona cualquier petición que llega a la apliación RviaRest */
public class OperationManager
{
	private static HttpSession	pSession;
	private static Logger		pLog	= LoggerFactory.getLogger(TranslateProcessor.class);

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
			MultivaluedMap<String, String> pListParams = Utils.getParam4Path(pUriInfo);
			/* se instancia el conector y se solicitan los datos */
			pRestConnector = new RestConnector();
			pResponseConnector = pRestConnector.getData(pRequest, strData, pSessionRviaData, pMiqQuests, pListParams);
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
		return pResponseConnector;
	}

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
	public static Response processTemplateFromRvia(HttpServletRequest pRequest, UriInfo pUriInfo, String strData)
	{
		MiqQuests pMiqQuests;
		ErrorResponse pErrorCaptured = null;
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
			pLog.info("La petición utiliza plantilla XHTML");
			strJsonData = TemplateManager.processTemplate(strTemplate, pSessionRviaData, strJsonData);
			pResponseConnector = Response.status(nReturnHttpCode).entity(strJsonData).encoding("UTF-8").build();
		}
		catch (Exception ex)
		{
			pLog.error("Se ha generado un error al procesar la respuesta final", ex);
			pErrorCaptured = ErrorManager.getErrorResponseObject(ex);
			pResponseConnector = Response.serverError().encoding("UTF-8").build();
		}
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
			MediaType pMediaType)
	{
		ErrorResponse pErrorCaptured = null;
		MiqQuests pMiqQuests;
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
		return pResponseConnector;
	}
}
