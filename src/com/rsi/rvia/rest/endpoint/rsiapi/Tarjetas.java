package com.rsi.rvia.rest.endpoint.rsiapi;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.client.OperationManager;

/** Clase que responde a las peticiones REST para las acciones relacionadas con el control de costes */
@Path("/rsiapi")
public class Tarjetas
{
    private static Logger pLog = LoggerFactory.getLogger(MovimientosContrato.class);

    /**
     * Obtiene un agregado de movimientos en tarjetas por mes y signo para vista, y a partir de una fecha, hasta una
     * fecha fin. Movimientos históricos en tarjetas En el documento WebService6
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/cards/movementsbymonth/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosMesTarjetasDesdeHasta(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene un agregado de movimientos en tarjetas por mes y signo para vista, a partir de una fecha, hasta última
     * fecha disonible. Movimientos históricos en tarjetas En el documento WebService6
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/cards/movementsbymonth/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosMesTarjetasHastaHoy(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene un agregado de movimientos en tarjetas por mes y signo para un acuerdo concreto, y a partir de una fecha,
     * hasta una fecha fin Adeudo y abonos históricos en acuerdos En el documento WebService6
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/cards/movementsbymonth/{idContract: [0-9]+}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosMesAcuerdoDesdeHasta(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene un agregado de movimientos en tarjetas por mes y signo para un acuerdo concreto, y a partir de una fecha,
     * hasta última fecha disponible Adeudo y abonos históricos en acuerdos En el documento WebService6
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/cards/movementsbymonth/{idContract: [0-9]+}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosMesAcuerdoDesdeHastaHoy(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para todos los contratos de tarjetas en el mes. En el documento WebService6b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/cards/detailmovements/for/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesAllContracts(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para todos los contratos de tarjetas de una categoría en el mes. En el
     * documento WebService6b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/cards/detailmovements/category/{categoria}/for/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesAllContractsCategoria(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para todos los contratos de tarjetas en una localidad en el mes. En el
     * documento WebService6b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/cards/detailmovements/location/{localidad}/for/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesAllContractsLocalidad(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para todos los contratos de tarjetas para un comercio en el mes. En el
     * documento WebService6b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/cards/detailmovements/commerce/{comercio}/for/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesAllContractsComercio(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para un contrato y mes. En el documento WebService6b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/cards/detailmovements/{idContract}/for/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMes(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para un contrato y mes, y una categoría concreta. En el documento WebService6b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/cards/detailmovements/{idContract}/category/{categoria}/for/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesContratoYCategoria(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para un contrato y mes, y una localización concreta. En el documento
     * WebService6b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/cards/detailmovements/{idContract}/location/{localidad}/for/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesContratoYLocalidad(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para un contrato y mes, y un comercio concreto. En el documento WebService6b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/cards/detailmovements/{idContract}/commerce/{comercio}/for/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesContratoYComercio(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene un agregado de movimientos en tarjetas por el concepto especificado (categoria,localidad,comercio), y a
     * partir de una fecha, hasta una fecha fin. Movimientos históricos en tarjetas En el documento WebService7
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/cards/sumbyconcept/{concepto}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosMesTarjetasGroupByConceptoDesdeHasta(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene un agregado de movimientos en tarjetas por el concepto especificado (categoria,localidad,comercio), y a
     * partir de una fecha, hasta última fecha desponible. Movimientos históricos en tarjetas En el documento
     * WebService7
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/cards/sumbyconcept/{concepto}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosMesTarjetasGroupByConceptoHastaHoy(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene un agregado de movimientos de una tarjeta por el concepto especificado (categoria,localidad,comercio),
     * entre fechas. Movimientos históricos en tarjetas En el documento WebService7
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/cards/{idContract: [0-9]+}/sumbyconcept/{concepto}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosMesTarjetasGroupByConceptoEntreFechasUnAcuerdo(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene un agregado de movimientos de una tarjeta por el concepto especificado (categoria,localidad,comercio), y
     * a partir de una fecha, hasta última fecha desponible. Movimientos históricos en tarjetas En el documento
     * WebService7
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/cards/{idContract: [0-9]+}/sumbyconcept/{concepto}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosMesTarjetasGroupByConceptoHastaHoyUnAcuerdo(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para todos los contratos de tarjetas hasta hoy. En el documento WebService6b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/cards/detailmovements/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesAllContractsDesdeHastaHoy(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para todos los contratos de tarjetas de una categoría hasta hoy. En el
     * documento WebService6b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/cards/detailmovements/category/{categoria}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesAllContractsCategoriaDesdeHastaHoy(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para todos los contratos de tarjetas en una localidad hasta hoy. En el
     * documento WebService6b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/cards/detailmovements/location/{localidad}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesAllContractsLocalidadDesdeHastaHoy(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para todos los contratos de tarjetas para un comercio hasta hoy. En el
     * documento WebService6b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/cards/detailmovements/commerce/{comercio}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesAllContractsComercioDesdeHastaHoy(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para un contrato, hasta hoy. En el documento WebService6b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/cards/detailmovements/{idContract}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesDesdeHastaHoy(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para un contrato, hasta hoy, y una categoría concreta. En el documento
     * WebService6b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/cards/detailmovements/{idContract}/category/{categoria}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesContratoYCategoriaDesdeHastaHoy(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para un contrato, hasta hoy, y una localización concreta. En el documento
     * WebService6b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/cards/detailmovements/{idContract}/location/{localidad}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesContratoYLocalidadDesdeHastaHoy(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para un contrato, hasta hoy, y un comercio concreto. En el documento
     * WebService6b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/cards/detailmovements/{idContract}/commerce/{comercio}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesContratoYComercioDesdeHastaHoy(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para todos los contratos de tarjetas entre dos fechas. En el documento
     * WebService6b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/cards/detailmovements/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesAllContractsDesdeHasta(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para todos los contratos de tarjetas de una categoría entre dos fechas. En el
     * documento WebService6b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/cards/detailmovements/category/{categoria}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesAllContractsCategoriaDesdeHasta(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para todos los contratos de tarjetas en una localidad entre dos fechas. En el
     * documento WebService6b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/cards/detailmovements/location/{localidad}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesAllContractsLocalidadDesdeHasta(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para todos los contratos de tarjetas para un comercio entre dos fechas. En el
     * documento WebService6b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/cards/detailmovements/commerce/{comercio}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesAllContractsComercioDesdeHasta(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para un contrato, entre dos fechas. En el documento WebService6b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/cards/detailmovements/{idContract}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesDesdeHasta(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para un contrato, entre dos fechas, y una categoría concreta. En el documento
     * WebService6b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/cards/detailmovements/{idContract}/category/{categoria}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesContratoYCategoriaDesdeHasta(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para un contrato, entre dos fechas, y una localización concreta. En el
     * documento WebService6b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/cards/detailmovements/{idContract}/location/{localidad}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesContratoYLocalidadDesdeHasta(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para un contrato, entre dos fechas, y un comercio concreto. En el documento
     * WebService6b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/cards/detailmovements/{idContract}/commerce/{comercio}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesContratoYComercioDesdeHasta(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }
}
