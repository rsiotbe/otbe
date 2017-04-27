package com.rsi.rvia.rest.endpoint.ruralvia.prestamos;

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
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rsi.rvia.rest.client.OperationManager;

@Path("/prestamopersonal")
public class PrestamoPersonal {

    private static Logger pLog = LoggerFactory.getLogger(PrestamoPersonal.class);

    /**
     * Obtiene el listado completo de las tarifas disponibles de un usuario
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarifas@
     */
    @GET
    @Path("/tarifas")
    @Produces({ MediaType.TEXT_HTML })
    public Response getTarifas(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro tarifas");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
    	//Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, id, MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Tarifas");
        return pReturn;
    }

    @GET
    @Path("/detalletarifas")
    @Produces({ MediaType.TEXT_HTML })
    public Response getDetTarifas(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro detalle tarifas");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Detalle tarifas");
        return pReturn;
    }

    @GET
    @Path("/lopd")
    @Produces({ MediaType.TEXT_HTML })
    public Response getLopd(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro lopd");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> LOPD");
        return pReturn;
    }

    @GET
    @Path("/datosprestamo")
    @Produces({ MediaType.TEXT_HTML })
    public Response getDatoPrestamo(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro datos prestamo");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Datos prestamo");
        return pReturn;
    }

    @POST
    @Path("/datospersona")
    @Produces({ MediaType.TEXT_HTML })
    public Response getDatosPersona(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro datos persona");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Datos persona");
        return pReturn;
    }

    @POST
    @Path("/scoring")
    @Produces({ MediaType.TEXT_HTML })
    public Response getScoring(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro scoring");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Scoring");
        return pReturn;
    }
}
