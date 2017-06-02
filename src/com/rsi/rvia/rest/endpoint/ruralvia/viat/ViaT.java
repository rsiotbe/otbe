package com.rsi.rvia.rest.endpoint.ruralvia.viat;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
import com.rsi.Constants;
import com.rsi.rvia.rest.client.OperationManager;
import com.rsi.rvia.rest.endpoint.ruralvia.prestamos.PrestamoPersonal;

@Path("/cards/viat")
public class ViaT
{
    private static Logger pLog = LoggerFactory.getLogger(PrestamoPersonal.class);

    @GET
    @Path("/rates")
    @Produces({ MediaType.TEXT_HTML })
    @Consumes({ MediaType.APPLICATION_XHTML_XML, MediaType.TEXT_HTML, MediaType.APPLICATION_FORM_URLENCODED,
            "application/x-ms-application" })
    public Response getSolicitudHtml(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se solicita el template HTML para ViaT");
        Response pReturn = OperationManager.processTemplate(pRequest, pUriInfo, true);
        pLog.info("Se finaliza la solicitud del template HTML para ViaT");
        return pReturn;
    }

    @GET
    @Path("/rates")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response getSolicitud(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtienen las tarjetas disponibles");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Se finaliza la obtencion de las tarjetas disponibles");
        return pReturn;
    }

    @GET
    @Path("/lopd")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response getLopd(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtienen los datos lopd");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Se finaliza la obtencion de datos lopd");
        return pReturn;
    }

    @GET
    @Path("/{idLinea}/{idGrupo}/{idPdv}/{idTrfa}/contract")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response getDatosTarjetas(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtienen los datos de la tarjeta");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Se finaliza la obtencion de datos de la tarjeta");
        return pReturn;
    }

    @GET
    @Path("/{idLinea}/{idGrupo}/{idPdv}/{idTrfa}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response getDetallesTarjetas(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtienen los detalles tarjetas");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Se finaliza la obtencion de los detalles de las tarjetas");
        return pReturn;
    }

    @GET
    @Path("/scoring/formdata")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response getDatosPersonales(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtienen los datos personales para scoring");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Se finaliza la obtencion de los datos personales para scoring");
        return pReturn;
    }

    @POST
    @Path("/{idLinea}/{idGrupo}/{idPdv}/{idTrfa}/scoring")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response getScoring(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se realiza la llamada al scoring");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Se finaliza la llamada al scoring");
        return pReturn;
    }

    @POST
    @Path("/{idLinea}/{idGrupo}/{idPdv}/{idTrfa}/signature")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response getFirma(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se realiza la llamada a la firma");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Se finaliza la llamada a la firma");
        return pReturn;
    }

    /**
     * Muestra la información relativa a los distintos Grupos de CNAE disponibles.
     * 
     * @param pRequest
     *            the request
     * @param pUriInfo
     *            the uri info
     * @return the det tarifas
     */
    @GET
    @Path("/scoring/formdata/cnae")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getSubGruposCNAEViaT(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtienen los datos relativos al listado de grupos CNAE para ViaT");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Se finaliza la obtencion de los datos relativos  al listado de grupos CNAE para ViaT");
        return pReturn;
    }

    /**
     * Devuelve PDF del documento INE de ViaT.
     * 
     * @param pRequest
     *            the request
     * @param pUriInfo
     *            the uri info
     * @return pdf con contrato de ViaT
     */
    @GET
    @Path("/{idLinea}/{idGrupo}/{idPdv}/{idTrfa}/pdf")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces(Constants.HTTP_HEADER_MEDIATYPE_PDF)
    public Response getpdfViaT(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        try
        {
            pLog.info("Se obtiene el pdf del contrato para ViaT");
            Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
            String strPdfBase64 = (new JSONObject(pReturn.getEntity().toString())).getJSONObject("response").getJSONObject("data").getString("buffer");
            byte[] abFile = org.apache.commons.codec.binary.Base64.decodeBase64(strPdfBase64.getBytes());
            String strFileName = "INE.pdf";
            String strHeaderDownload = "attachment; filename=\"" + strFileName + "\"";
            pLog.info("Se finaliza la obtencion de el pdf del contrato para ViaT");
            return Response.ok(abFile, Constants.HTTP_HEADER_MEDIATYPE_PDF).header("Content-Disposition", strHeaderDownload).build();
        }
        catch (Exception e)
        {
            return Response.serverError().build();
        }
    }
}
