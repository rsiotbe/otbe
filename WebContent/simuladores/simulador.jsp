<%@page
	import="java.sql.Connection,
 		 com.rsi.rvia.rest.DDBB.DDBBPoolFactory,
		 com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider,
		 com.rsi.rvia.rest.tool.Utils,
		 com.rsi.rvia.rest.applications.SimulatorsManager,		 
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
	/* Recibe el tipo de JS, minificado o sin minificar */
	String strType = (String) request.getParameter("jstype");
	strResponse = SimulatorsManager.getFunctions4Entity(strEntity, strFunctions,strType);
	JSONObject pJsonResponse = new JSONObject(strResponse);
	Hashtable<String,String> htConfig = SimulatorsManager.getParamConfigFromDDBB(strEntity);
	
	for (Enumeration en = htConfig.keys(); en.hasMoreElements();)
	{
		String strKey = (String) en.nextElement();
		String strValue = htConfig.get(strKey);
		pJsonResponse.put(strKey, strValue);
	}
	
	pJsonData.put("data",pJsonResponse);
	pJson.put("response",pJsonData);
	strResponse = pJson.toString();
	response.setContentType("text/json");
	
%><%=strResponse%>