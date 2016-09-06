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
import com.rsi.rvia.rest.error.ErrorManager;
import com.rsi.rvia.rest.error.exceptions.ISUMException;
import com.rsi.rvia.rest.error.exceptions.RVIAException;
import com.rsi.rvia.rest.error.exceptions.RviaRestException;
import com.rsi.rvia.rest.error.exceptions.WSException;
import com.rsi.rvia.rest.session.SessionRviaData;
import com.rsi.rvia.rest.template.TemplateManager;
import com.rsi.rvia.rest.tool.Utils;
import com.rsi.rvia.translates.TranslateProcessor;

public class OperationManager
{
	private static HttpSession	pSession;
	private static Logger		pLog	= LoggerFactory.getLogger(TranslateProcessor.class);

	public static Response proccesFromRvia(HttpServletRequest pRequest, UriInfo pUriInfo, String strData,
			MediaType pMediaType)
	{
		RestWSConnector pRestConnector;
		String strEntity = "";
		int nStatusCode = 200;
		String strTemplate = "";
		Response pReturn = null;
		SessionRviaData pSessionRviaData = null;
		pSession = pRequest.getSession(true);
		try
		{
			pSessionRviaData = new SessionRviaData(pRequest);
			if (pSessionRviaData != null)
			{
				pSession.setAttribute("token", pSessionRviaData.getToken());
				if (!IsumValidation.IsValidService(pSessionRviaData))
					throw new ISUMException(401);
				String strPrimaryPath = Utils.getPrimaryPath(pUriInfo);
				MultivaluedMap<String, String> pListParams = Utils.getParam4Path(pUriInfo);
				pRestConnector = new RestWSConnector();
				pReturn = pRestConnector.getData(pRequest, strData, pSessionRviaData, strPrimaryPath, pListParams);
				nStatusCode = pReturn.getStatus();
				strTemplate = pRestConnector.getMiqQuests().getTemplate();
				strEntity = pReturn.readEntity(String.class);
				pLog.trace("StrEntity preProcesado: " + strEntity);
				pLog.info("Respuesta recuperada del conector, se va a procesar.");
				strEntity = ResponseManager.processResponse(strEntity, nStatusCode);
				pLog.trace("StrEntity posProcesado: " + strEntity);
				pLog.info("Respuesta procesada correctamente.");
			}
		}
		catch (RviaRestException exRVIARest)
		{
			pLog.error("Rvia Rest error: " + exRVIARest.getMessage());
			strEntity = ErrorManager.getJsonError(exRVIARest);
			nStatusCode = exRVIARest.getErrorCode();
		}
		catch (Exception ex)
		{
			pLog.error("Internal error: " + ex.getMessage());
			strEntity = ErrorManager.getJsonError("500", "Error interno RviaRest", "Error Interno RviaRest");
			nStatusCode = 500;
		}
		
		/* Se añaden los datos la template */
		if (ErrorManager.isJsonError(strEntity))
		{
			pLog.info("La respuesta ha sido un error.");
			int nNewStatusCode = Integer.parseInt(ErrorManager.getCodeError(strEntity));
			// TODO Aqui se meteria el error en la plantilla de Error
			pReturn = Response.ok(strEntity).status(nNewStatusCode).build();
		}
		else
		{
			if (pMediaType == MediaType.APPLICATION_XHTML_XML_TYPE)
			{
				pLog.info("Se ha encontrado plantilla para la respuesta.");
				strEntity = TemplateManager.processTemplate(strTemplate, pSessionRviaData.getLanguage(), strEntity);
			}
			pReturn = Response.ok(strEntity).build();
		}
		return pReturn;
	}
}
