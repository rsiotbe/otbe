<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="
        com.rsi.rvia.rest.client.QueryCustomizer,
        org.slf4j.Logger,
        org.slf4j.LoggerFactory 
"
%>
<%
String uri = request.getRequestURI();
String pageName = uri.substring(uri.lastIndexOf("/")+1);
Logger pLog  = LoggerFactory.getLogger(pageName);
	String strContrato = request.getParameter("idContract");  
	String strLinea = request.getParameter("codLinea");
	String strEntidad = request.getParameter("codEntidad");
	String strTipoApunte = request.getParameter("tipoApunte");	
    String strDateFin = request.getParameter("mesFin"); 
    String strDateIni = request.getParameter("mesFin");
    
    strDateFin= QueryCustomizer.yearMonthToFirstDayOfNextMonth(strDateFin);
    strDateIni= QueryCustomizer.yearMonthToLastDayOfPreviousMonth(strDateIni);
 
    String strQuery =
   	      " select" +
           "   fecha_oprcn_dif \"fecha\"" +
           "   ,num_sec_ac \"acuerdo\"" +
           "   ,sgn \"tipoApunte\"" +
           "   ,trim(concpt_apnte) \"conceptoApunte\"" +
           "   ,imp_apnte \"importe\"" +
           " from rdwc01.mi_do_apte_cta" +
           " where cod_nrbe_en='" + strEntidad + "'" +
           " and fecha_oprcn_dif > to_date('" + strDateIni + "','yyyy-mm-dd') " +             
           " and fecha_oprcn_dif < to_date('" + strDateFin + "','yyyy-mm-dd') " +              
           " and cod_cta = '01'" +
           " and num_sec_ac = " + strContrato;
     if(strTipoApunte != null)    
   	    strQuery = strQuery + " and trim(sgn) = '" + strTipoApunte + "'" ; 
     pLog.info("Query al customizador: " + strQuery);
    String strResponse = QueryCustomizer.process(request,strQuery);
    response.setHeader("content-type", "application/json");
%>
<%=strResponse %>
