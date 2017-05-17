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
        pLog.info("Se obtienen las operaciones");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Se finaliza la obtencion de las operaciones");
        return pReturn;
    }


	@GET
    @Path("/rates")
    @Produces({ MediaType.TEXT_HTML })
    public Response getSolicitud(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtienen las tarjetas disponibles");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Se finaliza la obtencion de las tarjetas disponibles");
        return pReturn;
    }

	@GET
    @Path("/{idLinea}/{idGrupo}/{idPdv}/{idTrfa}/lopd")
    @Produces({ MediaType.TEXT_HTML })
    public Response getLopd(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtienen los datos lopd");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Se finaliza la obtencion de datos lopd");
        return pReturn;
    }

	@GET
    @Path("/{idLinea}/{idGrupo}/{idPdv}/{idTrfa}/contract")
    @Produces({ MediaType.TEXT_HTML })
    public Response getDatosTarjetas(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtienen los datos de la tarjeta");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Se finaliza la obtencion de datos de la tarjeta");
        return pReturn;
    }

	@GET
    @Path("/{idLinea}/{idGrupo}/{idPdv}/{idTrfa}")
    @Produces({ MediaType.TEXT_HTML })
    public Response getDetallesTarjetas(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtienen los detalles tarjetas");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Se finaliza la obtencion de los detalles de las tarjetas");
        return pReturn;
    }

	@GET
    @Path("/scoring/formdata")
    @Produces({ MediaType.TEXT_HTML })
    public Response getDatosPersonales(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtienen los datos personales para scoring");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Se finaliza la obtencion de los datos personales para scoring");
        return pReturn;
    }

	@POST
    @Path("/{idLinea}/{idGrupo}/{idPdv}/{idTrfa}/scoring")
    @Produces({ MediaType.TEXT_HTML })
    public Response getScoring(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se realiza la llamada al scoring");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Se finaliza la llamada al scoring");
        return pReturn;
    }

	@POST
    @Path("/{idLinea}/{idGrupo}/{idPdv}/{idTrfa}/signature")
    @Produces({ MediaType.TEXT_HTML })
    public Response getFirma(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se realiza la llamada a la firma");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Se finaliza la llamada a la firma");
        return pReturn;
    }
}
