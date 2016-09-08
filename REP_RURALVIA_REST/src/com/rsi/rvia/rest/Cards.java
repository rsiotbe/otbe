package com.rsi.rvia.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
@Path("/cards")
public class Cards
{
	private static Logger			pLog	= LoggerFactory.getLogger(Cards.class);

	/** Obtiene el listado completo de tarjetas de un usuario
	 * 
	 * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
	 * @throws Exception */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllUserCards(@Context HttpServletRequest pRequest,@Context UriInfo pUriInfo, String strData) throws Exception
	{
		Response pReturn = OperationManager.proccesFromRvia(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
		return pReturn;
	}
	
	@POST
	@Path("/{card}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getCard(@Context HttpServletRequest pRequest,@Context UriInfo pUriInfo, String strData) throws Exception
	{
		Response pReturn = OperationManager.proccesFromRvia(pRequest, pUriInfo, strData, MediaType.APPLICATION_JSON_TYPE);
		return pReturn;
	}
	
	/** Fija el estado de bloqueo de una tarjeta
	 * 
	 * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setCardLockStatus(@Context HttpServletRequest pRequest)
	{
		return Response.ok().entity("{\"info\":\"todo OK\"}").build();
	}
}
