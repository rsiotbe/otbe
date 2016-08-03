<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<script type="text/javascript">

/********************************************************************************/
/************************** ESTO CASI QUE VA A IR A JS A PARTE *******************/

/* función que realiza una llamada AJAX para refrescar la sesión de usuario de ISUM y ruralvia*/
function updateSessionIsumRvia() 
{
	ajaxJQ.post('/isum/srv.BDP_RVIA05_SOLICITAR_TARJ_PAR.BDP_RVIA05_SERV_CAM_CLA_CONTRA', 
			{}, 
			function(status) {
				if(status==200)
				{
					console.log('Session ISUM updated OK');	
				}
				else
					console.error('Session ISUM updated KO. Status:' + status);	
			},
			true);
}

/* CODE SNNIPET para emular AJAX de JQUERY */
/********************************************/
var ajaxJQ = {};
ajaxJQ.x = function() {
    if (typeof XMLHttpRequest !== 'undefined') {
        return new XMLHttpRequest();  
    }
    var versions = [
        "MSXML2.XmlHttp.6.0",
        "MSXML2.XmlHttp.5.0",   
        "MSXML2.XmlHttp.4.0",  
        "MSXML2.XmlHttp.3.0",   
        "MSXML2.XmlHttp.2.0",  
        "Microsoft.XmlHttp"
    ];

    var xhr;
    for(var i = 0; i < versions.length; i++) {  
        try {  
            xhr = new ActiveXObject(versions[i]);  
            break;  
        } catch (e) {
        }  
    }
    return xhr;
};

ajaxJQ.send = function(url, callback, method, data, sync) {
    var x = ajaxJQ.x();
    x.open(method, url, sync);
    x.onreadystatechange = function() {
        if (x.readyState == 4) {
            callback(x.status)
        }
    };
    if (method == 'POST') {
        x.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    }
    x.send(data)
};

ajaxJQ.get = function(url, data, callback, sync) {
    var query = [];
    for (var key in data) {
        query.push(encodeURIComponent(key) + '=' + encodeURIComponent(data[key]));
    }
    ajaxJQ.send(url + (query.length ? '?' + query.join('&') : ''), callback, 'GET', null, sync)
};

ajaxJQ.post = function(url, data, callback, sync) {
    var query = [];
    for (var key in data) {
        query.push(encodeURIComponent(key) + '=' + encodeURIComponent(data[key]));
    }
    ajaxJQ.send(url, callback, 'POST', query.join('&'), sync)
};
/********************************************/


/********************************************************************************/
function addSendEventToIframe(iframe)
{	
	var script = iframe.contentWindow.document.createElement("script");
	script.type = "text/javascript";
	script.src = '/javascripts_portal/es_ES/js_portal/Prueba.js';
	 //forceUpdateSessionAjaxRviaRest.js
	iframe.contentWindow.document.body.appendChild(script);
}

function iframeLoadCompleted(iframe)
{
	addSendEventToIframe(iframe);
}

</script>
<iframe id="proxyIframe" src="/api/testjs2.jsp" style="height:600px; width:800px;border: 1px black dotted;"></iframe>

</body>
</html>