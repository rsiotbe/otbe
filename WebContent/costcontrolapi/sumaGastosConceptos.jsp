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
    String strIdInternoPe = request.getParameter("idInternoPe");
	String strContrato = request.getParameter("idContract");  
	String strEntidad = request.getParameter("codEntidad");
	String strDateFin = request.getParameter("mesFin");    
	String strDateIni = request.getParameter("mesInicio"); 
    String strQuery =
          " select" +
          "    t1.sgn  \"tipoApunte\"" +
          "   ,nvl(trim(t1.concpt_apnte), trim(t3.txt_tipo_clop_brev)) \"conceptoApunte\"" +
          "   ,sum(t1.imp_apnte) \"importe\"" +
          " from rdwc01.mi_do_apte_cta t1" +
          " left outer join proc01.tp_clop t3" +
          " on trim(t1.cod_origen) = trim(t3.cod_clop_sist||t3.tipo_sbclop) " +
          " where t1.cod_nrbe_en='" + strEntidad + "'";
          
		    if(strContrato == null){
		       //strQuery = strQuery + whereLineaEq; 
		       strQuery = strQuery + " and t1.num_sec_ac in (" +
		               " select t2.num_sec_ac from rdwc01.mi_ac_cont_gen t2" +
		               " where t2.cod_nrbe_en='" + strEntidad + "' " +
		               "    and t2.id_interno_pe=" + strIdInternoPe +
		               "    and t2.mi_fecha_fin=to_date('31.12.9999','dd.mm.yyyy') " + 
		         ")";
		    }
		    else{
		       strQuery = strQuery + " and t1.num_sec_ac =" + strContrato; 
		    } 
		    
          if(strDateFin == null){
             strQuery = strQuery + " and t1.fecha_oprcn_dif <= (select max(t9.fecha_oprcn_dif) from rdwc01.mi_do_apte_cta t9 where t9.cod_nrbe_en='" + 
                   strEntidad + "')";
          }
          else{
             strDateFin= QueryCustomizer.yearMonthToFirstDayOfNextMonth(strDateFin);
             strQuery = strQuery + " and t1.fecha_oprcn_dif <= " +             
	             " ( select max(tm.fecha_oprcn_dif) " +
	             " from rdwc01.mi_do_apte_cta tm where cod_nrbe_en='" + strEntidad + "' " +
	             " and tm.fecha_oprcn_dif < to_date('" + strDateFin + "','yyyy-mm-dd') " + 
	             " )";
          }   
          strDateIni = strDateIni + "-01";         
          strQuery = strQuery + " and t1.fecha_oprcn_dif >= round(to_date('" + strDateIni + "','yyyy-mm-dd'),'mm')";   
          strQuery = strQuery + " and cod_cta = '01'" +       
          " group by nvl(trim(t1.concpt_apnte),trim(t3.txt_tipo_clop_brev)), t1.sgn  " ;
  pLog.info("Query al customizador: " + strQuery);            
  String strResponse = QueryCustomizer.process(request,strQuery);          
  response.setHeader("content-type", "application/json");
%>
<%=strResponse %>
