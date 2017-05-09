package com.rsi.rvia.rest.endpoint.ruralvia.userCommunication;

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
public class UserCommunication
{
    private static Logger pLog = LoggerFactory.getLogger(UserCommunication.class);

    /**
     * Comprueba si el cliente tiene mensajes pendientes de leer
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se si el cliente tiene mensajes sin leer
     * @throws Exception
     */
    @GET
    @Path("/unreadedList")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response unreadedMessages(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
            throws Exception
    {
    	Response pReturn = OperationManager.processGenericAPP(pRequest, pUriInfo, strData, MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Mensajes no leidos");
        return pReturn;
    }

    /**
     * Comprueba si el cliente tiene mensajes pendientes de leer
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se si el cliente tiene mensajes sin leer
     * @throws Exception
     */
    @GET
    @Path("/messageTypes")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response messageTypes(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
            throws Exception
    {
    	Response pReturn = OperationManager.processGenericAPP(pRequest, pUriInfo, strData, MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Tipos de mensaje");
        return pReturn;
    }
    
    /**
     * Obtiene el listado de mensajes de la bandeja de entrada del cliente.
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de mensajes
     * @throws Exception
     */
    @GET
    @Path("/receivedList")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response receivedMessagesList(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
            throws Exception
    {
    	Response pReturn = OperationManager.processGenericAPP(pRequest, pUriInfo, strData, MediaType.APPLICATION_JSON_TYPE);
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
    public Response deletedMessagesList(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
            throws Exception
    {
    	Response pReturn = OperationManager.processGenericAPP(pRequest, pUriInfo, strData, MediaType.APPLICATION_JSON_TYPE);
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
    @Path("/archivedList")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response archivedMessagesList(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
            throws Exception
    {
    	Response pReturn = OperationManager.processGenericAPP(pRequest, pUriInfo, strData, MediaType.APPLICATION_JSON_TYPE);
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
    public Response sentMessageList(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
            throws Exception
    {
    	Response pReturn = OperationManager.processGenericAPP(pRequest, pUriInfo, strData, MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Lista de mensajes");
        return pReturn;
    }
    
    /**
     * Obtiene el detalle de un mensaje.
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de mensajes
     * @throws Exception
     */
    @GET
    @Path("/historyList")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response historyMessageList(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
            throws Exception
    { 
    	Response pReturn = OperationManager.processGenericAPP(pRequest, pUriInfo, strData, MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Historico de Mensajes");
        return pReturn;
    }

    /**
     * Obtiene el listado de historicos de un mensaje.
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de mensajes
     * @throws Exception
     */
    @GET
    @Path("/detail")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response messageDetails(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
            throws Exception
    { 
    	Response pReturn = OperationManager.processGenericAPP(pRequest, pUriInfo, strData, MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Detalle de Mensajes");
        return pReturn;
    }
    
    /**
     * Envía un nuevo mensaje.
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     * @throws Exception
     */
    @GET
    @Path("/send")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response sendMessage(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
            throws Exception
    {
    	Response pReturn = OperationManager.processGenericAPP(pRequest, pUriInfo, strData, MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Enviar mensaje");
        return pReturn;
    }

    /**
     * Marca la lísta de mensajes recibidos como borrados.
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     * @throws Exception
     */
    @GET
    @Path("/delete")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response deleteMessage(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
            throws Exception
    {
    	Response pReturn = OperationManager.processGenericAPP(pRequest, pUriInfo, strData, MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Borrar Mensaje");
        return pReturn;
    }


    /**
     * Restaura la lista de mensajes recibidos.
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     * @throws Exception
     */
    @GET
    @Path("/restore")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response restoreMessage(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
            throws Exception
    {
    	Response pReturn = OperationManager.processGenericAPP(pRequest, pUriInfo, strData, MediaType.APPLICATION_JSON_TYPE);
        pLog.info("restaurar Mensajes");
        return pReturn;
    }
}
