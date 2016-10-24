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
public class MovimientosContrato
{
	private static Logger	pLog	= LoggerFactory.getLogger(MovimientosContrato.class);

	/** Obtiene un agregado de movimientos por mes y signo para vista, y a partir de una fecha, hasta una fecha fin.
	 * Adeudos y abonos históricos en acuerdos En el documento WebService3
	 * 
	 * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
	 * @throws Exception */
	@GET
	@Path("/contracts/movementsbymonth/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
	// @Path("/contracts/movementsbymonth/{idContract}/from/{mesInicio}/to/{mesFin}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response sumaMovimientosMesVistaDesdeHasta(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
			String strData) throws Exception
	{
		Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
		pLog.info("Movimientos de un contrato");
		return pReturn;
	}

	/** Obtiene un agregado de movimientos por mes y signo para vista, a partir de una fecha, hasta última fecha
	 * disonible. Adeudos y abonos históricos en acuerdos En el documento WebService3
	 * 
	 * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
	 * @throws Exception */
	@GET
	@Path("/contracts/movementsbymonth/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate")
	// @Path("/contracts/movementsbymonth/{idContract}/from/{mesInicio}/to/{mesFin}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response sumaMovimientosMesVistaHastaHoy(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
			String strData) throws Exception
	{
		Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
		pLog.info("Movimientos de un contrato");
		return pReturn;
	}

	/** Obtiene un agregado de movimientos por mes y signo para un acuerdo concreto, y a partir de una fecha, hasta una
	 * fecha fin Adeudo y abonos históricos en acuerdos En el documento WebService3
	 * 
	 * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
	 * @throws Exception */
	@GET
	@Path("/contracts/movementsbymonth/{idContract: [0-9]+}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
	// @Path("/contracts/movementsbymonth/{idContract}/from/{mesInicio}/to/{mesFin}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response sumaMovimientosMesAcuerdoDesdeHasta(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
			String strData) throws Exception
	{
		Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
		pLog.info("Movimientos de un contrato");
		return pReturn;
	}

	/** Obtiene un agregado de movimientos por mes y signo para un acuerdo concreto, y a partir de una fecha, hasta última
	 * fecha disponible Adeudo y abonos históricos en acuerdos En el documento WebService3
	 * 
	 * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
	 * @throws Exception */
	@GET
	@Path("/contracts/movementsbymonth/{idContract: [0-9]+}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate")
	@Produces(MediaType.APPLICATION_JSON)
	public Response sumaMovimientosMesAcuerdoDesdeHastaHoy(@Context HttpServletRequest pRequest,
			@Context UriInfo pUriInfo, String strData) throws Exception
	{
		Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
		pLog.info("Movimientos de un contrato");
		return pReturn;
	}

	/** Obtiene un agregado de movimientos por mes y signo para una línea, y a partir de una fecha Adeudos y abonos
	 * históricos para una linea En el documento WebService3
	 * 
	 * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
	 * @throws Exception */
	/*
	 * @GET
	 * @Path(
	 * "/contracts/movementsbymonth/line/{codLinea}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}"
	 * )
	 * @Produces(MediaType.APPLICATION_JSON) public Response sumaMovimientosMesLineaDesdeHasta(@Context
	 * HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData) throws Exception { Response pReturn =
	 * OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
	 * pLog.info("Movimientos de un contrato"); return pReturn; }
	 */
	/** Obtiene un agregado de movimientos por mes y signo para una línea, y a partir de una fecha, hasta hoy Adeudos y
	 * abonos históricos para una linea En el documento WebService3
	 * 
	 * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
	 * @throws Exception */
	/*
	 * @GET
	 * @Path("/contracts/movementsbymonth/line/{codLinea}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate")
	 * @Produces(MediaType.APPLICATION_JSON) public Response sumaMovimientosMesLineaDesdeHastaHoy(@Context
	 * HttpServletRequest pRequest,
	 * @Context UriInfo pUriInfo, String strData) throws Exception { Response pReturn =
	 * OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
	 * pLog.info("Movimientos de un contrato"); return pReturn; }
	 */
	/** Obtiene sumarizado de movimientos por concepto y tipo de movimiento para un rango de fecha En el documento
	 * WebService4
	 * 
	 * @return Objeto que contiene la respuesta de resultado
	 * @throws Exception */
	@GET
	@Path("/contracts/movementsbyconcept/{idContract}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response sumaMovimientosContratoConceptoYTipoDesdeHasta(@Context HttpServletRequest pRequest,
			@Context UriInfo pUriInfo, String strData) throws Exception
	{
		Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
		pLog.info("Movimientos de un contrato");
		return pReturn;
	}

	/** Obtiene sumarizado de movimientos por concepto y tipo de movimiento para un rango de fecha En el documento
	 * WebService4
	 * 
	 * @return Objeto que contiene la respuesta de resultado
	 * @throws Exception */
	@GET
	@Path("/contracts/movementsbyconcept/{idContract}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate")
	@Produces(MediaType.APPLICATION_JSON)
	public Response sumaMovimientosContratoConceptoYTipoHastaHoy(@Context HttpServletRequest pRequest,
			@Context UriInfo pUriInfo, String strData) throws Exception
	{
		Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
		pLog.info("Movimientos de un contrato");
		return pReturn;
	}

	/** Obtiene sumarizado de movimientos por concepto y tipo de movimiento para un rango de fecha En el documento
	 * WebService4
	 * 
	 * @return Objeto que contiene la respuesta de resultado
	 * @throws Exception */
	@GET
	@Path("/contracts/movementsbyconcept/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response sumaMovimientosVistaConceptoYTipoDesdeHasta(@Context HttpServletRequest pRequest,
			@Context UriInfo pUriInfo, String strData) throws Exception
	{
		Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
		pLog.info("Movimientos de un contrato");
		return pReturn;
	}

	/** Obtiene sumarizado de movimientos por concepto y tipo de movimiento para un rango de fecha En el documento
	 * WebService4
	 * 
	 * @return Objeto que contiene la respuesta de resultado
	 * @throws Exception */
	@GET
	@Path("/contracts/movementsbyconcept/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate")
	@Produces(MediaType.APPLICATION_JSON)
	public Response sumaMovimientosVistaConceptoYTipoHastaHoy(@Context HttpServletRequest pRequest,
			@Context UriInfo pUriInfo, String strData) throws Exception
	{
		Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
		pLog.info("Movimientos de un contrato");
		return pReturn;
	}

	/** Obtiene el detalle de movimientos por mes y signo para un contrato y mes. En el documento WebService3b
	 * 
	 * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
	 * @throws Exception */
	@GET
	@Path("/contracts/detailmovements/{idContract}/type/{tipoApunte}/for/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response detalleMovimientosMesParaTipoApunte(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
			String strData) throws Exception
	{
		Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
		pLog.info("Movimientos de un contrato");
		return pReturn;
	}

	/** Obtiene el detalle de movimientos por mes y signo para un contrato y mes. En el documento WebService3b
	 * 
	 * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
	 * @throws Exception */
	@GET
	@Path("/contracts/detailmovements/{idContract}/for/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response detalleMovimientosMes(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
			throws Exception
	{
		Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
		pLog.info("Movimientos de un contrato");
		return pReturn;
	}

	/** Obtiene un agregado de movimientos por mes, categoria y tipo de movimiento entre dos fechas En el documento
	 * WebService5
	 * 
	 * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
	 * @throws Exception */
	@GET
	@Path("/contracts/movementsbycategory/{idContract: [0-9]+}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response sumaMovimientosEntreMesesCategoria(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
			String strData) throws Exception
	{
		Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
		pLog.info("Movimientos de un contrato");
		return pReturn;
	}

	/** Obtiene un agregado de movimientos por mes, categoria y tipo de movimiento, a partir de una fecha, hasta hoy
	 * Adeudos y abonos históricos para una linea En el documento WebService5
	 * 
	 * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
	 * @throws Exception */
	@GET
	@Path("/contracts/movementsbycategory/{idContract: [0-9]+}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate")
	@Produces(MediaType.APPLICATION_JSON)
	public Response sumaMovimientosMesCategoriaDesdeHastaHoy(@Context HttpServletRequest pRequest,
			@Context UriInfo pUriInfo, String strData) throws Exception
	{
		Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
		pLog.info("Movimientos de un contrato");
		return pReturn;
	}

	/** Obtiene sumarizado de movimientos por categoría, y tipo UN de movimiento entre fechas En el documento WebService5
	 * 
	 * @return Objeto que contiene la respuesta de resultado
	 * @throws Exception */
	@GET
	@Path("/contracts/movementsbycategory/{idContract}/type/{tipoApunte}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response sumaMovimientosEntreMesesCategoriaYTipo(@Context HttpServletRequest pRequest,
			@Context UriInfo pUriInfo, String strData) throws Exception
	{
		Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
		pLog.info("Movimientos de un contrato");
		return pReturn;
	}

	/** Obtiene sumarizado de movimientos por categoría, y tipo UN de movimiento, desde una fecha hasta hoy En el
	 * documento WebService5
	 * 
	 * @return Objeto que contiene la respuesta de resultado
	 * @throws Exception */
	@GET
	@Path("/contracts/movementsbycategory/{idContract}/type/{tipoApunte}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate")
	@Produces(MediaType.APPLICATION_JSON)
	public Response sumaMovimientosHastaHoyCategoriaYTipo(@Context HttpServletRequest pRequest,
			@Context UriInfo pUriInfo, String strData) throws Exception
	{
		Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
		pLog.info("Movimientos de un contrato");
		return pReturn;
	}
	/** Obtiene un agregado de movimientos por mes y signo para un concepto de clasificaciOn de producto, entre dos fechas
	 * dadas. La clasificación de producto viene dada por: - 1) Pasivo a la vista: 03/11 y 03/21 - 2) Depósitos: 03/51 y
	 * 03/52 - 3) Préstamos: 01/51 - 4) Tarjetas de crédito: 01/51 - 5) Tarjetas crédito y débito: 01/51 y 05/51 Adeudos
	 * y abonos históricos para una linea En el documento WebService3
	 * 
	 * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
	 * @throws Exception */
	/*
	 * @GET
	 * @Path(
	 * "/contracts/movementsbymonth/classification/{codClasificacion}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/to/{mesFin: [0-9][0-9][0-9][0-9]-[0-9][0-9]}"
	 * )
	 * @Produces(MediaType.APPLICATION_JSON) public Response sumaMovimientosMesClasificacionDesdeHasta(@Context
	 * HttpServletRequest pRequest,
	 * @Context UriInfo pUriInfo, String strData) throws Exception { Response pReturn =
	 * OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
	 * pLog.info("Movimientos de un contrato"); return pReturn; }
	 */
	/** Obtiene un agregado de movimientos por mes y signo para un concepto de clasificaciOn de producto, y a partir de
	 * una fecha. La clasificación de producto viene dada por: - 1) Pasivo a la vista: 03/11 y 03/21 - 2) Depósitos:
	 * 03/51 y 03/52 - 3) Préstamos: 01/51 - 4) Tarjetas de crédito: 01/51 - 5) Tarjetas crédito y débito: 01/51 y 05/51
	 * Adeudos y abonos históricos para una linea En el documento WebService3
	 * 
	 * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas
	 * @throws Exception */
	/*
	 * @GET
	 * @Path(
	 * "/contracts/movementsbymonth/classification/{codClasificacion}/from/{mesInicio: [0-9][0-9][0-9][0-9]-[0-9][0-9]}/uptodate"
	 * )
	 * @Produces(MediaType.APPLICATION_JSON) public Response sumaMovimientosMesClasificacionDesdeHastaHoy(@Context
	 * HttpServletRequest pRequest,
	 * @Context UriInfo pUriInfo, String strData) throws Exception { Response pReturn =
	 * OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
	 * pLog.info("Movimientos de un contrato"); return pReturn; }
	 */
}
