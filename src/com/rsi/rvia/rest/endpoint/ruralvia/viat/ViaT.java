package com.rsi.rvia.rest.endpoint.ruralvia.viat;

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
import com.rsi.rvia.rest.endpoint.ruralvia.prestamos.PrestamoPersonal;

@Path("/viaT")
public class ViaT {

	private static Logger pLog = LoggerFactory.getLogger(PrestamoPersonal.class);
	
	@GET
    @Path("/operaciones")
    @Produces({ MediaType.TEXT_HTML })
    public Response getOperaciones(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro operaciones");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Operaciones");
        return pReturn;
    }


	@GET
    @Path("/solicitud")
    @Produces({ MediaType.TEXT_HTML })
    public Response getSolicitud(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro solicitud");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Solicitud");
        return pReturn;
    }

	@GET
    @Path("/lopd")
    @Produces({ MediaType.TEXT_HTML })
    public Response getLopd(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro lopd");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Lopd");
        return pReturn;
    }
}
