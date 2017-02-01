<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="com.rsi.rvia.rest.tool.RviaConnectCipher"%>
    
 <%
 	String strType = request.getParameter("type");
 	String strTextValue = request.getParameter("text");
 	String strResult = "";
 	String strKeyValues[] = null;
 	if("decrypt".equalsIgnoreCase(strType))
	{
 		strResult = RviaConnectCipher.symmetricDecrypt(strTextValue, RviaConnectCipher.RVIA_CONNECT_KEY);
 		strKeyValues = strResult.split("&");
 	}
 	else if("encrypt".equalsIgnoreCase(strType))
 	{
 		strResult = RviaConnectCipher.symmetricEncrypt(strTextValue, RviaConnectCipher.RVIA_CONNECT_KEY);
 	}
 	else if("session".equalsIgnoreCase(strType))
 	{
 		strTextValue = session.getAttribute("token").toString();
 		strResult = RviaConnectCipher.symmetricDecrypt(strTextValue, RviaConnectCipher.RVIA_CONNECT_KEY);
 		strKeyValues = strResult.split("&");		
 	} 		
 %>  
   
<!DOCTYPE>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Token de Rvia</title>
	</head>
<body>
	<div>
		<textArea id="textAreaInput"></textArea>
		<%if(session.getAttribute("token") != null)
		{%>
			<button onclick="fromSession()">Recuperar de sesión</button>
		<%
		}
		%>
	</div>
	<div>
		<div>
			<button onclick="decrypt()">Desencriptar</button>
		</div>
		<div>		
			<button onclick="encrypt()">Encriptar</button> 
		</div>
	</div>

	<div><%=strResult %></div>
	<%
	String strHtmlTable = "";
	/* Se generan las tablas HTML y el Json para el Javascript */
	if(strKeyValues != null)
	{
		%>
		<div id="divDetails" class="divDetails">
		<table class="contenedorServers">
			<tbody id="tableBodyServers">
				<tr class="centered textColorHTable">
					<th>Clave</th>
					<th>valor</th>
				</tr>
		<%
		for (int i = 0; i < strKeyValues.length; i++) 
		{ 
			String strValue = "";
			String strKey = strKeyValues[i].split("=")[0];
			if(strKeyValues[0].split("=").length>1)
				strValue = strKeyValues[i].split("=")[1];  
			strHtmlTable += "<tr class=\"lineaGris centered\">\n" +
						"<td style=\"text-align:left;padding-left:10px ;\">" + strKey + "</td>\n" +
						"<td style=\"\">" + strValue + "</td>\n" +
						"</tr>";	
		}
		%>		
		<%=strHtmlTable%>	
			</tbody>
		</table>
		</div>
	<%
	}
	%>	
	<form id="formulario" action ="/api/tool/tokenManager.jsp" method="post">
		<input id="text" name="text" type="hidden" value="">
		<input id="type" name="type" type="hidden" value="">
	</form>
	<script>
		function decrypt()
		{
			var texto = document.getElementById('textAreaInput').value;
			document.getElementById('type').value = 'decrypt';
			document.getElementById('text').value = texto;
			document.getElementById('formulario').submit();
		}
		
		function encrypt()
		{
			var texto = document.getElementById('textAreaInput').value;
			document.getElementById('type').value = 'encrypt';
			document.getElementById('text').value = texto;
			document.getElementById('formulario').submit();
		}
		function fromSession()
		{
			document.getElementById('type').value = 'session';
			document.getElementById('formulario').submit();
		}
	</script>

</body>
</html>