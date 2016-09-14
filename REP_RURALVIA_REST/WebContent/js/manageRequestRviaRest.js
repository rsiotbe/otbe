var updateIsumUrl = '/isum/srv.BDP_RVIA05_SOLICITAR_TARJ_PAR.BDP_RVIA05_SERV_CAM_CLA_CONTRA';

function updateSessionIsumRvia() 
{
	if(updateIsumUrl != settings.url)
	{	
		console.log('Refresco de sesión ISUM-RVIA solicitado');

		$.ajax({
			url : updateIsumUrl,
			type : 'POST',
			success : function(){
				console.log('Session ISUM updated OK');	
			},
			error : function(xhr, status) {
				console.error('Session ISUM updated KO. Status:' + status);	
			}
		});
	}
}

/* se captuaran todos los eventos de peticiones ajax para mantener la sesión de ISUM y RVIA */
$(document).ajaxSend(function(event,request, settings) 
{
	if(updateIsumUrl != settings.url)
	{	
		updateSessionIsumRvia();
	}

});