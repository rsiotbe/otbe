<%@page import="javax.ws.rs.GET"%>
<%@page import="javax.ws.rs.PUT"%>
<%@page import="java.net.HttpURLConnection"%>
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
<%@page import="org.jsoup.nodes.Element"%>
<%@page import="org.jsoup.parser.Parser"%>
<%@page import="org.jsoup.Jsoup"%>
<%@page import="org.jsoup.nodes.Document"%>
<%@page import="com.rsi.rvia.rest.tool.AppConfiguration"%>
<%
	pLog.debug("Messages ::: MessageDetails ::: Start");
	JSONObject pJsonResponse = new JSONObject();
	response.setHeader("content-type", "application/json");

	RequestConfigRvia pConfigRvia = new RequestConfigRvia(request);
	

	JSONObject pJsonResult = null;
	String strMethod = request.getMethod();
	pLog.info("GetNews ::: Start ::: strMethod " + strMethod);
	response.setHeader("content-type", "application/json");
	
	switch (MethodType.byName(strMethod))
	{
		case GET:
			String strMessage = request.getParameter("codMessage");
			pJsonResult = getMessageDetails(strMessage, pConfigRvia.getNRBE(), pConfigRvia.getRviaUserId());
			%><%=Utils.generateWSResponseJsonOk("message", pJsonResult.toString())%><%
		break;
		case PUT:
			try {
				pJsonResult = sendMessage(request, pConfigRvia);
				if (pJsonResult.getInt(strErrorCode) == 0) {
					pJsonResponse.put("data", pJsonResult);
					%><%=Utils.generateWSResponseJsonOk("restoreMessage", pJsonResponse.toString())%><%
				} else {
					%><%=Utils.generateWSResponseJsonError("restoreMessage", pJsonResult.getInt(strErrorCode), pJsonResult.getString(strErrorMessage))%><%
				}
		    } catch (Exception e){
				pLog.error("Messages ::: restoreMessage ::: Exception", e);%>
				<%=Utils.generateWSResponseJsonError("restoreMessage", -1, strDefaultErrorMessage)%>
		  <%}
		break;
	}
%>
		
<%!

String strErrorCode = "errorCode";
String strErrorMessage = "errorMessage";
Logger pLog = LoggerFactory.getLogger("messageDetails.jsp");

private static final String strDefaultErrorMessage  = "Error no controlado";
private static final String strAmper		  		= "&";
private static final String strComa			  		= ",";
// Datos necesarios para la petición pero que pueden ir vacios.
private static final String strDatosSesion 			= "CODIGO_APP=GCO&PATH=&IP=&num_session=&CLIENTE_EMPRESA=&PERCON=&SELCON=&PRITAR=&servidor=&NUMTAR=&marca=";
private final String strEndpoint = "restaurarMensaje.do";

/*
 * Devuelve el listado de noticias para mostrar al usuario.
 */
public JSONObject getMessageDetails (String strMessage, String strCodNrbe, String strUser) throws Exception
{
	pLog.debug("Messages ::: MessageDetails ::: Start ");
	Connection pConnection = null;
	JSONObject pJsongetMessageResponse = null;
	String strQuery = "{call " + AppConfiguration.getInstance().getProperty("BELScheme").trim() + ".PK_CONSULTA_BUZON_MOVIL.getMessageDetails(?,?,?,?)}";
	try
	{
		pLog.info("Messages ::: MessageDetails ::: DDBBProvider ");
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
		pLog.debug("Messages ::: MessageDetails ::: pCallableStatement ");
	    pCallableStatement = pConnection.prepareCall(strQuery);
		pCallableStatement.setString(1, strCodNrbe);
		pCallableStatement.setString(2, strMessage);
		pCallableStatement.setString(3, strUser);
		pCallableStatement.registerOutParameter(4, OracleTypes.CURSOR);
		
		pCallableStatement.executeUpdate();
		ResultSet pResultSet = (ResultSet) pCallableStatement.getObject(4);
		JSONArray pJsonArraygetMessageResponse = Utils.convertResultSetToJSON(pResultSet);
		if (pJsonArraygetMessageResponse.length()>0){
			JSONObject pAuxJson = new JSONObject(pJsonArraygetMessageResponse.getJSONObject(0).toString());
			String strHistoryCod = String.valueOf(pAuxJson.getInt("HISTORIA_ID"));
			String strMailCod = String.valueOf(pAuxJson.getInt("BUZON_ID"));
			
			int iHistoryNumber = CommunicationUtils.getHistoryNumber(strHistoryCod, strMailCod);
			pJsongetMessageResponse = pAuxJson.put("N_HISTORIA", iHistoryNumber);
		}
		pLog.debug("Messages ::: MessageDetails ::: pJsongetNewsResponse " + pJsongetMessageResponse);
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


private JSONObject sendMessage(HttpServletRequest request, RequestConfigRvia pConfigRvia) throws Exception {

	JSONObject pJsonResult = new JSONObject();
	StringBuilder pUrlParameters = new StringBuilder(strDatosSesion);

	String strMessage = request.getParameter("mensajeId");
	String strOffice = "0000";// request.getParameter("OFIALT");
	
	pUrlParameters.append(strAmper).append("mensajeId=").append(strMessage);
	pUrlParameters.append(strAmper).append("OFIALT=").append(strOffice);
	// Datos de sessión necesarios para la conexión con Gestión Comunicados
	pUrlParameters.append(strAmper).append("servidor=").append(pConfigRvia.getNodeRvia());
	pUrlParameters.append(strAmper).append("num_session=").append(pConfigRvia.getRviaSessionId());
	pUrlParameters.append(strAmper).append("USUARIO=").append(pConfigRvia.getRviaUserId());
	pUrlParameters.append(strAmper).append("TIPUSR=").append(pConfigRvia.getIsumUserProfile());
	pUrlParameters.append(strAmper).append("idioma=").append(pConfigRvia.getLanguage());
	pUrlParameters.append(strAmper).append("ENTALT=").append(pConfigRvia.getNRBE());
	pUrlParameters.append(strAmper).append("canal=").append(pConfigRvia.getCanalHost());
	pUrlParameters.append(strAmper).append("IP=").append(pConfigRvia.getIp());
	pUrlParameters.append(strAmper).append("PATH=").append(pConfigRvia.getUriRvia());
	
	pUrlParameters.append("&firmaRSI="+CommunicationUtils.getRsiSign(pConfigRvia.getRviaUserId(), pConfigRvia.getNRBE(), strOffice, pConfigRvia.getIsumUserProfile(), pConfigRvia.getLanguage().name()));
	pUrlParameters.append("&fechaRSI="+CommunicationUtils.getRsiDate());
	// Send post request 
	HttpURLConnection pCon = CommunicationUtils.sendCommunication(strEndpoint, pUrlParameters.toString(), request.getHeader("User-Agent"),pConfigRvia.getNodeRvia());

	int iResponseCode = pCon.getResponseCode();

	pLog.info("Messages ::: restoreMessage ::: sendMessage ::: respuesta del servidor " + iResponseCode);
	switch (iResponseCode) {
		case 200:
			pJsonResult.put(strErrorCode, 0);
			break;
		case 500:
			try {
				String strErrorResponse = CommunicationUtils.convertInputStream(pCon.getErrorStream());
				
				Document pDocumentError = Jsoup.parse(strErrorResponse, "", Parser.htmlParser());
				Element pErrorCode = pDocumentError.getElementById("HD_ERROR");
				pJsonResult.put(strErrorCode, Integer.parseInt((pErrorCode.text().split(":"))[1].replace(" ", "")));
				
				Element pErrorMessage = pDocumentError.getElementsByClass("txtaviso").first();
				pJsonResult.put(strErrorMessage, pErrorMessage.text());
				
			} catch (Exception e){
				pLog.error("Messages ::: restoreMessage ::: sendMessage ::: Exception tratando Error de servidor", e);
				pJsonResult.put(strErrorCode, -505);
				pJsonResult.put(strErrorMessage, "Error no controlador ocurrido en la aplicación");
			}
			break;
		default:
			pLog.error("Messages ::: restoreMessage ::: sendMessage ::: Código de respuesta no contemplado");
			pJsonResult.put(strErrorCode, iResponseCode);
			pJsonResult.put(strErrorMessage, "Error ocurrido en la aplicación");
			break;
	}
	return pJsonResult;
}

public enum MethodType
{
    GET("GET"), POST("POST"), PATCH("PATCH"), PUT("PUT"), DELETE("DELETE");
    
    private final String strMethod;
    
    private MethodType(String strMethod)
    {
    	this.strMethod = strMethod;
    }
        
    public static MethodType byName(String strMethod)
    {
    	for (MethodType s : values())
    	{
			if (s.getMethod().equals(strMethod)) return s;
		}
    	return null;
    }
    
    public String getMethod()
    {
    	return strMethod;
    }
};
%>

