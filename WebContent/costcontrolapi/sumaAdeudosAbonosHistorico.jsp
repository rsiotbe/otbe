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
String [] strRviaAcuerdos = AcuerdosRuralvia.getRviaContractsDecodeAliases(request.getParameter("codTarjeta"));
    String strContrato = request.getParameter("idContract");  
    String strIdInternoPe = request.getParameter("idInternoPe");
    String strEntidad = request.getParameter("codEntidad").toString();
    String strDateIni = request.getParameter("mesInicio").toString();
    String strDateFin = request.getParameter("mesFin");   
    String strCodCta = "";
    
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
           " select" +
           "    to_char(fecha_oprcn_dif,'YYYY-MM')  \"mes\"" +
           "   ,sgn  \"tipoApunte\"" +
           "   ,sum(imp_apnte) \"importe\"" ;
/*
        " case " + 
        "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0311','0321')  then 'PASIVO A LA VISTA'" + 
        "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0351','0352')  then 'DEPÓSITOS'" + 
        "  when trim(COD_LINEA)||trim(ID_GRP_PD) =  '0171'  then 'PRÉSTAMOS'" + 
        "  when trim(COD_LINEA)||trim(ID_GRP_PD) =  '0151'  then 'TARJETAS CRÉDITO'" + 
        "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0151','0551')  then 'TARJETAS CRÉDITO Y DÉBITO'" + 
        " end \"nombreClasificacion\", " + 
        " case " + 
        "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0311','0321')  then '1'" + 
        "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0351','0352')  then '2'" + 
        "  when trim(COD_LINEA)||trim(ID_GRP_PD) =  '0171'  then '3'" + 
        "  when trim(COD_LINEA)||trim(ID_GRP_PD) =  '0151'  then '4'" + 
        "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0151','0551')  then '5'" + 
        " end \"codClasificacion\" ";
*/           
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
   strQuery = strQuery + " and fecha_oprcn_dif >= to_date('" + strDateIni + "','yyyy-mm-dd')";
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
   strQuery = strQuery + " group by to_char(fecha_oprcn_dif,'YYYY-MM'), "+
   /*
         " case " + 
         "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0311','0321')  then 'PASIVO A LA VISTA'" + 
         "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0351','0352')  then 'DEPÓSITOS'" + 
         "  when trim(COD_LINEA)||trim(ID_GRP_PD) =  '0171'  then 'PRÉSTAMOS'" + 
         "  when trim(COD_LINEA)||trim(ID_GRP_PD) =  '0151'  then 'TARJETAS CRÉDITO'" + 
         "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0151','0551')  then 'TARJETAS CRÉDITO Y DÉBITO'" + 
         " end, " + 
         " case " + 
         "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0311','0321')  then '1'" + 
         "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0351','0352')  then '2'" + 
         "  when trim(COD_LINEA)||trim(ID_GRP_PD) =  '0171'  then '3'" + 
         "  when trim(COD_LINEA)||trim(ID_GRP_PD) =  '0151'  then '4'" + 
         "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0151','0551')  then '5'" + 
         " end,  " + 
    */     
   		 "sgn  " ;
   pLog.info("Query al customizador: " + strQuery);
   strResponse= QueryCustomizer.process(request,strQuery);          
   response.setHeader("content-type", "application/json");
%><%=strResponse%>
