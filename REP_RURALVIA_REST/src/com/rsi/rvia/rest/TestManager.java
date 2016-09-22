package com.rsi.rvia.rest;

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

@Path("/test")
public class TestManager
{
	private static Logger pLog = LoggerFactory.getLogger(TestManager.class);

	@GET
	@Path("/cashierLocatior")
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response getAllUserCards(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo) throws Exception
	{
		pLog.info("Se recibe una peticion de cashierLocatior");
		String strData = "";
		Response pReturn = OperationManager.processTemplateFromRvia(pRequest, pUriInfo, strData);
		return pReturn;
	}

	@GET
	@Path("/cashierLocatior")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getAllUserCards2(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo) throws Exception
	{
		pLog.info("Se recibe una peticion de cashierLocatior");
		Response pReturn = OperationManager.proccesDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
		return pReturn;
	}

	@GET
	@Path("/cards")
	@Produces(MediaType.TEXT_HTML)
	public Response getAllUserCards(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
			throws Exception
	{
		pLog.info("Se recibe una peticion de cards");
		Response pReturn = OperationManager.proccesDataFromRvia(pRequest, pUriInfo, strData, MediaType.APPLICATION_XHTML_XML_TYPE);
		return pReturn;
	}

	@POST
	@Path("/cards/{card}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getCard(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
			throws Exception
	{
		pLog.info("Se recibe una peticion de cards/{card}");
		Response pReturn = OperationManager.proccesDataFromRvia(pRequest, pUriInfo, strData, MediaType.APPLICATION_JSON_TYPE);
		return pReturn;
	}

	@GET
	@Path("/rviaerror")
	@Produces(MediaType.TEXT_HTML)
	public Response getError(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo) throws Exception
	{
		pLog.info("Se recibe una peticion de RviaError");
		String strData = "{}";
		if (pRequest.getParameter("errorCode") != null)
		{
			String strErrorCode = (String) pRequest.getParameter("errorCode");
			switch (strErrorCode)
			{
				case "1111":
					strData = "{" + "\"code\":400000," + "\"httpCode\":400,"
							+ "\"message\":\"Ha habído un problema con el identificador de la operativa.\","
							+ "\"description\":\"Error con el identificador de la operativa.\"" + "}";
					break;
				default:
					strData = "{" + "\"code\":999999," + "\"httpCode\":500," + "\"message\":\"Error interno del servidor.\","
							+ "\"description\":\"Se ha producido un error interno en el servidor.\"" + "}";
					break;
			}
		}
		Response pReturn = OperationManager.processTemplateFromRvia(pRequest, pUriInfo, strData);
		return pReturn;
	}
}
