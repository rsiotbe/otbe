<%@page import="com.rsi.rvia.multibank.CssMultiBankProcessor"%>
<%@page
	import="com.rsi.rvia.rest.template.TemplateManager,com.rsi.rvia.translates.TranslateProcessor"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%
	int nTemplateSize = TemplateManager.getSizeCache();
	int nTranslateSize = TranslateProcessor.getSizeCache();
	int nCssMultibankSize = CssMultiBankProcessor.getSizeCache();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"/>
<html>
<head>

<title>Cache Manager</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<meta charset="UTF-8" />
<script
	src="http://ajax.googleapis.com/ajax/libs/jquery/2.2.2/jquery.min.js"></script>
<style>
body {
	font-family: Arial, "sans-serif";
	padding: 5px 5px 5px;
}

#tittle {
	width: 100%;
	height: 35px;
	color: #007557;
	font-size: 24px;
	display: block;
	text-align: center;
	padding-bottom: 10px;
}

.button {
	background-color: #71c100;
	border: none;
	color: white;
	padding: 10px 20px;
	text-align: center;
	text-decoration: none;
	display: inline-block;
	font-size: 14px;
	margin: 4px 2px;
	cursor: pointer;
	width: 170px;
}

.buttonStop {
	background-color: #E07A00;
}

.button.buttonStop:hover:enabled {
	background-color: #B76503;
	font-style: italic;
}

.button:hover:enabled {
	background-color: #2ABB04;
	font-style: italic;
}

.button:active:enabled {
	background-color: white;
	color: #71c100;
}

.button span {
	cursor: pointer;
	display: inline-block;
	position: relative;
	transition: 0.5s;
}

.button span:after {
	content: '»';
	position: absolute;
	opacity: 0;
	top: 0;
	right: -20px;
	transition: 0.5s;
}

.button:hover:enabled span {
	padding-right: 25px;
}

.button:hover:enabled span:after {
	opacity: 1;
	right: 0;
}

.disabled {
	opacity: 0.2;
	cursor: not-allowed;
}

.botonera {
	width: 100%;
	height: 45px;
	background-color: #EAEAEA;
	display: block;
	text-align: center;
}

.bloqueBotoneraLeft {
	width: 540px;
	display: block;
	float: left;
	padding-top: 11px;
}

.bloqueBotoneraCenter {
	width: 460px;
	display: block;
	float: left;
	padding-top: 11px;
}

.bloqueBotoneraRight {
	width: 230px;
	display: block;
	float: left;
	padding-top: 11px;
}

.contenedorSelector select {
	border: 0;
	color: #7B7A7A;
	background: transparent;
	font-size: 14px;
	font-weight: bold;
	padding: 2px 7px;
	width: 230px;
	-webkit-appearance: none;
}

.contenedorSelector {
	overflow: hidden;
	width: 230px;
	background: white url("http://i62.tinypic.com/15xvbd5.png") no-repeat
		scroll 200px center;
	border: 1px solid green;
	float: left;
	margin-left: 35px;
}

.contenedorServers {
	margin-top: 15px;
	border-spacing: 0px;
}

.contenedorApps {
	margin-top: 15px;
}

.serversEncabezado {
	color: white;
}

.itemEncabezado {
	width: 8.33%;
	text-align: left;
	font-size: 12px;
	background-color: black;
	padding-left: 5px;
	padding-right: 8px;
	height: 18px;
	padding-top: 4px;
}

.appsEncabezado {
	color: white;
}

.itemEncabezadoApp {
	width: 31%;
	padding-left: -15px;
	font-size: 20px;
	margin: 3px;
	padding-left: 15px;
	height: 38px;
	padding-top: 15px;
	display: inline-table;
	border-radius: 11px 11px 11px 11px;
	-moz-border-radius: 11px 11px 11px 11px;
	-webkit-border-radius: 11px 11px 11px 11px;
	border: 2px solid #7a7a7a;
	text-align: center;
	cursor: pointer;
}

.itemEncabezadoDefault {
	background-color: gray;
}

.itemEncabezadoOK {
	background-color: #04FF04;
}

@
-webkit-keyframes flashAnimationError { 0% {
	background-color: #FF0404;
	color: white;
}

50%
{
background-color
:
 
#FF9A9A
;

				
color
:
 
black
;

			
}
100%
{
background-color
:
 
#FF0404
;

				
color
:
 
white

			
}
}
@
keyframes flashAnimationError { 0% {
	background-color: #FF0404;
	color: white;
}

50%
{
background-color
:
 
#FF9A9A
;

				
color
:
 
black
;

			
}
100%
{
background-color
:
 
#FF0404
;

				
color
:
 
white

			
}
}
.itemEncabezadoError {
	background-color: #FF0404;
	-webkit-animation-name: flashAnimationError;
	-webkit-animation-duration: 2000ms;
	-webkit-animation-iteration-count: infinite;
	-webkit-animation-timing-function: cubic-bezier(0.21, 0.87, 0.35, 0.85);
	-moz-animation-name: flashAnimationError;
	-moz-animation-duration: 2000ms;
	-moz-animation-iteration-count: infinite;
	-moz-animation-timing-function: cubic-bezier(0.21, 0.87, 0.35, 0.85);
}

@
-webkit-keyframes flashAnimationWarning { 0% {
	background-color: #FFFF00;
	color: black;
}

50%
{
background-color
:
 
#FFFFAA
;

				
color
:
 
#222222
;

			
}
100%
{
background-color
:
 
#FFFF00
;

				
color
:
 
black

			
}
}
@
keyframes flashAnimationWarning { 0% {
	background-color: #FFFF00;
	color: black;
}

50%
{
background-color
:
 
#FFFFAA
;

				
color
:
 
#222222
;

			
}
100%
{
background-color
:
 
#FFFF00
;

				
color
:
 
black

			
}
}
.itemEncabezadoWarning {
	background-color: #FFFF00;
	-webkit-animation-name: flashAnimationWarning;
	-webkit-animation-duration: 2000ms;
	-webkit-animation-iteration-count: infinite;
	-webkit-animation-timing-function: cubic-bezier(0.21, 0.87, 0.35, 0.85);
	-moz-animation-name: flashAnimationWarning;
	-moz-animation-duration: 2000ms;
	-moz-animation-iteration-count: infinite;
	-moz-animation-timing-function: cubic-bezier(0.21, 0.87, 0.35, 0.85);
}

#contenedorLineasServer {
	width: 100%;
}

.lineaGris {
	background-color: #E5E5E5;
}

.itemLineaServer {
	width: 10.5%;
	text-align: left;
	font-size: 11px;
	height: inherit;
	padding-left: 5px;
	padding-top: 4px;
	vertical-align: middle;
	border-bottom: 1px dotted gray;
}

.bola {
	border-radius: 11px 11px 11px 11px;
	-moz-border-radius: 11px 11px 11px 11px;
	-webkit-border-radius: 11px 11px 11px 11px;
	border: 1px solid #7a7a7a;
	height: 12px;
	width: 12px;
	display: inline-block;
}

.bola_inactiva {
	background-color: #c4c4c4;
}

.bola_ok {
	background-color: #04FF04;
}

.bola_error {
	background-color: #FF0404;
	-webkit-animation-name: flashAnimationError;
	-webkit-animation-duration: 2000ms;
	-webkit-animation-iteration-count: infinite;
	-webkit-animation-timing-function: cubic-bezier(0.21, 0.87, 0.35, 0.85);
	-moz-animation-name: flashAnimationError;
	-moz-animation-duration: 2000ms;
	-moz-animation-iteration-count: infinite;
	-moz-animation-timing-function: cubic-bezier(0.21, 0.87, 0.35, 0.85);
}

.bola_warning {
	background-color: #FFFF00;
	-webkit-animation-name: flashAnimationWarning;
	-webkit-animation-duration: 2000ms;
	-webkit-animation-iteration-count: infinite;
	-webkit-animation-timing-function: cubic-bezier(0.21, 0.87, 0.35, 0.85);
	-moz-animation-name: flashAnimationWarning;
	-moz-animation-duration: 2000ms;
	-moz-animation-iteration-count: infinite;
	-moz-animation-timing-function: cubic-bezier(0.21, 0.87, 0.35, 0.85);
}

table td {
	width: 8%;
	white-space: nowrap;
}

table td:last-child {
	width: 100%;
	white-space: normal;
}

table th {
	width: 8%;
	white-space: nowrap;
}

table th:last-child {
	width: 100%;
	white-space: normal;
}

a.tooltips {
	position: relative;
	word-wrap: break-word;
}

a.tooltips span {
	position: absolute;
	height: initial;
	line-height: 15px;
	text-align: center;
	visibility: hidden;
	border-radius: 7px;
	vertical-align: middle;
	white-space: initial;
	padding-right: 5px;
	padding-left: 5px;
	padding-top: 4px;
	padding-bottom: 4px;
	vertical-align: middle;
}

a.tooltips span:after {
	content: '';
	position: absolute;
	top: -9px;
	left: 311px;
	margin-left: -5px;
	width: 0px;
	height: 0;
	border-bottom: 8px solid #000000;
	border-right: 8px solid transparent;
	border-left: 8px solid transparent;
}

a:hover.tooltips span {
	color: #000000;
	width: 350px;
	background: #FBF5E6;
	background: -webkit-linear-gradient(top, #FBF5E6, #FFFFFF);
	background: linear-gradient(top, #FBF5E6, #FFFFFF);
	border: 1px solid #585858;
	visibility: visible;
	opacity: 1;
	left: -310px;
	z-index: 999;
	margin-top: 20px;
	display: inline;
}

.itemEncabezadoApp_Cont {
	margin: 0 auto;
	width: 60%;
	float: none;
	overflow: hidden;
	text-align: center;
}

.itemEncabezadoApp_Errors {
	margin: 0 auto;
	width: 90%;
	float: none;
	overflow: hidden;
	text-align: center;
	font-size: 9pt;
	padding-top: 5px;
}

.itemEncabezadoApp img {
	float: left !important;
	padding-top: 3px;
	width: 20px;
	height: 20px;
}

.itemEncabezadoApp_Cont span {
	margin-left: 10px;
	text-align: center;
	float: none;
	margin: 0px auto;
	display: inline-block;
}

.itemEncabezadoApp {
	font-size: 20px;
	height: 50px;
	text-align: center;
}

input[type=checkbox] {
	cursor: pointer;
}

input:disabled {
	background: #dddddd;
	cursor: not-allowed;
	color: #B7B6B6;
}

input:disabled+label {
	color: #ccc;
}

.headerFix {
	width: 100%;
	padding: 5px 5px 5px;
}

.divDetails {
	padding-bottom: 80px;
	display: block;
}

.divBodyApps {
	display: block;
}

.largeCheckBox {
	width: 20px;
	height: 20px;
	padding: 2px;
}
.centered{
	text-align: center;
	align-content: center;
}
.textColorHTable{
	color: #FFFFFF;
	background: #000000;
}
</style>


</head>
<body>
	<div id="staticHeader" class="headerFix">
		<div id="tittle">Cache Manager</div>
		<div class="botonera">
			<div class="bloqueBotoneraLeft">
				<div style="margin-top: -10px;">
					<button id="botonLanzar" class="button" type="button"
						onclick="freeCache()">
						<span id="spanBotonLanzar">Liberar Caches</span>
					</button>
				</div>
			</div>
			<div class="bloqueBotoneraCenter">
				<div style="margin-top: -10px;">
					<button id="botonLanzar" class="button" type="button"
						onclick="refreshCache()">
						<span id="spanBotonLanzar">Actualizar</span>
					</button>
				</div>
			</div>
			<div class="bloqueBotoneraRight">
				<div style="margin-top: -10px;"></div>
			</div>
		</div>
	</div>

	<div id="divDetails" class="divDetails">
		<table class="contenedorServers">
			<tbody id="tableBodyServers">
				<tr class="centered textColorHTable">
					<th>Clase</th>
					<th>Numero Elementos</th>
					<th>Accion</th>
				</tr>
				<tr class="lineaGris centered">
					<td>Todas</td>
					<td>-</td>
					<td><input id="allCheck" class="largeCheckBox" type="checkbox">
					</td>
				</tr>
				<tr class="lineaGris centered">
					<td>Plantillas HTML</td>
					<td class="cacheSize" id="cacheTemplate"><%=nTemplateSize%></td>
					<td><input id="templateCheck" class="largeCheckBox"
						type="checkbox"></td>
				</tr>
				<tr class="lineaGris centered">
					<td>Traducciones</td>
					<td class="cacheSize" id="cacheTranslate"><%=nTranslateSize%></td>
					<td><input id="translateCheck" class="largeCheckBox"
						type="checkbox"></td>
				</tr>
				<tr class="lineaGris centered">
					<td>Css Multientidad</td>
					<td class="cacheSize" id="cacheCssMultibank"><%=nCssMultibankSize%></td>
					<td><input id="cssMultibankCheck" class="largeCheckBox"
						type="checkbox"></td>
				</tr>				
			</tbody>

		</table>



	</div>

	<script type="text/javascript">
	
		function refreshCache(){
			var strRefresh = "true";
			var pLoading = '<div class="bola_warning"></div>';
			var dataGet = {};
			
			document.getElementById("cacheTemplate").innerHTML = pLoading;
			document.getElementById("cacheTranslate").innerHTML = pLoading;
			document.getElementById("cacheCssMultibank").innerHTML = pLoading;
			
			dataGet.refresh = strRefresh;
			console.log("Refresh: " + strRefresh);
			var strUrl = '/api/tool/cleanCache.jsp';
			$.ajax({
				url : strUrl,
				data : dataGet,
				type : 'GET',
				dataType : 'json',
				success : refreshData,
				error : function(xhr, status) {
					console.log("Error al liberar caches. xhr" + xhr
							+ " - status: " + status);
				}
			});
		}
		function freeCache() {
			var strParams = '';
			var pLoading = '<div class="bola_warning"></div>';
			var fCheckAll = document.getElementById("allCheck");
			var fCheckTemplate = document.getElementById("templateCheck");
			var fCheckTranslate = document.getElementById("translateCheck");
			var fCheckCssMultibank = document.getElementById("cssMultibankCheck");
			var dataGet = {};
			var pCacheSizes = document.getElementsByClassName("cacheSize");

			if (fCheckAll.checked) {
				strParams += 'all';
				for (var i = 0; i < pCacheSizes.length; i++) {
					pCacheSizes[i].innerHTML = pLoading;
				}
			} else {
				if (fCheckTemplate.checked) {
					strParams += 'template';
					document.getElementById("cacheTemplate").innerHTML = pLoading;
				}
				if (fCheckTranslate.checked) {
					strParams += ',translate';
					document.getElementById("cacheTranslate").innerHTML = pLoading;
				}
				if (fCheckCssMultibank.checked) {
					strParams += ',cssMultibank';
					document.getElementById("cacheCssMultibank").innerHTML = pLoading;
				}
			}
			dataGet.clean = strParams;
			console.log("StrParams: " + strParams);
			var strUrl = '/api/tool/cleanCache.jsp';
			$.ajax({
				url : strUrl,
				data : dataGet,
				type : 'GET',
				dataType : 'json',
				success : refreshData,
				error : function(xhr, status) {
					console.log("Error al liberar caches. xhr" + xhr
							+ " - status: " + status);
				}
			});
		}

		function refreshData(pJsonResponse) {
			console.log(pJsonResponse);
			if (typeof pJsonResponse.template !== "undefined") {
				document.getElementById("cacheTemplate").innerHTML = pJsonResponse.template.size;
			}
			if (typeof pJsonResponse.translate !== "undefined") {
				document.getElementById("cacheTranslate").innerHTML = pJsonResponse.translate.size;
			}
			if (typeof pJsonResponse.cssMultibank !== "undefined") {
				document.getElementById("cacheCssMultibank").innerHTML = pJsonResponse.cssMultibank.size;
			}
		}
	</script>
</body>
</html>