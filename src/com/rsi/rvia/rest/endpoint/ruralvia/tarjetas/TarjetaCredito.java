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
     * Obtiene los datos de la tarjeta de crédito que se va a solicitar.
     * 
     * @param pRequest
     *            the request
     * @param pUriInfo
     *            the uri info
     * @return the datos tarjetas credito
     */
    @GET
    @Path("/datos")
    @Produces({ MediaType.TEXT_HTML })
    public Response getDatosTarjetaCredito(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro datos tarjetas credito");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Datos tarjetas credito");
        return pReturn;
    }

    /**
     * Obtiene el detalle de la tarjeta de crédito que se va a solicitar.
     * 
     * @param pRequest
     *            the request
     * @param pUriInfo
     *            the uri info
     * @return the det tarifas
     */
    @GET
    @Path("/detalle")
    @Produces({ MediaType.TEXT_HTML })
    public Response getDetalleTarjetaCredito(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro detalle tarjetas credito");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Detalle tarjetas credito");
        return pReturn;
    }

    /**
     * Recupera los datos de la persona y los literales necesarios para los deplegables.
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
    public Response getDatosPersonaTarjetaCredito(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro datos persona Tarjeta de Crédito");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> datos persona Tarjeta de Crédito");
        return pReturn;
    }

    /**
     * Rellena la entrada con los datos recogidos y hace la llamada al Scoring.
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
    public Response getScoringTarjetaCredito(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro scoring tarjeta credito");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Scoring tarjeta credito");
        return pReturn;
    }

    /**
     * Solicitud scoring rechazado para las tarjetas de credito.
     * 
     * @param pRequest
     *            the request
     * @param pUriInfo
     *            the uri info
     * @return the scoring
     */
    @POST
    @Path("/errscoring")
    @Produces({ MediaType.TEXT_HTML })
    public Response getErrorScoringTarjetaCredito(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro error scoring");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Error scoring");
        return pReturn;
    }

    /**
     * Obtiene las clausulas tratamiento de datos de carácter personal de tarjetas de crédito.
     * 
     * @param pRequest
     *            the request
     * @param pUriInfo
     *            the uri info
     * @return the det tarifas
     */
    @GET
    @Path("/clausulatratamiento")
    @Produces({ MediaType.TEXT_HTML })
    public Response getClausulaTratamientoTarjetaCredito(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro clausula tratamiento de datos tarjeta credito");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" --------->  clausula tratamiento de datos tarjeta credito");
        return pReturn;
    }

    /**
     * Consulta de los subgrupos del cnae, tarjetas de crédito.
     * 
     * @param pRequest
     *            the request
     * @param pUriInfo
     *            the uri info
     * @return the det tarifas
     */
    @GET
    @Path("/subgrupocnae")
    @Produces({ MediaType.TEXT_HTML })
    public Response getSubGruposCNAETarjetaCredito(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro subgrupos CNAE tarjeta credito");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> SubGrupos CNAE tarjeta credito");
        return pReturn;
    }

    /**
     * Da de alta la solicitud y envía un correo a la oficina con los datos de la solicitud de tarjeta de crédito.
     * 
     * @param pRequest
     *            the request
     * @param pUriInfo
     *            the uri info
     * @return the det tarifas
     */
    @GET
    @Path("/firma")
    @Produces({ MediaType.TEXT_HTML })
    public Response getFirmaTarjetaCredito(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro firma tarjeta credito");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> firma tarjeta credito");
        return pReturn;
    }
}
