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
@Path("/cards/credit")
public class TarjetaCredito
{
    /** The p log. */
    private static Logger pLog = LoggerFactory.getLogger(TarjetaCredito.class);

    /**
     * Muestra el listado de tarjetas (tarifas) comercializadas de ese tipo de producto. Ejemplo: Cuando el cliente ha
     * seleccionado que desea contratar una tarjeta de crédito, este clave página recoge el listado total de tarjetas de
     * crédito que comercializa la entidad.
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
    public Response getListadoTarjetaCredito(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro Listado de tarjetas de crédito");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Listado de tarjetas de crédito");
        return pReturn;
    }

    /**
     * Además de mostrar la información relativa a la tarjeta (tarifa) seleccionada, permite seleccionar la forma de
     * pago y la cuenta de domiciliación deseada.
     * 
     * @param pRequest
     *            the request
     * @param pUriInfo
     *            the uri info
     * @return the datos tarjetas credito
     */
    @GET
    @Path("/{ID_TRFA_PDV}/contract")
    @Produces({ MediaType.TEXT_HTML })
    public Response getContratoTarjetaCredito(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro Datos de la tarjeta de crédito");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Datos  de la tarjeta de crédito");
        return pReturn;
    }

    /**
     * Recoge el detalle de la tarjeta (tarifa), como puede ser las comisiones, los gastos, tipo de tarjeta, etc.
     * 
     * @param pRequest
     *            the request
     * @param pUriInfo
     *            the uri info
     * @return the det tarifas
     */
    @GET
    @Path("/{ID_TRFA_PDV}")
    @Produces({ MediaType.TEXT_HTML })
    public Response getDetalleTarjetaCredito(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro Detalle de la tarjeta de crédito");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Detalle de la tarjeta de crédito");
        return pReturn;
    }

    /**
     * Recupera la información relativa a la persona (fecha de nacimiento, situación laboral, estado civil, nº de hijos,
     * etc.), se trata de la página del scoring.
     * 
     * @param pRequest
     *            the request
     * @param pUriInfo
     *            the uri info
     * @return the datos persona
     */
    @POST
    @Path("/scoring/formdata")
    @Produces({ MediaType.TEXT_HTML })
    public Response getDatosPersonaTarjetaCredito(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro Datos persona tarjeta de crédito");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Datos persona tarjeta de crédito");
        return pReturn;
    }

    /**
     * Tras la selección del grupo CNAE, muestra la selección disponible de este grupo CNAE.
     * 
     * @param pRequest
     *            the request
     * @param pUriInfo
     *            the uri info
     * @return the scoring
     */
    @POST
    @Path("/{ID_TRFA_PDV}/scoring/formdata/cnae/detail")
    @Produces({ MediaType.TEXT_HTML })
    public Response getListadoClausGrupCNAETarjetaCredito(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo)
    {
        pLog.info("Entro Listado clausulas del grupo CNAE seleccionado.");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Listado clausulas del grupo CNAE seleccionado.");
        return pReturn;
    }

    /**
     * Tras la introducción de los datos relativos a la persona, pasa el scoring y nos muestra el resultado de este.
     * Cuando el resultado es positivo, el cliente introduce la firma.
     * 
     * @param pRequest
     *            the request
     * @param pUriInfo
     *            the uri info
     * @return the det tarifas
     */
    @GET
    @Path("/{ID_TRFA_PDV}/scoring")
    @Produces({ MediaType.TEXT_HTML })
    public Response getScoringTarjetaCredito(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro Scoring tarjeta de crédito");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" --------->  Scoring tarjeta de crédito");
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
    @Path("/{ID_TRFA_PDV}/scoring/formdata/cnae")
    @Produces({ MediaType.TEXT_HTML })
    public Response getSubGruposCNAETarjetaCredito(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro Listado grupos CNAE");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Listado grupos CNAE");
        return pReturn;
    }

    /**
     * Tras la firma de la operativa, nos da el resultado de la solicitud (Procesado o Rechazado).
     * 
     * @param pRequest
     *            the request
     * @param pUriInfo
     *            the uri info
     * @return the det tarifas
     */
    @POST
    @Path("/{ID_TRFA_PDV}/sign")
    @Produces({ MediaType.TEXT_HTML })
    public Response getFirmaTarjetaCredito(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Entro Resultado de firma tarjeta de credito");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_PLAIN_TYPE);
        pLog.info(" ---------> Resultado de firma tarjeta de credito");
        return pReturn;
    }
}
