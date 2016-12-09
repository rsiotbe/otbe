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
import com.rsi.rvia.rest.multibank.CssMultiBankProcessor;
import com.rsi.rvia.rest.session.RequestConfig;
import com.rsi.rvia.rest.session.RequestConfigRvia;
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
	private final static String					IFRAME_SCRIPT_ADAPTER	= "/api/static/rviarest/js/iframe/iframeResizer.contentWindow.min.js";
	private final static String					UPDATE_RVIA_SESSION		= "/api/static/rviarest/js/session/manageSessionIsumRvia.js";
	private final static String					JQUERY						= "/api/static/rviarest/js/jquery-1.11.3.min.js";
	public static Hashtable<String, Document>	htCacheTemplate			= new Hashtable<String, Document>();

	/**
	 * Devuelve el tamaño de la cache
	 * 
	 * @return int con el tamaño de la cache
	 */
	public static int getCacheSize()
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
	public static void resetCache()
	{
		if (htCacheTemplate != null)
		{
			htCacheTemplate = new Hashtable<String, Document>();
		}
	}

	/**
	 * Devuelve los datos de la cache en formato texto
	 * 
	 * @return Contenido de la caché
	 * @throws Exception
	 */
	public static String cacheToString() throws Exception
	{
		String strReturn;
		strReturn = Utils.hastablePrettyPrintHtml(htCacheTemplate);
		return strReturn;
	}

	/**
	 * Busca el template y lo lee, carga las traducciones, inyecta el script para ajustar el iframe, ajusta los estilos
	 * para multicanalidad
	 * 
	 * @param strPathToTemplate
	 * @param pRequestConfigRvia
	 * @return
	 * @throws Exception
	 */
	public static String processTemplate(String strPathToTemplate, RequestConfigRvia pRequestConfigRvia)
			throws Exception
	{
		return processTemplate(strPathToTemplate, pRequestConfigRvia, "{}");
	}

	/**
	 * Busca el template y lo lee, carga las traducciones, inyecta el script para ajustar el iframe, ajusta los estilos
	 * para multicanalidad e inyecta los datos en json.
	 * 
	 * @param strPathToTemplate
	 *           Ruta del template
	 * @param pRequestConfig
	 *           Objeto datos de sesion ruralvia
	 * @param strDataJson
	 *           Datos en formato JSON.
	 * @return Template procesado.
	 * @throws Exception
	 */
	public static String processTemplate(String strPathToTemplate, RequestConfig pRequestConfig, String strDataJson)
			throws Exception
	{
		String strReturn;
		Document pDocument;
		try
		{
			String strCacheKey = strPathToTemplate;
			/* si se rtata de una petición codatos de ruralvia se itneta recupera la información */
			if (RequestConfigRvia.class.isAssignableFrom(pRequestConfig.getClass()))
			{
				/* en función del canalAix de la petición se obtiene la plantilla adecuada */
				strPathToTemplate = adjustTemplateNameByChannel(strPathToTemplate, (RequestConfigRvia) pRequestConfig);
			}
			strCacheKey = strPathToTemplate + "_" + pRequestConfig.getLanguage();
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
				pDocument = translateXhtml(pDocument, pRequestConfig);
				htCacheTemplate.put(strCacheKey, pDocument);
				pDocument = Jsoup.parse(pDocument.toString(), "", Parser.htmlParser());
			}
			pDocument = includeIframeScript(pDocument);
			pDocument = includeUpdateRviaScript(pDocument);
			pDocument = adjustCssMultiBank(pDocument, pRequestConfig);
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
				String strSrc = pItem.attr("src");
				if (strSrc != null
						&& (strSrc.toLowerCase().contains("jquery") || strSrc.toLowerCase().contains("scripts.min.js")))
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
	 * @param pRequestConfig
	 *           Datos de sesión de ruralvia para el usuario
	 * @return Documento HTML con las traducciones
	 */
	private static Document translateXhtml(Document pDocument, RequestConfig pRequestConfig)
	{
		return TranslateProcessor.processXHTML(pDocument, pRequestConfig);
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
		if (pInputStream == null)
		{
			pLog.error("No se encuentra el fichero template " + strPathToTemplate);
		}
		strHtml = Utils.getStringFromInputStream(pInputStream);
		pDocument = Jsoup.parse(strHtml, "", Parser.htmlParser());
		return pDocument;
	}

	/**
	 * Comprueba si es necesario modificar los css para adaptarlos a multientidad
	 * 
	 * @param pDocument
	 *           Documento html en jsoup
	 * @param pRequestConfig
	 *           Datos de sesión de ruralvia para el usuario
	 * @return
	 * @throws Exception
	 */
	private static Document adjustCssMultiBank(Document pDocument, RequestConfig pRequestConfig) throws Exception
	{
		return CssMultiBankProcessor.processXHTML(pDocument, pRequestConfig);
	}

	/**
	 * Obtiene el nombre de la plantilla adecuado al canal que solicita la página
	 * 
	 * @param strPathToTemplate
	 *           Nombre de la plantilla
	 * @param pRequestConfigRvia
	 *           Datos de sesión de ruralvia para el usuario
	 * @return
	 * @throws Exception
	 */
	private static String adjustTemplateNameByChannel(String strPathToTemplate, RequestConfigRvia pRequestConfigRvia)
	{
		String strReturn = null;
		int nLastDot;
		strReturn = strPathToTemplate;
		switch (pRequestConfigRvia.getCanalFront())
		{
			case MOVIL:
				nLastDot = strPathToTemplate.lastIndexOf('.');
				if (nLastDot != -1)
					strReturn = strPathToTemplate.substring(0, nLastDot) + "_movil" + strPathToTemplate.substring(nLastDot);
				else
					strReturn = strPathToTemplate + "_movil";
				break;
			case TABLET:
				nLastDot = strPathToTemplate.lastIndexOf('.');
				if (nLastDot != -1)
					strReturn = strPathToTemplate.substring(0, nLastDot) + "_tablet" + strPathToTemplate.substring(nLastDot);
				else
					strReturn = strPathToTemplate + "_tablet";
				break;
			default:
				break;
		}
		pLog.debug("Se cambia si es necesario el nombre del template según el canal. CanalAix:"
				+ pRequestConfigRvia.getCanalFront().name() + " - CAMBIO: " + strPathToTemplate + " -> " + strReturn);
		return strReturn;
	}
}
