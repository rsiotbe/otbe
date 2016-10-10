<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
 	import="java.sql.Connection,
		 com.rsi.rvia.rest.DDBB.DDBBPoolFactory,
		 com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider,
		 com.rsi.rvia.rest.tool.Utils, 
		 com.rsi.rvia.rest.validator.DataValidator,
		 com.rsi.rvia.rest.client.QueryCustomizer,
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
	QueryCustomizer qCustomizer=new QueryCustomizer();
	String strJsonError = "{}";
	String strPathRest = "/costcontrol/contracts/{idContract}";
	String strContrato = request.getParameter("idContract").toString();
	String strEntidad = request.getParameter("codEntidad").toString();
	String strDateIni = request.getParameter("fechaInicio").toString();
	String strDateFin = request.getParameter("fechaFin");
	
/*	
	Hashtable<String,String> htParams = new Hashtable<String,String>();
	
	htParams.put("idContract", strContrato);
	htParams.put("codEntidad", strEntidad);
	htParams.put("fechaInicio", strDateIni);
	try{
		strJsonError = DataValidator.validation(strPathRest, htParams);
	}catch(Exception ex){		
		strJsonError = "{\"Error\":\""+ ex.getMessage() +"\"}";
		ex.printStackTrace();
	}

*/

	if(strDateFin == null){
		strDateFin = "9999-12-31";
	}

	String strResponse = "{}";
	if("{}".equals(strJsonError)){
		
	// Query si todo en orden
		String q =
		" select" +
		" 	mi_fecha_fin_mes \"finMes\"," +
		" 	mi_sdo_ac_p \"saldoPuntual\"," +
		" 	mi_sdo_dispble_p \"saldoDisponible\"," +
		" 	mi_sdo_acr_p \"saldoAcreedor\"," +
		" 	mi_sdo_deu_p \"saldoDeudor\"" +
		" from rdwc01.mi_ac_eco_gen" +
		" where cod_nrbe_en='" + request.getParameter("codEntidad") + "'" +
		" and num_sec_ac =" + request.getParameter("idContract") + 
		" and mi_fecha_fin_mes >= to_date('" + strDateIni + "','yyyy-mm-dd')" +
		" and mi_fecha_fin_mes <= to_date('" + strDateFin + "','yyyy-mm-dd')";

		qCustomizer.setFieldsList(request.getParameter("fieldslist"));
		qCustomizer.setSortersList(request.getParameter("sorterslist"));
		q = qCustomizer.getParsedQuery(q);				
		
		Connection pConnection = null;
		PreparedStatement pPreparedStatement = null;
		ResultSet pResultSet = null;
		pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleCIP);
		pPreparedStatement = pConnection.prepareStatement(q);
		/*
		pPreparedStatement.setString(1, strEntidad);
		pPreparedStatement.setInt(2, nContrato);
		pPreparedStatement.setDate(3, java.sql.Date.valueOf(strDate));
		*/
		pResultSet = pPreparedStatement.executeQuery();

		JSONObject pJson = new JSONObject();
		JSONObject pJsonExit= new JSONObject();
		Logger	pLog = LoggerFactory.getLogger("jsp");	
		
		JSONArray json = Utils.convertResultSetToJSON(pResultSet);
		pResultSet.close();
		pPreparedStatement.close();
		pConnection.close();
		pJsonExit.put("data", json);
		pJson.put("response", pJsonExit);
		strResponse = pJson.toString();
	}else{
		strResponse = strJsonError;
	}
	
	response.setHeader("content-type", "application/json");
	//pLog.info("------------------------------- " + json.toString());
%><%=strResponse%>
