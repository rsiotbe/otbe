package com.rsi.rvia.rest.operation;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.rsi.isum.IsumValidation;
import com.rsi.rvia.rest.client.RestWSConnector;
import com.rsi.rvia.rest.session.SessionRviaData;
import com.rsi.rvia.rest.template.TemplateManager;

public class OperationManager
{
	public static Response proccesFromRvia(HttpServletRequest pRequest, String data, MediaType pMediaType) throws Exception
	{
		SessionRviaData pSessionRviaData = new SessionRviaData(pRequest);
		if(!IsumValidation.IsValidService(pSessionRviaData))
			throw new Exception("EL servicio solicitado no es permitido para este usuario por ISUM");
		
		/* comprueba si la operativa es de tipo WS o RVIA-JSON */
		/*if(operativa es de tipo WS) 
				recuerar pametros necesarios
				*/
		Response p = RestWSConnector.getData(pRequest, data, pSessionRviaData);	
		
		if(pMediaType == MediaType.APPLICATION_XHTML_XML_TYPE)
		{
			String strPageResult =TemplateManager.processTemplate("/test/sample.xhtml", pSessionRviaData.getLanguage(), p.readEntity(String.class));
			p = Response.ok(strPageResult).build();
		}
		
		
		return p;
	}
	
}
