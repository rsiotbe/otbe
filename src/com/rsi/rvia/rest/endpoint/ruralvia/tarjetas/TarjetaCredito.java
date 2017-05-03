package com.rsi.rvia.rest.endpoint.ruralvia.tarjetas;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.client.OperationManager;

// TODO: Auto-generated Javadoc
/**
 * The Class TarjetaCredito.
 */
@Path("/tarjetacredito")
public class TarjetaCredito
{
    /** The p log. */
    private static Logger pLog = LoggerFactory.getLogger(TarjetaCredito.class);

    /**
     * Obtiene el listado listado de las tarjetas de crédito disponibles.
     * 
     * @param pRequest
     *            the request
     * @param pUriInfo
     *            the uri info
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado las tarjetas de crédito
     *         disponibles
     */
    @GET
    @Path("/solicitud")
    @Produces({ MediaType.TEXT_HTML })
    public Response getSolicitud(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro Solicitud Tarjeta de Credito");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Solicitud Tarjeta de Credito");
        return pReturn;
    }

    /**
     * Gets the dato prestamo.
     * 
     * @param pRequest
     *            the request
     * @param pUriInfo
     *            the uri info
     * @return the dato prestamo
     */
    @GET
    @Path("/datostarjetascredito")
    @Produces({ MediaType.TEXT_HTML })
    public Response getDatosTarjetasCredito(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro datos tarjetas credito");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Datos tarjetas credito");
        return pReturn;
    }

    /**
     * Gets the det tarifas.
     * 
     * @param pRequest
     *            the request
     * @param pUriInfo
     *            the uri info
     * @return the det tarifas
     */
    @GET
    @Path("/detalletarifas")
    @Produces({ MediaType.TEXT_HTML })
    public Response getDetTarifas(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro detalle tarifas");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Detalle tarifas");
        return pReturn;
    }

    /**
     * Gets the lopd.
     * 
     * @param pRequest
     *            the request
     * @param pUriInfo
     *            the uri info
     * @return the lopd
     */
    @GET
    @Path("/lopd")
    @Produces({ MediaType.TEXT_HTML })
    public Response getLopd(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro lopd");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> LOPD");
        return pReturn;
    }

    /**
     * Gets the datos persona.
     * 
     * @param pRequest
     *            the request
     * @param pUriInfo
     *            the uri info
     * @return the datos persona
     */
    @POST
    @Path("/datospersona")
    @Produces({ MediaType.TEXT_HTML })
    public Response getDatosPersona(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro datos persona");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Datos persona");
        return pReturn;
    }

    /**
     * Gets the scoring.
     * 
     * @param pRequest
     *            the request
     * @param pUriInfo
     *            the uri info
     * @return the scoring
     */
    @POST
    @Path("/scoring")
    @Produces({ MediaType.TEXT_HTML })
    public Response getScoring(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro scoring");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Scoring");
        return pReturn;
    }
}
