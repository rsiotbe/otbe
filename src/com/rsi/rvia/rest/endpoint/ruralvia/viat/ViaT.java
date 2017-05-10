package com.rsi.rvia.rest.endpoint.ruralvia.viat;

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
import javax.ws.rs.QueryParam;
import javax.ws.rs.DefaultValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rsi.rvia.rest.client.OperationManager;
import com.rsi.rvia.rest.endpoint.ruralvia.prestamos.PrestamoPersonal;

@Path("/cards/viat")
public class ViaT {

	private static Logger pLog = LoggerFactory.getLogger(PrestamoPersonal.class);
	
	@GET
    @Path("/contracts")
    @Produces({ MediaType.TEXT_HTML })
    public Response getOperaciones(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro operaciones");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Operaciones");
        return pReturn;
    }


	@GET
    @Path("/rates")
    @Produces({ MediaType.TEXT_HTML })
    public Response getSolicitud(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro solicitud");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Solicitud");
        return pReturn;
    }

	@GET
    @Path("/{idLinea}/{idGrupo}/{idPdv}/{idTrfa}/lopd")
    @Produces({ MediaType.TEXT_HTML })
    public Response getLopd(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro lopd");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Lopd");
        return pReturn;
    }

	@GET
    @Path("/{idLinea}/{idGrupo}/{idPdv}/{idTrfa}/contract")
    @Produces({ MediaType.TEXT_HTML })
    public Response getDatosTarjetas(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro datos tarjetas");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Datos tarjetas");
        return pReturn;
    }

	@GET
    @Path("/{idLinea}/{idGrupo}/{idPdv}/{idTrfa}")
    @Produces({ MediaType.TEXT_HTML })
    public Response getDetallesTarjetas(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro detalles tarjetas");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Detalles tarjetas");
        return pReturn;
    }

	@GET
    @Path("/scoring/formdata")
    @Produces({ MediaType.TEXT_HTML })
    public Response getDatosPersonales(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro datos personales");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Datos personales");
        return pReturn;
    }

	@POST
    @Path("/{idLinea}/{idGrupo}/{idPdv}/{idTrfa}/scoring")
    @Produces({ MediaType.TEXT_HTML })
    public Response getScoring(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro scoring");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Scoring");
        return pReturn;
    }

	@POST
    @Path("/{idLinea}/{idGrupo}/{idPdv}/{idTrfa}/sign")
    @Produces({ MediaType.TEXT_HTML })
    public Response getFirma(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro firma");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Firma");
        return pReturn;
    }

	@POST
    @Path("/errscoring")
    @Produces({ MediaType.TEXT_HTML })
    public Response getErrScoring(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro errscoring");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Errscoring");
        return pReturn;
    }
}
