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
	String strDateFin = request.getParameter("mesFin");    
	String strDateIni = request.getParameter("mesInicio"); 
	String strExcluClops = " and trim(t1.cod_origen) not in (";
    if(strContrato != null){
        strExcluClops = strExcluClops + AcuerdosRuralvia.getExcludedClops() + ")";
    }
    else{
        strExcluClops = strExcluClops + AcuerdosRuralvia.getExcludedClops() + AcuerdosRuralvia.getExcludedClopsAlDebe() + AcuerdosRuralvia.getExcludedClopsAlHaber() + ")";
    }	
    String strQuery =
            " select " +
            "   z1.a \"conceptoApunte\", " +
            "   case when z1.b > 0 then 'H' else 'D' end  \"tipoApunte\", " +
            "   sum(abs(z1.b)) \"importe\" " +
            " from ( " +
            " select " +
            " nvl(trim(t1.concpt_apnte), trim(t3.txt_tipo_clop_brev)) a, " +
            " sum(imp_apnte * decode(sgn,'H',1,'D',-1)) b " +
            " from rdwc01.mi_do_apte_cta t1 " +
            " left outer join proc01.tp_clop t3 " +
            " on trim(t1.cod_origen) = trim(t3.cod_clop_sist||t3.tipo_sbclop) " +
          " where t1.cod_nrbe_en='" + strEntidad + "'";
          
            if(strContrato == null){
               if(strRviaAcuerdos[1] == null){
                   strQuery = strQuery + " and t1.num_sec_ac in (" +
                           " select t2.num_sec_ac from rdwc01.mi_ac_cont_gen t2" +
                           " where t2.cod_nrbe_en='" + strEntidad + "' " +
                           "    and t2.id_interno_pe=" + strIdInternoPe +
                           "    and t2.mi_fecha_fin=to_date('31.12.9999','dd.mm.yyyy') " + 
                     ")";
               }
               else{
                   strQuery = strQuery + " and t1.num_sec_ac in (" + strRviaAcuerdos[1] +  ")";
               }
            }
            else{
               strQuery = strQuery + " and t1.num_sec_ac =" + strContrato; 
            }           
          if(strDateFin == null){
             strDateFin =  AcuerdosRuralvia.getLastProcessDateMasUno("MI_DO_APTE_CTA");
          }
          else{
             strDateFin= QueryCustomizer.yearMonthToFirstDayOfNextMonth(strDateFin);
          } 
          strQuery = strQuery + " and t1.fecha_oprcn < to_date('" + strDateFin + "','yyyy-mm-dd') ";
          
          strDateIni = QueryCustomizer.yearMonthToLastDayOfPreviousMonth(strDateIni);
          strQuery = strQuery + " and t1.fecha_oprcn > to_date('" + strDateIni + "','yyyy-mm-dd') ";
          strQuery = strQuery + " and cod_cta = '01'  and ind_accion <> '3' " +  strExcluClops +
            " group by " +
            " nvl(trim(t1.concpt_apnte), trim(t3.txt_tipo_clop_brev)) " +
            " ,hora_oprcn " +
            " ) z1 " +
            " where abs(z1.b) > 0 " +
            " group by z1.a, " +
            "   case when z1.b > 0 then 'H' else 'D' end " ;

  pLog.info("Query al customizador: " + strQuery);            
  String strResponse = QueryCustomizer.process(request,strQuery);         
  response.setHeader("content-type", "application/json");
%>
<%=strResponse %>
