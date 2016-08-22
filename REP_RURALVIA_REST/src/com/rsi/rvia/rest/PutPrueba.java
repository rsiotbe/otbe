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
import com.rsi.rvia.rest.client.OperationManager;
import com.rsi.rvia.rest.session.SessionRviaData;
import com.rsi.rvia.rest.tool.LogController;

@Path("/putprueba")
public class PutPrueba
{
	private static Logger			pLog	= LoggerFactory.getLogger(Cards.class);
	private static LogController	pLogC	= new LogController();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllUserCards(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo) throws Exception
	{
		pLog.info("Se recibe una peticion de cashierLocatior");
		pLogC.addLog("Info", "Se recibe una peticion de cashierLocatior");
		String data = "{\"EE_I_ActivacionTarjetaBE\": {\"codigoEntidad\": \"3008\","
				+ "\"usuarioBE\": \"32894488\",\"acuerdoBE\": \"1932504291\","
				+ "\"acuerdo\": \"1932511486\",\"panToken\": \"4599846092220023\","
				+ "\"DatosFirma\":{\"firma\": \"G\",\"telefonoMovil\": \"666666666\"}}}";
		SessionRviaData pSessionRviaData = new SessionRviaData(pRequest);
		Response pReturn = OperationManager.proccesFromRvia(pRequest, pUriInfo, data, MediaType.TEXT_PLAIN_TYPE);
		// /??? La respuesta devuelve ahora mismo JSON para hacer pruebas. Deberia devolver un XHTML con los datos del
		// JSON.
		return pReturn;
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response updateThink(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
			throws Exception
	{
		pLog.info("Se recibe una peticion de cashierLocatior, PUT");
		pLogC.addLog("Info", "Se recibe una peticion de cashierLocatior, PUT");
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
		pLogC.addLog("Info", "Se recibe una peticion de cashierLocatior, POST");
		SessionRviaData pSessionRviaData = new SessionRviaData(pRequest);
		Response pReturn = OperationManager.proccesFromRvia(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
		return pReturn;
	}
}
