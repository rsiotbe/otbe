package com.rsi.rvia.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.client.OperationManager;
import com.rsi.rvia.rest.tool.ServiceHelper;
//import javax.ws.rs.Consumes;


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
	@Path("/login") 
   @Produces(MediaType.APPLICATION_JSON)
	public Response loginProcess(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
			String strData) throws Exception
	{	
		// Invocar servicio de login	desde operation manager?
		Response pReturn = OperationManager.proccesForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);	
		pLog.info(" ---------> Login");
		//int status = pReturn.getStatus();
		//if(status != 200)
		//	return Response.ok("{\"httpCode\":\"403\",\"message\":\"Login failed\"}").status(403).build();
		//else	
			return pReturn;
	}	
	
	/**
	 * Obtiene el listado completo de tarjetas de un usuario
	 * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
	 * @throws Exception 
	 */
	@GET
	@Path("/contracts") 
   @Produces(MediaType.APPLICATION_JSON)
	public Response listaDeContratos(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
			String strData) throws Exception
	{	
		//return Response.ok("Lista de contratos").build();
		Response pReturn = OperationManager.proccesForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
		pLog.info("Lista de contratos");
		return pReturn;		
	}	
	
	@GET
	@Path("/contracts/help") 
   @Produces(MediaType.APPLICATION_JSON)
	public Response listaDeContratosHelp(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
			String strData) throws Exception
	{			
		JSONObject jsonHelp = ServiceHelper.getHelp("/costcontrol/contracts");		
		pLog.info("Lista de contratos. Proceso help");
		return Response.ok(jsonHelp.toString()).build();			
	}	
	
	@GET
	@Path("/contracts/{idContract: [0-9]+}") 
   @Produces(MediaType.APPLICATION_JSON)
	public Response saldosDeUnContrato(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
			String strData) throws Exception
	{	
		//return Response.ok("Movimientos de un contrato").build();
		Response pReturn = OperationManager.proccesForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
		pLog.info("Movimientos de un contrato");
		return pReturn;	
	}
	
	@GET
	@Path("/contracts/{idContract: [0-9]+}/help") 
   @Produces(MediaType.APPLICATION_JSON)
	public Response saldosDeUnContratoHelp(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
			String strData) throws Exception
	{	
		//return Response.ok("Movimientos de un contrato").build();
		Response pReturn = OperationManager.proccesForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
		pLog.info("Movimientos de un contrato. Proceso help");
		return pReturn;	
	}	
	
	@GET
	@Path("/contracts/cardsmov/{idContract}") 
   @Produces(MediaType.APPLICATION_JSON)
	public Response moviminetosTarjeta(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
			String strData) throws Exception
	{	
		//return Response.ok("Movimientos de un contrato").build();
		Response pReturn = OperationManager.proccesForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
		pLog.info("Movimientos de un contrato");
		return pReturn;	
	}
	@GET
	@Path("/contracts/line/{codLinea}") 
   @Produces(MediaType.APPLICATION_JSON)
	public Response listaDeContratosPorLinea(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
			String strData) throws Exception
	{	
		//return Response.ok("Lista de contratos").build();
		Response pReturn = OperationManager.proccesForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
		pLog.info("Lista de contratos");
		return pReturn;		
	}	
}
