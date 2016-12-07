<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="com.rsi.rvia.rest.DDBB.DDBBPoolFactory,
    		com.rsi.rvia.rest.operation.MiqQuests,
		 	java.sql.Connection, java.net.URL,
		 	java.util.Enumeration,
		 	org.slf4j.Logger,org.slf4j.LoggerFactory"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
<title></title>
<script src="/api/static/rviarest/js/iframe/iframeResizer.contentWindow.min.js"></script>
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
	  Enumeration parameterList = request.getParameterNames();
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
%>
<body>
	<form id="formRedirect" action="/api/rest<%=strPathRest%>" method="<%=strMethod%>" enctype="multipart/form-data">
		<input type="hidden" name="token" value="<%=strToken%>">
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