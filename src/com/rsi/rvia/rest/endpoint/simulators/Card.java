package com.rsi.rvia.rest.endpoint.simulators;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rsi.Constants.SimulatorType;
import com.rsi.rvia.rest.client.OperationManager;

@Path("/simuladores")
public class Card
{
    private static Logger pLog = LoggerFactory.getLogger(Card.class);

    @GET
    @Path("/{idEntidad}/cards")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCardList(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            @PathParam("idEntidad") String idEntidad) throws Exception
    {
        //return this.processCardList(pRequest, pUriInfo, idEntidad, MediaType.APPLICATION_JSON_TYPE);
    	pLog.info("Card.getAllCardList");
    	Response pResponse = OperationManager.proccessDataFromDDBB(pRequest, idEntidad, null, MediaType.APPLICATION_JSON_TYPE);
    	pLog.info("Card.getAllCardList -- > salgo");
    	return pResponse;
    }

    @GET
    @Path("/{idEntidad}/cards/{idioma}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLanguageCardList(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            @PathParam("idEntidad") String idEntidad, @PathParam("idioma") String strLanguage) throws Exception
    {
    	pLog.info("Card.getAllCardList");
    	Response pResponse = OperationManager.proccessDataFromDDBB(pRequest, idEntidad, strLanguage, MediaType.APPLICATION_JSON_TYPE);
    	pLog.info("Card.getAllCardList -- > salgo");
    	return pResponse;
    }

    @GET
    @Path("/{idCard}/cards/simpublico")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCardSimulator(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            @PathParam("idCard") String idCard) throws Exception
    {
    	pLog.info("Card.getCardSimulator");
    	Response pResponse = OperationManager.processDataFromSimulators(pRequest, pUriInfo, null, SimulatorType.CARD, idCard, null, MediaType.APPLICATION_JSON_TYPE);
    	pLog.info("Card.getCardSimulator -- > salgo");
    	return pResponse;
    }

    @GET
    @Path("/{idCard}/cards/simpublico/{idioma}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLanguageCardSimulator(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            @PathParam("idCard") String idCard, @PathParam("idioma") String strLanguage) throws Exception
    {
    	pLog.info("Card.getCardSimulator");
    	Response pResponse = OperationManager.processDataFromSimulators(pRequest, pUriInfo, null, SimulatorType.CARD, idCard, strLanguage, MediaType.APPLICATION_JSON_TYPE);
    	pLog.info("Card.getCardSimulator -- > salgo");
    	return pResponse;
    }

}
