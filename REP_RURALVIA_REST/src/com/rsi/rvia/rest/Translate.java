package com.rsi.rvia.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.rsi.rvia.translates.TranslateProcessor;

@Path("/translate")
public class Translate {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String translateHTML() {
		String strHTMLPruebas = "<html><head><meta charset=\"UTF-8\"/><title>Prueba HTML translator</title></head>" +
				"<body><div><h2>Bloque Traducción 1</h2><p data-translate=\"COMUN_Literal_Escribe_tu_nombre\">bla bla bla</p></div><div>" +
				"<h2>Bloque Traducción 2</h2><p data-translate=\"COMUN_Literal_Contratacion_-_Buscar_oficina_no_ofi\">bla</p></div><div>" +
				"<h2>Bloque Traducción 2</h2><p data-translate=\"COMUN_Literal_Contratacion_-_Buscar_oficina_no_ofi\">bla</p></div><div>" +
				"<h2>Bloque Traducción 2</h2><p data-translate=\"COMUN_Literal_Contratacion_-_Buscar_oficina_no_ofi\">bla</p></div><div>" +
				"<h2>Bloque Traducción 3</h2><p data-translate=\"HC_comun_ClienteClau4\">bla bla bla</p></div></body></html>";
		String strReturn = "";
		strReturn = TranslateProcessor.processXHTML(strHTMLPruebas, "es_ES");
		return strReturn;
	}
	@Path("/prueba")
	public String translateHTML2() {
		String strHTMLPruebas = "<html><head><meta charset=\"UTF-8\"/><title>Prueba HTML translator</title></head>" +
				"<body><div><h2>Bloque Traducción 1</h2><p data-translate=\"COMUN_Literal_Escribe_tu_nombre\">bla bla bla</p></div><div>" +
				"<h2>Bloque Traducción 2</h2><p data-translate=\"COMUN_Literal_Contratacion_-_Buscar_oficina_no_ofi\">bla</p></div><div>" +
				"<h2>Bloque Traducción 2</h2><p data-translate=\"COMUN_Literal_Contratacion_-_Buscar_oficina_no_ofi\">bla</p></div><div>" +
				"<h2>Bloque Traducción 2</h2><p data-translate=\"COMUN_Literal_Contratacion_-_Buscar_oficina_no_ofi\">bla</p></div><div>" +
				"<h2>Bloque Traducción 3</h2><p data-translate=\"HC_comun_ClienteClau4\">bla bla bla</p></div></body></html>";
		String strReturn = "";
		strReturn = TranslateProcessor.processXHTML(strHTMLPruebas, "es_ES");
		return strReturn;
	}
}
