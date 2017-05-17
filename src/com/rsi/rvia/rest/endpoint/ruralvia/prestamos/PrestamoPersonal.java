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

@Path("/loan/personal")
public class PrestamoPersonal {

    private static Logger pLog = LoggerFactory.getLogger(PrestamoPersonal.class);

    /**
     * Obtiene el listado completo de las tarifas disponibles de un usuario
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarifas@
     */
    @GET
    @Path("/rates")
    @Produces({ MediaType.TEXT_HTML })
    public Response getTarifas(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtiene tarifas disponibles");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Finaliza la obtencion de las tarifas disponibles");
        return pReturn;
    }

    @GET
    @Path("/{idLinea}/{idGrupo}/{idProducto}/{idTarifa}")
    @Produces({ MediaType.TEXT_HTML })
    public Response getDetTarifas(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtiene el detalle de la tarifa");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Finaliza la obtencion del detalle de la tarifa");
        return pReturn;
    }

    @GET
    @Path("/{idLinea}/{idGrupo}/{idProducto}/{idTarifa}/lopd")
    @Produces({ MediaType.TEXT_HTML })
    public Response getLopd(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtienen los datos lopd");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Finaliza la obtencion de los datos lopd");
        return pReturn;
    }

    @GET
    @Path("/accounts")
    @Produces({ MediaType.TEXT_HTML })
    public Response getDatoPrestamo(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtienen los datos de usuario para contratacion");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Finaliza la obtencion de datos del usuario para contratacion");
        return pReturn;
    }

    @GET
    @Path("/idtype")
    @Produces({ MediaType.TEXT_HTML })
    public Response getTiposDocumento(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtienen los tipos de documentos identificativos");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Finaliza la obtencion de los tipos de documentos identificativos");
        return pReturn;
    }

    @POST
    @Path("/scoring/formdata")
    @Produces({ MediaType.TEXT_HTML })
    public Response getDatosPersona(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtienen datos personales");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Finaliza la obtencion de los datos personales");
        return pReturn;
    }

    @POST
    @Path("/{idLinea}/{idGrupo}/{idProducto}/{idTarifa}/scoring")
    @Produces({ MediaType.TEXT_HTML })
    public Response getScoring(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se realiza la llamada al scoring");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Finaliza la llamada al scoring");
        return pReturn;
    }

    @POST
    @Path("/signature")
    @Produces({ MediaType.TEXT_HTML })
    public Response getFirma(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se realiza la llamada a la firma");
    	Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Finaliza la llamada a la firma");
        return pReturn;
    }
}
