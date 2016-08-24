<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
 	import="com.rsi.rvia.rest.DDBB.DDBBConnection,
		 com.rsi.rvia.rest.DDBB.DDBBFactory,
		 com.rsi.rvia.rest.DDBB.DDBBFactory.DDBBProvider,
		 com.rsi.rvia.rest.DDBB.OracleDDBBCIP, 
		 com.rsi.rvia.rest.tool.Utils, 
		 java.sql.PreparedStatement,
		 java.sql.ResultSet,
		 org.json.JSONArray,
		 org.json.JSONObject,
		 org.slf4j.Logger,
		 org.slf4j.LoggerFactory,
		java.util.Date,
		java.text.ParseException,
		java.text.SimpleDateFormat		 		 
"
%>

<%

	DDBBConnection p3 = DDBBFactory.getDDBB(DDBBProvider.OracleCIP,"cip");
	String formato = "yyyy-mm-dd";
	SimpleDateFormat ft = new SimpleDateFormat(formato);
	String dato = request.getParameter("fechaInicio").toString();
	
	Date fechaIni = ft.parse(dato);

	String q =
			" select" +
			" 	mi_fecha_fin_mes \"finMes\"," +
			" 	mi_sdo_ac_p \"sdoPuntual\"," +
			" 	mi_sdo_dispble_p \"sdoDisponible\"," +
			" 	mi_sdo_acr_p \"sdoAcreedor\"," +
			" 	mi_sdo_deu_p \"sdoDeudor\"" +
			" from rdwc01.mi_ac_eco_gen" +
			" where cod_nrbe_en=?" +
			" and num_sec_ac =?" +				
			" and mi_fecha_fin_mes >=?" +
			" and mi_fecha_fin_mes < to_date('31129999','ddmmyyyy')";
	JSONObject pp= new JSONObject();
	pp.put("token", "sitio para el token");
	Logger	pLog = LoggerFactory.getLogger("jsp");	
		
	
	PreparedStatement ps = p3.prepareStatement(q);	
	int contrato = Integer.parseInt(request.getParameter("idContract"));	
	ps.setString(1,request.getParameter("codEntidad"));
	ps.setInt(2, contrato);
	ps.setDate(3, java.sql.Date.valueOf(dato));

	
	ResultSet rs = p3.executeQuery(ps);	
	JSONArray json = Utils.convertResultSet2JSON(rs);
	rs.close();
	ps.close();
	p3.BBDD_Disconnect();
	pp.put("output", json);	
	String respuesta=pp.toString();
	response.setHeader("content-type", "application/json");
	//pLog.info("------------------------------- " + json.toString());
%>
<%=respuesta %>
