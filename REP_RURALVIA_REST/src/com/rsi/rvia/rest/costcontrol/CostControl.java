package com.rsi.rvia.rest.costcontrol;


import javax.servlet.http.HttpServletRequest;
//import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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


/**
 * Clase que responde a las peticiones REST para las acciones relacionadas con el control de costes
 *
 */
@Path("/costcontrol") 
public class CostControl
{
	private static Logger pLog = LoggerFactory.getLogger(CostControl.class);
	
	/**
	 * Obtiene el listado completo de tarjetas de un usuario
	 * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
	 * @throws Exception 
	 */
	@GET
	@Path("/contracts") 
   @Produces(MediaType.APPLICATION_JSON)
	public Response do1(@Context HttpServletRequest request, @Context UriInfo pUriInfo,
			String data) throws Exception
	{	
		//return Response.ok("Lista de contratos").build();
		Response p = OperationManager.proccesFromRvia(request, pUriInfo, data, MediaType.TEXT_PLAIN_TYPE);
		pLog.info("Lista de contratos");
		return p;		
	}
	@GET
	@Path("/contracts/{c_number}") 
   @Produces(MediaType.APPLICATION_JSON)
	public Response do2(@Context HttpServletRequest request, @Context UriInfo pUriInfo,
			String data) throws Exception
	{	
		//return Response.ok("Movimientos de un contrato").build();
		Response p = OperationManager.proccesFromRvia(request, pUriInfo, data, MediaType.TEXT_PLAIN_TYPE);
		pLog.info("Movimientos de un contrato");
		return p;	
	}
}
