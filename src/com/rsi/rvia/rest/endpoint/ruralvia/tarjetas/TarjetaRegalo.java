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
 * The Class tarjeta regalo.
 */
@Path("/cards/gift")
public class TarjetaRegalo
{
    /** The p log. */
    private static Logger pLog = LoggerFactory.getLogger(TarjetaRegalo.class);

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
        pLog.info("Se obtienen el listado de tarjetas regalo disponibles.");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Se finaliza la obtencion de el listado de tarjetas regalo disponibles.");
        return pReturn;
    }

    /**
     * Muestra los detalles de la tarifa seleccionada de la tarjeta regalo, como pueden ser los límites, las comisiones,
     * etc.
     * 
     * @param pRequest
     *            the request
     * @param pUriInfo
     *            the uri info
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el detalle de la tarjeta regalo.
     */
    @GET
    @Path("/{idLinea}/{idGrupo}/{idPdv}/{idTrfa}")
    @Produces({ MediaType.TEXT_HTML })
    public Response getPrepaidCardDetail(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtienen el detalle la tarjeta regalo.");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Se finaliza la obtencion de el detalle la tarjeta regalo.");
        return pReturn;
    }

    /**
     * En esta clave página, el cliente selecciona la cuenta de donde desea recargar la tarjeta, el importe a recargar,
     * la imagen visual de la tarjeta, y el nombre y el apellido del beneficiario de la tarjeta. Asimismo, esta clave
     * página incluye la aceptación de las condiciones generales de servicio y la LOPD, así como la introducción de la
     * firma.
     * 
     * @param pRequest
     *            the request
     * @param pUriInfo
     *            the uri info
     * @return Objeto que contiene la respuesta de la firma del contrato de tarjeta regalo.
     */
    @GET
    @Path("/{idLinea}/{idGrupo}/{idPdv}/{idTrfa}/contract")
    @Produces({ MediaType.TEXT_HTML })
    public Response getPrepaidCardContract(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtienen los datos relativos a la cuenta domiciliacion, LOPD, imagen tarjeta y también los datos de introducción de la firma de tarjeta regalo.");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Se finaliza la obtencion de los datos relativos a la cuenta domiciliacion, LOPD, imagen tarjeta y también los datos de introducción de la firma de tarjeta regalo.");
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
        pLog.info("Se llama a la operativa de firma tarjeta regalo.");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Se finaliza la llamada a la operativa de firma tarjeta regalo.");
        return pReturn;
    }
}
