
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
		 	java.util.Enumeration,
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
	String URL_SERVER_2_LOCAL = "http://lnxntf05:8008/api/static/mock/localhostRedirect.json";

	MiqQuests pMiqQuests = null;
	String strPathRest = null;
	String strError = "";
	int nMiqQuestId = 0;
	String strIdMiq = null;
	String strToken = null;
	String strMethod = null;
	String strInputs = "";

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
	if (strMethod == null)
		strMethod = "GET";
	pLog.trace("Method: " + strMethod);
	pLog.trace("IdMiq: " + strIdMiq);
	pLog.trace("Token: " + strToken);
	

	Enumeration <String> enumParams = request.getParameterNames();
	while(enumParams.hasMoreElements())
	{
		String strParamName = enumParams.nextElement().toString();
		String[] sMultiple = request.getParameterValues(strParamName);
		if(1 >= sMultiple.length)
		{
			out.println("<!-- " + strParamName + " = " + request.getParameter(strParamName) + "-->\n" );
			strInputs += "<input type='hidden' value='" + request.getParameter(strParamName) + "' name='" + strParamName + "'>\n";
		}
		else
		{
			for( int i=0; i<sMultiple.length; i++ )
			{
				out.println("<!-- " + strParamName + "[" + i + "] = " + sMultiple[i] + "-->\n" );
				strInputs=strInputs+"<input type='hidden' value='" +sMultiple[i] + "' name='" + strParamName + "'>\n";
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
    String entorno = AppConfiguration.getInstance().getProperty("env");
    if (entorno.equals("TEST"))
    {         
    	pLog.info("Al estar en el entorno de TEST se entra a comprobar si es necesario redirir la petición a localhost");

    	/* se recupera la ip del usuairo que genra la invoación para comprobar si está en la lista de ips redireccionables */
        RequestConfigRvia pRequestConfigRvia;
        String strUserIp;
        JSONObject pConfig;
        HttpPost pHttpPost;
        HttpClient pHttpClient;
        HttpResponse pHttpResponse;
        HttpEntity pHttpEntity; 
        String strContent = null;
        try
        {
	        pRequestConfigRvia = new RequestConfigRvia(request); 
	        strUserIp = pRequestConfigRvia.getIp();
	    	pLog.info("Ip del usuario: " + strUserIp);
	    	pLog.info("Se contrasta la IP con la configuración leida en: " + URL_SERVER_2_LOCAL);
	      	pHttpPost = new HttpPost(URL_SERVER_2_LOCAL);
	     	pHttpClient = HttpClientBuilder.create().build();
	     	pHttpResponse = pHttpClient.execute(pHttpPost);
	     	pHttpEntity = pHttpResponse.getEntity();        
	     	strContent = EntityUtils.toString(pHttpEntity);
	        pConfig = new JSONObject(strContent);
	       	pLog.info("Configuración leida:" + pConfig.toString());
            JSONArray aIps = pConfig.getJSONArray("ips");
            for (int i =0; i < aIps.length();i++)
            {
               JSONObject pItem = (JSONObject)aIps.get(i);
               String strIp = pItem.getString("ip");
               if(strIp.equals(strUserIp))
               {
                   strHost = pItem.getString("redirect");
               	   pLog.info("Se detecta la configuración de redirección y se redige la peteción a: " + strHost + "/api/rest" + strPathRest);
                   break;
               }               
           } 
           if(strHost.isEmpty())
           		pLog.info("No se detecta ninguna redirección se continua con el path relativo: " + "/api/rest" + strPathRest);
               
        }
        catch(Exception ex)
        {
            pLog.error("Error al compronar la redirección a localhost desde TEST", ex);
            throw new Exception("Error al comprobar la redirección a localhost desde TEST", ex);
        }
    }
%>
<body>
	<form id="formRedirect" action="<%=strHost%>/api/rest<%=strPathRest%>" method="<%=strMethod%>" enctype="multipart/form-data">
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