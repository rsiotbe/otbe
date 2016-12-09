<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="com.rsi.rvia.rest.tool.RviaConnectCipher"%>
    
 <%
 	String strType = request.getParameter("type");
 	String strTextValue = request.getParameter("text");
 	String strResult;
 	if(strType == "decrypt")
 	{
 		strResult = RviaConnectCipher.symmetricDecrypt(strTextValue, RviaConnectCipher.RVIA_CONNECT_KEY);
 		String[] strParts = strResult.split("&");
 		
 	}
 	else if(strType == "encrypt")
 	{
 		
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
	</div>
	<div>
		<div>
			<button onclick="decrypt">Desencriptar</button>
		</div>
		<div>		
			<button onclick="encrypt">Encriptar</button> 
		</div>
	</div>
	<div>
		<div id="divDetails" class="divDetails">
			<table class="contenedorServers">
				<tbody id="tableBodyServers">
					<tr class="centered textColorHTable">
						<th>Clave</th>
						<th>valor</th>
					</tr>
	<%
	String strHtmlTable = "";
	/* Se generan las tablas HTML y el Json para el Javascript */
	for (Enumeration<String> e = htCaches.keys(); e.hasMoreElements(); ) 
	{ 
		String strKey = (String) e.nextElement();
	    Class<?> oCacheClass = (Class<?>)htCaches.get(strKey);
	    int nValue = -1;
	    try 
	    {
	   	  Method method = oCacheClass.getMethod("getCacheSize");
	   	  nValue = (Integer)method.invoke(oCacheClass);
	   	} 
	    catch (Exception ex) 
	   	{ 
	   		pLog.error("Error al obtener los tamaños de las caches. Error: " + ex);
	   	}
	    
		strHtmlTable += "<tr class=\"lineaGris centered\">\n" +
					"<td style=\"text-align:left;padding-left:10px ;\">" + strKey + "</td>\n" +
					"<td><div style=\"width:80px;float:left;height: 35px;padding-top: 12px;\">" + nValue + "</div>" +
						"<button class=\"button\" style=\"width:95px;height:35px;font-size: 11px;\" onclick=\"location.href ='/api/tool/cacheManager.jsp?viewData=" + strKey + "'\"> Ver datos </button>" + 
					"</td>\n" +
					"<td><input id=\"" + strKey + "\" class=\"largeCheckBox\" type=\"checkbox\"></td>\n" +
					"</tr>";	
	}
	
	
	
	%>		
	<%=strHtmlTable%>	
	
				</tbody>
			</table>
		</div>
	</div>	
	
	<form id="formulario" action ="/api/tool/tokenManager.jsp" method="post">
		<input id="text" type="hidden" value="">
		<input id="type" type="hidden" value="">
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
	</script>

</body>
</html>