<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
 	import="com.rsi.rvia.rest.DDBB.DDBBConnection,
		 com.rsi.rvia.rest.DDBB.DDBBFactory,
		 com.rsi.rvia.rest.DDBB.DDBBFactory.DDBBProvider,
		 com.rsi.rvia.rest.DDBB.OracleDDBBCIP, 
		 com.rsi.rvia.rest.tool.Utils, 
		 java.sql.PreparedStatement,
		 java.sql.ResultSet,
		 org.json.JSONArray,
		 org.json.JSONObject,
		 org.slf4j.Logger,
		 org.slf4j.LoggerFactory
"
%>

<%

/*
select mi_fecha_fin_mes,mi_sdo_ac_p, mi_sdo_dispble_p,mi_sdo_acr_p, mi_sdo_deu_p 
from rdwc01.mi_ac_eco_gen
where cod_nrbe_en='3076'
and mi_fecha_fin_mes < '31.12.9999'
and mi_fecha_fin_mes > '31.12.2015'
and num_sec_ac = 2407056379
*/

	String q =
		" SELECT" +   
		" 	t1.NUM_SEC_AC \"numeroAcuerdo\", trim(t2.NOMB_GRP_PD) \"nombreGrupo\"," +
		" 	trim(nvl(t3.NOMB_PDV, t2.NOMB_GRP_PD)) \"nombreProducto\"" +
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
		" 	AND t1.COD_NRBE_EN =?" +
		" 	AND t1.COD_RL_PERS_AC = '01'" +
		" 	AND t1.NUM_RL_ORDEN = 1" +
		" 	AND t1.COD_ECV_PERS_AC = '2'" +
		" 	AND t1.ID_INTERNO_PE =?" +
		" 	and t2.mi_fecha_fin = (select MI_FECHA_PROCESO from rdwc01.ce_carga_tabla" +
		" 		where nomtabla='MI_LINEA_GRUPO')" ;

	JSONObject pp= new JSONObject();
	pp.put("token", "sitio para el token");
	Logger	pLog = LoggerFactory.getLogger("jsp");	
	DDBBConnection p3 = DDBBFactory.getDDBB(DDBBProvider.OracleCIP,"cip");	
	PreparedStatement ps = p3.prepareStatement(q);
	ps.setString(1,request.getParameter("codEntidad"));
	ps.setString(2, request.getParameter("idInternoPe"));
	ResultSet rs = p3.executeQuery(ps);	
	JSONArray json = Utils.convertResultSet2JSON(rs);
	rs.close();
	ps.close();
	p3.BBDD_Disconnect();
	pp.put("output", json);	
	String respuesta=pp.toString();
	response.setHeader("content-type", "application/json");
	//pLog.info("------------------------------- " + json.toString());
%>
<%=respuesta %>
