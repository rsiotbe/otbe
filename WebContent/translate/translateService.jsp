<%@page import="com.rsi.rvia.translates.TranslateAppService"%>
<%@page import="java.util.Hashtable"%>
<%@page import="com.rsi.rvia.rest.simulators.SimulatorEmailConfig"%>
<%@page import="java.net.HttpURLConnection"%>
<%@page import="java.net.URLConnection"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.util.Properties"%>
<%@page import="java.io.ByteArrayOutputStream"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%@page import="com.rsi.rvia.rest.DDBB.DDBBPoolFactory"%>
<%@page import="com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider"%>
<%@page import="com.rsi.rvia.rest.tool.Utils"%>
<%@page import="com.rsi.rvia.rest.simulators.SimulatorsManager"%>		 
<%@page import="com.rsi.rvia.rest.simulators.SimulatorConfigObjectArray"%>
<%@page import="com.rsi.rvia.rest.client.RviaRestHttpClient"%>		 
<%@page import="com.rsi.rvia.rest.tool.*"%>		 
<%@page import="com.rsi.rvia.mail.*"%>		 
<%@page import="com.rsi.Constants"%>
<%@page import="com.rsi.Constants.Language"%>
<%@page import="javax.ws.rs.core.*"%>
<%@page import="javax.ws.rs.client.*"%>
<%@ page language="java" pageEncoding="UTF-8"%>

<%
	Logger pLog = LoggerFactory.getLogger("translateService.jsp");
	String strReturn = null;
	
    try
    {
		String strRequestContent = null;
		String strAppName = null;
		String strLanguage = null; 
		Language pLanguage = null;
   		strAppName = request.getParameter(Constants.TRANSLATE_APPNAME);
    	strLanguage = request.getParameter(Constants.TRANSLATE_LANG); 
		pLog.info("Se recupera la configuración que le llega al jsp: \n" +
				"AppName:  " + strAppName +"\n" +	
				"Language: " + strLanguage +"");
		if(strLanguage == null || strLanguage == "ALL")
		    pLanguage = null;
		else
		    pLanguage = Language.getEnumValue(strLanguage);
		
		/* se llama a la clase que obtiene las traducciones */
		strReturn = TranslateAppService.process(strAppName, pLanguage);
		strReturn = Utils.generateWSResponseJsonOk("translate", strReturn);		
    }
    catch (Exception ex)
    {
		pLog.error("Error al procesar la petición de traducción", ex);
		strReturn = Utils.generateWSResponseJsonError("translate", 99999, ex.toString());
    }
%><%=strReturn%>
