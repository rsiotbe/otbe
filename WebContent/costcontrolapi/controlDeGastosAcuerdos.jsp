<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
 	import="
com.rsi.rvia.rest.DDBB.DDBBPoolFactory,
com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider, 	
		 com.rsi.rvia.rest.client.QueryCustomizer,
		 com.rsi.rvia.rest.endpoint.rsiapi.AcuerdosRuralvia,
        java.sql.Connection,
        java.sql.PreparedStatement,
        java.sql.ResultSet,
        org.slf4j.Logger,
        org.slf4j.LoggerFactory   	 		 
"
%>
<%

String uri = request.getRequestURI();
String pageName = uri.substring(uri.lastIndexOf("/")+1);
Logger pLog  = LoggerFactory.getLogger(pageName);
String [] strRviaAcuerdos = AcuerdosRuralvia.getRviaContractsDecodeAliases(request);

String strResponse="";
	String strLinea = request.getParameter("codLinea");
    String strCodClasificacion = request.getParameter("codClasificacion");
	String whereLineaEq="";
	if(strLinea != null){
	    whereLineaEq=" AND T1.COD_LINEA = '" + strLinea + "'";
	}
	else if(strCodClasificacion != null){
	  switch(Integer.parseInt(strCodClasificacion)){
		  case 1:			  
			  whereLineaEq=" AND trim(T1.COD_LINEA)||trim(t1.ID_GRP_PD) in  ('0311','0321')";			  
			  break;
		  case 2:
			  whereLineaEq=" AND trim(T1.COD_LINEA)||trim(t1.ID_GRP_PD) in  ('0351','0352')";
			  break;			  
		  case 3:
			  whereLineaEq=" AND trim(T1.COD_LINEA)||trim(t1.ID_GRP_PD) = '0171'";
			  break;
		  case 4:
			  whereLineaEq=" AND trim(T1.COD_LINEA)||trim(t1.ID_GRP_PD) = '0151'";
			  break;
		  case 5:
			  whereLineaEq=" AND trim(T1.COD_LINEA)||trim(t1.ID_GRP_PD) in  ('0151','0551','0553')";
			  break;
	  }
    }
	else{
	    whereLineaEq=" AND trim(t1.COD_LINEA)||trim(t1.ID_GRP_PD) in  ('0311','0321','0351','0352','0171','0151','0551','0553')";
	}
	
    String strQuery;
	Connection pConnection = null;
	PreparedStatement pPreparedStatement = null;
	ResultSet pResultSet = null;
	
	
	try{
	    String strIdInterno=request.getParameter("idInternoPe");
        String strAliases = "";
        String strFiltroAcuerdos = "";
        String strFiltroAcuerdosTmp = "";
        String coma="";	    

	// FIXME: Eliminar en producción
	 // Failback: si no existen acuerdos en banca por tema de entornos los sacamos de ci
	    if(strRviaAcuerdos[1] != null){
	        strFiltroAcuerdosTmp = " and t1.num_sec_ac in (" + strRviaAcuerdos[1] + ")";
	    }
	    if(strRviaAcuerdos[1] == null){
		    strQuery = 
		          " SELECT" +   
		                "   t1.NUM_SEC_AC \"acuerdo\", trim(t2.NOMB_GRP_PD) \"txtproducto\"" +
		                " FROM" +
		                "   rdwc01.mi_clte_rl_ac t1" +
		                "   left join rdwc01.MI_LINEA_GRUPO t2" +
		                "   on" +
		                "       t1.COD_LINEA = t2.COD_LINEA" +
		                "       AND t1.ID_GRP_PD = t2.ID_GRP_PD" +
		                "   left outer join rdwc01.MI_LIN_GR_PRODTO t3" +
		                "   on" +
		                "       t1.COD_LINEA = t3.COD_LINEA" +
		                "       AND t1.ID_GRP_PD = t3.ID_GRP_PD" +
		                "       AND t1.ID_PDV = t3.ID_PDV" +
		                "       AND t1.cod_nrbe_en = t3.cod_nrbe_en" +
		                "       and t3.mi_fecha_fin = t2.mi_fecha_fin" +
		                " WHERE  t1.MI_FECHA_FIN = to_date('31.12.9999','dd.mm.yyyy')" +
		                "   AND t1.COD_NRBE_EN = '" + request.getParameter("codEntidad") + "'" +
		                "   AND t1.COD_RL_PERS_AC = '01'" +
		                "   AND t1.NUM_RL_ORDEN = 1" + 
		                "   AND t1.COD_ECV_PERS_AC = '2'" + whereLineaEq +
		                "   AND t1.ID_INTERNO_PE = " + strIdInterno + strFiltroAcuerdosTmp +
		                "   and t2.mi_fecha_fin = (select k.MI_FECHA_PROCESO from rdwc01.ce_carga_tabla k" +
		                "       where k.nomtabla='MI_LINEA_GRUPO')";
		    pConnection = null;
		    pPreparedStatement = null;
		    pResultSet = null;
		    pLog.info("Query a CI: "+strQuery);
		    pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleCIP);
		    pPreparedStatement = pConnection.prepareStatement(strQuery);	    
		    pResultSet = pPreparedStatement.executeQuery();
		    strAliases = "";
		    strFiltroAcuerdos = " and t1.num_sec_ac in (";
		    coma="";
		    strRviaAcuerdos[0]="";
		   while (pResultSet.next())
		   {      
		       String strAcuerdo = (String) pResultSet.getString("acuerdo");
		       String strAlias = (String) pResultSet.getString("txtproducto");
		       strRviaAcuerdos[0] = strRviaAcuerdos[0] + coma + "'" + strAcuerdo + "' , '" + strAlias + "'" ;
		       strFiltroAcuerdos = strFiltroAcuerdos + coma + strAcuerdo;
		      coma=",";
		   }
		   if(coma.equals("")){
		   	strFiltroAcuerdos = strFiltroAcuerdos + "-1";
		   	strAliases = " -1,'nulo' ";
		   } 
		   
		   strFiltroAcuerdos = strFiltroAcuerdos + ") ";
		   pResultSet.close();
		   pPreparedStatement.close();
		   
	    }
	    else{	        
	        strFiltroAcuerdos = " and t1.num_sec_ac in (" + strRviaAcuerdos[1] + ")";
	    } 
	        
	// ----------------------------------------
		
		strQuery =
			" SELECT" +   
			" 	t1.NUM_SEC_AC \"acuerdo\", trim(t2.NOMB_GRP_PD) \"nombreGrupo\"," +
			" 	trim(nvl(t3.NOMB_PDV, t2.NOMB_GRP_PD)) \"nombreProducto\"," +			
			" case " + 
			"  when trim(T1.COD_LINEA)||trim(t1.ID_GRP_PD) in  ('0311','0321')  then 'PASIVO A LA VISTA'" + 
			"  when trim(T1.COD_LINEA)||trim(t1.ID_GRP_PD) in  ('0351','0352')  then 'DEPÓSITOS'" + 
			"  when trim(T1.COD_LINEA)||trim(t1.ID_GRP_PD) =  '0171'  then 'PRÉSTAMOS'" + 
			"  when trim(T1.COD_LINEA)||trim(t1.ID_GRP_PD) =  '0151'  then 'TARJETAS CRÉDITO'" + 
			"  when trim(T1.COD_LINEA)||trim(t1.ID_GRP_PD) in  ('0151','0551','0553')  then 'TARJETAS CRÉDITO Y DÉBITO'" + 
			" end \"nombreClasificacion\", " + 
	        " case " + 
	        "  when trim(T1.COD_LINEA)||trim(t1.ID_GRP_PD) in  ('0311','0321')  then '1'" + 
	        "  when trim(T1.COD_LINEA)||trim(t1.ID_GRP_PD) in  ('0351','0352')  then '2'" + 
	        "  when trim(T1.COD_LINEA)||trim(t1.ID_GRP_PD) =  '0171'  then '3'" + 
	        "  when trim(T1.COD_LINEA)||trim(t1.ID_GRP_PD) =  '0151'  then '4'" + 
	        "  when trim(T1.COD_LINEA)||trim(t1.ID_GRP_PD) in  ('0151','0551','0553')  then '5'" + 
	        " end \"codClasificacion\", " + 
			"   decode (t1.num_sec_ac," + strRviaAcuerdos[0] + ") \"aliasBanca\", t1.COD_LINEA \"codLinea\"," +
			"     trim(t1.ID_GRP_PD) \"codGrupo\"" +
			" FROM" +
			" 	rdwc01.mi_clte_rl_ac t1" +
			" 	left join rdwc01.MI_LINEA_GRUPO t2" +
			" 	on" +
			" 		t1.COD_LINEA = t2.COD_LINEA" +
			" 		AND t1.ID_GRP_PD = t2.ID_GRP_PD" +
			" 	left outer join rdwc01.MI_LIN_GR_PRODTO t3" +
			" 	on" +
			" 		t1.COD_LINEA = t3.COD_LINEA" +
			" 		AND t1.ID_GRP_PD = t3.ID_GRP_PD" +
			" 		AND t1.ID_PDV = t3.ID_PDV" +
			" 		AND t1.cod_nrbe_en = t3.cod_nrbe_en" +
			" 		and t3.mi_fecha_fin = t2.mi_fecha_fin" +
			" WHERE  t1.MI_FECHA_FIN = to_date('31.12.9999','dd.mm.yyyy')" +
			" 	AND t1.COD_NRBE_EN = '" + request.getParameter("codEntidad") + "'" +
			" 	AND t1.COD_RL_PERS_AC = '01'" +
			" 	AND t1.NUM_RL_ORDEN = 1" + strFiltroAcuerdos +
			" 	AND t1.COD_ECV_PERS_AC = '2'" + whereLineaEq +
			" 	AND t1.ID_INTERNO_PE = " + strIdInterno +
	      " and case " + 
	      "  when trim(T1.COD_LINEA)||trim(t1.ID_GRP_PD) in  ('0311','0321')  then '1'" + 
	      "  when trim(T1.COD_LINEA)||trim(t1.ID_GRP_PD) in  ('0351','0352')  then '2'" + 
	      "  when trim(T1.COD_LINEA)||trim(t1.ID_GRP_PD) =  '0171'  then '3'" + 
	      "  when trim(T1.COD_LINEA)||trim(t1.ID_GRP_PD) =  '0151'  then '4'" + 
	      "  when trim(T1.COD_LINEA)||trim(t1.ID_GRP_PD) in  ('0151','0551','0553')  then '5'" + 
	      " end	is not null" +	
			" 	and t2.mi_fecha_fin = (select MI_FECHA_PROCESO from rdwc01.ce_carga_tabla" +
			" 		where nomtabla='MI_LINEA_GRUPO')";
		pLog.info("Query al customizador: " + strQuery);
		strResponse = QueryCustomizer.process(request,strQuery);       
				
	}
	catch(Exception ex){
	    throw new ApplicationException(500, 9999, "Jsp error", "", ex);
	}
	finally{
	   if(pConnection != null){
	        pResultSet.close();
	   }
        catch(Exception ex){
            throw new LogicalErrorException(500, 9999, "Jsp error", " - ", new Exception());
        }
        try {
            pPreparedStatement.close();
        }
        catch(Exception ex){
            throw new LogicalErrorException(500, 9999, "Jsp error", " - ", new Exception());
        }
        try {
            pConnection.close();
        }
        catch(Exception ex){
            throw new LogicalErrorException(500, 9999, "Jsp error", " - ", new Exception());
        }        
	}
	response.setHeader("content-type", "application/json");
%>
<%=strResponse %>
