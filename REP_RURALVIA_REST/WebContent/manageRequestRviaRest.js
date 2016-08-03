
//loadPage( url [, data ] [, callbackFunction ] )
function pageQuery(url, data, callbackFunction)
{
	/* se comprueba que existe la función antes de invocarla */
	if (typeof parent.updateSessionIsumRvia === "function") { 
		parent.updateSessionIsumRvia();
		console.log('Refresco de sesión ISUM-RVIA solicitado');
	}
	if(jQuery.isFunction(data))
	{
		callbackFunction = data;
		data = undefined;
	}
	$('html').load(url, data,callbackFunction);	
}

function postDataToLoadPage(parameters)
{   	
	var form = "";
	form = '<form action="/api(" method="post">';
    for(var attr in parameters)
    {
        var attrName = attr;
        var attrValue =  parameters[attr];
        form += '<input type="hidden" name="' + attrName + '" value="' + attrValue + '">';
    }
	form += '</form>';
	$(form).appendTo('body').submit();
}

/* Función genérica para las llamadas AJAX  */
function genericAjax (jParameters, beforeSendCallback, successCallback, erroCallback, aParams ) 
{
	var result = ""; 
	$.ajax({
		type: 	(jParameters.type || 'POST'),
		url: 	jParameters.url ,
		data: 	(jParameters.data || ''),
		headers : (jParameters.headers  || {}),
		dataType: (jParameters.dataType || 'json'),
		async: (jParameters.async  || false),
		cache: (jParameters.cache  || false),
		contentType: (jParameters.contentType  || 'application/x-www-form-urlencoded; charset=UTF-8'),
		beforeSend :  function (jqXHR, settings ){ 
			if (typeof(beforeSendCallback) == "function")
				beforeSendCallback (jqXHR, settings ); 
		},
		success: function( data, textStatus, jqXHR){
			if (typeof(successCallback) == "function")
				successCallback (data); 
			result = data;
		},
		complete: function(jqXHR, textStatus ){
			if (typeof(successCallback) == "function")
				successCallback (jqXHR, textStatus); 
		},
		error: function(jqXHR, textStatus, errorThrown) {
			if (typeof(erroCallback) == "function")
				erroCallback(xhr, textStatus, errorThrown);
			else
				console.log( 'ERROR genericAjax: ' + textStatus + " - " + errorThrown);
		}
	});
	return result ;
};

/* se captuaran todos los eventos de peticiones ajax para mantener la sesión de ISUM y RVIA */
$(document).ajaxSend(function(event,request, settings) 
{
	/* se compruba que existe la función antes de invocarla */
	if (typeof parent.updateSessionIsumRvia === "function") { 
		parent.updateSessionIsumRvia();
		console.log('Refresco de sesión ISUM-RVIA solicitado');
	}
 ;
});