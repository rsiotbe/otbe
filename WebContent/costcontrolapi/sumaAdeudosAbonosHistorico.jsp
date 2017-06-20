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
    String strContrato = request.getParameter("idContract");  
    String strIdInternoPe = request.getParameter("idInternoPe");
    String strEntidad = request.getParameter("codEntidad").toString();
    String strDateIni = request.getParameter("mesInicio").toString();
    String strDateFin = request.getParameter("mesFin");   
    String strCodCta = "";
    
    String strExcluClops = " and trim(t1.cod_origen) not in (";
    if(strContrato != null){
        strExcluClops = strExcluClops + AcuerdosRuralvia.getExcludedClops() + ")";
    }
    else{
        strExcluClops = strExcluClops + AcuerdosRuralvia.getExcludedClops() + AcuerdosRuralvia.getExcludedClopsAlDebe() + AcuerdosRuralvia.getExcludedClopsAlHaber() + ")";
    }
    /*
    // TODO: Parece que el código de clasificación no aplica para cod_cta <> 1.
    String strCodClasificacion = request.getParameter("codClasificacion");
    String whereLineaEq="";
    if(strCodClasificacion != null){
      switch(Integer.parseInt(strCodClasificacion)){
          case 1:             
              whereLineaEq=" AND trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0311','0321')";         
              break;
          case 2:
              whereLineaEq=" AND trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0351','0352')";
              break;              
          case 3:
              whereLineaEq=" AND trim(COD_LINEA)||trim(ID_GRP_PD) = '0171'";
              break;
          case 4:
              whereLineaEq=" AND trim(COD_LINEA)||trim(ID_GRP_PD) = '0151'";
              break;
          case 5:
              whereLineaEq=" AND trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0551','0151')";
              break;
      }
  }
  */  
    String strResponse = "{}";
    String strQuery =
            " select " + 
            "   z1.a \"mes\", " + 
            "   case when z1.b > 0 then 'H' else 'D' end  \"tipoApunte\", " + 
            "   sum(abs(z1.b)) \"importe\" " + 
            " from ( " + 
            "           select " + 
            "               to_char(fecha_oprcn,'YYYY-MM') a, " + 
            "               sum(imp_apnte * decode(sgn,'H',1,'D',-1)) b " + 
            "           from rdwc01.mi_do_apte_cta t1 " + 
            " where cod_nrbe_en='" + strEntidad + "'" ;

                     if(strDateFin == null){
                        strDateFin =  AcuerdosRuralvia.getLastProcessDateMasUno("MI_DO_APTE_CTA");
                     }
                     else{
                        strDateFin= QueryCustomizer.yearMonthToFirstDayOfNextMonth(strDateFin);
                     } 
                     strQuery = strQuery + " and t1.fecha_oprcn < to_date('" + strDateFin + "','yyyy-mm-dd') ";
                 
                   strDateIni = QueryCustomizer.yearMonthToLastDayOfPreviousMonth(strDateIni);
                   strQuery = strQuery + " and fecha_oprcn > to_date('" + strDateIni + "','yyyy-mm-dd') ";      
                   String strRestrictorApuntes = " and cod_cta = '01' and ind_accion <> '3' ";
                   
                   strQuery = strQuery + strRestrictorApuntes;
                   if(strContrato == null){
                      if(strRviaAcuerdos[1]==null){ 
                          strQuery = strQuery + " and num_sec_ac in (" +
                                  " select num_sec_ac from rdwc01.mi_ac_cont_gen " +
                                  " where cod_nrbe_en='" + strEntidad + "' " +
                                  "    and id_interno_pe=" + strIdInternoPe +
                                  "    and mi_fecha_fin=to_date('31.12.9999','dd.mm.yyyy') " + 
                            ")";
                      }
                      else{
                         strQuery = strQuery + " and t1.num_sec_ac in (" + strRviaAcuerdos[1] +  ")";
                      }
                      
                   }
                   else{
                      strQuery = strQuery + " and num_sec_ac =" + strContrato; 
                   }         
                   strQuery = strQuery +  strExcluClops;
                   
        strQuery = strQuery + " group by " + 
            " to_char(fecha_oprcn,'YYYY-MM') " + 
            " ,hora_oprcn " + 
            " ) z1 " + 
            " where abs(z1.b) > 0 " + 
            " group by z1.a, " + 
            "   case when z1.b > 0 then 'H' else 'D' end " ;
   pLog.info("Query al customizador: " + strQuery);
   strResponse= QueryCustomizer.process(request,strQuery);          
   response.setHeader("content-type", "application/json");
%><%=strResponse%>
