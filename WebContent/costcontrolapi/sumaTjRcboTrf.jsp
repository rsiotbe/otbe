<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="
         com.rsi.rvia.rest.client.QueryCustomizer,
         java.util.Calendar
"
%>
<%
    String strContrato = request.getParameter("idContract");  
    String strEntidad = request.getParameter("codEntidad");
    String strDateIni = request.getParameter("mesInicio");      
    String strDateFin = request.getParameter("mesFin");    
    String strTipoApunte = request.getParameter("tipoApunte"); 
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
   				 "   when trim(COD_ORGN_APNTE) = 'TF' then 'TRANSFERENCIAS'" +
   				 "   when trim(CONCPT_APNTE) like 'TJ-%' then 'TARJETAS'" +
   				 "   when trim(CONCPT_APNTE) like 'TARJETA%' then 'TARJETAS'" +
   				 "   when trim(CONCPT_APNTE) like 'RCBO%' then 'RECIBOS'" +
   				 "   else 'OTROS'" +
   				 "  end \"categoria\"" +
   				 " FROM  rdwc01.MI_DO_APTE_CTA" +
   				 " where cod_nrbe_en='" + strEntidad + "'" ;
   			          
    if(strDateFin == null){
       strQuery = strQuery + " and fecha_oprcn_dif <= (select max(fecha_oprcn_dif) from rdwc01.mi_do_apte_cta where cod_nrbe_en='" + 
             strEntidad + "')";
    }
    else{
       if(strDateIni.equals(strDateFin)){
           strDateFin = strDateFin + "-01";
           String partes[] = strDateFin.split("-");
           Calendar dateDateFin = Calendar.getInstance();    
           dateDateFin.set(Integer.parseInt(partes[0]),Integer.parseInt(partes[1]),Integer.parseInt(partes[2]));
           dateDateFin.add(Calendar.MONTH, 1);
           strDateFin=dateDateFin.toString();
           strDateFin = dateDateFin.get(Calendar.YEAR) + "-" + dateDateFin.get(Calendar.MONTH) + "-" +dateDateFin.get(Calendar.DATE);
           strQuery = strQuery + " and fecha_oprcn_dif < round(to_date('" + strDateFin + "','yyyy-mm-dd'),'mm')";
       }
       else{
         strDateFin = strDateFin + "-21";
         strQuery = strQuery + " and fecha_oprcn_dif <= round(to_date('" + strDateFin + "','yyyy-mm-dd'),'mm')";
       }    
    }   
    strDateIni = strDateIni + "-01";         
    strQuery = strQuery + " and fecha_oprcn_dif >= round(to_date('" + strDateIni + "','yyyy-mm-dd'),'mm')";
   strQuery = strQuery + " AND NUM_SEC_AC = " + strContrato +
   				 " AND COD_CTA='01'" + 
   				 filtroTipoApunte +
   				 " group by to_char(fecha_oprcn_dif,'YYYY-MM'), case" +
   				 "   when trim(CONCPT_APNTE) like 'TRF.%' then 'TRANSFERENCIAS'" +
   				 "   when trim(COD_ORGN_APNTE) = 'TF' then 'TRANSFERENCIAS'" +
   				 "   when trim(CONCPT_APNTE) like 'TJ-%' then 'TARJETAS'" +
   				 "   when trim(CONCPT_APNTE) like 'TARJETA%' then 'TARJETAS'" +
   				 "   when trim(CONCPT_APNTE) like 'RCBO%' then 'RECIBOS'" +
   				 "   else 'OTROS'" +
   				 "  end, sgn" ;
              
  String strResponse = QueryCustomizer.process(request,strQuery);          
  response.setHeader("content-type", "application/json");
%>
<%=strResponse %>
