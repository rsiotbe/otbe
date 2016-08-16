package com.rsi.rvia.rest;

import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.operation.OperationManager;
import com.rsi.rvia.rest.session.SessionRviaData;
import com.rsi.rvia.rest.template.TemplateManager;

@Path("/cashierLocatior")
public class CashierLocatior
{
	private static Logger	pLog	= LoggerFactory.getLogger(Cards.class);
	
	@GET
   @Produces(MediaType.APPLICATION_JSON)
	public Response getAllUserCards(@Context HttpServletRequest request,@Context UriInfo pUriInfo) throws Exception
	{
		pLog.info("Se recibe una peticion de cashierLocatior");
		String data = "";
		SessionRviaData pSessionRviaData = new SessionRviaData (request);
		Response p = OperationManager.proccesFromRvia(request, pUriInfo, data, MediaType.TEXT_PLAIN_TYPE);
		
		
		///??? La respuesta devuelve ahora mismo JSON para hacer pruebas. Deberia devolver un XHTML con los datos del JSON.
		
		return p;
	}
	
}
