package com.rsi.rvia.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
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

/** Clase que responde a las peticiones REST para las acciones sobre una coleccion de tarjetas */
@Path("/accountname")
public class AccountName
{
	private static Logger			pLog	= LoggerFactory.getLogger(Cards.class);


	@POST
	@Path("/{numCuenta}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response modifyAccoundName(@Context HttpServletRequest pRequest,@Context UriInfo pUriInfo, String strData) throws Exception
	{
		SessionRviaData pSessionRviaData = new SessionRviaData(pRequest);
		Response pReturn = OperationManager.proccesFromRvia(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
		return pReturn;
	}
	
	
}
