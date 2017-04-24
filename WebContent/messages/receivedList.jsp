<%@page import="java.io.InputStreamReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="org.json.JSONException"%>
<%@page import="org.json.JSONObject"%>
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
<%
	pLog.info("Messages ::: ReceivedList ::: Start");
	JSONObject pJsonResponse = new JSONObject();
	response.setHeader("content-type", "application/json");

	String strUser = (String) session.getAttribute("USUARIO");
	String strLanguage = (String) session.getAttribute("idioma");
	String strCodNrbe = (String) session.getAttribute("ENTALT");
	JSONArray pJsonResult = getReceivedList(strCodNrbe, strLanguage, strUser);
	pJsonResponse.put("receivedMessages", pJsonResult);
	%><%=Utils.generateWSResponseJsonOk("receivedMessages", pJsonResponse.toString())%>
<%!

String strErrorCode = "errorCode";
String strErrorMessage = "errorMessage";
Logger pLog = LoggerFactory.getLogger("getNews.jsp");

/*
 * Devuelve el listado de noticias para mostrar al usuario.
 */
public JSONArray getReceivedList (String strCodNrbe, String strLanguage, String strUser) throws Exception
{
	pLog.info("Messages ::: getReceivedList ::: Start ");
	Connection pConnection = null;
	JSONArray pJsongetNewsResponse = null;
	String strQuery = "{call BEL.PK_CONSULTA_BUZON_MOVIL.getReceivedMessages(?,?,?,?)}";
	try
	{
		pLog.info("Messages ::: getReceivedList ::: DDBBProvider ");
		pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
		
		pConnection.setAutoCommit(false);
	}
	catch (Exception ex)
	{
		pLog.info("Messages ::: getNews ::: DDBBProvider Exception " + ex.getMessage());
	}
	
	CallableStatement pCallableStatement = null;
	
	try
	{
		int iResultCode = 0;
		String strError;
		pLog.info("Messages ::: getReceivedList ::: pCallableStatement ");
	    pCallableStatement = pConnection.prepareCall(strQuery);
		pCallableStatement.setString(1, strCodNrbe);
	  	pCallableStatement.setString(2, strLanguage);
		pCallableStatement.setString(3, strUser);
		pCallableStatement.registerOutParameter(4, OracleTypes.CURSOR);
		
		pCallableStatement.executeUpdate();
		ResultSet pResultSet = (ResultSet) pCallableStatement.getObject(4);
		
		pJsongetNewsResponse = Utils.convertResultSetToJSON(pResultSet);
		pLog.info("Messages ::: getReceivedList ::: pJsongetNewsResponse " + pJsongetNewsResponse);
	}
	catch (Exception e)
	{
		pLog.error("Messages ::: getReceivedList ::: pCallableStatement Exception " + e.getMessage());		
	}
	finally
	{
		try{
			pCallableStatement.close();
			pConnection.close();
		}
		catch (Exception e)
		{
			pLog.error("Messages ::: getReceivedList ::: pCallableStatement Close Exception " + e.getMessage());				
		}
	}  
	return pJsongetNewsResponse;
}
%>

