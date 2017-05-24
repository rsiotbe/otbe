package com.rsi.rvia.rest.endpoint.ruralvia.tarjetas;

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
    // @GET
    // @Path("/rates")
    // @Produces({ MediaType.TEXT_HTML })
    // @Consumes({ MediaType.APPLICATION_XHTML_XML, MediaType.TEXT_HTML, MediaType.APPLICATION_FORM_URLENCODED,
    // "application/x-ms-application" })
    // public Response getListadoTarjetaCreditoHtml(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    // {
    // pLog.info("Se obtienen las tarjetas de crédito disponibles HTML");
    // Response pReturn = OperationManager.processTemplateFromRvia(pRequest, pUriInfo, "{}", MediaType.TEXT_HTML_TYPE);
    // pLog.info("Se finaliza la obtencion de las tarjetas de crédito disponibles HTML");
    // return pReturn;
    // }
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
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response getListadoTarjetaCredito(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtienen las tarjetas de crédito disponibles");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Se finaliza la obtencion de las tarjetas de crédito disponibles");
        return pReturn;
    }

    /**
     * Muestra el listado de cuentas del usuario.
     * 
     * @param pRequest
     *            the request
     * @param pUriInfo
     *            the uri info
     * @return the datos tarjetas credito
     */
    @GET
    @Path("/accounts")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getCuentasUsuarioTarjetaCredito(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtienen la lista de cuentas de usuario de tarjetas de crédito");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Se finaliza la obtencion de la lista de cuentas de usuario de tarjetas de crédito");
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
    @Path("/{idLinea}/{idGrupo}/{idPdv}/{idTrfa}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getDetalleTarjetaCredito(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtienen el detalle la tarjeta de crédito");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Se finaliza la obtencion de el detalle la tarjeta de crédito");
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
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getDatosPersonaTarjetaCredito(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtienen los datos relativos a la persona de tarjetas de crédito");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Se finaliza la obtencion de los datos relativos  a la persona de tarjetas de crédito");
        return pReturn;
    }

    /**
     * Tratamiento de LOPD en tarjeta de crédito
     * 
     * @param pRequest
     *            the request
     * @param pUriInfo
     *            the uri info
     * @return the scoring
     */
    @GET
    @Path("/lopd")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getLopdTarjetaCredito(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se llama al método de LOPD de tarjetas de crédito");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Se finaliza el método de LOPD de tarjetas de crédito.");
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
    @Path("/{idLinea}/{idGrupo}/{idPdv}/{idTrfa}/scoring")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getScoringTarjetaCredito(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtienen los datos relativos al scoring de tarjeta de crédito");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Se finaliza la obtencion de los datos relativos al scoring tarjeta de crédito");
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
    public Response getSubGruposCNAETarjetaCredito(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se obtienen los datos relativos al listado de grupos CNAE");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Se finaliza la obtencion de los datos relativos  al listado de grupos CNAE");
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
    @Path("/{idLinea}/{idGrupo}/{idPdv}/{idTrfa}/signature")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getFirmaTarjetaCredito(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo)
    {
        pLog.info("Se llama a la operativa de firma tarjeta de credito");
        Response pReturn = OperationManager.processDataFromRvia(pRequest, pUriInfo, "{}", MediaType.APPLICATION_JSON_TYPE);
        pLog.info("Se finaliza la llamada a la operativa de firma tarjeta de credito");
        return pReturn;
    }
}
