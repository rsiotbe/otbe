package com.rsi.rvia.rest.endpoint.ruralvia.sabiaQue;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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

/**
 * Servicio de novedades. Clase que responde a las peticiones REST
 */
@Path("/news")
public class News
{
	private static Logger	pLog	= LoggerFactory.getLogger(News.class);

	/**
	 * Obtiene el listado completo de novedades a mostrar al usuario
	 * 
	 * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de novedades
	 * @throws Exception
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNews(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
			throws Exception
	{
		Response pReturn = OperationManager.processGenericAPP(pRequest, pUriInfo, strData, MediaType.APPLICATION_JSON_TYPE);
		pLog.info(" ---------> News - getNews");
		return pReturn;
	}
	
	/**
	 * Registra en BBDD las acciones del usuario sobre las novedades
	 * 
	 * @throws Exception
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateNews(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
			throws Exception
	{
		Response pReturn = OperationManager.processGenericAPP(pRequest, pUriInfo, strData, MediaType.APPLICATION_JSON_TYPE);
		pLog.info(" ---------> News - updateNews");
		return pReturn;
	}
}
