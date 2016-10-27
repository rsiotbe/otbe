package com.rsi.rvia.rest;

import javax.servlet.http.HttpServletRequest;
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


/**
 * Clase que responde a las peticiones REST para las acciones relacionadas con el control de costes
 *
 */
@Path("/rsiapi") 
public class Contratos
{
    private static Logger pLog = LoggerFactory.getLogger(Contratos.class);
    
    /**
     * Obtiene el listado completo de acuerdos de un usuario
     * En el documento Webservice1 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     * @throws Exception 
     */
    @GET
    @Path("/contracts") 
   @Produces(MediaType.APPLICATION_JSON)
    public Response listaDeContratos(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            String strData) throws Exception
    {   
        //return Response.ok("Lista de contratos").build();
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Lista de contratos");
        return pReturn;     
    }   
    /**
     * Obtiene el listado de acuerdos de un usuario  para una línea concreta
     * En el documento Webservice1 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     * @throws Exception 
     */
    @GET
    @Path("/contracts/line/{codLinea}") 
   @Produces(MediaType.APPLICATION_JSON)
    public Response listaDeContratosPorLinea(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            String strData) throws Exception
    {   
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);  
        pLog.info("Lista de contratos - de una línea concreta.");
        return pReturn;
    }

}
