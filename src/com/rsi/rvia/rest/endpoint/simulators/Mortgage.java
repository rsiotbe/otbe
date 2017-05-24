package com.rsi.rvia.rest.endpoint.simulators;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
public class Mortgage
{
    private static Logger pLog = LoggerFactory.getLogger(Mortgage.class);

    @GET
    @Path("{entidad}/hipoteca")
    @Produces({ MediaType.TEXT_HTML })
    @Consumes({ MediaType.APPLICATION_XHTML_XML, MediaType.TEXT_HTML, MediaType.APPLICATION_FORM_URLENCODED,
            MediaType.APPLICATION_JSON, "application/x-ms-application" })
    public Response getAllSimulatorsDefaultLanguageGet(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            @PathParam("entidad") String strNRBEName)
    {
        return process(pRequest, pUriInfo, strNRBEName, null, null, OperationManager.getMediaType(pRequest));
    }

    @GET
    @Path("{entidad}/hipoteca/{idioma: [a-z]{2}[-_][A-Z]{2}}")
    @Produces({ MediaType.TEXT_HTML })
    @Consumes({ MediaType.APPLICATION_XHTML_XML, MediaType.TEXT_HTML, MediaType.APPLICATION_FORM_URLENCODED,
            MediaType.APPLICATION_JSON, "application/x-ms-application" })
    public Response getAllSimulatorsOneLanguageGet(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            @PathParam("entidad") String strBankName, @PathParam("idioma") String strLanguage)
    {
        return process(pRequest, pUriInfo, strBankName, null, strLanguage, OperationManager.getMediaType(pRequest));
    }

    @GET
    @Path("{entidad}/hipoteca/{nombreSimulador}")
    @Produces({ MediaType.TEXT_HTML })
    @Consumes({ MediaType.APPLICATION_XHTML_XML, MediaType.TEXT_HTML, MediaType.APPLICATION_FORM_URLENCODED,
            MediaType.APPLICATION_JSON, "application/x-ms-application" })
    public Response getSimulatorDefaultLanguageGet(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            @PathParam("entidad") String strNRBEName)
    {
        return process(pRequest, pUriInfo, strNRBEName, null, null, OperationManager.getMediaType(pRequest));
    }

    @GET
    @Path("{entidad}/hipoteca/{nombreSimulador}/{idioma}")
    @Produces({ MediaType.TEXT_HTML })
    @Consumes({ MediaType.APPLICATION_XHTML_XML, MediaType.TEXT_HTML, MediaType.APPLICATION_FORM_URLENCODED,
            MediaType.APPLICATION_JSON, "application/x-ms-application" })
    public Response getSimulatorOneLanguageGet(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            @PathParam("entidad") String strNRBEName, @PathParam("nombreSimulador") String strLoanName,
            @PathParam("idioma") String strLanguage)
    {
        return process(pRequest, pUriInfo, strNRBEName, null, strLanguage, OperationManager.getMediaType(pRequest));
    }

    @POST
    @Path("{entidad}/hipoteca")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllSimulatorsDefaultLanguagePost(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, @PathParam("entidad") String strNRBEName)
    {
        return process(pRequest, pUriInfo, strNRBEName, null, null, MediaType.APPLICATION_JSON_TYPE);
    }

    @POST
    @Path("{entidad}/hipoteca/{idioma: [a-z]{2}[-_][A-Z]{2}}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllSimulatorsOneLanguagePost(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            @PathParam("entidad") String strNRBEName, @PathParam("idioma") String strLanguage)
    {
        return process(pRequest, pUriInfo, strNRBEName, null, strLanguage, MediaType.APPLICATION_JSON_TYPE);
    }

    @POST
    @Path("{entidad}/hipoteca/{nombreSimulador}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getSimulatorDefaultLanguagePost(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            @PathParam("entidad") String strNRBEName, @PathParam("nombreSimulador") String strLoanName)
    {
        return process(pRequest, pUriInfo, strNRBEName, null, null, MediaType.APPLICATION_JSON_TYPE);
    }

    @POST
    @Path("{entidad}/hipoteca/{nombreSimulador}/{idioma}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getSimulatorOneLanguagePost(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            @PathParam("entidad") String strNRBEName, @PathParam("nombreSimulador") String strLoanName,
            @PathParam("idioma") String strLanguage)
    {
        return process(pRequest, pUriInfo, strNRBEName, null, strLanguage, MediaType.APPLICATION_JSON_TYPE);
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
    private Response process(HttpServletRequest pRequest, UriInfo pUriInfo, String strNRBEName, String strLoanName,
            String strLanguage, MediaType pMediaType)
    {
        pLog.info("entidad: " + strNRBEName);
        pLog.info("nombre: " + strLoanName);
        pLog.info("idioma: " + strLanguage);
        JSONObject pDataInput = null;
        try
        {
            pDataInput = new JSONObject();
            /* se obtiene la entidad dlenguaje */
            String strNRBE = SimulatorsManager.getNRBEFromName(strNRBEName);
            /* se conpone un objeto json con los datos necesarios para poder procesar la petición */
            pDataInput.put(Constants.SIMULADOR_NRBE, strNRBE);
            pDataInput.put(Constants.SIMULADOR_LANGUAGE, strLanguage);
            pDataInput.put(Constants.SIMULADOR_NRBE_NAME, strNRBEName);
            pDataInput.put(Constants.SIMULADOR_SIMPLE_NAME, strLoanName);
            pDataInput.put(Constants.SIMULADOR_TYPE, SimulatorType.MORTGAGE);
        }
        catch (Exception e)
        {
            pLog.error("Error al procesar la peticón de simuladores de hipoteca", e);
        }
        Response pReturn = OperationManager.processGenericAPP(pRequest, pUriInfo, pDataInput.toString(), pMediaType);
        pLog.info("Se devuelve la respuesta final al usuario");
        return pReturn;
    }
}
