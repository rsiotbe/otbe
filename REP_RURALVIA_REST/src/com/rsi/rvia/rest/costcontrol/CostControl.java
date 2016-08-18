package com.rsi.rvia.rest.costcontrol;


import javax.servlet.http.HttpServletRequest;
//import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
import com.rsi.rvia.rest.tool.LogController;


/**
 * Clase que responde a las peticiones REST para las acciones relacionadas con el control de costes
 *
 */
@Path("/costcontrol") 
public class CostControl
{
	private static Logger pLog = LoggerFactory.getLogger(CostControl.class);
	private static LogController pLogC = new LogController();
	
	/**
	 * Obtiene el listado completo de tarjetas de un usuario
	 * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
	 * @throws Exception 
	 */
	@GET
   @Produces(MediaType.TEXT_PLAIN)		
	public Response getAllContracts(@Context HttpServletRequest request) throws Exception
	{		
		DDBBConnection p3 = DDBBFactory.getDDBB(DDBBProvider.MySql);		
		Response p = MicroQResolver.getData(request);		
		pLog.info("Se recibe una peticion de control de costes");
		pLogC.addLog("Info", "Se recibe una peticion de control de costes");
		return Response.ok().entity(p.toString()).build();
	}

}
