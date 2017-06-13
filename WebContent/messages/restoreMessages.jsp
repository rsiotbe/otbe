<%@page import="com.rsi.rvia.rest.conector.RestRviaConnector"%>
<%@page import="com.rsi.rvia.rest.session.RequestConfigRvia"%>
<%@page import="org.jsoup.select.Elements"%>
<%@page import="org.jsoup.nodes.Element"%>
<%@page import="org.jsoup.parser.Parser"%>
<%@page import="org.jsoup.Jsoup"%>
<%@page import="org.jsoup.nodes.Document"%>
<%@page import="com.rsi.rvia.rest.userCommunication.CommunicationUtils"%>
<%@page import="java.util.Date"%>
<%@page import="java.net.HttpURLConnection"%>
<%@page import="java.net.URL"%>
<%@page import="org.json.JSONException"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>
<%@page import="com.rsi.rvia.rest.tool.Utils"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%@page import="org.slf4j.Logger"%>
<%
	pLog.info("Messages ::: restoreMessage ::: Start");
	JSONObject pJsonResponse = new JSONObject();
	response.setHeader("content-type", "application/json");

	String strCodNrbe =  request.getParameter("codNrbe");
	
	JSONObject pJsonResult = null;
	try {
		pJsonResult = sendMessage(request);
		if (pJsonResult.getInt(strErrorCode) == 0) {
			pJsonResponse.put("data", pJsonResult);
			%><%=Utils.generateWSResponseJsonOk("restoreMessage", pJsonResponse.toString())%><%
		} else {
			%><%=Utils.generateWSResponseJsonError("restoreMessage", pJsonResult.getInt(strErrorCode), pJsonResult.getString(strErrorMessage))%><%
		}
    } catch (Exception e){
		pLog.error("Messages ::: restoreMessage ::: Exception", e);%>
		<%=Utils.generateWSResponseJsonError("restoreMessage", -1, strDefaultErrorMessage)%>
  <%}%>
<%!
private static final String strDefaultErrorMessage  = "Error no controlado";
private static final String strErrorCode  			= "returnCode";
private static final String strErrorMessage  		= "errorMessage";
private static final String strAmper		  		= "&";
private static final String strComa			  		= ",";
// Datos necesarios para la petición pero que pueden ir vacios.
private static final String strDatosSesion 			= "CODIGO_APP=GCO&PATH=&IP=&num_session=&CLIENTE_EMPRESA=&PERCON=&SELCON=&PRITAR=&servidor=&NUMTAR=&marca=";
private Logger pLog = LoggerFactory.getLogger("restoreMessage.jsp");
private final String strEndpoint = "restaurarMensaje.do";

private JSONObject sendMessage(HttpServletRequest request) throws Exception {

	RequestConfigRvia pConfigRvia =  (RequestConfigRvia)RequestConfigRvia.getRequestConfig(request, null);
	pLog.info("Messages ::: restoreMessage ::: sendMessage ::: Hay parametros de sesión");
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
	pUrlParameters.append(strAmper).append("PATH=").append(RestRviaConnector.getRuralviaAddress(pConfigRvia.getNodeRvia()));
	
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

%>

