<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="
com.rsi.rvia.rest.client.QueryCustomizer            
"
%>
<%
	String strContrato = request.getParameter("idContract");  
	String strLinea = request.getParameter("codLinea");
	//String strIdInternoPe = request.getParameter("idInternoPe");
	String strEntidad = request.getParameter("codEntidad");
	String strTipoApunte = request.getParameter("tipoApunte");	
    String strDateFin = request.getParameter("mesFin");    
    strDateFin = strDateFin + "-01";
    String strQuery =
   	      " select" +
           "   fecha_oprcn_dif \"fecha\"" +
           "   ,num_sec_ac \"acuerdo\"" +
           "   ,sgn \"tipoApunte\"" +
           "   ,trim(concpt_apnte) \"conceptoApunte\"" +
           "   ,imp_apnte \"importe\"" +
           " from rdwc01.mi_do_apte_cta" +
           " where cod_nrbe_en='" + strEntidad + "'"  +
           " and fecha_oprcn_dif = round(to_date('" + strDateFin + "','yyyy-mm-dd'),'mm')" +
           " and cod_cta = '01'" +
           " and num_sec_ac = " + strContrato;
     if(strTipoApunte != null)    
   	    strQuery = strQuery + " and trim(sgn) = '" + strTipoApunte + "'" ;      
    String strResponse = QueryCustomizer.process(request,strQuery);
    response.setHeader("content-type", "application/json");
%>
<%=strResponse %>
