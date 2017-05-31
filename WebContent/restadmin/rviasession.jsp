<%@page import="com.rsi.rvia.rest.tool.Utils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="
        org.slf4j.Logger,
        org.slf4j.LoggerFactory              
"
%>
<%
	String strResponse = Utils.generateWSResponseJsonOk("rviatoken", "{}");
%>
<%=strResponse %>
