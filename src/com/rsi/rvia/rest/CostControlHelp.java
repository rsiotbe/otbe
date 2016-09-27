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
@Path("/rsiapi/help") 
public class CostControlHelp
{
	private static Logger pLog = LoggerFactory.getLogger(CostControlHelp.class);

	@GET
	@Path("/contracts") 
   @Produces(MediaType.APPLICATION_JSON)
	public Response listaDeContratosHelp(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
			String strData) throws Exception
	{			
		JSONObject jsonHelp = ServiceHelper.getHelp("/rsiapi/contracts");		
		pLog.info("Lista de contratos. Proceso help");
		return Response.ok(jsonHelp.toString()).build();			
	}	
	
	@GET
	@Path("/contracts/{idContract: [0-9]+}") 
   @Produces(MediaType.APPLICATION_JSON)
	public Response saldosDeUnContratoHelp(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
			String strData) throws Exception
	{	
		JSONObject jsonHelp = ServiceHelper.getHelp("/rsiapi/contracts/{idContract}");	
		pLog.info("Lista de contratos - Detalle. Proceso help");
		return Response.ok(jsonHelp.toString()).build();
	}	
	
	@GET
	@Path("/contracts/line/{codLinea}") 
   @Produces(MediaType.APPLICATION_JSON)
	public Response listaDeContratosPorLinea(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
			String strData) throws Exception
	{	
		JSONObject jsonHelp = ServiceHelper.getHelp("/rsiapi/contracts/line/{codLinea}");	
		pLog.info("Lista de contratos - de una línea concreta. Proceso help");
		return Response.ok(jsonHelp.toString()).build();	
	}	
}
