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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.client.OperationManager;

@Path("/loan/personal")
public class PrestamoPersonal
{
    private static Logger pLog = LoggerFactory.getLogger(PrestamoPersonal.class);

    /**
     * Obtiene el listado completo de las tarifas disponibles de un usuario
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarifas@
     */
    // @GET
    // @Path("/rates")
    // @Produces({ MediaType.TEXT_HTML })
    // @Consumes({ MediaType.APPLICATION_XHTML_XML, MediaType.TEXT_HTML, MediaType.APPLICATION_FORM_URLENCODED,
    // "application/x-ms-application" })
    // public Response getTarifasHtml(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    // {
    // pLog.info("Se obtiene tarifas disponibles HTML");
    // Response pReturn = OperationManager.processTemplateFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_HTML_TYPE);
    // pLog.info("Finaliza la obtencion de las tarifas disponibles HTML");
    // return pReturn;
    // }
    /**
     * Obtiene el listado completo de las tarifas disponibles de un usuario
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarifas@
     */
    @GET
    @Path("/rates")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response getTarifas(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtiene tarifas disponibles");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Finaliza la obtencion de las tarifas disponibles");
        return pReturn;
    }

    @GET
    @Path("/{idLinea}/{idGrupo}/{idProducto}/{idTarifa}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getDetTarifas(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtiene el detalle de la tarifa");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Finaliza la obtencion del detalle de la tarifa");
        return pReturn;
    }

    @GET
    @Path("/{idLinea}/{idGrupo}/{idProducto}/{idTarifa}/lopd")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getLopd(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtienen los datos lopd");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Finaliza la obtencion de los datos lopd");
        return pReturn;
    }

    @GET
    @Path("/accounts")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getDatoPrestamo(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtienen los datos de usuario para contratacion");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Finaliza la obtencion de datos del usuario para contratacion");
        return pReturn;
    }

    @GET
    @Path("/idtype")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getTiposDocumento(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtienen los tipos de documentos identificativos");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Finaliza la obtencion de los tipos de documentos identificativos");
        return pReturn;
    }

    @POST
    @Path("/scoring/formdata")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getDatosPersona(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtienen datos personales");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Finaliza la obtencion de los datos personales");
        return pReturn;
    }

    @POST
    @Path("/{idLinea}/{idGrupo}/{idProducto}/{idTarifa}/scoring")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getScoring(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se realiza la llamada al scoring");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Finaliza la llamada al scoring");
        return pReturn;
    }

    @POST
    @Path("/signature")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getFirma(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se realiza la llamada a la firma");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Finaliza la llamada a la firma");
        return pReturn;
    }
}
