<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="
         com.rsi.rvia.rest.client.MiqAdminValidator,
         org.slf4j.Logger,
        org.slf4j.LoggerFactory 
"
%>
<%
String target=request.getQueryString();


String partes[]=target.split("=");
target=partes[1];

String strHtml = null;
String strUser=request.getParameter("user");
if(strUser != null){    
    strHtml = MiqAdminValidator.doLogin(request, response);
}
if(strHtml == null){
    strHtml = MiqAdminValidator.makeHTML("", "","",target,"","",false);
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<%=strHtml%>
</body>
</html>