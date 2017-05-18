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

/**
 * The Class Tarjeta Prepago.
 */
@Path("/cards/prepaid")
public class TarjetaPrepago
{
    /** The p log. */
    private static Logger pLog = LoggerFactory.getLogger(TarjetaPrepago.class);

    /**
     * Muestra el listado de tarjetas (Tarifas) que comercializa la entidad para este producto.
     * 
     * @param pRequest
     *            the request
     * @param pUriInfo
     *            the uri info
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado las tarjetas de crédito
     *         disponibles
     */
    @GET
    @Path("/rates")
    @Produces({ MediaType.TEXT_HTML })
    public Response getPrepaidCardList(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtienen el listado de tarjetas prepago disponibles.");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Se finaliza la obtencion de el listado de tarjetas prepago disponibles.");
        return pReturn;
    }

    /**
     * Muestra los detalles de la tarifa seleccionada de la tarjeta prepago, como pueden ser los límites, las
     * comisiones, etc.
     * 
     * @param pRequest
     *            the request
     * @param pUriInfo
     *            the uri info
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el detalle de la tarjeta prepago.
     */
    @GET
    @Path("/{idLinea}/{idGrupo}/{idPdv}/{idTrfa}")
    @Produces({ MediaType.TEXT_HTML })
    public Response getPrepaidCardDetail(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtienen el detalle la tarjeta prepago.");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Se finaliza la obtencion de el detalle la tarjeta prepago.");
        return pReturn;
    }

    /**
     * El cliente selecciona la cuenta de domiciliación y la imagen visual de la tarjeta. Además de la aceptación de las
     * condiciones generales de servicio y la LOPD, así como la introducción de la firma.
     * 
     * @param pRequest
     *            the request
     * @param pUriInfo
     *            the uri info
     * @return Objeto que contiene la respuesta de la firma del contrato de tarjeta prepago.
     */
    @GET
    @Path("/{idLinea}/{idGrupo}/{idPdv}/{idTrfa}/contract")
    @Produces({ MediaType.TEXT_HTML })
    public Response getPrepaidCardContract(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtienen los datos relativos a la cuenta domiciliacion, LOPD, imagen tarjeta y también los datos de introducción de la firma de tarjeta prepago.");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Se finaliza la obtencion de los datos relativos a la cuenta domiciliacion, LOPD, imagen tarjeta y también los datos de introducción de la firma de tarjeta prepago.");
        return pReturn;
    }

    /**
     * Se trata de la pantalla de respuesta tras la introducción de la firma. El resultado puede ser Procesado
     * (Aceptado) o Rechazado.
     * 
     * @param pRequest
     *            the request
     * @param pUriInfo
     *            the uri info
     * @return Objeto que contiene la respuesta tras la introducción de la firma.
     */
    @POST
    @Path("/{idLinea}/{idGrupo}/{idPdv}/{idTrfa}/signature")
    @Produces({ MediaType.TEXT_HTML })
    public Response getPrepaidCardSignature(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se llama a la operativa de firma tarjeta prepago.");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Se finaliza la llamada a la operativa de firma tarjeta prepago.");
        return pReturn;
    }
}
