<%@page import="com.rsi.rvia.rest.client.MiqAdminValidator"%>
<%@page import="com.rsi.rvia.rest.tool.SqlExecutor"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
    //MiqAdminValidator.adminIn(request, (HttpServletResponse)response);
	String salida = SqlExecutor.exec(request, (HttpServletResponse)response);
	String ejecutando;

%>    

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="http://localhost/sass/sqlexecutor/api/static/css/style.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="http://localhost/jslib/codemirror/lib/codemirror.css">
<link rel="stylesheet" href="http://localhost/jslib/codemirror/addon/hint/show-hint.css" />
<link rel=stylesheet href=".http://localhost/jslib/codemirror/doc/docs.css">
<script src="http://localhost/jslib/codemirror/lib/codemirror.js"></script>
<script src="http://localhost/jslib/codemirror/mode/sql/sql.js"></script>
<script src="http://localhost/jslib/codemirror/addon/hint/show-hint.js"></script>
<script src="http://localhost/jslib/codemirror/addon/hint/sql-hint.js"></script>
<script src="http://localhost/jslib/Z$/Z$.js"></script>
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
                Z$.AJAX({
                    url: 'sqlExecutor.jsp',
                    method: 'post',
                    data: document.getElementById('code').value,
                    success: function(){
                        alert("ha ido y ha vuelto")
                    },
                    failure: function(){
                        alert("booo booo booo !!!")
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
                <textarea id="code" name="code">
                </textarea>
            </div>
            <div class="c1-n c12-12-prev">
                <div class="c1-n c3-12-prev">
                    <label>Selecciona esquema</label>
                    <select name="esquema">
                    <option>BEL</option>
                    <option>CIP</option>
                    </select>
                </div> 
                <div class="c1-n c9-12-post">
                    <input type="button" value="Ejecutar" id="execquery">
                </div>
            </div>
            <div class="c1-n c12-12-prev exit">
                
            </div>            
        </div>
    </form>
</body>
</html>