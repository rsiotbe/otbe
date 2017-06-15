package com.rsi.rvia.rest.endpoint.restadmin;

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

@Path("/rviasession")
public class RviaSessionEndPoint
{
    private static Logger pLog = LoggerFactory.getLogger(RviaSessionEndPoint.class);

    /**
     * Genera un token de cominuunicación entre rviarest y ruralviavia
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginGet(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
    {
        pLog.info("Se solciita un token de rvia-rviarest");
        Response pReturn = OperationManager.processGenericAPP(pRequest, pUriInfo, strData, MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Se retorna el resultado al cliente");
        return pReturn;
    }
}
