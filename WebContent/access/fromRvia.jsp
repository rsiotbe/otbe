
<%@page import="org.glassfish.jersey.internal.util.ExceptionUtils"%>
<%@page import="org.json.JSONArray"%>
<%@page import="org.json.JSONObject"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="org.apache.http.HttpEntity,
			org.apache.http.HttpResponse,
			org.apache.http.client.HttpClient,
			org.apache.http.client.methods.HttpPost,
			org.apache.http.entity.StringEntity,
			org.apache.http.impl.client.HttpClientBuilder,
			org.apache.http.util.EntityUtils,
	        com.rsi.rvia.rest.DDBB.DDBBPoolFactory,
	        com.rsi.rvia.rest.DDBB.DDBBPoolFactory,
    		com.rsi.rvia.rest.operation.MiqQuests,
    		com.rsi.rvia.rest.session.RequestConfigRvia,
		 	java.sql.Connection, java.net.URL,
		 	java.util.*,
		 	org.slf4j.Logger,org.slf4j.LoggerFactory,
		 	com.rsi.rvia.rest.tool.AppConfiguration,
		 	com.rsi.rvia.rest.session.RequestConfigRvia,
		 	org.json.JSONObject,
		 	javax.ws.rs.client.WebTarget,
		 	javax.ws.rs.core.Response,
		 	javax.ws.rs.core.UriBuilder,
		 	javax.ws.rs.client.Client,
		 	com.rsi.rvia.rest.client.RviaRestHttpClient
		 	"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
<title></title>
<!-- <script src="/api/static/rviarest/js/iframe/iframeResizer.contentWindow.min.js"></script> -->
</head>
<%
	Logger pLog = LoggerFactory.getLogger("fromRvia.jsp");

	MiqQuests pMiqQuests = null;
	String strPathRest = null;
	String strError = "";
	int nMiqQuestId = 0;
	String strIdMiq = null;
	String strMethod = null;
	String strData = null;
	String strInputs = "";
	boolean fToLocalhost = false;
	boolean fLocalhostHttps = false;
	String strLocalhostPort = "443";
	String strFinalUrl;
	
	/* se carga el array con los parámetros que no se desean enviar a rviarest */
	ArrayList<String> alParamToNotForwarding = new ArrayList<String>();
	alParamToNotForwarding.add("method");
	alParamToNotForwarding.add("codigo");
	alParamToNotForwarding.add("toLocalhost");
	alParamToNotForwarding.add("localhostHttps");
	alParamToNotForwarding.add("localhostPort");
	alParamToNotForwarding.add("idMiq");
	alParamToNotForwarding.add("token"); // antiguo token de acceso

	strIdMiq = request.getParameter("idMiq");
	pLog.info("Se recibe una petición para acceder a la operativa con idMiq " + strIdMiq);
	try 
	{
		nMiqQuestId = Integer.parseInt(strIdMiq);
	} catch (Exception ex) 
	{
		pLog.error("Imposible convertir strIdMiq a Integer, valor de strIdMiq: " + strIdMiq);
		throw new Exception("Error al procesar la petición. IdMiq no tiene un valor válido");
	}
	strMethod = request.getParameter("method");
	if (strMethod == null)
		strMethod = "GET";
	
	/* se comprueba si es necesario redirigir la petición a localhost */
	if((request.getParameter("toLocalhost")!= null) && ("on".equals(request.getParameter("toLocalhost"))))
		fToLocalhost = true;
	if(request.getParameter("localhostPort")!= null && !(request.getParameter("localhostPort").trim().isEmpty()))
	    strLocalhostPort = request.getParameter("localhostPort");
	if((request.getParameter("localhostHttps")!= null) && ("on".equals(request.getParameter("localhostHttps"))))
	    fLocalhostHttps = true;
	if(request.getParameter("data")!= null && !(request.getParameter("data").trim().isEmpty()))
	    strData = request.getParameter("data");

	pLog.trace("Method: " + strMethod);
	pLog.trace("IdMiq: " + strIdMiq);
	pLog.trace("Data: " + strData);
	pLog.trace("ToLocalhost: " + fToLocalhost);
	pLog.trace("LocalhostPort: " + strLocalhostPort);
	pLog.trace("LocalhostHttps: " + fLocalhostHttps);
	
	/* se componen los campos input de fomrulario para enviar la petición */
	Enumeration <String> pEnumParams = request.getParameterNames();
	while(pEnumParams.hasMoreElements())
	{
		String strParamName = pEnumParams.nextElement().toString();
		String[] astrValues = request.getParameterValues(strParamName);
		if(astrValues.length > 1)
		{
			pLog.warn("El parámetro: " + strParamName + "tiene más de un valor, se coge solo el primero");
		}
	    String strValue = "";;
	    if(astrValues.length == 1 && astrValues[0] != null)
	    {
	        strValue = astrValues[0];
	    }
		pLog.trace("Se procesa el parámetro: " + strParamName + "=" + strValue);
	    /* se comprueba si este parámetro se debe descartar */
	    if(alParamToNotForwarding.contains(strParamName))
	    {
			pLog.trace("El parámetro " + strParamName + " se descarta para enviarse a rviarest");
	        continue;
	    }
		out.println("<!-- " + strParamName + " = " + strValue + "-->");
		strInputs += "<input type=\"hidden\" name=\"" + strParamName + "\" value=\"" + strValue + "\">\n";
	}
	pMiqQuests = MiqQuests.getMiqQuests(nMiqQuestId);
	if(pMiqQuests != null)
	{
		strPathRest = pMiqQuests.getPathRest();
		pLog.info("Operación a jecutar: " + pMiqQuests.toString());
	}
	else
	{
		pLog.error("No se ha recuperado un objeto MiqQuest valido para el id: " + strIdMiq);
		throw new Exception("Error al procesar la petición. MiqQuests no válido");
	}

	/* se comprueba si es necesario redirigir a la máquina local */
 	String strHost = "";
    if (fToLocalhost)
    {         
        if(fLocalhostHttps)
        	strHost = "https://localhost:" + strLocalhostPort;
        else
        	strHost = "http://localhost:" + strLocalhostPort;
        pLog.info("Se procesa la petición apuntando a localhost, puerto " + strLocalhostPort);
    }
    strFinalUrl = strHost + "/api/rest" + strPathRest;
    pLog.info("Dirección final del iframe: " + strFinalUrl);
    
    /* se llama a generar el token JWT de acceso */
    String strNode = request.getParameter("node");
    String strRviaSession = request.getParameter("RVIASESION");
    String strIsumServiceId = request.getParameter("isumServiceId");
    pLog.info("Se procede a generar el token JWT de sesión");
    pLog.info("node:          " + strNode);
    pLog.info("RVIASESION:    " + strRviaSession);
    pLog.info("isumServiceId: " + strIsumServiceId);
    if( (strNode == null || strNode.trim().isEmpty()) || (strNode == null || strNode.trim().isEmpty()) || (strNode == null || strNode.trim().isEmpty()))
    {
		pLog.error("Faltan parámetros para gnerar el token de sesión");
		throw new Exception("Error al procesar la petición. Imposible crear JWT");       
    }
    String strUrlJWT = strHost + "/api/rest/rviasession/login?node=" + strNode + "&RVIASESION=" + strRviaSession + "&isumServiceId=" + strIsumServiceId;
    /* se proceas la peticicón de JWT */
    Client pClient = RviaRestHttpClient.getClient();
	WebTarget pTarget = pClient.target(UriBuilder.fromUri(strUrlJWT).build());
    Response pResponse = pTarget.request().get();
	pLog.info("Token JWT generado. Respuesta:" + pResponse.readEntity(String.class));
	if(pResponse.getStatus() != 200)
	{
		pLog.error("Error en la petición de token JWT");
		throw new Exception("Error al procesar la petición. Imposible generar el token JWT");           
	}
%>
<body>
	<form id="formRedirect" action="<%=strFinalUrl%>" method="<%=strMethod%>" enctype="multipart/form-data">
		<%=strInputs%> 
<%if(!strError.trim().isEmpty()){%><input type="hidden" name="errorCode" value="<%=strError%>"><%}%>		
	</form>
	<script type="text/javascript">	
		document.getElementById('formRedirect').submit();
	</script>
</body>
</html>