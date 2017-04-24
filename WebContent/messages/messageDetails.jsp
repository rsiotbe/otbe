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
	pLog.info("Messages ::: MessageDetails ::: Start");
	JSONObject pJsonResponse = new JSONObject();
	response.setHeader("content-type", "application/json");

	String strMessage = request.getParameter("codMessage");
	JSONObject pJsonResult = getMessageDetails(strMessage);
	%><%=Utils.generateWSResponseJsonOk("message", pJsonResult.toString())%>
<%!

String strErrorCode = "errorCode";
String strErrorMessage = "errorMessage";
Logger pLog = LoggerFactory.getLogger("messageDetails.jsp");

/*
 * Devuelve el listado de noticias para mostrar al usuario.
 */
public JSONObject getMessageDetails (String strMessage) throws Exception
{
	pLog.info("Messages ::: MessageDetails ::: Start ");
	Connection pConnection = null;
	JSONObject pJsongetMessageResponse = null;
	String strQuery = "{call BEL.PK_CONSULTA_BUZON_MOVIL.getMessageDetails(?,?)}";
	try
	{
		pLog.info("Messages ::: MessageDetails ::: DDBBProvider ");
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
		pLog.info("Messages ::: MessageDetails ::: pCallableStatement ");
	    pCallableStatement = pConnection.prepareCall(strQuery);
		pCallableStatement.setString(1, strMessage);
		pCallableStatement.registerOutParameter(2, OracleTypes.CURSOR);
		
		pCallableStatement.executeUpdate();
		ResultSet pResultSet = (ResultSet) pCallableStatement.getObject(2);
		JSONArray pJsonArraygetMessageResponse = Utils.convertResultSetToJSON(pResultSet);
		if (pJsonArraygetMessageResponse.length()>0){
			JSONObject pAuxJson = new JSONObject(pJsonArraygetMessageResponse.getJSONObject(0).toString());
			String strHistoryCod = String.valueOf(pAuxJson.getInt("HISTORIA_ID"));
			String strMailCod = String.valueOf(pAuxJson.getInt("BUZON_ID"));
			
			int iHistoryNumber = getHistoryNumber(strHistoryCod, strMailCod);
			String strHistoria = "N";
			if (iHistoryNumber > 1){
				strHistoria = "S";
			}
			pJsongetMessageResponse = pAuxJson.put("HISTORIA", strHistoria);
		}
		pLog.info("Messages ::: MessageDetails ::: pJsongetNewsResponse " + pJsongetMessageResponse);
	}
	catch (Exception e)
	{
		pLog.error("Messages ::: MessageDetails ::: pCallableStatement Exception " + e.getMessage());		
	}
	finally
	{
		try{
			pCallableStatement.close();
			pConnection.close();
		}
		catch (Exception e)
		{
			pLog.error("Messages ::: MessageDetails ::: pCallableStatement Close Exception " + e.getMessage());				
		}
	}  
	return pJsongetMessageResponse;
}

public int getHistoryNumber (String strHistoryCod, String strMailCod) throws Exception
{
	pLog.info("Messages ::: MessageDetails ::: Start ");
	Connection pConnection = null;
	JSONArray pJsongetNewsResponse = null;
	String strQuery = "{? = call BEL.PK_CONSULTA_BUZON_MOVIL.getHistoryNumber(?,?)}";
	int iHistoryNumber = 0;
	try
	{
		pLog.info("Messages ::: MessageDetails ::: DDBBProvider ");
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
		pLog.info("Messages ::: MessageDetails ::: pCallableStatement ");
	    pCallableStatement = pConnection.prepareCall(strQuery);
		pCallableStatement.registerOutParameter(1, Types.INTEGER);
		pCallableStatement.setString(2, strHistoryCod);
		pCallableStatement.setString(3, strMailCod);
		
		pCallableStatement.executeUpdate();
		iHistoryNumber = pCallableStatement.getInt(1);
		
		pLog.info("Messages ::: MessageDetails ::: pJsongetNewsResponse " + pJsongetNewsResponse);
	}
	catch (Exception e)
	{
		pLog.error("Messages ::: MessageDetails ::: pCallableStatement Exception " + e.getMessage());		
	}
	finally
	{
		try{
			pCallableStatement.close();
			pConnection.close();
		}
		catch (Exception e)
		{
			pLog.error("Messages ::: MessageDetails ::: pCallableStatement Close Exception " + e.getMessage());				
		}
	}  
	return iHistoryNumber;
}
%>

