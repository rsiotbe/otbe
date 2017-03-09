<%@page import="java.util.Arrays"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.json.JSONObject"%>
<%@page import="java.util.List"%>
<%@page import="java.sql.Types"%>
<%@page import="java.sql.CallableStatement"%>
<%@page import="org.json.JSONArray"%>
<%@page import="com.rsi.rvia.rest.tool.Utils"%>
<%@page import="oracle.jdbc.OracleTypes"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.rsi.rvia.rest.client.MiqAdminValidator"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider"%>
<%@page import="com.rsi.rvia.rest.DDBB.DDBBPoolFactory"%>
<%@page import="com.rsi.rvia.rest.tool.SqlExecutor"%>
<%
	//getNews (Entidad, PerfilIsum, Usuario)
	JSONArray pJsonResult = null;
	JSONObject pJsonResponse = new JSONObject();
	pLog.info("GetNews ::: Inicio");
	String strMethod = request.getMethod();
	pLog.info("GetNews ::: Inicio ::: strMethod " + strMethod);
	String codUser = request.getParameter("codUser");
	String isumProfile = request.getParameter("isumProfile");
	String codNrbe = request.getParameter("codNrbe");
	String[] viewed = request.getParameterMap().get("viewed[]");
	String noMore = request.getParameter("noMore");
	// 	switch (strMethod)
	// 	{
	// 	    case "GET":
		if (viewed == null){
			pJsonResult = getNews(codNrbe,isumProfile,codUser);
	    	// 	        break;
	    	// 	    case "POST":
		} else {
	    	updateNews(codUser, Arrays.asList(viewed), noMore);
		}
	// 	        break;
	    	// 	    case "PUT":
	// 	        break;
	    	// 	    case "PATCH":
	// 	        break;
	    	// 	    case "DELETE":
		//         break;
	    	//     default:
	// 	    	break;
	    	//     }
	
	response.setHeader("content-type", "application/json");
	pJsonResponse.put("result", pJsonResult);
%><%=pJsonResponse%>

<%!

Logger pLog = LoggerFactory.getLogger("getNews.jsp");
public JSONArray getNews (String strCodNrbe, String strProfile, String strUser) throws Exception{
	pLog.info("GetNews ::: getNews ::: Inicio ");
	Connection pConnection = null;
	JSONArray pJsongetNewsResponse = null;
	String strQuery = "{call BEL.PK_COMUNICACION_CLIENTE.getNews(?,?,?,?,?,?)}";
	try
	{
		pLog.info("GetNews ::: getNews ::: DDBBProvider ");
		pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
		
		pConnection.setAutoCommit(false);
	}
	catch (Exception ex)
	{
		pLog.info("GetNews ::: getNews ::: DDBBProvider Exception " + ex.getMessage());
	}
	
	CallableStatement pCallableStatement = null;
	
	try{
			int iResultCode = 0;
			String strError;
			pLog.info("GetNews ::: getNews ::: pCallableStatement ");
		    pCallableStatement = pConnection.prepareCall(strQuery);
			pLog.info("GetNews ::: getNews ::: pCallableStatement complete " + pCallableStatement.toString());
			pCallableStatement.setString(1, strCodNrbe);
		  	pCallableStatement.setString(2, strProfile);
			pCallableStatement.setString(3, strUser);
			pCallableStatement.registerOutParameter(4, Types.NUMERIC);
			pCallableStatement.registerOutParameter(5, Types.VARCHAR);
			pCallableStatement.registerOutParameter(6, OracleTypes.CURSOR);
			pLog.info("GetNews ::: getNews ::: pCallableStatement complete " + pCallableStatement.toString());
			
			pCallableStatement.executeUpdate();
			iResultCode = pCallableStatement.getInt(4);
			pLog.error("GetNews ::: getNews ::: resultCode " + iResultCode);
			if (iResultCode != 0){
				strError = pCallableStatement.getString(5);
				pLog.error("GetNews ::: getNews ::: BBDD ha respondido con error" + strError);
				throw new Exception(strError);
			}
			ResultSet pResultSet = (ResultSet) pCallableStatement.getObject(6);
			
			pJsongetNewsResponse = Utils.convertResultSetToJSON(pResultSet);
			pLog.info("GetNews ::: getNews ::: pJsongetNewsResponse " + pJsongetNewsResponse);
	}
	catch (Exception e)
	{
		pLog.error("GetNews ::: getNews ::: pCallableStatement Exception " + e.getMessage());		
	}
	finally
	{
		try{
			pCallableStatement.close();
			pConnection.close();
		}catch (Exception e){
			
		}
	}  
	return pJsongetNewsResponse;
}

public JSONArray updateNews (String strUser, List<String> pViews, String strNoMore){
	JSONArray pJsongetNewsResponse = null;
	String strQueryNoMore = "{call BEL.PK_COMUNICACION_CLIENTE.noMore(?,?,?,?)}";
	String strQueryViews  = "{call BEL.PK_COMUNICACION_CLIENTE.viewed(?,?,?,?)}";
	if ((pViews != null && !pViews.isEmpty()) || (strNoMore != null)){
		  for(String strCod: pViews){
			  callBBDD(Integer.parseInt(strCod), strUser, strQueryViews);
		  }
		  if (strNoMore != null && !strNoMore.isEmpty() ){
			  int iNoMore = Integer.parseInt(strNoMore);
			  callBBDD(iNoMore, strUser, strQueryNoMore);			  
		  }
	}
	return pJsongetNewsResponse;
}

public void callBBDD(int strCod, String strUser, String strQuery){
	Connection pConnection = null;
	try
	{
		pLog.info("GetNews ::: getNews ::: DDBBProvider ");
		pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
		
		pConnection.setAutoCommit(true);
	}
	catch (Exception ex)
	{
		pLog.info("GetNews ::: getNews ::: DDBBProvider Exception " + ex.getMessage());
	}
	
	CallableStatement pCallableStatement = null;
	try{
		int iResultCode = 0;
		String strError;
		pLog.info("GetNews ::: getNews ::: pCallableStatement ");
	    pCallableStatement = pConnection.prepareCall(strQuery);
		pLog.info("GetNews ::: getNews ::: pCallableStatement complete " + pCallableStatement.toString());
		pCallableStatement.setInt(1, strCod);
	  	pCallableStatement.setString(2, strUser);
		pCallableStatement.registerOutParameter(3, Types.NUMERIC);
		pCallableStatement.registerOutParameter(4, Types.VARCHAR);
		pLog.info("GetNews ::: getNews ::: pCallableStatement complete " + pCallableStatement.toString());
		
		pCallableStatement.execute();
		iResultCode = pCallableStatement.getInt(4);
		if (iResultCode != 0){
			strError = pCallableStatement.getString(5);
			throw new Exception(strError);
		}		
	}
	catch (Exception e)
	{
		pLog.info("GetNews ::: getNews ::: pCallableStatement Exception " + e.getMessage());
		e.printStackTrace();
	}
	finally
	{
		try{
			pCallableStatement.close();
			pConnection.close();
		}catch (Exception e){
			
		}
	}
}
%>

