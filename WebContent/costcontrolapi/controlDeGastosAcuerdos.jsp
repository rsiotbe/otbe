<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
 	import="
		 com.rsi.rvia.rest.client.QueryCustomizer,
		 com.rsi.rvia.rest.DDBB.DDBBPoolFactory,
		 com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider,
        java.sql.Connection,
        java.sql.PreparedStatement,
        java.sql.ResultSet		 
		 
"
%>
<%
	String strLinea = request.getParameter("codLinea");
	String whereLineaEq="";
	if(strLinea != null){
	    whereLineaEq=" AND T1.COD_LINEA = '" + strLinea + "'";
	}
/* BEGIN: Extracción de los acuerdos y sus alias de Banca */
    String strQuery = 
   		 " select  substr(b.cta_aso,11,20) acuerdo, trim (b.descr_txt) txtproducto " +
   		 " from bel.belts007 a, bel.belts009 b " +
   				 " where a.tarjeta_cod = b.tarjeta_cod" +
   				 " and a.estado='A'" +
   				 " and a.nrbe='" + request.getParameter("codEntidad") + "'" +
   				 " and a.tarjeta_cod='" + request.getParameter("codTarjeta") + "'" ;
	
	Connection pConnection = null;
	PreparedStatement pPreparedStatement = null;
	ResultSet pResultSet = null;
	pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
	pPreparedStatement = pConnection.prepareStatement(strQuery);
	pResultSet = pPreparedStatement.executeQuery();
	String strAliases = "";
	String strFiltroAcuerdos = " and t1.num_sec_ac in (";
	String coma="";
	
   while (pResultSet.next())
   {      
       String strAcuerdo = (String) pResultSet.getString("acuerdo");
       String strAlias = (String) pResultSet.getString("txtproducto");
       strAliases = coma + strAliases + "'" + strAcuerdo + "' , '" + strAlias + "'" ;
       strFiltroAcuerdos = strFiltroAcuerdos + coma + strAcuerdo;
      coma=",";
   }
   strFiltroAcuerdos = strFiltroAcuerdos + ") ";
   pResultSet.close();
   pPreparedStatement.close();
   pConnection.close();
/* END: Extracción de los acuerdos y sus alias de Banca */

// FIXME: Eliminar en producción
 // Failback: si no existen acuerdos en banca por tema de entornos los sacamos de ci
    if(! ",".equals(coma)){
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
	                "   AND t1.ID_INTERNO_PE = " + request.getParameter("idInternoPe") +
	                "   and t2.mi_fecha_fin = (select MI_FECHA_PROCESO from rdwc01.ce_carga_tabla" +
	                "       where nomtabla='MI_LINEA_GRUPO')";
	    pConnection = null;
	    pPreparedStatement = null;
	    pResultSet = null;
	    pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleCIP);
	    pPreparedStatement = pConnection.prepareStatement(strQuery);
	    pResultSet = pPreparedStatement.executeQuery();
	    strAliases = "";
	    strFiltroAcuerdos = " and t1.num_sec_ac in (";
	    coma="";
   
	   while (pResultSet.next())
	   {      
	       String strAcuerdo = (String) pResultSet.getString("acuerdo");
	       String strAlias = (String) pResultSet.getString("txtproducto");
	       strAliases = strAliases + coma + "'" + strAcuerdo + "' , '" + strAlias + "'" ;
	       strFiltroAcuerdos = strFiltroAcuerdos + coma + strAcuerdo;
	      coma=",";
	   }
	   strFiltroAcuerdos = strFiltroAcuerdos + ") ";
	   pResultSet.close();
	   pPreparedStatement.close();
	   pConnection.close();
    }
// ----------------------------------------


	strQuery =
		" SELECT" +   
		" 	t1.NUM_SEC_AC \"acuerdo\", trim(t2.NOMB_GRP_PD) \"nombreGrupo\"," +
		" 	trim(nvl(t3.NOMB_PDV, t2.NOMB_GRP_PD)) \"nombreProducto\"," +				
		"   decode (t1.num_sec_ac," + strAliases + ") \"aliasBanca\"" +
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
		" 	AND t1.ID_INTERNO_PE = " + request.getParameter("idInternoPe") +
		" 	and t2.mi_fecha_fin = (select MI_FECHA_PROCESO from rdwc01.ce_carga_tabla" +
		" 		where nomtabla='MI_LINEA_GRUPO')";
	String strResponse = QueryCustomizer.process(request,strQuery);       
	response.setHeader("content-type", "application/json");
%>
<%=strResponse %>
