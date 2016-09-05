package com.rsi.rvia.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.json.JSONArray;
import com.rsi.rvia.rest.DDBB.DDBBConnection;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.tool.Utils;

@Path("/pooltest")
public class DDBBPoolTest
{
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
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
		
		Connection pConection = DDBBPoolFactory.getDDBB(DDBBProvider. OracleBanca);
		PreparedStatement pPreparedStament = pConection.prepareStatement(strQuery);
		ResultSet pResultSet = pPreparedStament.executeQuery();
		JSONArray jsonArray = new JSONArray();
		jsonArray = Utils.convertResultSet2JSON(pResultSet);
		

		
		strReturn = "hola";
		pReturn = Response.ok(strReturn).build();
		return pReturn;
	}
}
