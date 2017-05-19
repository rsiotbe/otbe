<%@page import="com.rsi.rvia.rest.userCommunication.CommunicationUtils"%>
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
<%
	pLog.info("Messages ::: DeletedList ::: Start");
	JSONObject pJsonResponse = new JSONObject();
	response.setHeader("content-type", "application/json");
	
	RequestConfigRvia pConfigRvia = new RequestConfigRvia(request);
	JSONArray pJsonResult = getDeletedList(pConfigRvia.getNRBE(), pConfigRvia.getLanguage().name(), pConfigRvia.getRviaUserId());
	pJsonResponse.put("deletedMessages", pJsonResult);
	%><%=Utils.generateWSResponseJsonOk("deletedMessages", pJsonResponse.toString())%>
<%!

String strErrorCode = "errorCode";
String strErrorMessage = "errorMessage";
Logger pLog = LoggerFactory.getLogger("deletedList.jsp");

/*
 * Devuelve el listado de noticias para mostrar al usuario.
 */
public JSONArray getDeletedList (String strCodNrbe, String strLanguage, String strUser) throws Exception
{
	pLog.debug("Messages ::: getDeletedList ::: Start ");
	Connection pConnection = null;
	JSONArray pJsongetNewsResponse = null;
	String strQuery = "{call BEL.PK_CONSULTA_BUZON_MOVIL.getDeletedMessages(?,?,?,?)}";
	try
	{
		pLog.debug("Messages ::: getDeletedList ::: DDBBProvider ");
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
		pLog.debug("Messages ::: getDeletedList ::: pCallableStatement ");
	    pCallableStatement = pConnection.prepareCall(strQuery);
		pCallableStatement.setString(1, strCodNrbe);
	  	pCallableStatement.setString(2, strLanguage);
		pCallableStatement.setString(3, strUser);
		pCallableStatement.registerOutParameter(4, OracleTypes.CURSOR);
		
		pCallableStatement.executeUpdate();
		ResultSet pResultSet = (ResultSet) pCallableStatement.getObject(4);
		
		pJsongetNewsResponse = Utils.convertResultSetToJSON(pResultSet);
		if (pJsongetNewsResponse.length()>0){
			JSONArray pAuxResponse = new JSONArray();
			JSONObject pAuxJson = null;
			for (int i = 0; i < pJsongetNewsResponse.length(); i++) {
				pAuxJson = new JSONObject(pJsongetNewsResponse.getJSONObject(i).toString());
				String strHistoryCod = String.valueOf(pAuxJson.getInt("HISTORIA_ID"));
				String strMailCod = String.valueOf(pAuxJson.getInt("BUZON_ID"));
				
				int iHistoryNumber = CommunicationUtils.getHistoryNumber(strHistoryCod, strMailCod);
				pAuxJson.put("N_HISTORIA", iHistoryNumber);
				pAuxResponse.put(pAuxJson);
			}
			pJsongetNewsResponse = pAuxResponse;
		}
		pLog.debug("Messages ::: getDeletedList ::: pJsongetNewsResponse " + pJsongetNewsResponse);
	}
	catch (Exception e)
	{
		pLog.error("Messages ::: getDeletedList ::: pCallableStatement Exception " + e.getMessage());		
	}
	finally
	{
		try{
			pCallableStatement.close();
			pConnection.close();
		}
		catch (Exception e)
		{
			pLog.error("Messages ::: getDeletedList ::: pCallableStatement Close Exception " + e.getMessage());				
		}
	}  
	return pJsongetNewsResponse;
}
%>

