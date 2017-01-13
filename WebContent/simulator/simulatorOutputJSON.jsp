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
<%@page import="com.rsi.rvia.rest.simulators.SimulatorObjectArray"%>
<%@page import="com.rsi.Constantes"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%
    Logger pLog = LoggerFactory.getLogger("simulatorOutputJSON.jsp");
    JSONObject pJson = new JSONObject();
    JSONObject pJsonData = new JSONObject();
    String strResponse;
    String strNRBE;
    String strNRBEName;
    String strSimulatorType;
    String strSimulatorName;
    String strLanguage;
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
        strNRBE = jsonObject.optString(Constantes.SIMULADOR_NRBE);
        strNRBEName = jsonObject.optString(Constantes.SIMULADOR_NRBE_NAME);
        strSimulatorType = jsonObject.optString(Constantes.SIMULADOR_TYPE);
        strSimulatorName = jsonObject.optString(Constantes.SIMULADOR_SIMPLE_NAME);
        strLanguage = jsonObject.optString(Constantes.SIMULADOR_LANGUAGE);
    }
    else
    {
        /* GET: Se recuperan los parametros de entrada */
        strNRBE = (String) request.getParameter(Constantes.SIMULADOR_NRBE);
        strNRBEName = (String) request.getParameter(Constantes.SIMULADOR_NRBE_NAME);
        strSimulatorType = (String) request.getParameter(Constantes.SIMULADOR_TYPE);
        strSimulatorName = (String) request.getParameter(Constantes.SIMULADOR_SIMPLE_NAME);
        strLanguage = (String) request.getParameter(Constantes.SIMULADOR_LANGUAGE);
    }
    JSONObject pJsonResponse = new JSONObject();
    SimulatorObjectArray paSimulators = SimulatorsManager.getSimulatorsData(strNRBE, strNRBEName,
            strSimulatorType, strSimulatorName, strLanguage);
    pJsonData.put("data", paSimulators.toJson());
    pJson.put("response", pJsonData);
    strResponse = pJson.toString();
    response.setContentType("application/json");
%><%=strResponse%>