package com.rsi.rvia.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.operation.OperationManager;
import com.rsi.rvia.rest.session.SessionRviaData;
import com.rsi.rvia.rest.tool.LogController;

@Path("/cashierLocatior")
public class CashierLocatior
{
	private static Logger	pLog	= LoggerFactory.getLogger(Cards.class);
	private static LogController pLogC = new LogController();
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response getAllUserCards(@Context HttpServletRequest request, @Context UriInfo pUriInfo) throws Exception
	{
		pLog.info("Se recibe una peticion de cashierLocatior");
		pLogC.addLog("Info", "Se recibe una peticion de cashierLocatior");
		String data = "";
		SessionRviaData pSessionRviaData = new SessionRviaData(request);
		Response p = OperationManager.proccesFromRvia(request, pUriInfo, data, MediaType.APPLICATION_XHTML_XML_TYPE);
		// /??? La respuesta devuelve ahora mismo JSON para hacer pruebas. Deberia devolver un XHTML con los datos del
		// JSON.
		return p;
	}

}
