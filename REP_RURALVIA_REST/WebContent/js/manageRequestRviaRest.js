
//loadPage( url [, data ] [, callbackFunction ] )
function pageQuery(url, data, callbackFunction)
{
	/* se comprueba que existe la función antes de invocarla */
	updateSessionIsumRvia();
	console.log('Refresco de sesión ISUM-RVIA solicitado');
	if(jQuery.isFunction(data))
	{
		callbackFunction = data;
		data = undefined;
	}
	$('html').load(url, data,callbackFunction);	
}

/* Función genérica para las llamadas AJAX  */
function genericAjax (jParameters, beforeSendCallback, successCallback, errorCallback ) 
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
			if (typeof(errorCallback) == "function")
				erroCallback(xhr, textStatus, errorThrown);
			else
				console.log( 'ERROR genericAjax: ' + textStatus + " - " + errorThrown);
		}
	});
	return result ;
};

function updateSessionIsumRvia() 
{
	$.ajax({
		url : '/isum/srv.BDP_RVIA05_SOLICITAR_TARJ_PAR.BDP_RVIA05_SERV_CAM_CLA_CONTRA',
		type : 'POST',
		success : function(){
			console.log('Session ISUM updated OK');	
		},
		error : function(xhr, status) {
			console.error('Session ISUM updated KO. Status:' + status);	
		}
	});
}

/* se captuaran todos los eventos de peticiones ajax para mantener la sesión de ISUM y RVIA */
$(document).ajaxSend(function(event,request, settings) 
{
	updateSessionIsumRvia();
	console.log('Refresco de sesi�n ISUM-RVIA solicitado');
});