package com.rsi.rvia.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.tool.Utils;
import com.rsi.rvia.translates.TranslateProcessor;

@Path("/translate/{id}/{list}")
public class Translate
{
	private static Logger			pLog	= LoggerFactory.getLogger(Translate.class);
	@Context
	UriInfo								uriInfo;

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String translateHTML()
	{
		String strPrimaryPath = Utils.getPrimaryPath(uriInfo);
		pLog.debug("uriInfo.getPath(): " + strPrimaryPath);
		String strHTMLPruebas = "<html><head><meta charset=\"UTF-8\"/><title>Prueba HTML translator</title></head>"
				+ "<body><div><h2>Bloque Traducción 1</h2><p data-translate=\"COMUN_Literal_Escribe_tu_nombre\">bla bla bla</p></div><div>"
				+ "<h2>Bloque Traducción 2</h2><p data-translate=\"COMUN_Literal_Contratacion_-_Buscar_oficina_no_ofi\">bla</p></div><div>"
				+ "<h2>Bloque Traducción 2</h2><p data-translate=\"COMUN_Literal_Contratacion_-_Buscar_oficina_no_ofi\">bla</p></div><div>"
				+ "<h2>Bloque Traducción 2</h2><p data-translate=\"COMUN_Literal_Contratacion_-_Buscar_oficina_no_ofi\">bla</p></div><div>"
				+ "<h2>Bloque Traducción 3</h2><p data-translate=\"HC_comun_ClienteClau4\">bla bla bla</p></div></body></html>";
		String strReturn = "";
		strReturn = ((Object)TranslateProcessor.processXHTML(strHTMLPruebas, null)).toString();
		return strReturn;
	}
}
