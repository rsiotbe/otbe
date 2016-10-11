<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
 	import="
		 com.rsi.rvia.rest.client.QueryCustomizer
"
%>
<%
	String strContrato = request.getParameter("idContract").toString();
	String strEntidad = request.getParameter("codEntidad").toString();
	String strDateIni = request.getParameter("fechaInicio").toString();
	String strDateFin = request.getParameter("fechaFin");
	
	if(strDateFin == null){
		strDateFin = "9999-12-31";
	}

	String strResponse = "{}";
	String strQuery =
			" select" +
			" 	mi_fecha_fin_mes \"finMes\"," +
			" 	mi_sdo_ac_p \"saldoPuntual\"," +
			" 	mi_sdo_dispble_p \"saldoDisponible\"," +
			" 	mi_sdo_acr_p \"saldoAcreedor\"," +
			" 	mi_sdo_deu_p \"saldoDeudor\"" +
			" from rdwc01.mi_ac_eco_gen" +
			" where cod_nrbe_en='" + request.getParameter("codEntidad") + "'" +
			" and num_sec_ac =" + request.getParameter("idContract") + 
			" and mi_fecha_fin_mes >= to_date('" + strDateIni + "','yyyy-mm-dd')" +
			" and mi_fecha_fin_mes <= to_date('" + strDateFin + "','yyyy-mm-dd')";
	strResponse = QueryCustomizer.process(request,strQuery);	
	response.setHeader("content-type", "application/json");
%><%=strResponse%>
