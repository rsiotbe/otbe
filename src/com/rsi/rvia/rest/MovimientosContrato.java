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


/**
 * Clase que responde a las peticiones REST para las acciones relacionadas con el control de costes
 *
 */
@Path("/rsiapi") 
public class MovimientosContrato
{
    private static Logger pLog = LoggerFactory.getLogger(MovimientosContrato.class);


    /**
     * Obtiene un agregado de movimientos por mes y signo para un acuerdo concreto, y a partir de una fecha, hasta una fecha fin
     * Adeudo y abonos históricos en acuerdos
     * En el documento WebService3
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     * @throws Exception 
     */
    @GET
    @Path("/contracts/movementsbymonth/{idContract: [0-9]+}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}") 
    //@Path("/contracts/movementsbymonth/{idContract}/from/{mesInicio}/to/{mesFin}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosMesAcuerdoDesdeHasta(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            String strData) throws Exception
    {    
        Response pReturn = OperationManager.proccesForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;    
    }   
    

    /**
     * Obtiene un agregado de movimientos por mes y signo para un acuerdo concreto, y a partir de una fecha, hasta última fecha disponible
     * Adeudo y abonos históricos en acuerdos
     * En el documento WebService3
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     * @throws Exception 
     */
    @GET
    @Path("/contracts/movementsbymonth/{idContract: [0-9]+}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate") 
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosMesAcuerdoDesdeHastaHoy(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            String strData) throws Exception
    {           Response pReturn = OperationManager.proccesForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }   
    
    
    
    /**
     * Obtiene un agregado de movimientos por mes y signo para una línea, y a partir de una fecha
     * Adeudos y abonos históricos para una linea
     * En el documento WebService3
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     * @throws Exception 
     */
    @GET
    @Path("/contracts/movementsbymonth/line/{codLinea}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}") 
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosMesLineaDesdeHasta(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            String strData) throws Exception
    {    
        Response pReturn = OperationManager.proccesForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }    

    /**
     * Obtiene un agregado de movimientos por mes y signo para una línea, y a partir de una fecha, hasta hoy
     * Adeudos y abonos históricos para una linea
     * En el documento WebService3
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     * @throws Exception 
     */
    @GET
    @Path("/contracts/movementsbymonth/line/{codLinea}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate") 
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumaMovimientosMesLineaDesdeHastaHoy(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            String strData) throws Exception
    {    
        Response pReturn = OperationManager.proccesForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }    
}

