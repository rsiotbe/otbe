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
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.Constants;
import com.rsi.Constants.SimulatorType;
import com.rsi.rvia.rest.client.OperationManager;
import com.rsi.rvia.rest.simulators.SimulatorsManager;

@Path("/simuladores")
public class Card
{
    private static Logger pLog = LoggerFactory.getLogger(Card.class);

    @GET
    @Path("/{idEntidad}/cards")
    @Produces({ MediaType.TEXT_HTML })
    @Consumes({ MediaType.APPLICATION_XHTML_XML, MediaType.TEXT_HTML, MediaType.APPLICATION_FORM_URLENCODED,
            "application/x-ms-application" })
    public Response getHtml(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        return getLanguageHtml(pRequest, pUriInfo, null);
    }

    @GET
    @Path("/{idEntidad}/cards/{idioma}")
    @Produces({ MediaType.TEXT_HTML })
    @Consumes({ MediaType.APPLICATION_XHTML_XML, MediaType.TEXT_HTML, MediaType.APPLICATION_FORM_URLENCODED,
            "application/x-ms-application" })
    public Response getLanguageHtml(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            @PathParam("idioma") String strLanguage)
    {
        Response pReturn = OperationManager.processTemplate(pRequest, pUriInfo, false);
        return pReturn;
    }

    @GET
    @Path("/{idEntidad}/cards")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCardList(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            @PathParam("idEntidad") String idEntidad) throws Exception
    {
        Response pResponse = process(pRequest, pUriInfo, idEntidad, null, null, MediaType.APPLICATION_JSON_TYPE);
        return pResponse;
    }

    @GET
    @Path("/{idEntidad}/cards/{idioma}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLanguageCardList(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            @PathParam("idEntidad") String idEntidad, @PathParam("idioma") String strLanguage) throws Exception
    {
        Response pResponse = process(pRequest, pUriInfo, idEntidad, null, strLanguage, MediaType.APPLICATION_JSON_TYPE);
        return pResponse;
    }

    @GET
    @Path("/cards/{idCard}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCard(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            @PathParam("idCard") String idCard) throws Exception
    {
        Response pResponse = process(pRequest, pUriInfo, null, idCard, null, MediaType.APPLICATION_JSON_TYPE);
        return pResponse;
    }

    @GET
    @Path("/cards/{idCard}/{idioma}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLanguageCard(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            @PathParam("idCard") String idCard, @PathParam("idioma") String strLanguage) throws Exception
    {
        Response pResponse = process(pRequest, pUriInfo, null, idCard, strLanguage, MediaType.APPLICATION_JSON_TYPE);
        return pResponse;
    }

    /**
     * Processes the passed request.
     * 
     * @param pRequest
     * @param pUriInfo
     * @param strNRBEName
     * @param strCardId
     * @param strLanguage
     * @param pMediaType
     * @return
     */
    private Response process(HttpServletRequest pRequest, UriInfo pUriInfo, String strNRBEName, String strCardId,
            String strLanguage, MediaType pMediaType)
    {
        pLog.info("entidad: " + strNRBEName);
        pLog.info("tarjeta: " + strCardId);
        pLog.info("idioma: " + strLanguage);
        JSONObject pDataInput = null;
        try
        {
            pDataInput = new JSONObject();
            if (strNRBEName != null)
            {
                // Se obtiene el id de la entidad a partir del nombre simple.
                String strNRBE = SimulatorsManager.getNRBEFromName(strNRBEName);
                pDataInput.put(Constants.SIMULADOR_NRBE, strNRBE);
            }
            // Se compone un objeto json con los datos necesarios para poder procesar la petición.
            pDataInput.put(Constants.SIMULADOR_LANGUAGE, strLanguage);
            pDataInput.put(Constants.SIMULADOR_NRBE_NAME, strNRBEName);
            pDataInput.put(Constants.PARAM_CARD_ID, strCardId);
            pDataInput.put(Constants.SIMULADOR_TYPE, SimulatorType.CARD);
        }
        catch (Exception e)
        {
            pLog.error("Error al procesar la petición de simuladores tarjetas", e);
        }
        Response pReturn = OperationManager.processGenericAPP(pRequest, pUriInfo, pDataInput.toString(), pMediaType);
        pLog.info("Se devuelve la respuesta final al usuario");
        return pReturn;
    }
}
