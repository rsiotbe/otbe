<%@page import="com.rsi.rvia.rest.userCommunication.CommunicationUtils"%>
<%@page import="java.util.Date"%>
<%@page import="java.security.NoSuchAlgorithmException"%>
<%@page import="java.security.MessageDigest"%>
<%@page import="java.io.DataOutputStream"%>
<%@page import="java.net.HttpURLConnection"%>
<%@page import="java.net.URL"%>
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
	pLog.info("Messages ::: newMessage ::: Start");
	JSONObject pJsonResponse = new JSONObject();
	response.setHeader("content-type", "application/json");

	String strCodNrbe =  request.getParameter("codNrbe");
	
	JSONObject pJsonResult = null;
	try {
		pJsonResult = sendMessage();
	} catch (Exception e){
		%><%=Utils.generateWSResponseJsonError("newMessage", strErrorCode, strErrorMessage)%><%
	}
	pJsonResponse.put("data", pJsonResult);
	%><%=Utils.generateWSResponseJsonOk("newMessage", pJsonResponse.toString())%>
<%!
private final String USER_AGENT = "Mozilla/5.0";
private int strErrorCode        = -1;
private String strErrorMessage  = "errorMessage";
private Logger pLog = LoggerFactory.getLogger("newMessage.jsp");
private final String strEndpoint = "altaMensaje.do";

private JSONObject sendMessage() throws Exception{

	String strUrlParameters = "CODIGO_APP=GCO&ENTALT=198&OFIALT=900&USUARIO=03030300&TIPUSR=3&NUMTAR=198030300&idioma=es_ES&canal=000003&marca=0000&PATH=%2Fportal_rvia%2FServletDirectorPortal%3BRVIASESION%3DVcSqdOseUZ11oa1HZDa9XCkQ6O10UxrCnAid8T2vRHkouS9qj0V-%21-1324441015%21-1738423937&IP=10.1.243.186&num_session=21781507&CLIENTE_EMPRESA=P&PERCON=005&SELCON=00000000002021262692&PRITAR=198052445&servidor=Internet_el91teswls01_07&entornoRVIA=TEST&clMensajeId=5&asunto=Probando Rest&textoOculto=&textClob=%3Cdiv+style%3D%22text-align%3A+center%3B%22%3EUna+m%E1aass%3C%2Fdiv%3E";
	strUrlParameters += "&firmaRSI="+CommunicationUtils.getRsiSign("03030301", "198", "900", "3", "es_ES");
	strUrlParameters += "&fechaRSI="+CommunicationUtils.getRsiDate();
	// Send post request 
	HttpURLConnection pCon = CommunicationUtils.sendCommunication(strEndpoint, strUrlParameters, USER_AGENT);

	int responseCode = pCon.getResponseCode();
	
	BufferedReader in;
	switch (responseCode) {
		case 200:
	
			in = new BufferedReader(new InputStreamReader(pCon.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
				System.out.println(inputLine);
			}
			in.close();
	
			System.out.println(response.toString());
			break;
		case 302:
			strErrorCode = -32;
			strErrorMessage = "Error ocurrido en la aplicación";
			throw new Exception("Error de aplicación");
		case 500:
			in = new BufferedReader(new InputStreamReader(pCon.getErrorStream()));
			strErrorCode = -505;
			while ((inputLine = in.readLine()) != null) {
				System.out.println(inputLine);
			}
			in.close();
			strErrorMessage = "Error no controlado ocurrido en la aplicación";
			throw new Exception("Error de aplicación");
		default:
			break;
	}
	
	return null;
}
%>

