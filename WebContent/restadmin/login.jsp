<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="
         com.rsi.rvia.rest.client.QueryCustomizer,
         com.rsi.rvia.rest.DDBB.DDBBPoolFactory,
         com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider,
        java.sql.Connection,
        java.sql.PreparedStatement,
        java.sql.ResultSet,
        org.slf4j.Logger,
        org.slf4j.LoggerFactory              
"
%>
<%

String uri = request.getRequestURI();
String pageName = uri.substring(uri.lastIndexOf("/")+1);
Logger pLog  = LoggerFactory.getLogger(pageName);
    String strLinea = request.getParameter("codLinea");
    String strCodClasificacion = request.getParameter("codClasificacion");
    String whereLineaEq="";
    if(strLinea != null){
        whereLineaEq=" AND T1.COD_LINEA = '" + strLinea + "'";
    }
    else if(strCodClasificacion != null){
      switch(Integer.parseInt(strCodClasificacion)){
          case 1:             
              whereLineaEq=" AND trim(T1.COD_LINEA)||trim(t1.ID_GRP_PD) in  ('0311','0321')";             
              break;
          case 2:
              whereLineaEq=" AND trim(T1.COD_LINEA)||trim(t1.ID_GRP_PD) in  ('0351','0352')";
              break;              
          case 3:
              whereLineaEq=" AND trim(T1.COD_LINEA)||trim(t1.ID_GRP_PD) = '0171'";
              break;
          case 4:
              whereLineaEq=" AND trim(T1.COD_LINEA)||trim(t1.ID_GRP_PD) = '0151'";
              break;
          case 5:
              whereLineaEq=" AND trim(T1.COD_LINEA)||trim(t1.ID_GRP_PD) in  ('0551','0151')";
              break;
      }
  }
    else{
        whereLineaEq=" AND trim(t1.COD_LINEA)||trim(t1.ID_GRP_PD) in  ('0311','0321','0351','0352','0171','0551','0151')";
    }
    

    
    
    String strResponse = "{\"1\":\"2\",\"2\":\"1\"}";       
    response.setHeader("content-type", "application/json");
%>
<%=strResponse %>
