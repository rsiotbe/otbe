<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
 	import="java.sql.Connection,
		 com.rsi.rvia.rest.DDBB.DDBBPoolFactory,
		 com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider,
		 com.rsi.rvia.rest.tool.Utils, 
		 com.rsi.rvia.rest.validator.DataValidator,
		 java.sql.PreparedStatement,
		 java.sql.ResultSet,
		 org.json.JSONArray,
		 org.json.JSONObject,
		 org.slf4j.Logger,
		 org.slf4j.LoggerFactory,
		java.util.Date,
		java.util.Hashtable,
		java.text.ParseException,
		java.text.SimpleDateFormat		 		 
"
%>
<%


	// Validación de forma de número de acuerdo
	String strJsonError = "{}";
	String strPathRest = "/costcontrol/contracts/{idContract}";
	String strContrato = request.getParameter("idContract").toString();
	String strEntidad = request.getParameter("codEntidad").toString();
	String strDate = request.getParameter("fechaInicio").toString();
	Hashtable<String,String> htParams = new Hashtable<String,String>();
	
	htParams.put("idContract", strContrato);
	htParams.put("codEntidad", strEntidad);
	htParams.put("fechaInicio", strDate);
	try{
		strJsonError = DataValidator.validation(strPathRest, htParams);
	}catch(Exception ex){
		
		strJsonError = "{\"Error\":\""+ ex.getMessage() +"\"}";
		ex.printStackTrace();
	}

	String strResponse = "{}";
	if("{}".equals(strJsonError)){
		
	// Query si todo en orden
		String strQuery =
				" select" +
				" 	mi_fecha_fin_mes \"finMes\"," +
				" 	mi_sdo_ac_p \"saldoPuntual\"," +
				" 	mi_sdo_dispble_p \"saldoDisponible\"," +
				" 	mi_sdo_acr_p \"saldoAcreedor\"," +
				" 	mi_sdo_deu_p \"saldoDeudor\"" +
				" from rdwc01.mi_ac_eco_gen" +
				" where cod_nrbe_en=?" +
				" and num_sec_ac =?" +				
				" and mi_fecha_fin_mes >=?" +
				" and mi_fecha_fin_mes < to_date('31129999','ddmmyyyy')";
				
		int nContrato = Integer.parseInt(strContrato);
		Connection pConnection = null;
		PreparedStatement pPreparedStatement = null;
		ResultSet pResultSet = null;
		pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleCIP);
		pPreparedStatement = pConnection.prepareStatement(strQuery);
		pPreparedStatement.setString(1, strEntidad);
		pPreparedStatement.setInt(2, nContrato);
		pPreparedStatement.setDate(3, java.sql.Date.valueOf(strDate));
		pResultSet = pPreparedStatement.executeQuery();

		JSONObject pJson = new JSONObject();
		JSONObject pJsonExit= new JSONObject();
		pJsonExit.put("token", "sitio para el token");
		Logger	pLog = LoggerFactory.getLogger("jsp");	
		
		JSONArray json = Utils.convertResultSet2JSON(pResultSet);
		pResultSet.close();
		pPreparedStatement.close();
		pConnection.close();
		pJsonExit.put("output", json);
		pJson.put("response", pJsonExit);
		strResponse = pJson.toString();
	}else{
		strResponse = strJsonError;
	}
	
	response.setHeader("content-type", "application/json");
	//pLog.info("------------------------------- " + json.toString());
%><%=strResponse%>
