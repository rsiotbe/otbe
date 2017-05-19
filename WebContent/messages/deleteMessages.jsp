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
	pLog.info("Messages ::: deleteMessage ::: Start");
	JSONObject pJsonResponse = new JSONObject();
	response.setHeader("content-type", "application/json");

	String strCodNrbe =  request.getParameter("codNrbe");
	
	JSONObject pJsonResult = null;
	try {
		pJsonResult = sendMessage(request);
		if (pJsonResult.getInt(strErrorCode) == 0) {
			pJsonResponse.put("data", pJsonResult);
			%><%=Utils.generateWSResponseJsonOk("deleteMessage", pJsonResponse.toString())%><%
		} else {
			%><%=Utils.generateWSResponseJsonError("deleteMessage", pJsonResult.getInt(strErrorCode), pJsonResult.getString(strErrorMessage))%><%
		}
    } catch (Exception e){
		pLog.error("Messages ::: deleteMessage ::: Exception", e);%>
		<%=Utils.generateWSResponseJsonError("deleteMessage", -1, strDefaultErrorMessage)%>
  <%}%>
<%!
private static final String strDefaultErrorMessage  = "Error no controlado";
private static final String strErrorCode  			= "returnCode";
private static final String strErrorMessage  		= "errorMessage";
private static final String strAmper		  		= "&";
private static final String strComa			  		= ",";
// Datos necesarios para la petición pero que pueden ir vacios.
private static final String strDatosSesion 			= "CODIGO_APP=GCO&PATH=&IP=&num_session=&CLIENTE_EMPRESA=&PERCON=&SELCON=&PRITAR=&servidor=&NUMTAR=&marca=";
private Logger pLog = LoggerFactory.getLogger("deleteMessage.jsp");
private final String strEndpoint = "eliminarMensaje.do";

private JSONObject sendMessage(HttpServletRequest request) throws Exception {

	RequestConfigRvia pConfigRvia = new RequestConfigRvia(request);
	pLog.info("Messages ::: deleteMessage ::: sendMessage ::: Hay parametros de sesión");
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
	pLog.info("Messages ::: deleteMessage ::: sendMessage ::: Respuesta del servidor " + iResponseCode);
	
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
				pLog.error("Messages ::: deleteMessage ::: sendMessage ::: Exception tratando Error de servidor", e);
				pJsonResult.put(strErrorCode, -505);
				pJsonResult.put(strErrorMessage, "Error no controlador ocurrido en la aplicación");
			}
			break;
		default:
			pLog.error("Messages ::: deleteMessage ::: sendMessage ::: Código de respuesta no contemplado");
			pJsonResult.put(strErrorCode, iResponseCode);
			pJsonResult.put(strErrorMessage, "Error ocurrido en la aplicación");
			break;
	}
	return pJsonResult;
}

%>

