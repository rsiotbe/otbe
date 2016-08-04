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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.session.SessionRviaData;
import com.rsi.rvia.rest.template.TemplateManager;




/**
 * Clase que responde a las peticiones REST para las acciones sobre una coleccion de tarjetas
 *
 */
@Path("/cards")
public class Cards
{
	private static Logger pLog = LoggerFactory.getLogger(Cards.class);
	
	
	/**
	 * Obtiene el listado completo de tarjetas de un usuario
	 * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
	 * @throws Exception 
	 */
	@GET
   @Produces(MediaType.APPLICATION_XHTML_XML)
	public Response getAllUserCards(@Context HttpServletRequest request) throws Exception
	{
		SessionRviaData pSessionRviaData = new SessionRviaData (request);

		String strReturn = TemplateManager.processTemplate("/test/sample.xhtml", pSessionRviaData.getLanguage());
		
		
		return Response.ok(strReturn).build();
	}
	
	/**
	 * Fija el estado de bloqueo de una tarjeta
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
