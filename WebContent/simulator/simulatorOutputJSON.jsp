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
<%@page import="com.rsi.rvia.rest.simulators.SimulatorConfigObjectArray"%>
<%@page import="com.rsi.Constantes"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%
	Logger pLog = LoggerFactory.getLogger("simulatorOutputJSON.jsp");
	JSONObject pJson = new JSONObject();
	JSONObject pJsonData = new JSONObject();
	String strResponse;
	/* Se recuperan los parametros de entrada*/
	String strNRBE = (String) request.getParameter(Constantes.SIMULADOR_NRBE);
	String strNRBEName = (String) request.getParameter(Constantes.SIMULADOR_NRBE_NAME);
	String strSimulatorType = (String) request.getParameter(Constantes.SIMULADOR_TYPE);
	String strSimulatorName = (String) request.getParameter(Constantes.SIMULADOR_SIMPLE_NAME);
	String strLanguage = (String) request.getParameter(Constantes.SIMULADOR_LANGUAGE);
	JSONObject pJsonResponse = new JSONObject();
	SimulatorConfigObjectArray paSimulators = SimulatorsManager.getSimulatorsData(strNRBE, strNRBEName, strSimulatorType, strSimulatorName, strLanguage);
	pJsonData.put("data",paSimulators.toJson());
	pJson.put("response",pJsonData); 
	strResponse = pJson.toString();
	response.setContentType("application/json");
%><%=strResponse%>