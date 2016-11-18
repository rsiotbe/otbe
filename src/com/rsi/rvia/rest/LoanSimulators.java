package com.rsi.rvia.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
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
import com.rsi.rvia.rest.client.OperationManager;

@Path("/simuladores")
public class LoanSimulators
{
    private static final String MEDIATYPE_PDF    = "application/pdf";
    private static final String PDF_RENDERER_URL = "http://docrender.risa/docrender/rest/render/download/pdf/";
    private static Logger       pLog             = LoggerFactory.getLogger(LoanSimulators.class);

    @GET
    @Path("{entidad}")
    @Produces({ MediaType.TEXT_HTML })
    @Consumes({ MediaType.APPLICATION_XHTML_XML, MediaType.TEXT_HTML, MediaType.APPLICATION_FORM_URLENCODED,
            "application/x-ms-application" })
    public Response getAllSimulatorsDefaultLanguageGet(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            @PathParam("entidad") String strNRBEName) throws Exception
    {
        return process(pRequest, pUriInfo, strNRBEName, null, null, MediaType.TEXT_HTML_TYPE);
    }

    @GET
    @Path("{entidad}/{idioma: [a-z]{2}[-_][A-Z]{2}}")
    @Produces({ MediaType.TEXT_HTML })
    @Consumes({ MediaType.APPLICATION_XHTML_XML, MediaType.TEXT_HTML, MediaType.APPLICATION_FORM_URLENCODED,
            "application/x-ms-application" })
    public Response getAllSimulatorsOneLanguageGet(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            @PathParam("entidad") String strBankName, @PathParam("idioma") String strLanguage) throws Exception
    {
        return process(pRequest, pUriInfo, strBankName, null, strLanguage, MediaType.TEXT_HTML_TYPE);
    }

    @GET
    @Path("{entidad}/{nombreSimulador}")
    @Produces({ MediaType.TEXT_HTML })
    @Consumes({ MediaType.APPLICATION_XHTML_XML, MediaType.TEXT_HTML, MediaType.APPLICATION_FORM_URLENCODED,
            "application/x-ms-application" })
    public Response getSimulatorDefaultLanguageGet(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            @PathParam("entidad") String strBankName, @PathParam("nombreSimulador") String strLoanName) throws Exception
    {
        return process(pRequest, pUriInfo, strBankName, strLoanName, null, MediaType.TEXT_HTML_TYPE);
    }

    @GET
    @Path("{entidad}/{nombreSimulador}/{idioma}")
    @Produces({ MediaType.TEXT_HTML })
    @Consumes({ MediaType.APPLICATION_XHTML_XML, MediaType.TEXT_HTML, MediaType.APPLICATION_FORM_URLENCODED,
            "application/x-ms-application" })
    public Response getSimulatorOneLanguageGet(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            @PathParam("entidad") String strBankName, @PathParam("nombreSimulador") String strLoanName,
            @PathParam("idioma") String strLanguage) throws Exception
    {
        return process(pRequest, pUriInfo, strBankName, strLoanName, strLanguage, MediaType.TEXT_HTML_TYPE);
    }

    @POST
    @Path("{entidad}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllSimulatorsDefaultLanguagePost(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            @PathParam("entidad") String strBankName) throws Exception
    {
        return process(pRequest, pUriInfo, strBankName, null, null, MediaType.APPLICATION_JSON_TYPE);
    }

    @POST
    @Path("{entidad}/{idioma: [a-z]{2}[-_][A-Z]{2}}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllSimulatorsOneLanguagePost(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            @PathParam("entidad") String strBankName, @PathParam("idioma") String strLanguage) throws Exception
    {
        return process(pRequest, pUriInfo, strBankName, null, strLanguage, MediaType.APPLICATION_JSON_TYPE);
    }

    @POST
    @Path("{entidad}/{nombreSimulador}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getSimulatorDefaultLanguagePost(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            @PathParam("entidad") String strBankName, @PathParam("nombreSimulador") String strLoanName) throws Exception
    {
        return process(pRequest, pUriInfo, strBankName, strLoanName, null, MediaType.APPLICATION_JSON_TYPE);
    }

    @POST
    @Path("{entidad}/{nombreSimulador}/{idioma}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getSimulatorOneLanguagePost(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            @PathParam("entidad") String strBankName, @PathParam("nombreSimulador") String strLoanName,
            @PathParam("idioma") String strLanguage) throws Exception
    {
        return process(pRequest, pUriInfo, strBankName, strLoanName, strLanguage, MediaType.APPLICATION_JSON_TYPE);
    }

    @POST
    @Path("/pdf")
    @Produces(MEDIATYPE_PDF)
    @Consumes({ MediaType.APPLICATION_XHTML_XML, MediaType.TEXT_HTML, MediaType.APPLICATION_FORM_URLENCODED,
            "application/x-ms-application" })
    public Response getSimulatorPdfPrinter(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse,
            @Context UriInfo pUriInfo, @FormParam("data") String data) throws Exception
    {
        String strJsonData = URLDecoder.decode(data, "UTF-8");
        Response processPdfDownload = OperationManager.processGenericAPP(pRequest, pUriInfo, strJsonData, MediaType.APPLICATION_JSON_TYPE);
        String pdfId = (new JSONObject(processPdfDownload.getEntity().toString())).getJSONObject("response").getJSONObject("data").getString("id");
        pLog.info("Se recibe una petición de descarga PDF con ID " + pdfId);
        ByteArrayOutputStream pdf = loadPdf(PDF_RENDERER_URL + pdfId);
        pResponse.setHeader("Content-Type", MEDIATYPE_PDF);
        pResponse.setHeader("Content-Disposition", "attachment; filename=\""
                + new SimpleDateFormat("'simulacion_'yyyyMMddHHmm'.pdf'\"").format(new Date()));
        pResponse.setHeader("Content-Length", Integer.toString(pdf.size()));
        pResponse.getOutputStream().write(pdf.toByteArray());
        pResponse.getOutputStream().close();
        return Response.ok().build();
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
        Response pReturn = OperationManager.processDataFromSimulators(pRequest, pUriInfo, strNRBEName, strLoanName, strLanguage, pMediaType);
        pLog.info("Se devuelve la respuesta final al usuario");
        return pReturn;
    }

    /**
     * Downloads the file by URL.
     * 
     * @param fileUrl
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    private ByteArrayOutputStream loadPdf(String fileUrl) throws MalformedURLException, IOException
    {
        InputStream is = new URL(fileUrl).openStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        try
        {
            for (int readNum; (readNum = is.read(buf)) != -1;)
            {
                bos.write(buf, 0, readNum);
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        bos.close();
        is.close();
        return bos;
    }
}
