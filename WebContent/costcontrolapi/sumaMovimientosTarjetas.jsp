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
    String strIdInternoPe = request.getParameter("idInternoPe");
    String strEntidad = request.getParameter("codEntidad").toString();
    String strDateIni = request.getParameter("mesInicio").toString();
    String strDateFin = request.getParameter("mesFin");   
    String strCodCta = "";
    if(strDateFin == null){
        strDateFin = "9999-12-31";
     }
     else{
        strDateFin = QueryCustomizer.yearMonthToFirstDayOfNextMonth(strDateFin);
     }           
    strDateIni = QueryCustomizer.yearMonthToLastDayOfPreviousMonth(strDateIni);
   String strQuery =          
           " select /" + "*" + "+ FULL(e) *" + "/  to_char(e.fecha_oprcn,'YYYY-MM')  \"mes\", " +
                   " e.num_sec_ac \"acuerdo\", " + 
	               " sum (case " +
	               "       when e.tipfac2 = '0033' then (e.imptrn * -1) else e.imptrn " +
	               "   end) \"importe\", " +        
                   " count(*) \"numero\" " +
                   " from" +
                   "     rdwc01.MI_MPA2_OPERAC_TARJETAS e," +
                   "     rdwc01.mi_ac_cont_gen f" +
                   " WHERE e.cod_nrbe_en = f.cod_nrbe_en " +
                   " and e.COD_NRBE_EN = '" + strEntidad + "'" +
                   " and e.num_sec_ac = f.num_sec_ac" +
                   " and e.FECHA_OPRCN < to_date('" + strDateFin + "', 'yyyy-mm-dd')" +
                   " and e.FECHA_OPRCN > to_date('" + strDateIni + "', 'yyyy-mm-dd')" +
                   " and e.codrespu = '000' " +
                   " and e.indcruce = 1 " +
                   " and f.mi_fecha_fin = to_date('9999-12-31', 'yyyy-mm-dd') " ;
                   if(strContrato == null){
                       strQuery = strQuery +  " and f.id_interno_pe = " + strIdInternoPe;
                    }
                    else{
                       strQuery = strQuery + " and e.num_sec_ac =" + strContrato; 
                    }  
                   strQuery = strQuery + " group by to_char(e.fecha_oprcn,'YYYY-MM'), e.num_sec_ac";
   pLog.info("Query al customizador: " + strQuery);
   String strResponse = QueryCustomizer.process(request,strQuery);          
   response.setHeader("content-type", "application/json");
%><%=strResponse%>


