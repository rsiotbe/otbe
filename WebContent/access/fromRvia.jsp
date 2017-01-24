
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="
			org.apache.http.HttpEntity,
			org.apache.http.HttpResponse,
			org.apache.http.client.HttpClient,
			org.apache.http.client.methods.HttpPost,
			org.apache.http.entity.StringEntity,
			org.apache.http.impl.client.HttpClientBuilder,
			org.apache.http.util.EntityUtils,
	        com.rsi.rvia.rest.DDBB.DDBBPoolFactory,
    		com.rsi.rvia.rest.operation.MiqQuests,
    		com.rsi.rvia.rest.session.RequestConfigRvia,
		 	java.sql.Connection, java.net.URL,
		 	java.util.Enumeration,
		 	org.slf4j.Logger,org.slf4j.LoggerFactory,
		 	com.rsi.rvia.rest.tool.AppConfigurationFactory"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
<title></title>
<!-- <script src="/api/static/rviarest/js/iframe/iframeResizer.contentWindow.min.js"></script> -->
</head>
<%
	Logger pLog = LoggerFactory.getLogger("access.jsp");
	MiqQuests pMiqQuests = null;
	String strPathRest = null;
	String strError = "";
	int nMiqQuestId = 0;
	String strIdMiq = request.getParameter("idMiq");
	pLog.info("Se recibe una petición para acceder a la operativa con idMiq "
			+ strIdMiq);
	try {
		nMiqQuestId = Integer.parseInt(strIdMiq);
	} catch (Exception ex) {
		pLog.error("Imposible convertir strIdMiq a Integer, valor de strIdMiq: "
				+ strIdMiq);
		nMiqQuestId = 0; 
	}

	String strToken = request.getParameter("token");
	String strMethod = request.getParameter("method");
	if (strMethod == null)
		strMethod = "GET";
	pLog.trace("Method: " + strMethod);
	pLog.trace("IdMiq: " + strIdMiq);
	pLog.trace("Token: " + strToken);
	
	  String inputs="";
	  Enumeration <String> parameterList = request.getParameterNames();
	  while( parameterList.hasMoreElements() )
	  {
	    String sName = parameterList.nextElement().toString();
	      String[] sMultiple = request.getParameterValues( sName );
	      if( 1 >= sMultiple.length ){
	        out.println("<!-- " + sName + " = " + request.getParameter( sName ) + "-->\n" );
	        inputs=inputs+"<input type='hidden' value='" + request.getParameter( sName ) + "' name='" + sName + "'>";
	      }
	      else{
	        for( int i=0; i<sMultiple.length; i++ ){
	          inputs=inputs+"<input type='hidden' value='" +sMultiple[i] + "' name='" + sName + "'>";
	          out.println("<!-- " + sName + "[" + i + "] = " + sMultiple[i] + "-->\n" );
	        }
	      }
	  }
	
	if (nMiqQuestId > 0) {
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
			strPathRest = "/test/rviaerror";
		}

	} else {
		strError = "1111";
		strPathRest = "/rviaerror";
	}
	
 	String strHost="";
     String entorno = AppConfigurationFactory.getConfiguration().getProperty("env");
     if (entorno.equals("TEST"))
     {         
         // Ip actual de conexión
         RequestConfigRvia RQ = new RequestConfigRvia(request); 
         String strAvtualIp=RQ.getIp();

         // Cliente para consumir lista de ips que se derivarán a localhost
         // .... generando cadena de ips ....
         String strIpsToLocalhost="";
         String strIpsListToLocalhostFile = "http://localhost:8008/api/static/mock/localhostRedirect.json";
         HttpPost httpPost = new HttpPost(strIpsListToLocalhostFile);
         HttpClient httpClient = HttpClientBuilder.create().build();
         HttpResponse resp = httpClient.execute(httpPost);
         HttpEntity entity = resp.getEntity();         
         if (entity != null)
         {
             strIpsToLocalhost = EntityUtils.toString(entity);
         }                
         // Buscamos la ip actual en el fichero de ips que derivarán a localhost          
         if(strIpsToLocalhost.indexOf(strAvtualIp) != -1){
             strHost="http://localhost:8080";
         }
     }	
%>
<body>
	<form id="formRedirect" action="<%=strHost%>/api/rest<%=strPathRest%>" method="<%=strMethod%>" enctype="multipart/form-data">
		<%=inputs%> 
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