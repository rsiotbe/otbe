package com.rsi.rvia.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.client.OperationManager;

@Path("/simuladores")
public class LoanSimulators
{
	private static Logger	pLog	= LoggerFactory.getLogger(LoanSimulators.class);

	@GET
	@Path("{entidad}/{idioma}")
	@Produces({ MediaType.TEXT_HTML })
	@Consumes({ MediaType.APPLICATION_XHTML_XML, MediaType.TEXT_HTML, MediaType.APPLICATION_FORM_URLENCODED,
			"application/x-ms-application" })
	public Response getAllSimulators(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
			@PathParam("entidad") String strBankName, @PathParam("idioma") String strlanguage) throws Exception
	{
		pLog.info("Se recibe una peticion de pagina inical de todos los simuladores");
		pLog.info("entidad: " + strBankName);
		pLog.info("idioma: " + strlanguage);
		Response pReturn = OperationManager.processDataFromSimulators(pRequest, pUriInfo, strBankName, strlanguage, MediaType.TEXT_HTML_TYPE);
		pLog.info("Se devuelve la respuesta final al usuario");
		return pReturn;
	}

	@GET
	@Path("{entidad}")
	@Produces({ MediaType.TEXT_HTML })
	@Consumes({ MediaType.APPLICATION_XHTML_XML, MediaType.TEXT_HTML, MediaType.APPLICATION_FORM_URLENCODED,
			"application/x-ms-application" })
	public Response getAllSimulators(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
			@PathParam("entidad") String strBankName) throws Exception
	{
		pLog.info("Se recibe una peticion de pagina inical de todos los simuladores sin idioma específico");
		pLog.info("entidad: " + strBankName);
		Response pReturn = OperationManager.processDataFromSimulators(pRequest, pUriInfo, strBankName, null, MediaType.TEXT_HTML_TYPE);
		pLog.info("Se devuelve la respuesta final al usuario");
		return pReturn;
	}

	@POST
	@Path("{entidad}/{idioma}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getConfigSimulators(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
			@PathParam("entidad") String strBankName, @PathParam("idioma") String strlanguage) throws Exception
	{
		pLog.info("Se recibe una peticion de pagina inical de todos los simuladores");
		pLog.info("entidad: " + strBankName);
		pLog.info("idioma: " + strlanguage);
		Response pReturn = OperationManager.processDataFromSimulators(pRequest, pUriInfo, strBankName, strlanguage, MediaType.TEXT_HTML_TYPE);
		pLog.info("Se devuelve la respuesta final al usuario");
		return pReturn;
	}

	@POST
	@Path("{entidad}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getConfigSimulators(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
			@PathParam("entidad") String strBankName) throws Exception
	{
		pLog.info("Se recibe una peticion de pagina inical de todos los simuladores sin idioma específico");
		pLog.info("entidad: " + strBankName);
		Response pReturn = OperationManager.processDataFromSimulators(pRequest, pUriInfo, strBankName, null, MediaType.TEXT_HTML_TYPE);
		pLog.info("Se devuelve la respuesta final al usuario");
		return pReturn;
	}
}
