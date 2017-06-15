<%@page import="org.json.JSONObject"%>
<%@page import="com.rsi.rvia.rest.tool.Utils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="
        org.slf4j.Logger,
        org.slf4j.LoggerFactory,
        java.util.*              
"
%>
<%
	JSONObject pJson = new JSONObject();
	String strJWT = (String)request.getHeader("Authorization");
	pJson.put("JWT", strJWT);
	String strResponse = Utils.generateWSResponseJsonOk("rviatoken", pJson.toString());
%>
<%=strResponse %>
