<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
 	import="com.rsi.rvia.rest.DDBB.DDBBConnection,
		 com.rsi.rvia.rest.DDBB.DDBBFactory,
		 com.rsi.rvia.rest.DDBB.DDBBFactory.DDBBProvider,com.rsi.rvia.rest.DDBB.CIPOracleDDBB, 
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
