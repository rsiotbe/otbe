package com.rsi.rvia.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
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
		// Response pReturn = OperationManager.processTemplateFromRvia(pRequest, pUriInfo, strData);
		Response pReturn = Response.status(200).build();
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
		pLog.info("Se recibe una peticion de pagina inical de todos los simuladores");
		pLog.info("entidad: " + strBankName);
		pLog.info("idioma: " + "es_ES");
		// Response pReturn = OperationManager.processTemplateFromRvia(pRequest, pUriInfo, strData);
		Response pReturn = Response.status(200).build();
		pLog.info("Se devuelve la respuesta final al usuario");
		return pReturn;
	}
}
