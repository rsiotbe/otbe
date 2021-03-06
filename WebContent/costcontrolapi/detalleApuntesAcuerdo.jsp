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
    String strLinea = request.getParameter("codLinea");
    String strEntidad = request.getParameter("codEntidad");
    String strTipoApunte = request.getParameter("tipoApunte");  
    String strCategoria = request.getParameter("categoria"); 
    String strConceptoApunte = request.getParameter("concepto");
    String strDateFin = request.getParameter("mesFin");
    String strDateIni = request.getParameter("mesFin"); 
    String strExcluClops = " and trim(t1.cod_origen) not in (";
    if(strContrato != null){
        strExcluClops = strExcluClops + AcuerdosRuralvia.getExcludedClops() + ")";
    }
    else{
        strExcluClops = strExcluClops + AcuerdosRuralvia.getExcludedClops() + AcuerdosRuralvia.getExcludedClopsAlDebe() + AcuerdosRuralvia.getExcludedClopsAlHaber() + ")";
    }    
    
    
    strDateFin= QueryCustomizer.yearMonthToFirstDayOfNextMonth(strDateFin);
    strDateIni= QueryCustomizer.yearMonthToLastDayOfPreviousMonth(strDateIni); 
    String strQuery =
            " select " +
            "   z1.a \"fecha\", " +
            "   z1.c \"acuerdo\", " +
            "   case when z1.e > 0 then 'H' else 'D' end  \"tipoApunte\", " +
            "   z1.f \"categoria\", " +
            "   z1.g \"conceptoApunte\", " +
            "   abs(z1.e) \"importe\" " +
            " from ( " +
            "   select " +
            "       t1.fecha_oprcn   a, " +
            "       max(t1.num_sec_ac)  c, " +
            "       sum(t1.imp_apnte * decode(t1.sgn,'H',1,'D',-1)) e, " +
            "       case " +
            "           when trim(t1.CONCPT_APNTE) like 'TRF.%' then 'TRANSFERENCIAS' " +
            "           when trim(t1.COD_ORGN_APNTE) in ('TF','TR') then 'TRANSFERENCIAS' " +
            "           when trim(t1.CONCPT_APNTE) like 'TJ-%' then 'TARJETAS' " +
            "           when trim(t1.CONCPT_APNTE) like 'TARJETA%' then 'TARJETAS' " +
            "           when trim(t1.CONCPT_APNTE) like 'RCBO%' then 'RECIBOS' else 'OTROS' " +
            "       end f, " +
            "       nvl(trim(t1.concpt_apnte), trim(h3.txt_tipo_clop_brev)) g, " +
            "       sum(t1.imp_apnte) h " +
            "   from rdwc01.mi_do_apte_cta t1 left outer join proc01.tp_clop h3 " +
            "       on trim(t1.cod_origen) = trim(h3.cod_clop_sist||h3.tipo_sbclop) " +
            " where t1.cod_nrbe_en='" + strEntidad + "'" +
            " and t1.fecha_oprcn > to_date('" + strDateIni + "','yyyy-mm-dd') " +             
            " and t1.fecha_oprcn < to_date('" + strDateFin + "','yyyy-mm-dd') " +              
            " and t1.cod_cta = '01'" + strExcluClops +            
            " and t1.ind_accion <> '3' ";
          if(strContrato == null){
              if(strRviaAcuerdos[1]==null){
                  strQuery = strQuery + " and t1.num_sec_ac in (" +
                          " select t2.num_sec_ac from rdwc01.mi_ac_cont_gen t2" +
                          " where t2.cod_nrbe_en='" + strEntidad + "' " +
                          "    and t2.id_interno_pe=" + strIdInternoPe +
                          "    and t2.mi_fecha_fin=to_date('31.12.9999','dd.mm.yyyy') " + 
                    ")";
              }
              else{
                  strQuery = strQuery + " and t1.num_sec_ac in (" + strRviaAcuerdos[1] +  ") ";
              }
           }
           else{
              strQuery = strQuery + " and t1.num_sec_ac =" + strContrato; 
           }                       
           if (strCategoria != null){
               strQuery = strQuery + "   and (case" +
                   "     when trim(t1.CONCPT_APNTE) like 'TRF.%' then 'TRANSFERENCIAS'" +
                   "     when trim(t1.COD_ORGN_APNTE) in ('TF','TR') then 'TRANSFERENCIAS'" +
                   "     when trim(t1.CONCPT_APNTE) like 'TJ-%' then 'TARJETAS'" +
                   "     when trim(t1.CONCPT_APNTE) like 'TARJETA%' then 'TARJETAS'" +
                   "     when trim(t1.CONCPT_APNTE) like 'RCBO%' then 'RECIBOS'" +
                   "     else 'OTROS'" +
                   "    end) = '" + strCategoria + "'";      
               
           }
           else if (strConceptoApunte != null){
               strQuery = strQuery + " and nvl(trim(t1.concpt_apnte),trim(h3.txt_tipo_clop_brev)) = '" + strConceptoApunte + "' ";
           }
           if(strTipoApunte != null)    
              strQuery = strQuery + " and trim(t1.sgn) = '" + strTipoApunte + "'" ; 
          
        strQuery = strQuery + "     group by " +
            "       t1.fecha_oprcn, " +
            "       t1.hora_oprcn, " +
            "       case " +
            "           when trim(t1.CONCPT_APNTE) like 'TRF.%' then 'TRANSFERENCIAS' " +
            "           when trim(t1.COD_ORGN_APNTE) in ('TF','TR') then 'TRANSFERENCIAS' " +
            "           when trim(t1.CONCPT_APNTE) like 'TJ-%' then 'TARJETAS' " +
            "           when trim(t1.CONCPT_APNTE) like 'TARJETA%' then 'TARJETAS' " +
            "           when trim(t1.CONCPT_APNTE) like 'RCBO%' then 'RECIBOS'       else 'OTROS' " +
            "       end, " +
            "       nvl(trim(t1.concpt_apnte), trim(h3.txt_tipo_clop_brev)) " +
            " ) z1 " +
            " where abs(z1.e) > 0 ";    
     pLog.info("Query al customizador: " + strQuery);
     String strResponse = QueryCustomizer.process(request,strQuery);
     response.setHeader("content-type", "application/json");
%>
<%=strResponse %>
