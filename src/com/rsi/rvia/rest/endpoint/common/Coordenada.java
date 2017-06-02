package com.rsi.rvia.rest.endpoint.common;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
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
import com.rsi.rvia.rest.endpoint.ruralvia.domiciliacionrecibo.DomiciliacionRecibo;

@Path("/comun")
public class Coordenada
{
    private static Logger pLog = LoggerFactory.getLogger(DomiciliacionRecibo.class);

    /**
     * Obtiene la coordenada
     * 
     * @return Objeto que contiene la coordenada @
     */
    @GET
    @Path("/coordenada")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({ MediaType.APPLICATION_XHTML_XML, MediaType.TEXT_HTML, MediaType.APPLICATION_FORM_URLENCODED,
            "application/x-ms-application", MediaType.APPLICATION_JSON })
    public Response getCoord(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
    {
        pLog.info("Se obtiene la coordenada");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" Se finaliza la obtencion de la Coordenada");
        return pReturn;
    }
}
