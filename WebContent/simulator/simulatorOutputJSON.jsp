<%@page import="java.sql.Connection"%>
<%@page import="com.rsi.rvia.rest.DDBB.DDBBPoolFactory"%>
<%@page import="com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider"%>
<%@page import="com.rsi.rvia.rest.tool.Utils"%>
<%@page import="com.rsi.rvia.rest.simulators.SimulatorsManager"%>		 
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Enumeration"%>		 
<%@page import="org.json.JSONObject"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%@page import="com.rsi.rvia.rest.simulators.SimulatorObjectArray"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%
	Logger pLog = LoggerFactory.getLogger("simulatorOutputJSON.jsp");
	JSONObject pJson = new JSONObject();
	JSONObject pJsonData = new JSONObject();
	String strResponse;
	/* Se recuperan los parametros de entrada*/
	String strNRBE = (String) request.getParameter("codEntidad");
	String strNRBEName = (String) request.getParameter("entidad");
	String strSimulatorName = (String) request.getParameter("nombreComercialSimulador");
	String strLanguage = (String) request.getParameter("idioma");
	JSONObject pJsonResponse = new JSONObject();
	SimulatorObjectArray paSimulators = SimulatorsManager.getSimulatorsData(strNRBE, strNRBEName, strSimulatorName, strLanguage);
	pJsonData.put("data",paSimulators.toJson());
	pJson.put("response",pJsonData);
	strResponse = pJson.toString();
	response.setContentType("application/json");
%><%=strResponse%>