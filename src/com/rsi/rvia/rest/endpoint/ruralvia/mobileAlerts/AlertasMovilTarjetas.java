package com.rsi.rvia.rest.endpoint.ruralvia.mobileAlerts;

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

@Path("/alerts/mobile/cards")
public class AlertasMovilTarjetas
{
    private static Logger pLog = LoggerFactory.getLogger(AlertasMovilTarjetas.class);

    /**
     * Obtiene el listado de cuentas del cliente.
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     * @throws Exception
     */
    @GET
    @Path("/contracts")
    @Produces({ MediaType.TEXT_HTML })
    @Consumes({ MediaType.APPLICATION_XHTML_XML, MediaType.TEXT_HTML, MediaType.APPLICATION_FORM_URLENCODED,
            "application/x-ms-application" })
    public Response listaDeContratos(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
            throws Exception
    { // return Response.ok("Lista de contratos").build();
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_HTML_TYPE);
        pLog.info("Lista de contratos");
        return pReturn;
    }

    @GET
    @Path("/contracts")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response listaDeContratosJSON(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
            throws Exception
    {
        // return Response.ok("Lista de contratos").build();
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_HTML_TYPE);
        pLog.info("Lista de contratos");
        return pReturn;
    }

    /**
     * Obtiene el listado de emisores de asociados a una cuenta.
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     * @throws Exception
     */
    @GET
    @Path("/{idContract}/transmitter")
    @Produces({ MediaType.TEXT_HTML })
    @Consumes({ MediaType.APPLICATION_XHTML_XML, MediaType.TEXT_HTML, MediaType.APPLICATION_FORM_URLENCODED,
            "application/x-ms-application" })
    public Response listaDeEmisores(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
            throws Exception
    { // return Response.ok("Lista de contratos").build();
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Lista de contratos");
        return pReturn;
    }

    /**
     * Obtiene el listado de emisores de decibos para una cuenta.
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     * @throws Exception
     */
    @GET
    @Path("/{idContract}/transmitter")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listaDeEmisoresJSONbyGET(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            String strData) throws Exception
    {
        // return Response.ok("Lista de contratos").build();
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Lista de emisores");
        return pReturn;
    }

    /**
     * Obtiene el listado de emisores de decibos para una cuenta.
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     * @throws Exception
     */
    @POST
    @Path("/{idContract}/transmitter")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listaDeEmisoresJSONbyPOST(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            String strData) throws Exception
    {
        // return Response.ok("Lista de contratos").build();
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Lista de emisores");
        return pReturn;
    }

    /**
     * Obtiene el listado de emisores de decibos para una cuenta.
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     * @throws Exception
     */
    @GET
    @Path("/{idContract}/transmitter/{idTransmitter}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response datosTransmitterJSONGet(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            String strData) throws Exception
    {
        // return Response.ok("Lista de contratos").build();
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Datos de un emisor previa baja");
        return pReturn;
    }

    /**
     * Obtiene el listado de emisores de decibos para una cuenta.
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     * @throws Exception
     */
    @POST
    @Path("/{idContract}/transmitter/{idTransmitter}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response datosTransmitterJSONPost(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            String strData) throws Exception
    {
        // return Response.ok("Lista de contratos").build();
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Datos de un emisor previa baja");
        return pReturn;
    }
}
