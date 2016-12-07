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

@Path("/domiciliations")
public class DomiciliacionRcbo
{
	private static Logger	pLog	= LoggerFactory.getLogger(DomiciliacionRcbo.class);

	/**
	 * Obtiene el listado de cuentas del cliente.
	 * 
	 * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
	 * @throws Exception
	 */
	@GET
	@Path("/contracts")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listaDeContratos(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
			throws Exception
	{
		// return Response.ok("Lista de contratos").build();
		Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
		pLog.info("Lista de contratos");
		return pReturn;
	}

	/**
	 * Obtiene el listado de emisores de asociados a una cuenta.
	 * 
	 * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
	 * @throws Exception
	 */
	@GET
	@Path("/transmitter/{idContract}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listaDeEmisores(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
			throws Exception
	{
		// return Response.ok("Lista de contratos").build();
		Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
		pLog.info("Lista de contratos");
		return pReturn;
	}
}
