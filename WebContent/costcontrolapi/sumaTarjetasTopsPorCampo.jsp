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
    String strConceptoAgregacion = request.getParameter("concepto");     
    String strGroupBy = "";
    String strFieldDsiplay = "";
    if(strConceptoAgregacion != null){
        if("1".equals(strConceptoAgregacion)){
            strGroupBy = " group by nvl(trim(h.descripcion),'Otros')";
            strFieldDsiplay = " nvl(trim(h.descripcion),'Otros') \"concepto\", ";
        }
        else if("2".equals(strConceptoAgregacion)){
            strGroupBy = " group by nvl(trim(e.localidad2),'OTROS')";
            strFieldDsiplay = " nvl(trim(e.localidad2),'OTROS') \"concepto\", ";
        }
        else if("3".equals(strConceptoAgregacion)){
            strGroupBy = " group by nvl(trim(e.nomcomred),'OTROS')";
            strFieldDsiplay = " nvl(trim(e.nomcomred),'OTROS') \"concepto\", ";
        }          
    }
    
    if(strDateFin == null){
        strDateFin = "9999-12-31";
     }
     else{
        strDateFin = QueryCustomizer.yearMonthToFirstDayOfNextMonth(strDateFin);
     }           
    strDateIni = QueryCustomizer.yearMonthToLastDayOfPreviousMonth(strDateIni);

   String strQuery =          
           " select /" + "*" + "+ FULL(e) *" + "/  " +
                   strFieldDsiplay + 
                   
                   "   sum(case " +
                   "       when e.tipfac2 = '0033' then (e.imptrn * -1) else e.imptrn " +
                   "   end) \"importe\", " +
                   "   avg(case " +
                   "       when e.tipfac2 = '0033' then (e.imptrn * -1) else e.imptrn " +
                   "   end) \"media\", " +                           
                   " count(*) \"numero\" " +
                   " from " +
                   "   rdwc01.MI_MPA2_OPERAC_TARJETAS e " +
                   " left join rdwc01.mi_ac_cont_gen f " +
                   "   on e.cod_nrbe_en = f.cod_nrbe_en " +
                   "   and e.num_sec_ac = f.num_sec_ac " +
                   "   and f.mi_fecha_fin = to_date('9999-12-31','yyyy-mm-dd') " +
                   " left outer join proc01.tp_codact_tasas h " +
                   "   on e.codact = h.codacti " +
                   "   and e.cod_nrbe_en = h.cod_nrbe_en " +
                   " WHERE e.COD_NRBE_EN = '" + strEntidad + "'" +
                   " and e.codrespu = '000' " +
                   " and e.indcruce = 1 " +
                   " and e.FECHA_OPRCN < to_date('" + strDateFin + "', 'yyyy-mm-dd')" +
                   " and e.FECHA_OPRCN > to_date('" + strDateIni + "', 'yyyy-mm-dd')" +
                   " and f.mi_fecha_fin = to_date('9999-12-31', 'yyyy-mm-dd') " ;
                   if(strContrato == null){
                       strQuery = strQuery +  " and f.id_interno_pe = " + strIdInternoPe;
                    }
                    else{
                       strQuery = strQuery + " and e.num_sec_ac =" + strContrato; 
                    }  
                   strQuery = strQuery + strGroupBy;
   pLog.info("Query al customizador: " + strQuery);
   String strResponse = QueryCustomizer.process(request,strQuery);          
   response.setHeader("content-type", "application/json");
%><%=strResponse%>


