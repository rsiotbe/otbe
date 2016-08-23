package com.rsi.rvia.rest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.DDBB.DDBBConnection;
import com.rsi.rvia.rest.DDBB.DDBBFactory;
import com.rsi.rvia.rest.DDBB.DDBBFactory.DDBBProvider;
import com.rsi.rvia.rest.client.OperationManager;
import com.rsi.rvia.rest.session.SessionRviaData;
import com.rsi.rvia.rest.tool.LogController;
import com.rsi.rvia.rest.tool.Utils;

@Path("/putprueba")
public class PutPrueba
{
	private static Logger			pLog	= LoggerFactory.getLogger(Cards.class);
	private static LogController	pLogC	= new LogController();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllUserCards(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo) throws Exception
	{
		pLog.info("Se recibe una peticion de cashierLocatior");
		pLogC.addLog("Info", "Se recibe una peticion de cashierLocatior");
		String data = "{\"EE_I_ActivacionTarjetaBE\": {\"codigoEntidad\": \"3008\","
				+ "\"usuarioBE\": \"32894488\",\"acuerdoBE\": \"1932504291\","
				+ "\"acuerdo\": \"1932511486\",\"panToken\": \"4599846092220023\","
				+ "\"DatosFirma\":{\"firma\": \"G\",\"telefonoMovil\": \"666666666\"}}}";
		SessionRviaData pSessionRviaData = new SessionRviaData(pRequest);
		Response pReturn = OperationManager.proccesFromRvia(pRequest, pUriInfo, data, MediaType.TEXT_PLAIN_TYPE);
		// /??? La respuesta devuelve ahora mismo JSON para hacer pruebas. Deberia devolver un XHTML con los datos del
		// JSON.
		return pReturn;
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response updateThink(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
			throws Exception
	{
		pLog.info("Se recibe una peticion de cashierLocatior, PUT");
		pLogC.addLog("Info", "Se recibe una peticion de cashierLocatior, PUT");
		SessionRviaData pSessionRviaData = new SessionRviaData(pRequest);
		Response pReturn = OperationManager.proccesFromRvia(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
		return pReturn;
	}

	@Path("/post")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response updateThinkpost(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
			throws Exception
	{
		pLog.info("Se recibe una peticion de cashierLocatior, POST");
		pLogC.addLog("Info", "Se recibe una peticion de cashierLocatior, POST");
		SessionRviaData pSessionRviaData = new SessionRviaData(pRequest);
		Response pReturn = OperationManager.proccesFromRvia(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
		return pReturn;
	}

	@Path("/getddbb")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDDBBInfo(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
			throws Exception
	{
		String strQuery = null;
		String strTabla = pRequest.getParameter("tabla");
		String strParams = pRequest.getParameter("params");
		String strWhereKey = pRequest.getParameter("wherekey");
		String strWhereValue = pRequest.getParameter("wherevalue");
		String strOther = pRequest.getParameter("other");
		Response pReturn;
		StringBuilder pSB = new StringBuilder();
		pSB.append("select ");
		if (strParams != null)
		{
			pSB.append(strParams);
		}
		else
		{
			pSB.append("*");
		}
		pSB.append(" from ");
		if (strTabla != null)
		{
			pSB.append(strTabla);
		}
		else
		{
			strQuery = null;
		}
		if ((strWhereKey != null) && (strWhereValue != null))
		{
			pSB.append(" where " + strWhereKey + " = '" + strWhereValue + "'");
			if (strOther != null)
			{
				pSB.append(" " + strOther);
			}
			strQuery = pSB.toString();
		}
		else
		{
			strQuery = pSB.toString();
		}
		
		if (strQuery != null)
		{
			DDBBConnection pDBConection = DDBBFactory.getDDBB(DDBBProvider.OracleBDES);
			PreparedStatement pPreparedStament = pDBConection.prepareStatement(strQuery);
			ResultSet pResultSet = pDBConection.executeQuery(pPreparedStament);
			JSONArray jsonArray = new JSONArray();
			jsonArray = Utils.convertResultSet2JSON(pResultSet);
			pReturn = Response.ok(jsonArray.toString()).build();
		}
		else
		{
			pReturn = Response.ok("{\"Error\" : \"Query mal formada.\"}").build();
		}
		return pReturn;
	}

	@Path("/getddbb/help")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDDBBhelp(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
			throws Exception
	{

		Response pReturn;
		StringBuilder pSB = new StringBuilder();
		String strHelp = "{\"Info\":\"Petición para devolver datos de la BBDD en formato JSON\","
				+ "\"Lista de parametros\":{\"params\":\"Parametros separados por [,] a recuperar)\","
				+ "\"tabla\":\"Nombre de la tabla.\",\"wherekey\":\"Nombre del campo para la sentencia where\","
				+ "\"wherevalue\":\"Valor del campo para la sentencia where\","
				+ "\"other\":\"Otra sentencia (añadir and si va despues del where)\"}" + "}";
		pReturn = Response.ok(strHelp).build();
		return pReturn;
	}
}
