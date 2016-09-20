package com.rsi.rvia.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.client.OperationManager;

/** Clase que responde a las peticiones REST para las acciones sobre una coleccion de tarjetas */
@Path("/rviaerror")
public class RviaError
{
	private static Logger pLog = LoggerFactory.getLogger(Cards.class);

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response getError(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo) throws Exception
	{
		pLog.info("Se recibe una peticion de cashierLocatior");
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
