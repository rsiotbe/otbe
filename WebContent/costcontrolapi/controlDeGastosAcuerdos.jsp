<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
 	import="java.sql.Connection,
 		 com.rsi.rvia.rest.DDBB.DDBBPoolFactory,
		 com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider,
		 com.rsi.rvia.rest.tool.Utils,		 
		 java.sql.PreparedStatement,
		 java.sql.ResultSet,		 
		 org.json.JSONArray,
		 org.json.JSONObject,
         org.slf4j.Logger,
		 org.slf4j.LoggerFactory
"
%>
<%
	String q =
		" SELECT" +   
		" 	t1.NUM_SEC_AC \"acuerdo\", trim(t2.NOMB_GRP_PD) \"nombreGrupo\"," +
		" 	trim(nvl(t3.NOMB_PDV, t2.NOMB_GRP_PD)) \"nombreProducto\"" +
		" FROM" +
		" 	rdwc01.mi_clte_rl_ac t1" +
		" 	left join rdwc01.MI_LINEA_GRUPO t2" +
		" 	on" +
		" 		t1.COD_LINEA = t2.COD_LINEA" +
		" 		AND t1.ID_GRP_PD = t2.ID_GRP_PD" +
		" 	left outer join rdwc01.MI_LIN_GR_PRODTO t3" +
		" 	on" +
		" 		t1.COD_LINEA = t3.COD_LINEA" +
		" 		AND t1.ID_GRP_PD = t3.ID_GRP_PD" +
		" 		AND t1.ID_PDV = t3.ID_PDV" +
		" 		AND t1.cod_nrbe_en = t3.cod_nrbe_en" +
		" 		and t3.mi_fecha_fin = t2.mi_fecha_fin" +
		" WHERE  t1.MI_FECHA_FIN = to_date('31.12.9999','dd.mm.yyyy')" +
		" 	AND t1.COD_NRBE_EN =?" +
		" 	AND t1.COD_RL_PERS_AC = '01'" +
		" 	AND t1.NUM_RL_ORDEN = 1" +
		" 	AND t1.COD_ECV_PERS_AC = '2'" +
		" 	AND t1.ID_INTERNO_PE =?" +
		" 	and t2.mi_fecha_fin = (select MI_FECHA_PROCESO from rdwc01.ce_carga_tabla" +
		" 		where nomtabla='MI_LINEA_GRUPO')";
	JSONObject pp= new JSONObject();
	JSONObject pJson = new JSONObject();
	Logger	pLog = LoggerFactory.getLogger("jsp");

	//pLog.info("--------------------------------: " + request.getParameter("idMiq"));
		
	Connection pConnection = null;
	PreparedStatement pPreparedStatement = null;
	ResultSet pResultSet = null;
	pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleCIP);
	pPreparedStatement = pConnection.prepareStatement(q);
	pPreparedStatement.setString(1, request.getParameter("codEntidad"));
	pPreparedStatement.setString(2, request.getParameter("idInternoPe"));
	pResultSet = pPreparedStatement.executeQuery();
	
	JSONArray json = Utils.convertResultSet2JSON(pResultSet);
	pResultSet.close();
	pPreparedStatement.close();
	pConnection.close();
	pp.put("data", json);
	pJson.put("response",pp);
	String respuesta=pJson.toString();
	response.setHeader("content-type", "application/json");
	//pLog.info("------------------------------- " + json.toString());
%>
<%=respuesta %>
