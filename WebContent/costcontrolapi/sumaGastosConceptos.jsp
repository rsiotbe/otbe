<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="
         com.rsi.rvia.rest.client.QueryCustomizer
"
%>
<%
	String strContrato = request.getParameter("idContract");  
	String strEntidad = request.getParameter("codEntidad");
	String strDateFin = request.getParameter("mesFin");    
	strDateFin = strDateFin + "-01";  
    String strQuery =
          " select" +
          "    sgn  \"tipoApunte\"" +
          "   ,nvl(trim(concpt_apnte),'-') \"conceptoApunte\"" +
          "   ,sum(imp_apnte) \"importe\"" +
          " from rdwc01.mi_do_apte_cta t1" +
          " where cod_nrbe_en='" + strEntidad + "'" +
          " and num_sec_ac =" + strContrato +
          " and fecha_oprcn_dif = round(to_date('" + strDateFin + "','yyyy-mm-dd'),'mm')" +
          " and ind_accion <> 3" +
          " and ind_2 in ('S','N','O')" +
          " and cod_numrco_moneda = '978'" +          
          " group by concpt_apnte, sgn  " ;
              
  String strResponse = QueryCustomizer.process(request,strQuery);          
  response.setHeader("content-type", "application/json");
%>
<%=strResponse %>
