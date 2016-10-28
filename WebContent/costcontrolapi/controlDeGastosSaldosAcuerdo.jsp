<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
 	import="
		 com.rsi.rvia.rest.client.QueryCustomizer
"
%>
<%
	String strContrato = request.getParameter("idContract");
    String strIdInternoPe = request.getParameter("idInternoPe");
	String strEntidad = request.getParameter("codEntidad");
	String strDateIni = request.getParameter("mesInicio");
	String strDateFin = request.getParameter("mesFin");

   strDateIni = strDateIni + "-25";
   if(strDateFin != null){
   	   strDateFin= QueryCustomizer.yearMonthToFirstDayOfNextMonth(strDateFin);
   }
   
   String strCodClasificacion = request.getParameter("codClasificacion");
   String whereLineaEq="";
   String strGroupClassif="";
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
   else{
   	whereLineaEq=" AND trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0311','0321','0351','0352','0171','0551','0151')";
   	strGroupClassif = ", " +
   	            " case " + 
   	            "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0311','0321')  then '1'" + 
   	            "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0351','0352')  then '2'" + 
   	            "  when trim(COD_LINEA)||trim(ID_GRP_PD) =  '0171'  then '3'" + 
   	            "  when trim(COD_LINEA)||trim(ID_GRP_PD) =  '0151'  then '4'" + 
   	            "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0151','0551')  then '5'" + 
   	            " end " ;   			
   }
 		
	if(strDateFin == null){
		strDateFin = "9999-12-31";
	}

	String strResponse = "{}";
	String strQuery =
			" select" +
			" 	mi_fecha_fin_mes \"finMes\"," +
			"   mi_fec_ult_mov \"fechaUltimaModificacion\"," +
			        " case " + 
			        "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0311','0321')  then '1'" + 
			        "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0351','0352')  then '2'" + 
			        "  when trim(COD_LINEA)||trim(ID_GRP_PD) =  '0171'  then '3'" + 
			        "  when trim(COD_LINEA)||trim(ID_GRP_PD) =  '0151'  then '4'" + 
			        "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0151','0551')  then '5'" + 
			        " end \"codClasificacion\", " + 	
	                    " max(case " + 
	                    "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0311','0321')  then 'PASIVO A LA VISTA'" + 
	                    "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0351','0352')  then 'DEPÓSITOS'" + 
	                    "  when trim(COD_LINEA)||trim(ID_GRP_PD) =  '0171'  then 'PRÉSTAMOS'" + 
	                    "  when trim(COD_LINEA)||trim(ID_GRP_PD) =  '0151'  then 'TARJETAS CRÉDITO'" + 
	                    "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0151','0551')  then 'TARJETAS CRÉDITO Y DÉBITO'" + 
	                    " end) \"nombreClasificacion\", " + 			        
			" 	sum(mi_sdo_ac_p) \"saldoPuntual\"," +
			" 	sum(mi_sdo_dispble_p) \"saldoDisponible\"," +
			" 	sum(mi_sdo_acr_p) \"saldoAcreedor\"," +
			" 	sum(mi_sdo_deu_p) \"saldoDeudor\"" +
			" from rdwc01.mi_ac_eco_gen" +
			" where cod_nrbe_en='" + request.getParameter("codEntidad") + "'" ;
			
	if(strContrato != null){
		strQuery = strQuery + " and num_sec_ac =" + request.getParameter("idContract") ; 
	}
	else{
	    strQuery = strQuery + " and num_sec_ac in (" +
             " select num_sec_ac from rdwc01.mi_ac_cont_gen " +
             " where cod_nrbe_en='" + strEntidad + "' " +
             "    and id_interno_pe=" + strIdInternoPe +
             "    and mi_fecha_fin=to_date('31.12.9999','dd.mm.yyyy') " + whereLineaEq +
       ")";
	}
			
	strQuery = strQuery + " and mi_fecha_fin_mes > to_date('" + strDateIni + "','yyyy-mm-dd')" +
			" and mi_fecha_fin_mes <= to_date('" + strDateFin + "','yyyy-mm-dd')" + 
			" group by mi_fecha_fin_mes, mi_fec_ult_mov, " +					
                   " case " + 
                   "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0311','0321')  then '1'" + 
                   "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0351','0352')  then '2'" + 
                   "  when trim(COD_LINEA)||trim(ID_GRP_PD) =  '0171'  then '3'" + 
                   "  when trim(COD_LINEA)||trim(ID_GRP_PD) =  '0151'  then '4'" + 
                   "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0151','0551')  then '5'" + 
                   " end " ; 			

	
	
	
	
	
	
	
	
	
/*	
	
	strQuery = strQuery + " union " +
         " select" +
         "   to_date('2022-12-31','yyyy-mm-dd') \"finMes\"," +
         "   mi_fecha_fin_mes \"fechaUltimaModificacion\"," +
                 " case " + 
                 "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0311','0321')  then '1'" + 
                 "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0351','0352')  then '2'" + 
                 "  when trim(COD_LINEA)||trim(ID_GRP_PD) =  '0171'  then '3'" + 
                 "  when trim(COD_LINEA)||trim(ID_GRP_PD) =  '0151'  then '4'" + 
                 "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0151','0551')  then '5'" + 
                 " end \"codClasificacion\", " +     
                     " max(case " + 
                     "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0311','0321')  then 'PASIVO A LA VISTA'" + 
                     "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0351','0352')  then 'DEPÓSITOS'" + 
                     "  when trim(COD_LINEA)||trim(ID_GRP_PD) =  '0171'  then 'PRÉSTAMOS'" + 
                     "  when trim(COD_LINEA)||trim(ID_GRP_PD) =  '0151'  then 'TARJETAS CRÉDITO'" + 
                     "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0151','0551')  then 'TARJETAS CRÉDITO Y DÉBITO'" + 
                     " end) \"nombreClasificacion\", " +                     
         "   sum(mi_sdo_ac_p) \"saldoPuntual\"," +
         "   sum(mi_sdo_dispble_p) \"saldoDisponible\"," +
         "   sum(mi_sdo_acr_p) \"saldoAcreedor\"," +
         "   sum(mi_sdo_deu_p) \"saldoDeudor\"" +
         " from rdwc01.mi_ac_eco_gen_dia" +
         " where cod_nrbe_en='" + request.getParameter("codEntidad") + "'" ;
         
 if(strContrato != null){
     strQuery = strQuery + " and num_sec_ac =" + request.getParameter("idContract") ; 
 }
 else{
     strQuery = strQuery + " and num_sec_ac in (" +
          " select num_sec_ac from rdwc01.mi_ac_cont_gen " +
          " where cod_nrbe_en='" + strEntidad + "' " +
          "    and id_interno_pe=" + strIdInternoPe +
          "    and mi_fecha_fin=to_date('31.12.9999','dd.mm.yyyy') " + whereLineaEq +
    ")";
 }
         
 strQuery = strQuery + "  and mi_fecha_fin_mes=( " +
          "  select max(mi_fecha_fin_mes) from rdwc01.mi_ac_eco_gen_dia  " +
          "      where cod_nrbe_en='" + strEntidad  + "'  " ;
                  if(strContrato != null){
                      strQuery = strQuery + " and num_sec_ac =" + request.getParameter("idContract") ; 
                  }
                  else{
                      strQuery = strQuery + " and num_sec_ac in (" +
                           " select num_sec_ac from rdwc01.mi_ac_cont_gen " +
                           " where cod_nrbe_en='" + strEntidad + "' " +
                           "    and id_interno_pe=" + strIdInternoPe +
                           "    and mi_fecha_fin=to_date('31.12.9999','dd.mm.yyyy') " + whereLineaEq +
                     ")";
                  }
 
          strQuery = strQuery + "      and mi_fecha_fin_mes < to_date('9999-12-31','yyyy-mm-dd')  )  " +
          
         " group by mi_fecha_fin_mes, mi_fec_ult_mov, " +                    
                " case " + 
                "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0311','0321')  then '1'" + 
                "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0351','0352')  then '2'" + 
                "  when trim(COD_LINEA)||trim(ID_GRP_PD) =  '0171'  then '3'" + 
                "  when trim(COD_LINEA)||trim(ID_GRP_PD) =  '0151'  then '4'" + 
                "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0151','0551')  then '5'" + 
                " end " ;	
*/	
	
	strResponse = QueryCustomizer.process(request,strQuery);	
	response.setHeader("content-type", "application/json");
	
%><%=strResponse%>
