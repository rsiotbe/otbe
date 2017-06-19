<%@page import="com.rsi.rvia.rest.tool.Utils"%>
<%@page import="com.rsi.rvia.rest.security.IdentityProviderFactory"%>
<%@page import="com.rsi.rvia.rest.security.IdentityProviderRVIASession"%>
<%@page import="java.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="com.rsi.rvia.rest.tool.RviaConnectCipher"%>
    
 <%
 	String strType = request.getParameter("type");
 	String strTextValue = request.getParameter("text");
 	String strResult = "";
 	HashMap<String,String> pPayload;
 	String strKeyValues[] = null;
 	if("decryptJWT".equalsIgnoreCase(strType))
	{
 	    IdentityProviderRVIASession pIdentityProviderRVIASession = new IdentityProviderRVIASession(null, null);
 	    pPayload = pIdentityProviderRVIASession.validateJWT(strTextValue, IdentityProviderRVIASession.TOKEN_ID);
 	    strResult = Utils.hashMapToQueryString(pPayload);
 		strKeyValues = strResult.split("&");
 	}
 	if("decrypt".equalsIgnoreCase(strType))
	{
 		strResult = RviaConnectCipher.symmetricDecrypt(strTextValue, RviaConnectCipher.RVIA_CONNECT_KEY);
 		strKeyValues = strResult.split("&");
 	}
 	else if("encryptOld".equalsIgnoreCase(strType))
 	{
 		strResult = RviaConnectCipher.symmetricDecryptOld(strTextValue, RviaConnectCipher.RVIA_CONNECT_KEY);
 		strKeyValues = strResult.split("&");
 	}
 	else if("encrypt".equalsIgnoreCase(strType))
 	{
 		strResult = RviaConnectCipher.symmetricEncrypt(strTextValue, RviaConnectCipher.RVIA_CONNECT_KEY);
 	}
 	else if("encryptOld".equalsIgnoreCase(strType))
 	{
 		strResult = RviaConnectCipher.symmetricEncryptOld(strTextValue, RviaConnectCipher.RVIA_CONNECT_KEY);
 	}
 	else if("session".equalsIgnoreCase(strType))
 	{
 		strTextValue = session.getAttribute("token").toString();
 		strResult = RviaConnectCipher.symmetricDecrypt(strTextValue, RviaConnectCipher.RVIA_CONNECT_KEY);
 		strKeyValues = strResult.split("&");		
 	} 
 	else if("sessionOld".equalsIgnoreCase(strType))
 	{
 		strTextValue = session.getAttribute("token").toString();
 		strResult = RviaConnectCipher.symmetricDecryptOld(strTextValue, RviaConnectCipher.RVIA_CONNECT_KEY);
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
			<button onclick="fromSession('new')">Recuperar de sesión</button>
			<button onclick="fromSession('old')">Recuperar de sesión con metodo antiguo</button>
		<%
		}
		%>
	</div>
	<div>
		<div>
			<button onclick="decryptJWT()">Desencriptar JWT</button>
		</div>
		<div>
			<button onclick="decrypt('new')">Desencriptar token</button>
		</div>		
		<div>
			<button onclick="decrypt('old')">Desencriptar token con método antiguo</button>
		</div>
		<div>		
			<button onclick="encrypt('new')">Encriptar token </button> 
		</div>
		<div>
			<button onclick="decrypt('old')">Encriptar token con método antiguo</button>
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
	function decryptJWT(type)
	{
		var texto = document.getElementById('textAreaInput').value;
		document.getElementById('type').value = 'decryptJWT';
		document.getElementById('text').value = texto;
		document.getElementById('formulario').submit();
	}
	
		function decrypt(type)
		{
			var texto = document.getElementById('textAreaInput').value;
			document.getElementById('type').value = 'decrypt';
			if(type == 'old')
				document.getElementById('type').value = 'decryptOld';
			document.getElementById('text').value = texto;
			document.getElementById('formulario').submit();
		}
		
		function encrypt(type)
		{
			var texto = document.getElementById('textAreaInput').value;
			document.getElementById('type').value = 'encrypt';
			if(type == 'old')
				document.getElementById('type').value = 'encryptOld';
			document.getElementById('text').value = texto;
			document.getElementById('formulario').submit();
		}
		function fromSession(type)
		{
			document.getElementById('type').value = 'session';
			if(type == 'old')
				document.getElementById('type').value = 'sessionOld';
			document.getElementById('formulario').submit();
		}
	</script>

</body>
</html>