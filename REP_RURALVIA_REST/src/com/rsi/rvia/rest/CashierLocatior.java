package com.rsi.rvia.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.client.OperationManager;
import com.rsi.rvia.rest.session.SessionRviaData;
import com.rsi.rvia.rest.tool.LogController;

@Path("/cashierLocatior")
public class CashierLocatior
{
	private static Logger			pLog	= LoggerFactory.getLogger(Cards.class);
	private static LogController	pLogC	= new LogController();

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response getAllUserCards(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo) throws Exception
	{
		pLog.info("Se recibe una peticion de cashierLocatior");
		pLogC.addLog("Info", "Se recibe una peticion de cashierLocatior");
		String strData = "";
		SessionRviaData pSessionRviaData = new SessionRviaData(pRequest);
		Response pReturn = OperationManager.proccesFromRvia(pRequest, pUriInfo, strData, MediaType.APPLICATION_XHTML_XML_TYPE);
		return pReturn;
	}
}
