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
import com.rsi.rvia.rest.session.SessionRviaData;
import com.rsi.rvia.rest.template.TemplateManager;
import com.rsi.rvia.rest.tool.Utils;
import com.rsi.rvia.translates.TranslateProcessor;

public class OperationManager
{
	private static HttpSession	pSession;
	private static Logger		pLog	= LoggerFactory.getLogger(TranslateProcessor.class);

	public static Response proccesFromRvia(HttpServletRequest pRequest, UriInfo pUriInfo, String strData,
			MediaType pMediaType) throws Exception
	{
		RestWSConnector pRestConnector;
		String strPageResult = null;
		Response pReturn = null;
		SessionRviaData pSessionRviaData = null;
		pSession = pRequest.getSession(true);
		try
		{
			pSessionRviaData = new SessionRviaData(pRequest);
		}
		catch (Exception ex)
		{
			pLog.error("Error la iniciar un nuevo SessionRviaData: ", ex);
			pSessionRviaData = null;
		}
		if (pSessionRviaData != null)
		{
			pSession.setAttribute("token", pSessionRviaData.getToken());
			if (!IsumValidation.IsValidService(pSessionRviaData))
				throw new Exception("El servicio solicitado no es permitido para este usuario por ISUM");
			String strPrimaryPath = Utils.getPrimaryPath(pUriInfo);
			MultivaluedMap<String, String> pListParams = Utils.getParam4Path(pUriInfo);
			pRestConnector = new RestWSConnector();
			pReturn = pRestConnector.getData(pRequest, strData, pSessionRviaData, strPrimaryPath, pListParams);
			int nStatusCode = pReturn.getStatus();
			String strEntity = pReturn.readEntity(String.class);
			if ((strEntity != null) && ((!strEntity.trim().startsWith("{")) || (!strEntity.trim().endsWith("}"))))
			{
				pLog.error("Error recibido de RVIA, se procede a procesarlo: " + strEntity);
				strEntity = ErrorManager.getJsonFormRviaError(strEntity);
			}
			if (pMediaType == MediaType.APPLICATION_XHTML_XML_TYPE)
			{
				String strTemplate = pRestConnector.getMiqQuests().getTemplate();
				if (strTemplate != null && !strTemplate.trim().isEmpty())
				{
					strPageResult = TemplateManager.processTemplate(strTemplate, pSessionRviaData.getLanguage(), strEntity);
				}
			}
			if (strPageResult != null)
			{
				pReturn = Response.ok(strPageResult).build();
			}
			else
			{
				pReturn = Response.ok(strEntity).status(nStatusCode).build();
			}
		}
		else
		{
			String strError = ErrorManager.getJsonError("500","Sesion mal recuperada","Ha habido un error con la sesion de RVIA.");
			pReturn = Response.ok(strError).status(500).build();
		}
		return pReturn;
	}
}
