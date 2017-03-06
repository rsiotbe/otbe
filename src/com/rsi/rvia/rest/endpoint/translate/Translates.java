package com.rsi.rvia.rest.endpoint.translate;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.Constants;
import com.rsi.rvia.rest.client.OperationManager;

@Path("/translate")
public class Translates
{
    private static Logger pLog = LoggerFactory.getLogger(Translates.class);

    @GET
    @Path("{appName}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XHTML_XML, MediaType.TEXT_HTML,
            MediaType.APPLICATION_FORM_URLENCODED, "application/x-ms-application" })
    public Response getTranslateAllLanguageGet(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            @PathParam("appName") String strAppName) throws Exception
    {
        return process(pRequest, pUriInfo, strAppName, "ALL", MediaType.APPLICATION_JSON_TYPE);
    }

    @GET
    @Path("{appName}/{idioma: [a-z]{2}[-_][A-Z]{2}}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XHTML_XML, MediaType.TEXT_HTML,
            MediaType.APPLICATION_FORM_URLENCODED, "application/x-ms-application" })
    public Response getTranslateLanguageGet(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            @PathParam("appName") String strAppName, @PathParam("idioma") String strLanguage) throws Exception
    {
        return process(pRequest, pUriInfo, strAppName, strLanguage, MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Processes the passed request.
     * 
     * @param pRequest
     * @param pUriInfo
     * @param strAppName
     * @param strLanguage
     * @param pMediaType
     * @return
     */
    private Response process(HttpServletRequest pRequest, UriInfo pUriInfo, String strAppName, String strLanguage,
            MediaType pMediaType)
    {
        pLog.info("Nombre app: " + strAppName);
        pLog.info("idioma: " + strLanguage);
        String strJsonData = "{\"" + Constants.TRANSLATE_APPNAME + "\":\"" + strAppName + "\",\""
                + Constants.TRANSLATE_LANG + "\":\"" + strLanguage + "\"}";
        Response pReturn = OperationManager.processGenericAPP(pRequest, pUriInfo, strJsonData, pMediaType);
        pLog.info("Se devuelve la respuesta final al usuario");
        return pReturn;
    }
}
