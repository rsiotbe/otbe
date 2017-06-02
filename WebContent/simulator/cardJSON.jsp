<%@page import="com.rsi.rvia.rest.endpoint.simulators.CardObject"%>
<%@page import="com.rsi.rvia.rest.simulators.CardsManager"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.rsi.rvia.rest.DDBB.DDBBPoolFactory"%>
<%@page import="com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider"%>
<%@page import="com.rsi.rvia.rest.tool.Utils"%>
<%@page import="com.rsi.rvia.rest.simulators.CardsManager"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="org.json.HTTP"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%@page import="com.rsi.Constants"%>
<%@page import="com.rsi.Constants.Language"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%
    Logger pLog = LoggerFactory.getLogger("cardJSON.jsp");
    String strResponse;
    String strCardId;
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
            pLog.error("Error al leer el buffer de entrada", e);
        }
        JSONObject jsonObject = new JSONObject(jb.toString());
        strCardId = jsonObject.optString(Constants.PARAM_CARD_ID);
    }
    else
    {
        /* GET: Se recuperan los parametros de entrada */
        strCardId = (String) request.getParameter(Constants.PARAM_CARD_ID);
    }
    
    JSONObject card = CardsManager.getCardDetails(request);
    /* se comprueba si se genera algún objeto de respuesta, en caso negativo se devuelve un error 404 */
    if(card == null)
    {
        strResponse = Utils.generateWSResponseJsonError("card", 404, "Datos no encontrados");
    }
    else
    {
        strResponse = Utils.generateWSResponseJsonOk("card", card.toString());
        response.setContentType("application/json");
    }
%><%=strResponse%>