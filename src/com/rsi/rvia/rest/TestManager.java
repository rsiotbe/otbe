package com.rsi.rvia.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
	private static Logger	pLog	= LoggerFactory.getLogger(TestManager.class);

	@GET
	@Path("/cashierLocatior")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getAllUserCards2(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo) throws Exception
	{
		pLog.info("Se recibe una peticion de cashierLocatior de tipo " + MediaType.APPLICATION_JSON + " que genera "
				+ MediaType.APPLICATION_JSON);
		Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
		pLog.info("Se devuelve la respuesta final al usuario");
		return pReturn;
	}

	@GET
	@Path("/cashierLocatior")
	@Produces({ MediaType.TEXT_HTML })
	@Consumes({ MediaType.APPLICATION_XHTML_XML, MediaType.TEXT_HTML, MediaType.APPLICATION_FORM_URLENCODED })
	public Response getAllUserCards(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse,
			@Context UriInfo pUriInfo) throws Exception
	{
		pLog.info("Se recibe una peticion de cashierLocatior de tipo " + MediaType.MULTIPART_FORM_DATA + " que genera "
				+ MediaType.TEXT_HTML);
		String strData = "";
		Response pReturn = OperationManager.processTemplateFromRvia(pRequest, pUriInfo, strData);
		pLog.info("Se devuelve la respuesta final al usuario");
		pResponse.setContentType(MediaType.APPLICATION_XHTML_XML);
		return pReturn;
	}

	@GET
	@Path("/simuladores")
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response getSimuladoresJSON(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo) throws Exception
	{
		pLog.info("Se recibe una peticion de simuladores de tipo " + MediaType.MULTIPART_FORM_DATA + " que genera "
				+ MediaType.TEXT_HTML);
		String strData = "";
		Response pReturn = OperationManager.processTemplate(pRequest, pUriInfo, strData);
		pLog.info("Se devuelve la respuesta final al usuario");
		return pReturn;
	}

	@GET
	@Path("/simuladores")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getSimuladores(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo) throws Exception
	{
		pLog.info("Se recibe una peticion de cashierLocatior de tipo " + MediaType.APPLICATION_JSON + " que genera "
				+ MediaType.APPLICATION_JSON);
		String strData = "";
		Response pReturn = OperationManager.processGenericAPP(pRequest, pUriInfo, strData, MediaType.APPLICATION_JSON_TYPE);
		pLog.info("Se devuelve la respuesta final al usuario");
		return pReturn;
	}

	@GET
	@Path("/cards")
	@Produces(MediaType.TEXT_HTML)
	public Response getAllUserCards(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
			throws Exception
	{
		pLog.info("Se recibe una peticion de cards");
		Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, strData, MediaType.APPLICATION_XHTML_XML_TYPE);
		return pReturn;
	}

	@POST
	@Path("/cards/{card}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getCard(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
			throws Exception
	{
		pLog.info("Se recibe una peticion de cards/{card}");
		Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, strData, MediaType.APPLICATION_JSON_TYPE);
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
					strData = "{" + "\"code\":999999," + "\"httpCode\":500,"
							+ "\"message\":\"Error interno del servidor.\","
							+ "\"description\":\"Se ha producido un error interno en el servidor.\"" + "}";
					break;
			}
		}
		else
		{
			strData = "{" + "\"code\":999999," + "\"httpCode\":500," + "\"message\":\"Error interno del servidor.\","
					+ "\"description\":\"Se ha producido un error interno en el servidor.\"" + "}";
		}
		Response pReturn = OperationManager.processTemplate(pRequest, pUriInfo, strData);
		return pReturn;
	}
}
