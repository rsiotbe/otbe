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
import com.rsi.rvia.rest.DDBB.DDBBConnection;
import com.rsi.rvia.rest.DDBB.DDBBFactory;
import com.rsi.rvia.rest.DDBB.DDBBFactory.DDBBProvider;
import com.rsi.rvia.rest.client.*;
import com.rsi.rvia.rest.operation.OperationManager;
import com.rsi.rvia.rest.template.TemplateManager;
import com.rsi.rvia.utils.Utils;

/** Clase que responde a las peticiones REST para las acciones sobre una coleccion de tarjetas */
@Path("/periodictransfers")
public class PeriodicTransfers
{
	private static Logger	pLog	= LoggerFactory.getLogger(Cards.class);

	/** Obtiene el listado completo de tarjetas de un usuario
	 * 
	 * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
	 * @throws Exception */
	@POST
	//@Produces(MediaType.TEXT_PLAIN)
	public Response getAllUserPeriodicTransfers(@Context HttpServletRequest request, @Context UriInfo pUriInfo,
			String data) throws Exception
	{
		Response p = OperationManager.proccesFromRvia(request, pUriInfo, data, MediaType.TEXT_PLAIN_TYPE);
		pLog.info("Se recibe una peticion de listado de transferencias periodicas");
		//return Response.ok(p.getEntity()).build();
		//return Response.ok().entity(p.getEntity()).build();
		return p;
	}

	/** Obtiene el listado completo de tarjetas de un usuario
	 * 
	 * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
	 * @throws Exception */
	@GET
	@Produces(MediaType.APPLICATION_XHTML_XML)
	public Response getAllUserPeriodicTransfersXhtml(@Context HttpServletRequest request, @Context UriInfo pUriInfo,
			String data) throws Exception
	{
		Response p = OperationManager.proccesFromRvia(request, pUriInfo, data, MediaType.APPLICATION_XHTML_XML_TYPE);
		pLog.info("Se recibe unsa peticion de listado de transferencias periódicas");
		return p;
	}

	/** Fija el estado de bloqueo de unatarjeta
	 * 
	 * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setCardLockStatus(@Context HttpServletRequest request)
	{
		return Response.ok().entity("{\"info\":\"todo OK\"}").build();
	}
}
