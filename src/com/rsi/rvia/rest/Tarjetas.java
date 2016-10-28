package com.rsi.rvia.rest;

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
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     * @throws Exception
     */
    @GET
    @Path("/cards/movementsbymonth/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    // @Path("/cards/movementsbymonth/{idContract}/from/{mesInicio}/to/{mesFin}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosMesVistaDesdeHasta(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            String strData) throws Exception
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene un agregado de movimientos en tarjetas por mes y signo para vista, a partir de una fecha, hasta última
     * fecha disonible. Movimientos históricos en tarjetas En el documento WebService6
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     * @throws Exception
     */
    @GET
    @Path("/cards/movementsbymonth/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate")
    // @Path("/cards/movementsbymonth/{idContract}/from/{mesInicio}/to/{mesFin}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosMesVistaHastaHoy(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            String strData) throws Exception
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene un agregado de movimientos en tarjetas por mes y signo para un acuerdo concreto, y a partir de una fecha,
     * hasta una fecha fin Adeudo y abonos históricos en acuerdos En el documento WebService6
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     * @throws Exception
     */
    @GET
    @Path("/cards/movementsbymonth/{idContract: [0-9]+}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    // @Path("/cards/movementsbymonth/{idContract}/from/{mesInicio}/to/{mesFin}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosMesAcuerdoDesdeHasta(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData) throws Exception
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene un agregado de movimientos en tarjetas por mes y signo para un acuerdo concreto, y a partir de una fecha,
     * hasta última fecha disponible Adeudo y abonos históricos en acuerdos En el documento WebService6
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     * @throws Exception
     */
    @GET
    @Path("/cards/movementsbymonth/{idContract: [0-9]+}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosMesAcuerdoDesdeHastaHoy(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData) throws Exception
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene sumarizado de movimientos por concepto y tipo de movimiento para un rango de fecha En el documento
     * WebService4
     * 
     * @return Objeto que contiene la respuesta de resultado
     * @throws Exception
     */
    @GET
    @Path("/cards/movementsbyconcept/{idContract}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosContratoConceptoYTipoDesdeHasta(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData) throws Exception
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene sumarizado de movimientos por concepto y tipo de movimiento para un rango de fecha En el documento
     * WebService4
     * 
     * @return Objeto que contiene la respuesta de resultado
     * @throws Exception
     */
    @GET
    @Path("/cards/movementsbyconcept/{idContract}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosContratoConceptoYTipoHastaHoy(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData) throws Exception
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene sumarizado de movimientos por concepto y tipo de movimiento para un rango de fecha En el documento
     * WebService4
     * 
     * @return Objeto que contiene la respuesta de resultado
     * @throws Exception
     */
    @GET
    @Path("/cards/movementsbyconcept/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosVistaConceptoYTipoDesdeHasta(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData) throws Exception
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene sumarizado de movimientos por concepto y tipo de movimiento para un rango de fecha En el documento
     * WebService4
     * 
     * @return Objeto que contiene la respuesta de resultado
     * @throws Exception
     */
    @GET
    @Path("/cards/movementsbyconcept/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosVistaConceptoYTipoHastaHoy(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData) throws Exception
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para todos los contratos de tarjetas en el mes. En el documento WebService6b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     * @throws Exception
     */
    @GET
    @Path("/cards/detailmovements/for/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesAllContracts(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            String strData) throws Exception
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para todos los contratos de tarjetas de una categoría en el mes. En el
     * documento WebService6b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     * @throws Exception
     */
    @GET
    @Path("/cards/detailmovements/category/{categoria}/for/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesAllContractsCategoria(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData) throws Exception
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para todos los contratos de tarjetas en una localidad en el mes. En el
     * documento WebService6b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     * @throws Exception
     */
    @GET
    @Path("/cards/detailmovements/location/{localidad}/for/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesAllContractsLocalidad(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData) throws Exception
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para todos los contratos de tarjetas para un comercio en el mes. En el
     * documento WebService6b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     * @throws Exception
     */
    @GET
    @Path("/cards/detailmovements/commerce/{comercio}/for/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesAllContractsComercio(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData) throws Exception
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para un contrato y mes. En el documento WebService6b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     * @throws Exception
     */
    @GET
    @Path("/cards/detailmovements/{idContract}/for/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMes(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            String strData) throws Exception
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para un contrato y mes, y una categoría concreta. En el documento WebService6b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     * @throws Exception
     */
    @GET
    @Path("/cards/detailmovements/{idContract}/category/{categoria}/for/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesContratoYCategoria(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData) throws Exception
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para un contrato y mes, y una localización concreta. En el documento
     * WebService6b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     * @throws Exception
     */
    @GET
    @Path("/cards/detailmovements/{idContract}/location/{localidad}/for/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesContratoYLocalidad(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData) throws Exception
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para un contrato y mes, y un comercio concreto. En el documento WebService6b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     * @throws Exception
     */
    @GET
    @Path("/cards/detailmovements/{idContract}/commerce/{comercio}/for/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesContratoYComercio(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData) throws Exception
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }
}
