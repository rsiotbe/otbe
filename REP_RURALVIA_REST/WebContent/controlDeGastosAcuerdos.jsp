<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
 	import="com.rsi.rvia.rest.DDBB.DDBBConnection,
		 com.rsi.rvia.rest.DDBB.DDBBFactory,
		 com.rsi.rvia.rest.DDBB.DDBBFactory.DDBBProvider,
		 com.rsi.rvia.rest.DDBB.OracleDDBB, 
		 java.sql.PreparedStatement,
		 java.sql.ResultSet,
		 org.slf4j.Logger,
		 org.slf4j.LoggerFactory
"
%>

<%
	String q =
					" SELECT                                                     " + 
					" NUM_SEC_AC                                                 " +
					" FROM rdwc01.mi_clte_rl_ac                                  " +
					" WHERE MI_FECHA_FIN = to_date('31/12/9999','dd/mm/yyyy')    " +
					" AND COD_NRBE_EN = ?                                        " +
					" AND COD_RL_PERS_AC = '01'                                  " +
					" AND NUM_RL_ORDEN = 1                                       " +
					" AND COD_ECV_PERS_AC = '2'                                  " +
					" AND ID_INTERNO_PE = ?                                      " ;

	Logger	pLog = LoggerFactory.getLogger("jsp");
	
	DDBBConnection p3 = DDBBFactory.getDDBB(DDBBProvider.Oracle);
	//pLog.debug("Path Rest: " + strPrimaryPath);
	PreparedStatement ps = p3.prepareStatement(q);
	
	ps.setString(1,request.getParameter("codEntidad"));
	ps.setString(2, request.getParameter("idInternoPe"));
	
	ResultSet rs = p3.executeQuery(ps);
	pLog.info("Query Ejecutada!");
	String method = request.getMethod();
	

	//Response pReturn = null;

%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>

</body>
</html>