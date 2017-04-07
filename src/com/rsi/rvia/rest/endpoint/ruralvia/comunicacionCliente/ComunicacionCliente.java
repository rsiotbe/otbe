package com.rsi.rvia.rest.endpoint.ruralvia.comunicacionCliente;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rsi.rvia.rest.client.OperationManager;

@Path("/messages/mobile")
public class ComunicacionCliente
{
    private static Logger pLog = LoggerFactory.getLogger(ComunicacionCliente.class);

    /**
     * Comprueba si el cliente tiene mensajes pendientes de leer
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se si el cliente tiene mensajes sin leer
     * @throws Exception
     */
    @GET
    @Path("/unreaded")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response MessagesUnreaded(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
            throws Exception
    {
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_HTML_TYPE);
        pLog.info("Mensajes no leidos");
        return pReturn;
    }

    /**
     * Obtiene el listado de mensajes de la bandeja de entrada del cliente.
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de mensajes
     * @throws Exception
     */
    @GET
    @Path("/ReceivedList")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response MessagesReceivedList(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
            throws Exception
    {
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_HTML_TYPE);
        pLog.info("Lista de mensajes");
        return pReturn;
    }
    
    /**
     * Obtiene el listado de mensajes de la bandeja de borrados del cliente.
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de mensajes
     * @throws Exception
     */
    @GET
    @Path("/deletedList")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response MessageDeletedList(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
            throws Exception
    {
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_HTML_TYPE);
        pLog.info("Lista de mensajes");
        return pReturn;
    }
    
    /**
     * Obtiene el listado de mensajes de la bandeja de enviados del cliente.
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de mensajes
     * @throws Exception
     */
    @GET
    @Path("/sentList")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response MessageSentList(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
            throws Exception
    {
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_HTML_TYPE);
        pLog.info("Lista de mensajes");
        return pReturn;
    }
    
    /**
     * Obtiene el detalle de un mensaje.
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     * @throws Exception
     */
    @GET
    @Path("/detail")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response MessageDetails(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
            throws Exception
    { 
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_HTML_TYPE);
        pLog.info("Detalle de Mensajes");
        return pReturn;
    }

    /**
     * Envía un nuevo mensaje.
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     * @throws Exception
     */
    @POST
    @Path("/send")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response sendMessage(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
            throws Exception
    {
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_HTML_TYPE);
        pLog.info("Enviar mensaje");
        return pReturn;
    }

    /**
     * Marca la lísta de mensajes recibidos como borrados.
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     * @throws Exception
     */
    @PUT
    @Path("/delete")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response deleteMessage(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
            throws Exception
    {
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_HTML_TYPE);
        pLog.info("Borrar Mensaje");
        return pReturn;
    }


    /**
     * Restaura la lista de mensajes recibidos.
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     * @throws Exception
     */
    @PUT
    @Path("/restore")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response restoreMessage(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
            throws Exception
    {
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_HTML_TYPE);
        pLog.info("restaurar Mensajes");
        return pReturn;
    }
}
