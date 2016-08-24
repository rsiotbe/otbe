package com.rsi.rvia.rest.client;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.isum.IsumValidation;
import com.rsi.rvia.rest.DDBB.DDBBConnection;
import com.rsi.rvia.rest.DDBB.DDBBFactory;
import com.rsi.rvia.rest.DDBB.DDBBFactory.DDBBProvider;
import com.rsi.rvia.rest.session.SessionRviaData;
import com.rsi.rvia.rest.template.TemplateManager;
import com.rsi.rvia.rest.tool.RviaConnectCipher;
import com.rsi.rvia.rest.tool.Utils;
import com.rsi.rvia.translates.TranslateEntry;
import com.rsi.rvia.translates.TranslateProcessor;

public class OperationManager
{
	private static HttpSession	pSession;
	private static Logger		pLog	= LoggerFactory.getLogger(TranslateProcessor.class);
	public static Response proccesFromRvia(HttpServletRequest pRequest, UriInfo pUriInfo, String strData,
			MediaType pMediaType) throws Exception
	{
		String strPageResult = null;
		Response pReturn = null;
		pSession = pRequest.getSession(true);
		SessionRviaData pSessionRviaData = new SessionRviaData(pRequest);
		pSession.setAttribute("token", pSessionRviaData.getToken());
		if (!IsumValidation.IsValidService(pSessionRviaData))
			throw new Exception("EL servicio solicitado no es permitido para este usuario por ISUM");
		
		Utils pUtil = new Utils();
		String strPrimaryPath = pUtil.getPrimaryPath(pUriInfo);
		RestWSConnector pRestConnector = new RestWSConnector();
		
		pReturn = pRestConnector.getData(pRequest, strData, pSessionRviaData, strPrimaryPath);
		if (pMediaType == MediaType.APPLICATION_XHTML_XML_TYPE)
		{
			String strTemplate = pRestConnector.getTemplate();
			if(strTemplate != null){
				strPageResult = TemplateManager.processTemplate(strTemplate, pSessionRviaData.getLanguage(), pReturn.readEntity(String.class));
			}
		}
		NewCookie pCookieToken = new NewCookie("token", pSessionRviaData.getToken());

		if(strPageResult != null){
			pReturn = Response.ok(strPageResult).cookie(pCookieToken).build();
		}else{
			pReturn = Response.ok(pReturn.getEntity()).cookie(pCookieToken).build();
		}
		
		pLog.info("Se Añade la Cookie con el Token a la respuesta.");
		
		return pReturn;
	
	}
}
