<%@page
	import="com.rsi.rvia.rest.template.TemplateManager,
				com.rsi.rvia.translates.TranslateProcessor
				"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
	
	<%
		int nTemplateSize = TemplateManager.getSizeCache();
		int nTranslateSize = TranslateProcessor.getSizeCache();
	%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"/>
<html>
<head>

<title>Cache Manager</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
<meta name="viewport" content="width=device-width, initial-scale=1"/>
<meta charset="UTF-8"/>
<link rel="stylesheet" href="http://www.w3schools.com/lib/w3.css"/>
<script src="http://ajax.googleapis.com/ajax/libs/jquery/2.2.2/jquery.min.js"></script>
<link rel="stylesheet" href="http://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.6.3/css/font-awesome.min.css">

</head>
<body>

<h1>Cache Manager</h1>
<div class="grip20"></div>
<div class="grip60">
	<table class="w3-table w3-striped w3-bordered w3-border w3-hoverable w3-centered">
<thead>
<tr class="w3-light-green">
  <th>Clase</th>
  <th>Numero Elementos</th>
  <th>Accion</th>
</tr>
</thead>
<tr>
  <td>Todas</td>
  <td>-</td>
  <td>
	<input id="allCheck" class="w3-check" type="checkbox">
  </td>
</tr>
<tr>
  <td>Templates</td>
  <td class="cacheSize" id="cacheTemplate"><%=nTemplateSize%></td>
  <td><input id="templateCheck" class="w3-check" type="checkbox"></td>
</tr>
<tr>
  <td>Translates</td>
  <td class="cacheSize" id="cacheTranslate"><%=nTranslateSize%></td>
  <td><input id="translateCheck" class="w3-check" type="checkbox"></td>
</tr>
</table>
<button onclick="freeCache()" class="w3-btn w3-green">Liberar</button>
	
</div>
<div class="grip20"></div>

<script type="text/javascript">
	function freeCache() {
		var strParams = '';
		var pSpin = '<p><i class="fa fa-spinner w3-spin"></i></p>';
		var fCheckAll = document.getElementById("allCheck");
		var fCheckTemplate = document.getElementById("templateCheck");
		var fCheckTranslate = document.getElementById("translateCheck");
		var dataGet = {};
		var pCacheSizes= document.getElementsByClassName("cacheSize");
		
		if(fCheckAll.checked){
			strParams += 'all';
			for(var i=0; i<pCacheSizes.length; i++) {
    			pCacheSizes[i].innerHTML = pSpin;
			}
		}else{
			if(fCheckTemplate.checked){
				strParams += 'template';
				document.getElementById("cacheTemplate").innerHTML = pSpin;
			}
			if(fCheckTranslate.checked){
				strParams += ',translate';
				document.getElementById("cacheTranslate").innerHTML = pSpin;
			}
		}
		dataGet.clean = strParams;
		console.log("StrParams: " + strParams);
		var strUrl = 'http://localhost:8080/api/tool/cleanCache.jsp';
		$.ajax({
			url : strUrl,
			data : dataGet,
			type : 'GET',
			dataType : 'json',
			success : refreshData,
			error : function(xhr, status) {
				console.log("Error al liberar caches. xhr" + xhr + " - status: " + status);
			}
	});
	}

	function refreshData(pJsonResponse){
		console.log(pJsonResponse);
		if(typeof pJsonResponse.template !== "undefined"){
				document.getElementById("cacheTemplate").innerHTML = pJsonResponse.template.size;
		}
		if(typeof pJsonResponse.translate !== "undefined"){
				document.getElementById("cacheTranslate").innerHTML = pJsonResponse.translate.size;
		}

	}



</script>
</body>
</html>