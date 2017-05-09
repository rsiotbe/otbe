<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
 	import="
 	     com.rsi.rvia.rest.endpoint.rsiapi.AcuerdosRuralvia,
		 com.rsi.rvia.rest.client.QueryCustomizer,
         com.rsi.rvia.rest.DDBB.DDBBPoolFactory,
         com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider,
         com.rsi.rvia.rest.error.exceptions.ApplicationException,
        java.sql.Connection,
        java.sql.PreparedStatement,
        java.sql.ResultSet,		 
		 org.slf4j.Logger,
         org.slf4j.LoggerFactory,
         com.rsi.Constants,
         com.rsi.rvia.rest.tool.AppConfiguration
"
%>
<%

String uri = request.getRequestURI();
String pageName = uri.substring(uri.lastIndexOf("/")+1);
Logger pLog  = LoggerFactory.getLogger(pageName);
String [] strRviaAcuerdos = AcuerdosRuralvia.getRviaContractsDecodeAliases(request.getParameter("codTarjeta"));

String strQuery =
		" select " +
		"    substr(b.cta_aso,11,20) acuerdo " +
		" from bel.belts009 b " +
		" where b.tarjeta_cod='" + request.getParameter("codTarjeta") + "' " +
		" union " +
		" select " +
		"    acuerdo " +
		" from BEL.BDPTB083_TARJETAS_MP c " +
		" where c.tarjeta_cod='" + request.getParameter("codTarjeta") + "' ";
		
		pLog.info("Query para extraer los acuerdos visibles en banca: "+ strQuery);  
		String strFiltroAcuerdos = " and num_sec_ac in (";
		String coma="";		
		Connection pConnection = null;
		PreparedStatement pPreparedStatement = null;
		ResultSet pResultSet = null;

	    try{
            pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
            pPreparedStatement = pConnection.prepareStatement(strQuery);
            pResultSet = pPreparedStatement.executeQuery();
            
           while (pResultSet.next())
           {      
               String strAcuerdo = (String) pResultSet.getString("acuerdo");
               strFiltroAcuerdos = strFiltroAcuerdos + coma + strAcuerdo;
              coma=",";
           }
           if(coma.equals("")){
            strFiltroAcuerdos="-1";
           }
           strFiltroAcuerdos = strFiltroAcuerdos + ") ";                
           DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
           
	    }
	    catch(Exception ex){
	        //throw new Exception();
	        throw new ApplicationException(500, 9999, "Jsp error", "", ex);
	    }
	    finally{
	        DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
	    }
		
        String entorno = AppConfiguration.getInstance().getProperty(Constants.ENVIRONMENT);
        if (entorno.equals("TEST"))
        {
            if(strFiltroAcuerdos.indexOf("-1") != -1){
                strFiltroAcuerdos="";               
            }
        }		

    String strLastChargeDate = QueryCustomizer.getLastChargeDate("MI_AC_ECO_GEN","D");
	String strContrato = request.getParameter("idContract");
    String strIdInternoPe = request.getParameter("idInternoPe");
	String strEntidad = request.getParameter("codEntidad");
	String strDateIni = request.getParameter("mesInicio");
	String strDateFin = request.getParameter("mesFin");
	String strCaseNum = 
            " case " + 
            "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0311','0321')  then '1'" + 
            "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0351','0352')  then '2'" + 
            "  when trim(COD_LINEA)||trim(ID_GRP_PD) =  '0171'  then '3'" + 
            "  when trim(COD_LINEA)||trim(ID_GRP_PD) =  '0151'  then '4'" + 
            "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0151','0551','0553')  then '5'" + 
            " end " ; 
	
   strDateIni = strDateIni + "-25";
   if(strDateFin != null){
   	   strDateFin= QueryCustomizer.yearMonthToFirstDayOfNextMonth(strDateFin);
   }
   
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
             strCaseNum = 
                     " case " + 
                     "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0311','0321')  then '1'" + 
                     "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0351','0352')  then '2'" + 
                     "  when trim(COD_LINEA)||trim(ID_GRP_PD) =  '0171'  then '3'" + 
                     "  when trim(COD_LINEA)||trim(ID_GRP_PD) =  '0151'  then '4'" +
                     " end " ;              
             break;
         case 5:
             whereLineaEq=" AND trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0151','0551','0553')";
             strCaseNum = 
                     " case " + 
                     "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0311','0321')  then '1'" + 
                     "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0351','0352')  then '2'" + 
                     "  when trim(COD_LINEA)||trim(ID_GRP_PD) =  '0171'  then '3'" + 
                     "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0151','0551','0553')  then '5'" + 
                     " end " ;              
             break;
     }
 }
   else{
   	whereLineaEq=" AND trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0311','0321','0351','0352','0171','0151','0551','0553')";
   }
 		
	if(strDateFin == null){
		strDateFin = "9999-12-31";
	}

	String strResponse = "{}";
	strQuery =
			" select" +
			" decode(to_char(mi_fecha_fin_mes,'yyyy-mm-dd'),'9999-12-31', '" + strLastChargeDate + "', to_char(mi_fecha_fin_mes,'yyyy-mm-dd')) \"finMes\"," +
			         strCaseNum +  " \"codClasificacion\", " + 	
	                    " max(case " + 
	                    "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0311','0321')  then 'PASIVO A LA VISTA'" + 
	                    "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0351','0352')  then 'DEPÓSITOS'" + 
	                    "  when trim(COD_LINEA)||trim(ID_GRP_PD) =  '0171'  then 'PRÉSTAMOS'" + 
	                    "  when trim(COD_LINEA)||trim(ID_GRP_PD) =  '0151'  then 'TARJETAS CRÉDITO'" + 
	                    "  when trim(COD_LINEA)||trim(ID_GRP_PD) in  ('0151','0551','0553')  then 'TARJETAS CRÉDITO Y DÉBITO'" + 
	                    " end) \"nombreClasificacion\", " + 			        
			" 	sum(mi_sdo_ac_p) \"saldoPuntual\"," +
			" 	sum(mi_sdo_dispble_p) \"saldoDisponible\"," +
			" 	sum(mi_sdo_acr_p) \"saldoAcreedor\"," +
			" 	sum(mi_sdo_deu_p) \"saldoDeudor\"" +
			" from rdwc01.mi_ac_eco_gen t1" +
			" where cod_nrbe_en='" + request.getParameter("codEntidad") + "'" ;

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
            strQuery = strQuery + " and t1.num_sec_ac in (" + strRviaAcuerdos[1] + ") ";
        }
     }
     else{
        strQuery = strQuery + " and t1.num_sec_ac =" + strContrato; 
     } 
	
	strQuery = strQuery + strFiltroAcuerdos + " " + whereLineaEq;	
	strQuery = strQuery + " and mi_fecha_fin_mes > to_date('" + strDateIni + "','yyyy-mm-dd')" +
			" and mi_fecha_fin_mes <= to_date('" + strDateFin + "','yyyy-mm-dd')" + 
			" group by decode(to_char(mi_fecha_fin_mes,'yyyy-mm-dd'),'9999-12-31', '" + strLastChargeDate + "', to_char(mi_fecha_fin_mes,'yyyy-mm-dd')),";
	strQuery = strQuery + strCaseNum;
			

	
/* NO BORRAR EL BLOQUE DE COMENTARIO TODAVÍA: PENDIENTE DE PRUEBAS DE RENDIMIENTO	
	
	
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
    pLog.info("Query al customizador: " + strQuery);
	strResponse = QueryCustomizer.process(request,strQuery);	
	response.setHeader("content-type", "application/json");
	
%><%=strResponse%>
