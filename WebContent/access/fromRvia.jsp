
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
		 	org.json.JSONObject"%>
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
	String strToken = null;
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

	strIdMiq = request.getParameter("idMiq");
	pLog.info("Se recibe una petición para acceder a la operativa con idMiq " + strIdMiq);
	try 
	{
		nMiqQuestId = Integer.parseInt(strIdMiq);
	} catch (Exception ex) 
	{
		pLog.error("Imposible convertir strIdMiq a Integer, valor de strIdMiq: " + strIdMiq);
		nMiqQuestId = 0; 
	}
	strToken = request.getParameter("token");
	strMethod = request.getParameter("method");
	
	/* se comprueba si es necesario redirigir la petición a localhost */
	if((request.getParameter("toLocalhost")!= null) && ("on".equals(request.getParameter("toLocalhost"))))
		fToLocalhost = true;
	if(request.getParameter("localhostPort")!= null && !(request.getParameter("localhostPort").trim().isEmpty()))
	    strLocalhostPort = request.getParameter("localhostPort");
	if((request.getParameter("localhostHttps")!= null) && ("on".equals(request.getParameter("localhostHttps"))))
	    fLocalhostHttps = true;
	if(request.getParameter("data")!= null && !(request.getParameter("data").trim().isEmpty()))
	    strData = request.getParameter("data");

	if (strMethod == null)
		strMethod = "GET";
	pLog.trace("Method: " + strMethod);
	pLog.trace("IdMiq: " + strIdMiq);
	pLog.trace("Token: " + strToken);
	pLog.trace("Data: " + strData);
	pLog.trace("ToLocalhost: " + fToLocalhost);
	pLog.trace("LocalhostPort: " + strLocalhostPort);
	pLog.trace("LocalhostHttps: " + fLocalhostHttps);
	
	Enumeration <String> pEnumParams = request.getParameterNames();
	while(pEnumParams.hasMoreElements())
	{
		String strParamName = pEnumParams.nextElement().toString();
		String[] astrValues = request.getParameterValues(strParamName);
		if(astrValues.length <=1)
		{
		    String strValue = "";;
		    if(astrValues.length == 1 && astrValues[0] != null)
		    {
		        strValue = astrValues[0];
		    }
			pLog.trace("Se procesa el parámetro: " + strParamName + "=" + strValue);
		    
		    if(alParamToNotForwarding.contains(strParamName))
		    {
				pLog.trace("El parámetro " + strParamName + " se descarta para enviarse a rviarest");
		        continue;
		    }
			out.println("<!-- " + strParamName + " = " + strValue + "-->" );
			strInputs += "<input type=\"hidden\" name=\"" + strParamName + "\" value=\"" + strValue + ">\"\n";
		}
		else
		{
			pLog.trace("Se procesa el parámetro: " + strParamName + "=" + astrValues);
			for( int i=0; i < astrValues.length; i++ )
			{
				out.println("<!-- " + strParamName + "[" + i + "] = " + astrValues[i] + "-->" );
				strInputs=strInputs+"<input type=\"hidden\" value=\"" +astrValues[i] + "\" name=\"" + strParamName + "\">\n";
			}
		}
	}
	if (nMiqQuestId > 0) 
	{
		pMiqQuests = MiqQuests.getMiqQuests(nMiqQuestId);
		if(pMiqQuests != null)
		{
			strPathRest = pMiqQuests.getPathRest();
			pLog.info("Datos de la operativa: " + pMiqQuests.toString());
		}
		else
		{
			pLog.error("No se ha recuperado un objeto MiqQuest valido para el id: " + strIdMiq);
			strError = "1111";
			strPathRest = "/rviaerror";
		}

	} else {
		strError = "1111";
		strPathRest = "/rviaerror";
	}
	
	/* se comprueba si es necesario redirigir a la máquina local */
 	String strHost = "";
    if (fToLocalhost)
    {         
    	/* se recupera la ip del usuairo que genra la invoación para comprobar si está en la lista de ips redireccionables */
        RequestConfigRvia pRequestConfigRvia;
        String strUserIp;
        JSONObject pConfig;
        HttpPost pHttpPost;
        HttpClient pHttpClient;
        HttpResponse pHttpResponse;
        HttpEntity pHttpEntity; 
        String strContent = null;
        pRequestConfigRvia = new RequestConfigRvia(request); 
        strUserIp = pRequestConfigRvia.getIp();
        if(fLocalhostHttps)
        	strHost = "https://localhost:" + strLocalhostPort;
        else
        	strHost = "http://localhost:" + strLocalhostPort;
        pLog.info("Se procesa la petición apuntando a localhost, puerto " + strLocalhostPort + " apuntando a la máquina " + strUserIp);
    }
    strFinalUrl = strHost + "/api/rest" + strPathRest;
    pLog.info("Dirección final del iframe: " + strFinalUrl);
%>
<body>
	<form id="formRedirect" action="<%=strFinalUrl%>" method="<%=strMethod%>" enctype="multipart/form-data">
		<%=strInputs%> 
<%
	if(!strError.trim().isEmpty())
	{
%>
		<input type="hidden" name="errorCode" value="<%=strError%>">
<%
	}
%>		
	</form>
	<script type="text/javascript">	
		document.getElementById('formRedirect').submit();
	</script>
</body>
</html>