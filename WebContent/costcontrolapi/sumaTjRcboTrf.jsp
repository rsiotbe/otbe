<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="
        com.rsi.rvia.rest.endpoint.rsiapi.AcuerdosRuralvia,
         com.rsi.rvia.rest.client.QueryCustomizer,
         org.slf4j.Logger,
        org.slf4j.LoggerFactory 
"
%>
<%
String uri = request.getRequestURI();
String pageName = uri.substring(uri.lastIndexOf("/")+1);
Logger pLog  = LoggerFactory.getLogger(pageName);
String [] strRviaAcuerdos = AcuerdosRuralvia.getRviaContractsDecodeAliases(request);
    String strIdInternoPe = request.getParameter("idInternoPe");
    String strContrato = request.getParameter("idContract");
    String strEntidad = request.getParameter("codEntidad");
    String strDateIni = request.getParameter("mesInicio");
    String strDateFin = request.getParameter("mesFin");
    String strTipoApunte = request.getParameter("tipoApunte"); 
    String strExcluClops = " and trim(cod_origen) not in (" + AcuerdosRuralvia.getExcludedClops() + ")";
    String filtroTipoApunte = "";
    if(strTipoApunte != null){
   	 filtroTipoApunte = " and sgn='"+strTipoApunte+"'"; 
    }
    String strQuery =
   		 " SELECT" +
   		         "  to_char(fecha_oprcn_dif,'YYYY-MM')  \"mes\"," +
   				 "  sum(imp_apnte) \"importe\"," +
   				 "  count(*) \"numero\"," +
   				 "  sgn \"tipoApunte\"," +
   				 "  case" +
   				 "   when trim(CONCPT_APNTE) like 'TRF.%' then 'TRANSFERENCIAS'" +
   				 "   when trim(COD_ORGN_APNTE) in ('TF','TR') then 'TRANSFERENCIAS'" +
   				 "   when trim(CONCPT_APNTE) like 'TJ-%' then 'TARJETAS'" +
   				 "   when trim(CONCPT_APNTE) like 'TARJETA%' then 'TARJETAS'" +
   				 "   when trim(CONCPT_APNTE) like 'RCBO%' then 'RECIBOS'" +
   				 "   else 'OTROS'" +
   				 "  end \"categoria\"" +
   				 " FROM  rdwc01.MI_DO_APTE_CTA" +
   				 " where cod_nrbe_en='" + strEntidad + "'" + strExcluClops;
   			          
    if(strDateFin == null){
       strQuery = strQuery + " and fecha_oprcn_dif <= (select max(fecha_oprcn_dif) from rdwc01.mi_do_apte_cta where cod_nrbe_en='" + 
             strEntidad + "')";
    }
    else{
           strDateFin= QueryCustomizer.yearMonthToFirstDayOfNextMonth(strDateFin);  
           strQuery = strQuery + " and fecha_oprcn_dif <= " +             
                 " ( select max(tm.fecha_oprcn_dif) " +
                 " from rdwc01.mi_do_apte_cta tm where cod_nrbe_en='" + strEntidad + "' " +
                 " and tm.fecha_oprcn_dif < to_date('" + strDateFin + "','yyyy-mm-dd') " + 
                 " )";
    }   
    strDateIni = strDateIni + "-01";         
    strQuery = strQuery + " and fecha_oprcn_dif >= round(to_date('" + strDateIni + "','yyyy-mm-dd'),'mm')";
        
    if(strContrato == null){
        if(strRviaAcuerdos[1] == null){
	        strQuery = strQuery + " and num_sec_ac in (" +
	                " select t2.num_sec_ac from rdwc01.mi_ac_cont_gen t2" +
	                " where t2.cod_nrbe_en='" + strEntidad + "' " +
	                "    and t2.id_interno_pe=" + strIdInternoPe +
	                "    and t2.mi_fecha_fin=to_date('31.12.9999','dd.mm.yyyy') " + 
	          ")";
        }
        else{
            strQuery = strQuery + " and num_sec_ac in (" + strRviaAcuerdos[1] +  ")";   
        }
     }
     else{
        strQuery = strQuery + " and num_sec_ac =" + strContrato; 
     } 
    
   strQuery = strQuery + " and cod_cta = '01' and ind_accion <> '3' " + 
      	 filtroTipoApunte +
   				 " group by to_char(fecha_oprcn_dif,'YYYY-MM'), case" +
   				 "   when trim(CONCPT_APNTE) like 'TRF.%' then 'TRANSFERENCIAS'" +
   				 "   when trim(COD_ORGN_APNTE) in ('TF','TR') then 'TRANSFERENCIAS'" +
   				 "   when trim(CONCPT_APNTE) like 'TJ-%' then 'TARJETAS'" +
   				 "   when trim(CONCPT_APNTE) like 'TARJETA%' then 'TARJETAS'" +
   				 "   when trim(CONCPT_APNTE) like 'RCBO%' then 'RECIBOS'" +
   				 "   else 'OTROS'" +
   				 "  end, sgn" ;
  pLog.info("Query al customizador: " + strQuery);            
  String strResponse = QueryCustomizer.process(request,strQuery);          
  response.setHeader("content-type", "application/json");
%>
<%=strResponse %>
