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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.DDBB.DDBBConnection;
import com.rsi.rvia.rest.DDBB.DDBBFactory;
import com.rsi.rvia.rest.DDBB.DDBBFactory.DDBBProvider;
import com.rsi.rvia.rest.client.*;



/**
 * Clase que responde a las peticiones REST para las acciones sobre una coleccion de tarjetas
 *
 */
@Path("/periodictransfers")
public class PeriodicTransfers
{
	private static Logger pLog = LoggerFactory.getLogger(Cards.class);
	
	
	/**
	 * Obtiene el listado completo de tarjetas de un usuario
	 * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
	 * @throws Exception 
	 */ 
	@POST
   @Produces(MediaType.TEXT_PLAIN)		
	public Response getAllUserPeriodicTransfers(@Context HttpServletRequest request) throws Exception
	{
		Response p = RestWSConnector.getData(request);		
		pLog.info("Se recibe una peticion de listado de transferencias periódicas");
		return Response.ok().entity(p.toString()).build();
	}
	
	/**
	 * Fija el estado de bloqueo de unatarjeta
	 * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
	 */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setCardLockStatus(@Context HttpServletRequest request)
	{
		
		return Response.ok().entity("{\"info\":\"todo OK\"}").build();
	}

}
