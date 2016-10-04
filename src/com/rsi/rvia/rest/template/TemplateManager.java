package com.rsi.rvia.rest.template;

import java.io.InputStream;
import java.util.Hashtable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.multibank.CssMultiBankProcessor;
import com.rsi.rvia.rest.session.SessionRviaData;
import com.rsi.rvia.rest.tool.Utils;
import com.rsi.rvia.translates.TranslateProcessor;

/**
 * Clase para administar los templates. Inserta el Json en el template y utiliza el Translator para traducir las
 * etiquetas del template.
 */
public class TemplateManager
{
	static Logger										pLog							= LoggerFactory.getLogger(TemplateManager.class);
	public final static String						JSON_DATA_TAG				= "'__JSONDATA__'";
	private final static String					IFRAME_SCRIPT_ADAPTER	= "/api/js/iframe/iframeResizer.contentWindow.min.js";
	private final static String					UPDATE_RVIA_SESSION		= "/api/js/session/manageSessionIsumRvia.js";
	private final static String					JQUERY						= "/api/js/jquery/jquery-1.11.3.min.js";
	// private final static String JQUERY = "/api/js/jquery/jquery-2.2.4.min.js";
	public static Hashtable<String, Document>	htCacheTemplate			= new Hashtable<String, Document>();

	/**
	 * Devuelve el tamaño de la cache
	 * 
	 * @return int con el tamaño de la cache
	 */
	public static int getSizeCache()
	{
		int nReturn = 0;
		if (htCacheTemplate != null)
		{
			nReturn = htCacheTemplate.size();
		}
		return nReturn;
	}

	/**
	 * Reinicia la Cache
	 */
	public static void restartCache()
	{
		if (htCacheTemplate != null)
		{
			htCacheTemplate = new Hashtable<String, Document>();
		}
	}

	/**
	 * Busca el template y lo lee, carga las traducciones, inyecta el script para ajustar el iframe, ajusta los estilos
	 * para multicanalidad
	 * 
	 * @param strPathToTemplate
	 * @param pSessionRviaData
	 * @return
	 * @throws Exception
	 */
	public static String processTemplate(String strPathToTemplate, SessionRviaData pSessionRviaData) throws Exception
	{
		return processTemplate(strPathToTemplate, pSessionRviaData, "{}");
	}

	/**
	 * Busca el template y lo lee, carga las traducciones, inyecta el script para ajustar el iframe, ajusta los estilos
	 * para multicanalidad e inyecta los datos en json.
	 * 
	 * @param strPathToTemplate
	 *           Ruta del template
	 * @param pSessionRviaData
	 *           Objeto datos de sesion ruralvia
	 * @param strDataJson
	 *           Datos en formato JSON.
	 * @return Template procesado.
	 * @throws Exception
	 */
	public static String processTemplate(String strPathToTemplate, SessionRviaData pSessionRviaData, String strDataJson)
			throws Exception
	{
		String strReturn;
		Document pDocument;
		try
		{
			String strCacheKey = strPathToTemplate;
			/* en función del canalAix de la petición se obtiene la plantilla adecuada */
			strPathToTemplate = adjustTemplateNameByChannel(strPathToTemplate, pSessionRviaData);
			if (pSessionRviaData != null)
				strCacheKey = strPathToTemplate + "_" + pSessionRviaData.getLanguage();
			pLog.debug("strCacheKey:" + strCacheKey);
			if (htCacheTemplate.containsKey(strCacheKey))
			{
				pLog.info("Template cacheado, recuperandolo...");
				Document pCacheDocument = htCacheTemplate.get((strCacheKey));
				pDocument = Jsoup.parse(pCacheDocument.toString(), "", Parser.htmlParser());
			}
			else
			{
				pLog.info("Template NO cacheado, se procede a leerlo, tratarlo y cachearlo");
				pDocument = readTemplate(strPathToTemplate);
				pDocument = translateXhtml(pDocument, pSessionRviaData);
				htCacheTemplate.put(strCacheKey, pDocument);
				pDocument = Jsoup.parse(pDocument.toString(), "", Parser.htmlParser());
			}
			pDocument = includeIframeScript(pDocument);
			pDocument = includeUpdateRviaScript(pDocument);
			pDocument = adjustCssMultiBank(pDocument, pSessionRviaData);
			pDocument.outputSettings().escapeMode(EscapeMode.base);
			strReturn = pDocument.html();
			strReturn = includeJsonData(strReturn, strDataJson);
		}
		catch (Exception ex)
		{
			pLog.error("No ha sido posible procesar la plantilla xhtml", ex);
			throw ex;
		}
		return strReturn;
	}

	/**
	 * Incluye el script para ajustar el tamaño del iframe
	 * 
	 * @param pDocument
	 *           Documento html en jsoup
	 * @return Documento jsoupHTML con la etiqueta script insertada
	 */
	private static Document includeIframeScript(Document pDocument)
	{
		Element pScript = pDocument.createElement("script");
		pScript.attr("src", IFRAME_SCRIPT_ADAPTER);
		pDocument.body().appendChild(pScript);
		return pDocument;
	}

	/**
	 * Incluye el script para asegurar el refresco de sesión de ruralvia e ISUM
	 * 
	 * @param pDocument
	 *           Documento html en jsoup
	 * @return HTML con la etiqueta script insertada
	 */
	private static Document includeUpdateRviaScript(Document pDocument)
	{
		boolean fInsert = true;
		Elements pJsScript = pDocument.select("script[src]");
		for (Element pItem : pJsScript)
		{
			if (pItem != null)
			{
				String strSrc = pItem.attr("abs:src");
				if (strSrc != null && strSrc.toLowerCase().contains("jquery"))
				{
					fInsert = false;
					break;
				}
			}
		}
		if (fInsert)
		{
			Element pScript = pDocument.createElement("script");
			pScript.attr("src", JQUERY);
			pDocument.body().appendChild(pScript);
		}
		Element pScript = pDocument.createElement("script");
		pScript.attr("src", UPDATE_RVIA_SESSION);
		pDocument.body().appendChild(pScript);
		return pDocument;
	}

	/**
	 * Añade el Json al template de salida
	 * 
	 * @param pDocument
	 *           Documento html en jsoup
	 * @param strJsonData
	 * @return Documento html en jsoup
	 */
	private static String includeJsonData(String strReturn, String strJsonData)
	{
		if (strJsonData == null || strJsonData.trim().isEmpty())
			strJsonData = "{}";
		return strReturn.replace(JSON_DATA_TAG, strJsonData);
	}

	/**
	 * Añade el contenido de las traducciones al HTML
	 * 
	 * @param pDocument
	 *           Documento html en jsoup
	 * @param pSessionRviaData
	 *           Datos de sesión de ruralvia para el usuario
	 * @return Documento HTML con las traducciones
	 */
	private static Document translateXhtml(Document pDocument, SessionRviaData pSessionRviaData)
	{
		return TranslateProcessor.processXHTML(pDocument, pSessionRviaData);
	}

	/**
	 * Abre el XHTML para procesarlo
	 * 
	 * @param strPathToTemplate
	 * @return Documento jsoup con el HTML
	 * @throws Exception
	 */
	private static Document readTemplate(String strPathToTemplate) throws Exception
	{
		Document pDocument;
		String strHtml = "";
		InputStream pInputStream = (TemplateManager.class.getResourceAsStream(strPathToTemplate));
		strHtml = Utils.getStringFromInputStream(pInputStream);
		pDocument = Jsoup.parse(strHtml, "", Parser.htmlParser());
		return pDocument;
	}

	/**
	 * Comprueba si es necesario modificar los css para adaptarlos a multientidad
	 * 
	 * @param pDocument
	 *           Documento html en jsoup
	 * @param pSessionRviaData
	 *           Datos de sesión de ruralvia para el usuario
	 * @return
	 * @throws Exception
	 */
	private static Document adjustCssMultiBank(Document pDocument, SessionRviaData pSessionRviaData) throws Exception
	{
		return CssMultiBankProcessor.processXHTML(pDocument, pSessionRviaData);
	}

	/**
	 * Obtiene el nombre de la plantilla adecuado al canal que solicita la página
	 * 
	 * @param strPathToTemplate
	 *           Nombre de la plantilla
	 * @param pSessionRviaData
	 *           Datos de sesión de ruralvia para el usuario
	 * @return
	 * @throws Exception
	 */
	private static String adjustTemplateNameByChannel(String strPathToTemplate, SessionRviaData pSessionRviaData)
	{
		String strReturn = null;
		strReturn = strPathToTemplate;
		if (pSessionRviaData != null)
		{
			switch (pSessionRviaData.getCanalAix())
			{
				case BANCA_MOVIL:
				case BANCA_TABLET:
				case BANCA_TABLET_CAU:
					int nLastDot = strPathToTemplate.lastIndexOf('.');
					if (nLastDot != -1)
						strReturn = strPathToTemplate.substring(0, nLastDot) + "_movil"
								+ strPathToTemplate.substring(nLastDot);
					else
						strReturn = strPathToTemplate + "_movil";
					break;
				case VALORES_BANCA_INTERNET:
				case VALORES_BANCA_TELEFONICA:
				case BANCA_INTERNET:
				case BANCA_TELEFONICA:
				case ABOGADOS:
				case ABOGADOS_TELEFONICA:
				case OFICINA:
				case SEGUROS:
				case TPV_VIRTUAL:
				case TPV_VIRTUAL_TELEFONICA:
				default:
					break;
			}
			pLog.debug("Se cambia si es necesario el nombre del template según el canal. CanalAix:"
					+ pSessionRviaData.getCanalAix().name() + " - CAMBIO: " + strPathToTemplate + " -> " + strReturn);
		}
		return strReturn;
	}
}
