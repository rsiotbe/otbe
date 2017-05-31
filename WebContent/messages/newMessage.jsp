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
	pLog.info("Messages ::: newMessage ::: Start");
	JSONObject pJsonResponse = new JSONObject();
	response.setHeader("content-type", "application/json");
	
	JSONObject pJsonResult = null;
	try {
		pJsonResult = sendMessage(request);
		if (pJsonResult.getInt(strErrorCode) == 0) {
			pJsonResponse.put("data", pJsonResult);
			%><%=Utils.generateWSResponseJsonOk("newMessage", pJsonResponse.toString())%><%
		} else {
			%><%=Utils.generateWSResponseJsonError("newMessage", pJsonResult.getInt(strErrorCode), pJsonResult.getString(strErrorMessage))%><%
		}
    } catch (Exception e){
		pLog.error("Messages ::: newMessage ::: Exception", e);%>
		<%=Utils.generateWSResponseJsonError("newMessage", -1, strDefaultErrorMessage)%>
  <%}%>
<%!
private static final String strDefaultErrorMessage  = "Error no controlado";
private static final String strErrorCode  			= "returnCode";
private static final String strErrorMessage  		= "errorMessage";
private static final String strAmper		  		= "&";
// Datos necesarios para la petición pero que pueden ir vacios.
private static final String strDatosSesion 			= "CODIGO_APP=GCO&PATH=&IP=&num_session=&CLIENTE_EMPRESA=&PERCON=&SELCON=&PRITAR=&servidor=&NUMTAR=&marca=";
private Logger pLog = LoggerFactory.getLogger("newMessage.jsp");
private final String strEndpoint = "altaMensaje.do";

private JSONObject sendMessage(HttpServletRequest request) throws Exception {

	RequestConfigRvia pConfigRvia = new RequestConfigRvia(request);
	JSONObject pJsonResult = new JSONObject();
	StringBuilder pUrlParameters = new StringBuilder(strDatosSesion);
	
	String strClMensajeId = request.getParameter("clMensajeId");
	String strAsunto = request.getParameter("asunto");
	String strTextoOculto = request.getParameter("textoOculto");
	String strTextClob = request.getParameter("textClob");
	String strOffice = "0000";// request.getParameter("OFIALT");
	
	JSONObject pValidacion = validateInput(strClMensajeId, strAsunto, strTextClob);
	
	if (pValidacion.getInt(strErrorCode) != 0)
		return pValidacion;
	
	pUrlParameters.append(strAmper).append("clMensajeId=").append(strClMensajeId);
	pUrlParameters.append(strAmper).append("asunto=").append(strAsunto);
	pUrlParameters.append(strAmper).append("textoOculto=").append(strTextoOculto);
	pUrlParameters.append(strAmper).append("textClob=").append(strTextClob);
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
	pLog.info("Messages ::: restoreMessage ::: newMessage ::: respuesta del servidor " + iResponseCode);
	
	switch (iResponseCode) {
		case 200:

			String strResponse = CommunicationUtils.convertInputStream(pCon.getInputStream());
			
			Document pDocument = Jsoup.parse(strResponse, "", Parser.htmlParser());
			Element pResult = pDocument.select("input[name=paginaVista]").first();
			pLog.info("Messages ::: newMessage ::: newMessage ::: pResult " + pResult.toString());
			if (!pResult.toString().contains("listarMensajesEnviados")) {
				pJsonResult.put(strErrorCode, -200);
				pJsonResult.put(strErrorMessage, "Error no controlado");
			} else {
				pJsonResult.put(strErrorCode, 0);
			}
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
				pLog.error("Messages ::: newMessage ::: sendMessage ::: Exception tratando Error de servidor", e);
				pJsonResult.put(strErrorCode, -505);
				pJsonResult.put(strErrorMessage, "Error no controlador ocurrido en la aplicación");
			}
			break;
		default:
			pLog.error("Messages ::: newMessage ::: sendMessage ::: Código de respuesta no contemplado");
			pJsonResult.put(strErrorCode, iResponseCode);
			pJsonResult.put(strErrorMessage, "Error ocurrido en la aplicación");
			break;
	}
	return pJsonResult;
}

private JSONObject validateInput(String strClMensajeId, String strAsunto, String strTextClob) throws JSONException{
	JSONObject pJsonResult = new JSONObject();
	pLog.debug("Messages ::: newMessage ::: validateInput ::: Error de validación ");
	try {
		pJsonResult.put(strErrorCode, 0);
		
		if (strClMensajeId == null || strClMensajeId.isEmpty()){
			pJsonResult.put(strErrorCode, -5);
			pJsonResult.put(strErrorMessage, "El mensaje debe tener un tipo");
			return pJsonResult;
		}
	
		if (strAsunto == null || strAsunto.isEmpty()){
			pJsonResult.put(strErrorCode, -6);
			pJsonResult.put(strErrorMessage, "El mensaje debe tener un asunto");
			return pJsonResult;
		}
	
		if (strTextClob == null || strTextClob.isEmpty()){
			pJsonResult.put(strErrorCode, -7);
			pJsonResult.put(strErrorMessage, "El mensaje no puede estar vacio");
			return pJsonResult;
		}
	}catch (JSONException e){
		pLog.error("Messages ::: newMessage ::: validateInput ::: Error de validación ", e);
		throw e;
	}
	return pJsonResult;
}
%>

