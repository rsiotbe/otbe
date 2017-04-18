<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="
         com.rsi.rvia.rest.client.QueryCustomizer,
         java.util.Calendar,
         org.slf4j.Logger,
         org.slf4j.LoggerFactory 
"
%>
<%
String uri = request.getRequestURI();
String pageName = uri.substring(uri.lastIndexOf("/")+1);
Logger pLog  = LoggerFactory.getLogger(pageName);
    String strContrato = request.getParameter("idContract");  
    String strIdInternoPe = request.getParameter("idInternoPe");
    String strEntidad = request.getParameter("codEntidad").toString();
    String strDateIni = request.getParameter("mesInicio").toString();
    String strDateFin = request.getParameter("mesFin");   

    String strLinea = request.getParameter("codLinea");
    String whereLineaEq="";
    if(strLinea != null){
        whereLineaEq =  " and cod_linea ='" + strLinea + "'"; 
    }

    String strResponse = "{}";
    String strQuery =
           " select" +
           "    to_char(fecha_oprcn_dif,'YYYY-MM')  \"mes\"" +
           "   ,sgn  \"tipoApunte\"" +
           //"   ,cod_linea \"codLinea\"" +
           "   ,sum(imp_apnte) \"importe\" ";           
    strQuery = strQuery + " from rdwc01.mi_do_apte_cta t1" +
           " where cod_nrbe_en='" + strEntidad + "'" ;
           
   if(strDateFin == null){
      strQuery = strQuery + " and fecha_oprcn_dif <= (select max(fecha_oprcn_dif) from rdwc01.mi_do_apte_cta where cod_nrbe_en='" + 
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
   strQuery = strQuery + " and fecha_oprcn_dif >= round(to_date('" + strDateIni + "','yyyy-mm-dd'),'mm')";   
   strQuery = strQuery + " and cod_cta = '01' and ind_accion <> '3' ";
  
   if(strContrato == null){
      //strQuery = strQuery + whereLineaEq; 
      strQuery = strQuery + " and num_sec_ac in (" +
              " select num_sec_ac from rdwc01.mi_ac_cont_gen " +
              " where cod_nrbe_en='" + strEntidad + "' " +
              "    and id_interno_pe=" + strIdInternoPe +
              "    and mi_fecha_fin=to_date('31.12.9999','dd.mm.yyyy') " + whereLineaEq +
        ")";
   }
   else{
      strQuery = strQuery + " and num_sec_ac =" + strContrato; 
   }      
   strQuery = strQuery + " group by to_char(fecha_oprcn_dif,'YYYY-MM'), "+
         " cod_linea, " + 
         "sgn  " ;
   pLog.info("Query al customizador: " + strQuery);
   strResponse= QueryCustomizer.process(request,strQuery);          
   response.setHeader("content-type", "application/json");
%><%=strResponse%>
