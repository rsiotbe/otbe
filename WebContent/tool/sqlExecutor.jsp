<%@page import="com.rsi.rvia.rest.client.MiqAdminValidator"%>
<%@page import="com.rsi.rvia.rest.tool.SqlExecutor"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
    MiqAdminValidator.adminIn(request, (HttpServletResponse)response);
    String ejecutando = request.getParameter("SQLcode");
    SqlExecutor sqlExecutor = new SqlExecutor();
	String salida = sqlExecutor.exec(request, (HttpServletResponse)response);	
%>    

<%if(ejecutando == null){ %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="/api/static/css/apirestadmin/sqlexecutor.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="/api/static/css/apirestadmin/codemirror/codemirror.css">
<link rel="stylesheet" href="/api/static/css/apirestadmin/codemirror/show-hint.css" />
<link rel="stylesheet" href="/api/static/css/apirestadmin/codemirror/docs.css">
<script src="/api/static/js/apirestadmin/codemirror/codemirror.js"></script>
<script src="/api/static/js/apirestadmin/codemirror/sql.js"></script>
<script src="/api/static/js/apirestadmin/codemirror/show-hint.js"></script>
<script src="/api/static/js/apirestadmin/codemirror/sql-hint.js"></script>
<script src="/api/static/js/apirestadmin/Z$.js"></script>
<script>
        Z$.ready(function() {        
            var mime = 'text/x-mariadb';
            // get mime type
            if (window.location.href.indexOf('mime=') > -1) {
                mime = window.location.href.substr(window.location.href.indexOf('mime=') + 5);
            }
            window.editor = CodeMirror.fromTextArea(document.getElementById('code'), {
                    mode: mime,
                    indentWithTabs: true,
                    smartIndent: true,
                    lineNumbers: true,
                    matchBrackets : true,
                    autofocus: true,
                    extraKeys: {"Ctrl-Space": "autocomplete"},
                    hintOptions: {tables: {
                    users: {name: null, score: null, birthDate: null},
                    countries: {name: null, population: null, size: null}
                }}
            });
            
            Z$.eventCataloger.addEventHandler (document.getElementById('execquery'),'click',function(){            	
            	var datos = window.editor.getValue();
            	datos=datos.replace(/--.*\n/g," ");
            	datos=datos.replace(/\n/g," ");
            	datos=datos.replace(/\s+/g," ");
            	datos=escape(datos);
            	datos = "SQLcode="+datos;
            	datos = datos + "&esquema=" + document.getElementById("esquema").value;
                console.log(datos);
                Z$.AJAX({
                    url: '/api/tool/sqlExecutor.jsp',
                    method: 'post',
                    data: datos,
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    success: function(response){
                    	document.getElementById("resultado_ejecucion").innerHTML=response.responseText;
                    },
                    failure: function(){
                    	document.getElementById("resultado_ejecucion").innerHTML="Ha cascado el servidor.<br>Comprueba la traza.";
                    }                    
                });
            });
        });
</script>
<style>
.CodeMirror {
    border-top: 1px solid black;
    border-bottom: 1px solid black;
}
</style>
</head>
<body>
    <form>
        <div class="dash-body">       
            <div class="c1-n c12-12-prev">        
                <textarea id="code" name="code">-- Your SQL here
                </textarea>
                <!-- <input type="hidden" value="" name="_code" id="_code">  -->
            </div>
            <div class="c1-n c12-12-prev">
                <div class="c1-n c3-12-prev">
                    <label>Selecciona esquema</label>
                    <select name="esquema" id="esquema">
                    <option value="OracleBanca">BEL</option>
                    <option value="OracleCIP">CIP</option>
                    </select>
                </div> 
                <div class="c1-n c9-12-post">
                    <input type="button" value="Ejecutar" id="execquery">
                </div>
            </div>
            <div class="c1-n c12-12-prev exit" id="resultado_ejecucion">
                
            </div>            
        </div>
    </form>
</body>
</html>

<% } else { %>

<%=salida %>

<% } %>

