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
import com.rsi.rvia.rest.client.OperationManager;

@Path("/simuladores")
public class Card
{
    private static Logger pLog = LoggerFactory.getLogger(Card.class);

    @GET
    @Path("/tarjeta/{codigoEntidad}/{codigoLinea}/{grupoProductos}/{producto}/{tarifa}/{panToken}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getSimulatorPrivateCard(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            @PathParam("codigoEntidad") String strNRBEName, @PathParam("codigoLinea") String strLinea,
            @PathParam("grupoProductos") String strProducto, @PathParam("producto") String strGProducto,
            @PathParam("tarifa") String strTarifa, @PathParam("panToken") String strToken) throws Exception
    {
        return this.process(pRequest, pUriInfo, strNRBEName, strLinea, strProducto, strGProducto, strTarifa, strToken, MediaType.APPLICATION_JSON_TYPE);
    }

    @GET
    @Path("/tarjeta/{codigoEntidad}/{panToken}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getSimulatorPrivateCard(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            @PathParam("codigoEntidad") String strNRBEName, @PathParam("panToken") String strToken) throws Exception
    {
        return this.process(pRequest, pUriInfo, strNRBEName, null, null, null, null, strToken, MediaType.APPLICATION_JSON_TYPE);
    }

    @GET
    @Path("/tarjeta/publico/{strData}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getSimulatorPrivateCard(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            @PathParam("strData") String strData) throws Exception
    {
        return this.process(pRequest, pUriInfo, strData, MediaType.APPLICATION_JSON_TYPE);
    }

    @GET
    @Path("/cards/{idEntidad}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllCardList(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            @PathParam("idEntidad") String idEntidad) throws Exception
    {
        return this.processCardList(pRequest, pUriInfo, idEntidad, MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Processes the passed request.
     * 
     * @param pRequest
     * @param pUriInfo
     * @param strNRBEName
     * @param strLoanName
     * @param strLanguage
     * @param pMediaType
     * @return
     */
    private Response process(HttpServletRequest pRequest, UriInfo pUriInfo, String strEntidad, String strLinea,
            String strProducto, String strGProducto, String strTarifa, String strToken, MediaType pMediaType)
    {
        pLog.info("Card.process:entidad: " + strEntidad);
        // Response pReturn = OperationManager.processDataFromSimulators(pRequest, pUriInfo, strNRBEName,
        // SimulatorType.PERSONAL, strLoanName, strLanguage, pMediaType);
        Response pReturn = OperationManager.processDataFromSimulators(pRequest, pUriInfo, strEntidad, strLinea, strProducto, strGProducto, strTarifa, strToken, pMediaType);
        pLog.info("Card.process:Se devuelve la respuesta final al usuario");
        return pReturn;
    }

    /**
     * Processes the passed request.
     * 
     * @param pRequest
     * @param pUriInfo
     * @param strNRBEName
     * @param strLoanName
     * @param strLanguage
     * @param pMediaType
     * @return
     */
    private Response process(HttpServletRequest pRequest, UriInfo pUriInfo, String strData, MediaType pMediaType)
    {
        pLog.info("Card.process:entidad: " + strData);
        JSONObject jsonData = null;
        Response pReturn = null;
        try
        {
            jsonData = new JSONObject(strData);
            pReturn = OperationManager.processDataFromSimulators(pRequest, pUriInfo, jsonData, pMediaType);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        pLog.info("Card.process:Se devuelve la respuesta final al usuario");
        return pReturn;
    }

    /**
     * Processes the passed request.
     * 
     * @param pRequest
     * @param pUriInfo
     * @param strNRBEName
     * @param strLoanName
     * @param strLanguage
     * @param pMediaType
     * @return
     */
    private Response processCardList(HttpServletRequest pRequest, UriInfo pUriInfo, String strEntidad,
            MediaType pMediaType)
    {
        pLog.info("Card.processCardList:entidad: " + strEntidad);
        pReturn = OperationManager.processCardListEntity(pRequest, pUriInfo, strEntidad, pMediaType);
        pLog.info("Card.process:Se devuelve la respuesta final al usuario");
        return pReturn;
    }
}
