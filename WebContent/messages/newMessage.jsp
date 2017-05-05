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

	String strCodNrbe =  request.getParameter("codNrbe");
	
	JSONObject pJsonResult = null;
	try {
		pJsonResult = sendMessage(request);
		if (pJsonResult.getInt(strErrorCode) == 0) {
			pJsonResponse.put("data", pJsonResult);
			%><%=Utils.generateWSResponseJsonOk("newMessage", pJsonResponse.toString())%><%
		} else {
			%><%=Utils.generateWSResponseJsonError("newMessage", pJsonResult.getInt(strErrorCode), pJsonResult.getString(strErrorMessage))%><%
		}
    } catch (Exception e){%>
		<%=Utils.generateWSResponseJsonError("newMessage", -1, strDefaultErrorMessage)%>
  <%}%>
<%!
private static final String strDefaultErrorMessage  = "Error no controlado";
private static final String strErrorCode  			= "returnCode";
private static final String strErrorMessage  		= "errorMessage";
private static final String strAmper		  		= "&";
private Logger pLog = LoggerFactory.getLogger("newMessage.jsp");
private final String strEndpoint = "altaMensaje.do";

private JSONObject sendMessage(HttpServletRequest request) throws Exception {

	JSONObject pJsonResult = new JSONObject();
	StringBuilder pUrlParameters = new StringBuilder("CODIGO_APP=GCO&ENTALT=198&OFIALT=900&USUARIO=03030300&TIPUSR=3&NUMTAR=198030300&idioma=es_ES&canal=000003&marca=0000&PATH=%2Fportal_rvia%2FServletDirectorPortal%3BRVIASESION%3DVcSqdOseUZ11oa1HZDa9XCkQ6O10UxrCnAid8T2vRHkouS9qj0V-%21-1324441015%21-1738423937&IP=10.1.243.186&num_session=21781507&CLIENTE_EMPRESA=P&PERCON=005&SELCON=00000000002021262692&PRITAR=198052445&servidor=Internet_el91teswls01_07");
	//NODE("node"), servidor
	//RVIASESION("RVIASESION"), num_session
	//RVIAUSERID("rviaUserId"), USUARIO
	//ISUMUSERPROFILE("isumUserProfile"), TIPUSR
	//ISUMSERVICEID("isumServiceId"),
	//LANG("lang"), idioma
	//NRBE("NRBE"),ENTALT
	//CANALAIX("canalAix"), 
	//CANAL("canal"), canal
	//IP("ip"); IP
	/*
	 OFIALT=900&
	 CLIENTE_EMPRESA=P&
	 PERCON=005&
	 SELCON=00000000002021262692&
	 PRITAR=198052445&
	 marca=0000&
	 PATH=%2Fportal_rvia%2FServletDirectorPortal%3BRVIASESION%3DVcSqdOseUZ11oa1HZDa9XCkQ6O10UxrCnAid8T2vRHkouS9qj0V-%21-1324441015%21-1738423937&
	*/
    
	String clMensajeId = request.getParameter("clMensajeId");
	String asunto = request.getParameter("asunto");
	String textoOculto = request.getParameter("textoOculto");
	String textClob = request.getParameter("textClob");
	
	pUrlParameters.append(strAmper).append("clMensajeId=").append(clMensajeId);
	pUrlParameters.append(strAmper).append("asunto=").append(asunto);
	pUrlParameters.append(strAmper).append("textoOculto=").append(textoOculto);
	pUrlParameters.append(strAmper).append("textClob=").append(textClob);
	
	pUrlParameters.append("&firmaRSI="+CommunicationUtils.getRsiSign("03030300", "198", "900", "3", "es_ES"));
	pUrlParameters.append("&fechaRSI="+CommunicationUtils.getRsiDate());
	// Send post request 
	HttpURLConnection pCon = CommunicationUtils.sendCommunication(strEndpoint, pUrlParameters.toString(), request.getHeader("User-Agent"));

	int responseCode = pCon.getResponseCode();
	
	switch (responseCode) {
		case 200:

			String strResponse = CommunicationUtils.convertInputStream(pCon.getInputStream());
			
			Document pDocument = Jsoup.parse(strResponse, "", Parser.htmlParser());
			System.out.println(pDocument.text());
			Element pResult = pDocument.select("input[name=paginaVista]").first();
			System.out.println(pResult.toString());
			if (!pResult.toString().contains("listarMensajesEnviados")) {
				pJsonResult.put(strErrorCode, -200);
				pJsonResult.put(strErrorMessage, "Error no controlado");
			} else {
				pJsonResult.put(strErrorCode, 0);
			}
			break;
		case 302:
			pJsonResult.put(strErrorCode, -32);
			pJsonResult.put(strErrorMessage, "Error ocurrido en la aplicación");
			break;
		case 500:
			try {
				String strErrorResponse = CommunicationUtils.convertInputStream(pCon.getErrorStream());
				
				Document pDocumentError = Jsoup.parse(strErrorResponse, "", Parser.htmlParser());
				Element pErrorCode = pDocumentError.getElementById("HD_ERROR");
				System.out.println(pErrorCode.text());
				pJsonResult.put(strErrorCode, Integer.parseInt((pErrorCode.text().split(":"))[1].replace(" ", "")));
				
				Element pErrorMessage = pDocumentError.getElementsByClass("txtaviso").first();
				System.out.println(pErrorMessage.text());
				pJsonResult.put(strErrorMessage, pErrorMessage.text());
				
			} catch (Exception e){
				pJsonResult.put(strErrorCode, -505);
				pJsonResult.put(strErrorMessage, "Error no controlador ocurrido en la aplicación");
			}
			break;
		default:
			pJsonResult.put(strErrorCode, responseCode);
			pJsonResult.put(strErrorMessage, "Error ocurrido en la aplicación");
			break;
	}
	return pJsonResult;
}
%>

