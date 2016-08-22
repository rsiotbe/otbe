<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
 	import="com.rsi.rvia.rest.DDBB.DDBBConnection,
		 com.rsi.rvia.rest.DDBB.DDBBFactory,
		 com.rsi.rvia.rest.DDBB.DDBBFactory.DDBBProvider,
		 com.rsi.rvia.rest.DDBB.OracleDDBB, 
		 com.rsi.rvia.rest.tool.Resultset2JSONConverter,		 
		 java.sql.PreparedStatement,
		 java.sql.ResultSet,
		 org.json.JSONArray,
		 org.slf4j.Logger,
		 org.slf4j.LoggerFactory
"
%>

<%
	String q =
			" SELECT" + 
			" NUM_SEC_AC \"numAcuerdo\", id_interno_pe" +
			" FROM rdwc01.mi_clte_rl_ac" +
			" WHERE MI_FECHA_FIN=to_date('31/12/9999','dd/mm/yyyy')" +
			" AND COD_NRBE_EN=?" +
			" AND COD_RL_PERS_AC='01'" +
			" AND NUM_RL_ORDEN=1" +
			" AND COD_ECV_PERS_AC='2'" +
			" AND ID_INTERNO_PE=?" ;

	Logger	pLog = LoggerFactory.getLogger("jsp");
	
	DDBBConnection p3 = DDBBFactory.getDDBB(DDBBProvider.Oracle,"cip");	
	PreparedStatement ps = p3.prepareStatement(q);
	ps.setString(1,request.getParameter("codEntidad"));
	ps.setString(2, request.getParameter("idInternoPe"));
	ResultSet rs = p3.executeQuery(ps);
	JSONArray json = Resultset2JSONConverter.convert(rs);
	rs.close();
	ps.close();
	p3.BBDD_Disconnect();	
	String respuesta=json.toString();
	
	response.setHeader("content-type", "application/json");
	
	pLog.info("------------------------------- " + json.toString());
	
%>
<%=respuesta %>
