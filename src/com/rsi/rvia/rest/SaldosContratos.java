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
 * Clase que responde a las peticiones REST para las acciones relacionadas con la extracción de saldos a fin de mes
 */
@Path("/rsiapi") 
public class SaldosContratos
{
    private static Logger pLog = LoggerFactory.getLogger(SaldosContratos.class);
    
    /**
     * Obtiene posiciones de saldos a fin de mes para un acuerdo desde fecha inicio hasta fecha fin
     * En el documento Webservice2
     * @return Objeto que contiene la respuesta y en caso positivo los saldos por fin de mes
     * @throws Exception 
     */
    
    @GET
    @Path("/contracts/balances/{idContract: [0-9]+}/from/{fechaInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]}/to/{fechaFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]}") 
   @Produces(MediaType.APPLICATION_JSON)
    public Response saldosDeUnContratoDesdeHasta(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            String strData) throws Exception
    {   
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn; 
    }
    
    /**
     * Obtiene posiciones de saldos a fin de mes para un acuerdo desde fecha inicio hasta hoy
     * En el documento Webservice2
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
     * @throws Exception 
     */ 
    @GET
    @Path("/contracts/balances/{idContract: [0-9]+}/from/{fechaInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]}/uptodate") 
   @Produces(MediaType.APPLICATION_JSON)
    public Response saldosDeUnContratoDesdeHastaHoy(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            String strData) throws Exception
    {   
        pLog.info(strData);
        //strData=strData + "&fechaFin=9999-12-31";
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);      
        return pReturn; 
    }   
    
}

