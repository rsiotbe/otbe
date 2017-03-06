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
    String strLinea = request.getParameter("codLinea");
    String strEntidad = request.getParameter("codEntidad");
    String strTipoApunte = request.getParameter("tipoApunte");  
    String strCategoria = request.getParameter("categoria"); 
    String strConceptoApunte = request.getParameter("concepto");
    String strDateFin = request.getParameter("mesFin"); 
    String strDateIni = request.getParameter("mesFin");
    
    strDateFin= QueryCustomizer.yearMonthToFirstDayOfNextMonth(strDateFin);
    strDateIni= QueryCustomizer.yearMonthToLastDayOfPreviousMonth(strDateIni); 
    String strQuery =
          " select" +
           "   fecha_oprcn_dif \"fecha\"" +
           "   ,num_sec_ac \"acuerdo\"" +
           "   ,sgn \"tipoApunte\"" +          
           "   ,case" +
           "     when trim(CONCPT_APNTE) like 'TRF.%' then 'TRANSFERENCIAS'" +
           "     when trim(COD_ORGN_APNTE) in ('TF','TR') then 'TRANSFERENCIAS'" +
           "     when trim(CONCPT_APNTE) like 'TJ-%' then 'TARJETAS'" +
           "     when trim(CONCPT_APNTE) like 'TARJETA%' then 'TARJETAS'" +
           "     when trim(CONCPT_APNTE) like 'RCBO%' then 'RECIBOS'" +
           "     else 'OTROS'" +
           "    end \"categoria\"" +                           
           "   ,trim(concpt_apnte) \"conceptoApunte\"" +
           "   ,imp_apnte \"importe\"" +           
           " from rdwc01.mi_do_apte_cta" +
           " where cod_nrbe_en='" + strEntidad + "'" +
           " and fecha_oprcn_dif > to_date('" + strDateIni + "','yyyy-mm-dd') " +             
           " and fecha_oprcn_dif < to_date('" + strDateFin + "','yyyy-mm-dd') " +              
           " and cod_cta = '01'";
           
    if(strContrato == null){
        strQuery = strQuery + " and num_sec_ac in (" +
                " select t2.num_sec_ac from rdwc01.mi_ac_cont_gen t2" +
                " where t2.cod_nrbe_en='" + strEntidad + "' " +
                "    and t2.id_interno_pe=" + strIdInternoPe +
                "    and t2.mi_fecha_fin=to_date('31.12.9999','dd.mm.yyyy') " + 
          ")";
     }
     else{
        strQuery = strQuery + " and num_sec_ac =" + strContrato; 
     }                       
     if (strCategoria != null){
         strQuery = strQuery + "   and (case" +
             "     when trim(CONCPT_APNTE) like 'TRF.%' then 'TRANSFERENCIAS'" +
             "     when trim(COD_ORGN_APNTE) in ('TF','TR') then 'TRANSFERENCIAS'" +
             "     when trim(CONCPT_APNTE) like 'TJ-%' then 'TARJETAS'" +
             "     when trim(CONCPT_APNTE) like 'TARJETA%' then 'TARJETAS'" +
             "     when trim(CONCPT_APNTE) like 'RCBO%' then 'RECIBOS'" +
             "     else 'OTROS'" +
             "    end) = '" + strCategoria + "'";      
         
     }
     else if (strConceptoApunte != null){
         strQuery = strQuery + " and trim(concpt_apnte) = '" + strConceptoApunte + "' ";
     }
           
     if(strTipoApunte != null)    
        strQuery = strQuery + " and trim(sgn) = '" + strTipoApunte + "'" ; 
     pLog.info("Query al customizador: " + strQuery);
    String strResponse = QueryCustomizer.process(request,strQuery);
    response.setHeader("content-type", "application/json");
%>
<%=strResponse %>
