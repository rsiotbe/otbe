<%@page import="com.rsi.rvia.rest.session.RequestConfigRvia"%>
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
<%@page import="com.rsi.rvia.rest.tool.AppConfiguration"%>
<%
	pLog.debug("Messages ::: MessageTypes ::: Start");
	JSONObject pJsonResponse = new JSONObject();
	response.setHeader("content-type", "application/json");

	RequestConfigRvia pConfigRvia = new RequestConfigRvia(request);
	JSONArray pJsonResult = getMessageTypes(pConfigRvia.getNRBE());
	pJsonResponse.put("messageTypes", pJsonResult);
	%><%=Utils.generateWSResponseJsonOk("messageTypes", pJsonResponse.toString())%>
<%!

String strErrorCode = "errorCode";
String strErrorMessage = "errorMessage";
Logger pLog = LoggerFactory.getLogger("MessageTypes.jsp");

/*
 * Devuelve el listado de noticias para mostrar al usuario.
 */
public JSONArray getMessageTypes (String strCodNrbe) throws Exception
{
	pLog.debug("Messages ::: getMessageTypes ::: Start ");
	Connection pConnection = null;
	JSONArray pJsongetNewsResponse = null;
	String strQuery = "{call " + AppConfiguration.getInstance().getProperty("BELScheme").trim() + ".PK_CONSULTA_BUZON_MOVIL.getMessageTypes(?,?)}";
	try
	{
		pLog.debug("Messages ::: getMessageTypes ::: DDBBProvider ");
		pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
		
		pConnection.setAutoCommit(false);
	}
	catch (Exception ex)
	{
		pLog.error("Messages ::: getNews ::: DDBBProvider Exception " + ex.getMessage());
	}
	
	CallableStatement pCallableStatement = null;
	
	try
	{
		int iResultCode = 0;
		String strError;
		pLog.debug("Messages ::: getMessageTypes ::: pCallableStatement ");
	    pCallableStatement = pConnection.prepareCall(strQuery);
		pCallableStatement.setString(1, strCodNrbe);
		pCallableStatement.registerOutParameter(2, OracleTypes.CURSOR);
		
		pCallableStatement.executeUpdate();
		ResultSet pResultSet = (ResultSet) pCallableStatement.getObject(2);
		
		pJsongetNewsResponse = Utils.convertResultSetToJSON(pResultSet);
		pLog.debug("Messages ::: getMessageTypes ::: pJsongetNewsResponse " + pJsongetNewsResponse);
	}
	catch (Exception e)
	{
		pLog.error("Messages ::: getMessageTypes ::: pCallableStatement Exception " + e.getMessage());		
	}
	finally
	{
		try{
			pCallableStatement.close();
			pConnection.close();
		}
		catch (Exception e)
		{
			pLog.error("Messages ::: getMessageTypes ::: pCallableStatement Close Exception " + e.getMessage());				
		}
	}  
	return pJsongetNewsResponse;
}
%>

