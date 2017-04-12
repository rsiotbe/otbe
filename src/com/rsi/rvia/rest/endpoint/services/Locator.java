package com.rsi.rvia.rest.endpoint.services;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rsi.rvia.rest.client.OperationManager;
import com.rsi.rvia.rest.response.RviaRestResponse;

@Path("/locator")
public class Locator
{
    private static Logger pLog = LoggerFactory.getLogger(Locator.class);

    @GET
    @Path("/cashier")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cashierGET(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
    	 pLog.info("Se recibe una peticion de cashier que genera "
                 + MediaType.APPLICATION_JSON);
    	  
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);          		
        pLog.info("Se devuelve la respuesta final al usuario");
        return pReturn;
    }

    
    @GET
    @Path("/office")
    @Produces(MediaType.APPLICATION_JSON)
    public Response officeGET(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se recibe una peticion de office que genera "
                + MediaType.APPLICATION_JSON);
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);          		
        pLog.info("Se devuelve la respuesta final al usuario");
        return pReturn;
    }

}
