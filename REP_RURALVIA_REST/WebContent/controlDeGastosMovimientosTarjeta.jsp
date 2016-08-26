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
		 org.slf4j.LoggerFactory	 		 
"
%>

<%

	JSONObject jsonError = new JSONObject();
	DDBBConnection p3 = DDBBFactory.getDDBB(DDBBProvider.OracleCIP,"cip");
	int contrato = Integer.parseInt(request.getParameter("idContract"));
	String entidad = request.getParameter("codEntidad");
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
	
	PreparedStatement ps = p3.prepareStatement(q);	
	ResultSet rs = p3.executeQuery(ps);					
	ps = p3.prepareStatement(q);			
	ps.setString(1,entidad);

	
	rs = p3.executeQuery(ps);		
	JSONObject jsonExit= new JSONObject();
	jsonExit.put("token", "sitio para el token");
	Logger	pLog = LoggerFactory.getLogger("jsp");	
	
	JSONArray json = Utils.convertResultSet2JSON(rs);
	rs.close();
	ps.close();
	p3.BBDD_Disconnect();
	jsonExit.put("output", json);	
	String respuesta=jsonExit.toString();
	response.setHeader("content-type", "application/json");
	//pLog.info("------------------------------- " + json.toString());
%>
<%=respuesta %>
