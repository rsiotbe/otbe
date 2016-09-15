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
	JSONObject jsonError = new JSONObject();
	int contrato = Integer.parseInt(request.getParameter("idContract"));
	String entidad = request.getParameter("codEntidad");
	String q =
			" select /*+ FULL(e) */" +
			" NUM_SEC_AC," +
			" FECHA_OPRCN," +
			" HORA_OPRCN_U," +
			" IMPTRN," +
			" CODRESPU," +
			" NOMCOMRED," +
			" CLAMONTRN," +
			" CENTAUT," +
			" LOCALIDAD2" +
			" from rdwc01.MI_MPA2_OPERAC_TARJETAS e" +
			" WHERE COD_NRBE_EN = '3076'" +
			" and MI_FECHA_FIN = to_date('9999.12.31', 'YYYY.MM.DD')" +
			" and FECHA_OPRCN >= to_date('01.01.2016','dd.mm.yyyy')" +
			" AND NUM_SEC_AC=2092922182;" ;			
			

	Connection pConnection = null;
	PreparedStatement pPreparedStatement = null;
	ResultSet pResultSet = null;
	pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleCIP);
	pPreparedStatement = pConnection.prepareStatement(q);
	pPreparedStatement.setString(1, entidad);
	pResultSet = pPreparedStatement.executeQuery();

	JSONObject jsonExit= new JSONObject();
	JSONObject pJson = new JSONObject();
	jsonExit.put("token", "sitio para el token");
	Logger	pLog = LoggerFactory.getLogger("jsp");	
	
	JSONArray json = Utils.convertResultSet2JSON(pResultSet);
	pResultSet.close();
	pPreparedStatement.close();
	pConnection.close();
	
	jsonExit.put("output", json);	
	pJson.put("response", jsonExit);
	String respuesta=pJson.toString();
	response.setHeader("content-type", "application/json");
	//pLog.info("------------------------------- " + json.toString());
%>
<%=respuesta %>
