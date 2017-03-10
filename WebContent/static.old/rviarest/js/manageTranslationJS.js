$(document).ready(function() {
	console.log(translator);
	var userLang = $('html')[0].lang.replace('-','_');
	if(userLang != 'es_ES')
		ajaxRequest(translator, userLang);
});

function ajaxRequest(translator, lang){
	if(typeof(lang) == 'undefined'){
		lang = 'es_ES';
	}
	var id = "";
	var dataGet = {};
	for(var key in translator){
		if(id !== ""){
			id += ",";
		}
		id += key;
	}
	dataGet.id = id;
	dataGet.lang = lang;

	console.log(dataGet);
	$.ajax({
		url : '/api/translate',
		data : dataGet,
		type : 'GET',
		dataType : 'json',
		success : newTranslateAplication,
		error : function(xhr, status) {
			console.log("Error al procesar traducciones de javascript. xhr" + xhr + " - status: " + status);
		}
	});
}

function newTranslateAplication(response){
	translator = response;

}