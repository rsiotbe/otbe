package com.rsi.rvia.rest.endpoint.simulators;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.client.OperationManager;

@Path("/simuladores")
public class Common
{
    private static final String MEDIATYPE_PDF = "application/pdf";
    // private static final String MEDIATYPE_PDF = "application/vnd.ms.excel";
    private static Logger       pLog          = LoggerFactory.getLogger(Common.class);

    @POST
    @Path("/pdf")
    @Produces(MEDIATYPE_PDF)
    @Consumes({ MediaType.APPLICATION_XHTML_XML, MediaType.TEXT_HTML, MediaType.APPLICATION_FORM_URLENCODED,
            "application/x-ms-application" })
    public Response getSimulatorPdfPrinter(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse,
            @Context UriInfo pUriInfo, @FormParam("data") String data) throws Exception
    {
        String strJsonData;
        Response pResponsePdfGeneration;
        pLog.info("Entra una petición para generar PDF con los datos de simmulación.");
        strJsonData = URLDecoder.decode(data, "UTF-8");
        pResponsePdfGeneration = OperationManager.processGenericAPP(pRequest, pUriInfo, strJsonData,
                MediaType.APPLICATION_JSON_TYPE);
        String strPdfBase64 = (new JSONObject(pResponsePdfGeneration.getEntity().toString())).getJSONObject(
                "response").getJSONObject("data").getString("file");
        byte[] abFile = org.apache.commons.codec.binary.Base64.decodeBase64(strPdfBase64.getBytes());
        SimpleDateFormat pSdf = new SimpleDateFormat("yyyyMMddHHmm");
        String strDate = pSdf.format(new Date());
        String strFileName = "simulacion_" + strDate + ".pdf";
        String strHeaderDownload = "attachment; filename=\"" + strFileName + "\"";
        return Response.ok(abFile, MEDIATYPE_PDF).header("Content-Disposition", strHeaderDownload).build();
    }

    @POST
    @Path("/email")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendEmailToBank(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strJsonData)
    {
        pLog.info("Entra una petición para enviar correo con los datos de simmulación.");
        Response pResponse = OperationManager.processDataFromSimulators(pRequest, pUriInfo, strJsonData,
                MediaType.APPLICATION_JSON_TYPE);
        return pResponse;
    }
}
