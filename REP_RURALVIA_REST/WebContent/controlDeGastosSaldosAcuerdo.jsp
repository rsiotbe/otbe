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

	JSONObject jsonError = new JSONObject();
	DDBBConnection p3 = DDBBFactory.getDDBB(DDBBProvider.OracleCIP,"cip");
	

	// Validación de forma de número de acuerdo
	int contrato = 0;
	try{
		contrato = Integer.parseInt(request.getParameter("idContract"));	
		if(contrato <= 0){
			jsonError.put("idContract", "Identificador de acuerdo <=  0");
		}
	} 
	catch(Exception ex){
		jsonError.put("idContract", "Identificador de acuerdo no numérico");		
	}
	
	// Validación de existencia de entidad
	String entidad = request.getParameter("codEntidad");
	String q =
		" select cod_nrbe_en from prox01.sx_entidad " +
		" where cod_nrbe_en=?";
	
	PreparedStatement ps = p3.prepareStatement(q);	
	ps.setString(1,entidad);
	ResultSet rs = p3.executeQuery(ps);		
	if(!rs.next()){
		jsonError.put("codEntidad", "Código de entidad no válido");
	}
	rs.close();
	ps.close();
	
	// Validación de fecha en formato y forma
	String formato = "yyyy-mm-dd";
	SimpleDateFormat ft = new SimpleDateFormat(formato);
	String dato = request.getParameter("fechaInicio").toString();
	if(dato.trim().equals("")){
		jsonError.put("fechaInicio", "Fecha de inicio inexistente o inválida");
	}	
	Date fechaIni;
	try{
		fechaIni = ft.parse(dato);
	}
	catch(Exception ex){
		jsonError.put("fechaInicio", "Fecha de inicio no ajusta a formato yyyy-mm-dd");
	}
	
	// Query si todo en orden
	q =
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
	
			
	ps = p3.prepareStatement(q);			
	ps.setString(1,entidad);
	ps.setInt(2, contrato);
	ps.setDate(3, java.sql.Date.valueOf(dato));
	
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
