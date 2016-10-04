package com.rsi.rvia.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.client.OperationManager;
import com.rsi.rvia.rest.tool.ServiceHelper;
// import javax.ws.rs.Consumes;

/**
 * Clase que responde a las peticiones REST para las acciones relacionadas con el control de costes
 */
@Path("/rsiapi")
public class CostControl
{
   private static Logger pLog = LoggerFactory.getLogger(CostControl.class);

   /**
    * Obtiene el listado completo de tarjetas de un usuario
    * 
    * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
    * @throws Exception
    */
   @GET
   @Path("/login")
   @Produces(MediaType.APPLICATION_JSON)
   public Response loginProcess(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
         throws Exception
   {
      // Invocar servicio de login desde operation manager?
      Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
      pLog.info(" ---------> Login");
      return pReturn;
   }

   /**
	 * Obtiene el listado completo de acuerdos de un usuario
	 * En el documento Webservice1 
    * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
    * @throws Exception
    */
   @GET
   @Path("/contracts")
   @Produces(MediaType.APPLICATION_JSON)
   public Response listaDeContratos(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
         throws Exception
   {
      // return Response.ok("Lista de contratos").build();
      Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
      pLog.info("Lista de contratos");
      return pReturn;
   }
	/**
	 * Obtiene el listado de acuerdos de un usuario  para una línea concreta
	 * En el documento Webservice1 
	 * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
	 * @throws Exception 
	 */
	@GET
	@Path("/contracts/line/{codLinea}") 
   @Produces(MediaType.APPLICATION_JSON)
	public Response listaDeContratosPorLinea(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
			String strData) throws Exception
	{	
		Response pReturn = OperationManager.proccesForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);	
		pLog.info("Lista de contratos - de una línea concreta.");
		return pReturn;
	}
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
         throws Exception
	{	
		Response pReturn = OperationManager.proccesForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
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
		Response pReturn = OperationManager.proccesForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);		
		return pReturn;	
	}	
	
	
	/**
	 * Obtiene un agregado de movimientos por mes y signo para un acuerdo concreto, y a partir de una fecha, hasta una fecha fin
	 * Adeudo y abonos históricos en acuerdos
	 * En el documento WebService3
	 * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
	 * @throws Exception 
	 */		
	@GET
	@Path("/contracts/movementsbymonth/{idContract: [0-9]+}/from/{fechaInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]}/to/{fechaFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]}") 
   @Produces(MediaType.APPLICATION_JSON)
	public Response sumaMovimientosMesAcuerdoDesdeHasta(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
   {
      Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
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
	@Path("/contracts/movementsbymonth/{idContract: [0-9]+}/from/{fechaInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]}/uptodate") 
   @Produces(MediaType.APPLICATION_JSON)
	public Response sumaMovimientosMesAcuerdoDesdeHastaHoy(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
         String strData) throws Exception
   {
      Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
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
	@Path("/contracts/movementsbymonth/line/{codLinea}/from/{fechaInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]}/to/{fechaFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]}") 
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
	@Path("/contracts/movementsbymonth/line/{codLinea}/from/{fechaInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]}/uptodate") 
   @Produces(MediaType.APPLICATION_JSON)
	public Response sumaMovimientosMesLineaDesdeHastaHoy(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
         throws Exception
   {
      Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
      pLog.info("Movimientos de un contrato");
      return pReturn;
	}	
	
	
	/**
	 * Detalle de movimientos para una acuerdo, mes, y tipo entre haber y debe
	 * Adeudos y abonos históricos para un aucerdo - detalle
	 * En el documento WebService3
	 * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
	 * @throws Exception 
	 */	
	/*
   @GET
	@Path("/contracts/movementsbymonth/line/{codLinea}/from/{fechaInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]}") 
   @Produces(MediaType.APPLICATION_JSON)
	public Response detalleMovimientos(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
         String strData) throws Exception
   {
      Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
		pLog.info("Movimientos de un contrato");
		return pReturn;	
	}		
	*/
}
