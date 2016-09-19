package com.rsi.rvia.rest;


import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
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
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.client.OperationManager;
import com.rsi.rvia.rest.session.SessionRviaData;


@Path("/prueba")
public class PruebaMetodos
{
	private static Logger			pLog	= LoggerFactory.getLogger(PruebaMetodos.class);
	
	
	
	@PUT
	@Path("/put")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response updateThink(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
			throws Exception
	{
		pLog.info("Se recibe una peticion de cashierLocatior, PUT");
	
		SessionRviaData pSessionRviaData = new SessionRviaData(pRequest);
		Response pReturn = OperationManager.proccesFromRvia(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
		return pReturn;
	}

	@Path("/post")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response updateThinkpost(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
			throws Exception
	{
		pLog.info("Se recibe una peticion de cashierLocatior, POST");
		SessionRviaData pSessionRviaData = new SessionRviaData(pRequest);
		Response pReturn = OperationManager.proccesFromRvia(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
		return pReturn;
	}

	
	/**
	 * Devuelve un mensaje en json con información de bbdd para hacer pruebas con respuestas en json
	 * @param pRequest
	 * @param pUriInfo
	 * @param strData
	 * @return
	 * @throws Exception
	 */
	@Path("/getjson")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDDBBhelp(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
			throws Exception
	{
		
		JSONObject pJson = new JSONObject();	
		Response pReturn;
		pJson.put("DATA", "EXAMPLE SAMPLE EXAMPLE");
		pJson.put("Username", "Perico Palotes");
		pJson.put("Estado", "Serio");

		pReturn = Response.ok(pJson.toString()).build();
		return pReturn;
	}
	

}
