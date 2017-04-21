<%@page import="java.sql.Connection"%>
<%@page import="com.rsi.rvia.rest.DDBB.DDBBPoolFactory"%>
<%@page import="com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider"%>
<%@page import="com.rsi.rvia.rest.tool.Utils"%>
<%@page import="com.rsi.rvia.rest.simulators.SimulatorsManager"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="org.json.HTTP"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%@page import="com.rsi.rvia.rest.simulators.SimulatorConfigObjectArray"%>
<%@page import="com.rsi.Constants"%>
<%@page import="com.rsi.Constants.Language"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%
    Logger pLog = LoggerFactory.getLogger("simulatorOutputJSON.jsp");
    String strResponse;
    String strNRBE;
    String strNRBEName;
    String strSimulatorType;
    String strSimulatorName;
    String strLanguage;
    Language pLanguage;
    if (request.getMethod().equals(javax.ws.rs.HttpMethod.POST))
    {
        StringBuffer jb = new StringBuffer();
        String line = null;
        try
        {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        }
        catch (Exception e)
        {
            // TODO: report an error.
        }
        JSONObject jsonObject = new JSONObject(jb.toString());
        strNRBE = jsonObject.optString(Constants.SIMULADOR_NRBE);
        strNRBEName = jsonObject.optString(Constants.SIMULADOR_NRBE_NAME);
        strSimulatorType = jsonObject.optString(Constants.SIMULADOR_TYPE);
        strSimulatorName = jsonObject.optString(Constants.SIMULADOR_SIMPLE_NAME);
        strLanguage = jsonObject.optString(Constants.SIMULADOR_LANGUAGE);
    }
    else
    {
        /* GET: Se recuperan los parametros de entrada */
        strNRBE = (String) request.getParameter(Constants.SIMULADOR_NRBE);
        strNRBEName = (String) request.getParameter(Constants.SIMULADOR_NRBE_NAME);
        strSimulatorType = (String) request.getParameter(Constants.SIMULADOR_TYPE);
        strSimulatorName = (String) request.getParameter(Constants.SIMULADOR_SIMPLE_NAME);
        strLanguage = (String) request.getParameter(Constants.SIMULADOR_LANGUAGE);
    }
    /* se obtiene le objeto idioma */
    if(strLanguage == null || strLanguage.trim().isEmpty())
    {
        pLanguage = Constants.DEFAULT_LANGUAGE;
    }
    else
    {
        pLanguage = Language.valueOf(strLanguage);
    }
	SimulatorConfigObjectArray paSimulators = SimulatorsManager.getSimulatorsData(strNRBE, strNRBEName, 
	        strSimulatorType, strSimulatorName, pLanguage);
	/* se comprueba si se genera algún objeto de respuesta, en caso negativo se devuelve un error 404 */
	if(paSimulators.isEmpty())
	{
	    strResponse = Utils.generateWSResponseJsonError("simulatorConfig", 404, "Datos no encontrados");
	}
	else
	{
	    strResponse = Utils.generateWSResponseJsonOk("simulatorConfig", paSimulators.toJson().toString());
	    response.setContentType("application/json");	    
	}
%><%=strResponse%>