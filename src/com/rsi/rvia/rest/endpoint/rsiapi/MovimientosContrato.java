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
public class MovimientosContrato
{
    private static Logger pLog = LoggerFactory.getLogger(MovimientosContrato.class);

    /**
     * Obtiene un agregado de movimientos por mes y signo para vista, y a partir de una fecha, hasta una fecha fin.
     * Adeudos y abonos históricos en acuerdos En el documento WebService3
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/contracts/movementsbymonth/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    // @Path("/contracts/movementsbymonth/{idContract: [0-9]+}/from/{mesInicio}/to/{mesFin}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosMesVistaDesdeHasta(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene un agregado de movimientos por mes y signo para vista, a partir de una fecha, hasta última fecha
     * disonible. Adeudos y abonos históricos en acuerdos En el documento WebService3
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/contracts/movementsbymonth/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate")
    // @Path("/contracts/movementsbymonth/{idContract: [0-9]+}/from/{mesInicio}/to/{mesFin}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosMesVistaHastaHoy(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene un agregado de movimientos por mes y signo para un acuerdo concreto, y a partir de una fecha, hasta una
     * fecha fin Adeudo y abonos históricos en acuerdos En el documento WebService3
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/contracts/movementsbymonth/{idContract: [0-9]+}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    // @Path("/contracts/movementsbymonth/{idContract: [0-9]+}/from/{mesInicio}/to/{mesFin}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosMesAcuerdoDesdeHasta(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene un agregado de movimientos por mes y signo para un acuerdo concreto, y a partir de una fecha, hasta
     * última fecha disponible Adeudo y abonos históricos en acuerdos En el documento WebService3
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/contracts/movementsbymonth/{idContract: [0-9]+}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosMesAcuerdoDesdeHastaHoy(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene un agregado de movimientos por mes y signo para una línea, y a partir de una fecha Adeudos y abonos
     * históricos para una linea En el documento WebService3
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    /*
     * @GET
     * @Path(
     * "/contracts/movementsbymonth/line/{codLinea}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}"
     * )
     * @Produces(MediaType.APPLICATION_JSON) public Response sumaMovimientosMesLineaDesdeHasta(@Context
     * HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData) { Response pReturn =
     * OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
     * pLog.info("Movimientos de un contrato"); return pReturn; }
     */
    /**
     * Obtiene un agregado de movimientos por mes y signo para una línea, y a partir de una fecha, hasta hoy Adeudos y
     * abonos históricos para una linea En el documento WebService3
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    /*
     * @GET
     * @Path("/contracts/movementsbymonth/line/{codLinea}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate")
     * @Produces(MediaType.APPLICATION_JSON) public Response sumaMovimientosMesLineaDesdeHastaHoy(@Context
     * HttpServletRequest pRequest,
     * @Context UriInfo pUriInfo, String strData) { Response pReturn = OperationManager.processForAPI(pRequest,
     * pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE); pLog.info("Movimientos de un contrato"); return pReturn; }
     */
    /**
     * Obtiene sumarizado de movimientos por concepto y tipo de movimiento para un rango de fecha En el documento
     * WebService4
     * 
     * @return Objeto que contiene la respuesta de resultado @
     */
    @GET
    @Path("/contracts/movementsbyconcept/{idContract: [0-9]+}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosContratoConceptoYTipoDesdeHasta(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene sumarizado de movimientos por concepto y tipo de movimiento para un rango de fecha En el documento
     * WebService4
     * 
     * @return Objeto que contiene la respuesta de resultado @
     */
    @GET
    @Path("/contracts/movementsbyconcept/{idContract: [0-9]+}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosContratoConceptoYTipoHastaHoy(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene sumarizado de movimientos por concepto y tipo de movimiento para un rango de fecha En el documento
     * WebService4
     * 
     * @return Objeto que contiene la respuesta de resultado @
     */
    @GET
    @Path("/contracts/movementsbyconcept/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosVistaConceptoYTipoDesdeHasta(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene sumarizado de movimientos por concepto y tipo de movimiento para un rango de fecha En el documento
     * WebService4
     * 
     * @return Objeto que contiene la respuesta de resultado @
     */
    @GET
    @Path("/contracts/movementsbyconcept/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosVistaConceptoYTipoHastaHoy(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos por mes y signo para un contrato y mes. En el documento WebService3b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/contracts/detailmovements/{idContract: [0-9]+}/type/{tipoApunte}/for/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesParaTipoApunteYContranto(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos por mes para un contrato y mes. En el documento WebService3b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/contracts/detailmovements/{idContract: [0-9]+}/for/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesYContrato(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene un agregado de movimientos por mes, categoria y tipo de movimiento entre dos fechas En el documento
     * WebService5
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/contracts/movementsbycategory/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosEntreMesesCategoriaTodosAc(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene un agregado de movimientos por mes, categoria y tipo de movimiento, a partir de una fecha, hasta hoy
     * Adeudos y abonos históricos para una linea En el documento WebService5
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/contracts/movementsbycategory/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosMesCategoriaDesdeHastaHoyTodosAc(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene sumarizado de movimientos por categoría, y tipo UN de movimiento entre fechas En el documento WebService5
     * 
     * @return Objeto que contiene la respuesta de resultado @
     */
    @GET
    @Path("/contracts/movementsbycategory/type/{tipoApunte}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosEntreMesesCategoriaYTipoTodosAc(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene sumarizado de movimientos por categoría, y tipo UN de movimiento, desde una fecha hasta hoy En el
     * documento WebService5
     * 
     * @return Objeto que contiene la respuesta de resultado @
     */
    @GET
    @Path("/contracts/movementsbycategory/type/{tipoApunte}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosHastaHoyCategoriaYTipoTodosAc(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene un agregado de movimientos por mes, categoria y tipo de movimiento entre dos fechas En el documento
     * WebService5
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/contracts/movementsbycategory/{idContract: [0-9]+}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosEntreMesesCategoria(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene un agregado de movimientos por mes, categoria y tipo de movimiento, a partir de una fecha, hasta hoy
     * Adeudos y abonos históricos para una linea En el documento WebService5
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/contracts/movementsbycategory/{idContract: [0-9]+}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosMesCategoriaDesdeHastaHoy(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene sumarizado de movimientos por categoría, y tipo UN de movimiento entre fechas En el documento WebService5
     * 
     * @return Objeto que contiene la respuesta de resultado @
     */
    @GET
    @Path("/contracts/movementsbycategory/{idContract: [0-9]+}/type/{tipoApunte}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosEntreMesesCategoriaYTipo(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene sumarizado de movimientos por categoría, y tipo UN de movimiento, desde una fecha hasta hoy En el
     * documento WebService5
     * 
     * @return Objeto que contiene la respuesta de resultado @
     */
    @GET
    @Path("/contracts/movementsbycategory/{idContract: [0-9]+}/type/{tipoApunte}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosHastaHoyCategoriaYTipo(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos por mes y signo para todos los contratos del cliente. En el documento
     * WebService3b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/contracts/detailmovements/type/{tipoApunte}/for/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesParaTipoApunte(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos para un mes y todos los contratos del cliente. En el documento WebService3b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/contracts/detailmovements/for/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMes(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos de un mes y concepto, para todos los contratos del cliente. En el documento
     * WebService3b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/contracts/detailmovements/concept/{concepto}/for/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesParaConcepto(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos de un mes, signo, y concepto, para todos los contratos del cliente. En el
     * documento WebService3b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/contracts/detailmovements/concept/{concepto}/type/{tipoApunte}/for/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesParaTipoApunteYConcepto(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos de un mes, y concepto, para UN único acuerdo. En el documento WebService3b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/contracts/detailmovements/{idContract: [0-9]+}/concept/{concepto}/for/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosAcuerdoMesParaConcepto(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos de un mes, signo, y concepto, para UN único acuerdo. En el documento
     * WebService3b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/contracts/detailmovements/{idContract: [0-9]+}/concept/{concepto}/type/{tipoApunte}/for/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosAcuerdoMesParaTipoApunteYConcepto(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos de un mes y categoria, para todos los contratos del cliente. En el documento
     * WebService3b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/contracts/detailmovements/category/{categoria}/for/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesParacategoria(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos de un mes, signo, y categoria, para todos los contratos del cliente. En el
     * documento WebService3b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/contracts/detailmovements/category/{categoria}/type/{tipoApunte}/for/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosMesParaTipoApunteYcategoria(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos de un mes, y categoria, para UN único acuerdo. En el documento WebService3b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/contracts/detailmovements/{idContract: [0-9]+}/category/{categoria}/for/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosAcuerdoMesParacategoria(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        return pReturn;
    }

    /**
     * Obtiene el detalle de movimientos de un mes, signo, y categoria, para UN único acuerdo. En el documento
     * WebService3b
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/contracts/detailmovements/{idContract: [0-9]+}/category/{categoria}/type/{tipoApunte}/for/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detalleMovimientosAcuerdoMesParaTipoApunteYcategoria(@Context HttpServletRequest pRequest,
            @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        return pReturn;
    }
}
