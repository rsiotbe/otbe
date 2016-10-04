<%@page
	import="java.sql.Connection,
 		 com.rsi.rvia.rest.DDBB.DDBBPoolFactory,
		 com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider,
		 com.rsi.rvia.rest.tool.Utils,
		 com.rsi.rvia.rest.simulators.SimulatorsManager,		 
		 java.sql.PreparedStatement,
		 java.sql.ResultSet,
		 java.util.Hashtable,
		 java.util.Enumeration,		 
		 org.json.JSONObject,
         org.slf4j.Logger,
		 org.slf4j.LoggerFactory"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%
	Logger pLog = LoggerFactory.getLogger("simulador.jsp");
	JSONObject pJson = new JSONObject();
	JSONObject pJsonData = new JSONObject();
	String strResponse = "{}";
	/* Se recuperan los parametros de entrada*/
	String strEntity = (String) request.getParameter("codEntidad");
	/* Las funciones tienen que venir separadas por punto y coma a;b;c */
	String strFunctions = (String) request.getParameter("funciones");
	strResponse = SimulatorsManager.getJSFunctionsByEntity(strEntity, strFunctions);
	JSONObject pJsonResponse = new JSONObject(strResponse);
	
	pJsonData.put("data",pJsonResponse);
	pJson.put("response",pJsonData);
	strResponse = pJson.toString();
	response.setContentType("text/json");
%><%=strResponse%>