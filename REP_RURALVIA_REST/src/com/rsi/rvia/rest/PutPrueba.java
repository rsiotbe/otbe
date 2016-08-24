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
import com.rsi.rvia.rest.tool.Utils;

@Path("/prueba")
public class PutPrueba
{
	private static Logger			pLog	= LoggerFactory.getLogger(Cards.class);

	@PUT
	@Path("/put")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response updateThink(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
			throws Exception
	{
		pLog.info("Se recibe una peticion de cashierLocatior, PUT");
	
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
			DDBBConnection pDBConection = DDBBFactory.getDDBB(DDBBProvider.OracleBDES,"beld");
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
	
	@Path("/checkddbb")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkDoubleDDBB(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
			throws Exception
	{
		Response pReturn;
		String strReturn = "";
		String strQuery = "select * from belts100 where clave_pagina = 'BDP_HC_NC_CLAUSULAS_P'";
		String strQueryCip = " SELECT" + 
				" NUM_SEC_AC \"numAcuerdo\", id_interno_pe" +
				" FROM rdwc01.mi_clte_rl_ac" +
				" WHERE MI_FECHA_FIN=to_date('31/12/9999','dd/mm/yyyy')" +
				" AND COD_NRBE_EN='3076'" +
				" AND COD_RL_PERS_AC='01'" +
				" AND NUM_RL_ORDEN=1" +
				" AND COD_ECV_PERS_AC='2'" +
				" AND ID_INTERNO_PE=16" ;
		DDBBConnection pDBConection = DDBBFactory.getDDBB(DDBBProvider.OracleBTEST, "belt");
		PreparedStatement pPreparedStament = pDBConection.prepareStatement(strQuery);
		ResultSet pResultSet = pDBConection.executeQuery(pPreparedStament);
		JSONArray jsonArray = new JSONArray();
		jsonArray = Utils.convertResultSet2JSON(pResultSet);

		DDBBConnection pDBConection2 = DDBBFactory.getDDBB(DDBBProvider.OracleBDES, "beld");
		PreparedStatement pPreparedStament2 = pDBConection2.prepareStatement(strQuery);
		ResultSet pResultSet2 = pDBConection2.executeQuery(pPreparedStament2);
		JSONArray jsonArray2 = new JSONArray();
		jsonArray2 = Utils.convertResultSet2JSON(pResultSet2);
		
		DDBBConnection pDBConection3 = DDBBFactory.getDDBB(DDBBProvider.OracleCIP, "cip");
		PreparedStatement pPreparedStament3 = pDBConection3.prepareStatement(strQueryCip);
		ResultSet pResultSet3 = pDBConection3.executeQuery(pPreparedStament3);
		JSONArray jsonArray3 = new JSONArray();
		jsonArray3 = Utils.convertResultSet2JSON(pResultSet3);
		
		strReturn = "{\"1\":" + jsonArray.toString() + ",\"2\":" + jsonArray2.toString() + ",\"3\":" + jsonArray3.toString() + "}";
		pReturn = Response.ok(strReturn).build();
		return pReturn;
	}
	
	@Path("/checkcip")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkCipDDBB(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
			throws Exception
	{
		Response pReturn;
		String strReturn = "";
		String strQueryCip = " SELECT" + 
				" NUM_SEC_AC \"numAcuerdo\", id_interno_pe" +
				" FROM rdwc01.mi_clte_rl_ac" +
				" WHERE MI_FECHA_FIN=to_date('31/12/9999','dd/mm/yyyy')" +
				" AND COD_NRBE_EN='3076'" +
				" AND COD_RL_PERS_AC='01'" +
				" AND NUM_RL_ORDEN=1" +
				" AND COD_ECV_PERS_AC='2'" +
				" AND ID_INTERNO_PE=20" ;
		DDBBConnection pDBConection3 = DDBBFactory.getDDBB(DDBBProvider.OracleCIP, "cip");
		PreparedStatement pPreparedStament3 = pDBConection3.prepareStatement(strQueryCip);
		ResultSet pResultSet3 = pDBConection3.executeQuery(pPreparedStament3);
		JSONArray jsonArray3 = new JSONArray();
		jsonArray3 = Utils.convertResultSet2JSON(pResultSet3);
		
		strReturn = jsonArray3.toString();
		pReturn = Response.ok(strReturn).build();
		return pReturn;
	}
}
