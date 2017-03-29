package com.rsi.rvia.rest.endpoint.rsiapi;

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
 * Servicio de login. Clase que responde a las peticiones REST
 */
@Path("/rsiapi")
public class Login
{
    private static Logger pLog = LoggerFactory.getLogger(Login.class);

    /**
     * Obtiene el listado completo de tarjetas de un usuario
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     */
    @GET
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginProcess(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, "", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Login");
        return pReturn;
    }
}
