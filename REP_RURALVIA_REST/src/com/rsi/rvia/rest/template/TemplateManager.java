package com.rsi.rvia.rest.template;

import java.io.InputStream;
import java.util.Hashtable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.multibank.CssMultiBankProcessor;
import com.rsi.rvia.rest.session.SessionRviaData;
import com.rsi.rvia.rest.tool.Utils;
import com.rsi.rvia.translates.TranslateProcessor;

/** Clase para administar los templates. Inserta el Json en el template y utiliza el Translator para traducir las
 * etiquetas del template. */
public class TemplateManager
{
	static Logger										pLog							= LoggerFactory.getLogger(TemplateManager.class);
	public final static String						JSON_DATA_TAG				= "'__JSONDATA__'";
	private final static String					IFRAME_SCRIPT_ADAPTER	= "http://cdn.jsdelivr.net/iframe-resizer/3.5.3/iframeResizer.contentWindow.min.js";
	public static Hashtable<String, String>	htCacheTemplate			= new Hashtable<String, String>();

	/** Devuelve el tamaño de la cache
	 * 
	 * @return int con el tamaño de la cache */
	public static int getSizeCache()
	{
		int nReturn = 0;
		if (htCacheTemplate != null)
		{
			nReturn = htCacheTemplate.size();
		}
		return nReturn;
	}

	/** Busca el template y lo lee, carga las traducciones, inyecta el script para ajustar el iframe, ajusta los estilos
	 * para multicanalidad
	 * 
	 * @param strPathToTemplate
	 * @param pSessionRviaData
	 * @return */
	public static String processTemplate(String strPathToTemplate, SessionRviaData pSessionRviaData)
	{
		return processTemplate(strPathToTemplate, pSessionRviaData, "{}");
	}

	/** Busca el template y lo lee, carga las traducciones, inyecta el script para ajustar el iframe, ajusta los estilos
	 * para multicanalidad e inyecta los datos en json.
	 * 
	 * @param strPathToTemplate
	 *           Ruta del template
	 * @param pSessionRviaData
	 *           Objeto datos de sesion ruralvia
	 * @param strDataJson
	 *           Datos en formato JSON.
	 * @return Template procesado. */
	public static String processTemplate(String strPathToTemplate, SessionRviaData pSessionRviaData, String strDataJson)
	{
		String strReturn;
		try
		{
			String strCacheKey = strPathToTemplate;
			if(pSessionRviaData != null)
				strCacheKey = strPathToTemplate + "_" + pSessionRviaData.getLanguage();
			else
				
			pLog.debug("strCacheKey: " + strCacheKey);
			if (htCacheTemplate.containsKey(strCacheKey))
				strReturn = htCacheTemplate.get(strCacheKey);
			else
			{
				strReturn = readTemplate(strPathToTemplate);
				strReturn = translateXhtml(strReturn, pSessionRviaData);
				htCacheTemplate.put(strCacheKey, strReturn);
			}
			strReturn = includeIframeScript(strReturn);
			strReturn = adjustCssMultiBank(strReturn, pSessionRviaData);
			strReturn = includeJsonData(strReturn, strDataJson);
		}
		catch (Exception ex)
		{
			strReturn = null;
			pLog.error("No ha sido posible procesar la plantilla xhtml", ex);
		}
		return strReturn;
	}

	/** Incluye el script para ajustar el tamaño del iframe
	 * 
	 * @param strReturn
	 * @return HTML con la etiqueta script insertada */
	private static String includeIframeScript(String strReturn)
	{
		Document pHtml = (Document) Jsoup.parse(strReturn, "", Parser.htmlParser());
		pHtml.outputSettings().prettyPrint(false);
		Element pScript = pHtml.createElement("script");
		pScript.attr("src", IFRAME_SCRIPT_ADAPTER);
		pHtml.body().appendChild(pScript);
		return pHtml.html();
	}

	/** Añade el Json al template de salida
	 * 
	 * @param strXhtml
	 * @param strJsonData
	 * @return HTML con el json de datos añadido */
	private static String includeJsonData(String strXhtml, String strJsonData)
	{
		if (strJsonData == null || strJsonData.trim().isEmpty())
			return strXhtml;
		else
		{
			pLog.info("RESULTADO: " + strJsonData);
			return strXhtml.replace(JSON_DATA_TAG, strJsonData.replaceAll("\"", "\\\""));
		}
	}

	/** Añade el contenido de las traducciones al HTML
	 * 
	 * @param strXhtml
	 *           Codigo xhtml evaluado hasta entonces
	 * @param pSessionRviaData
	 *           Datos de sesión de ruralvia para el usuario
	 * @return HTML con las traducciones */
	private static String translateXhtml(String strXhtml, SessionRviaData pSessionRviaData)
	{
		return TranslateProcessor.processXHTML(strXhtml, pSessionRviaData);
	}

	/** Abre el XHTML para procesarlo
	 * 
	 * @param strPathToTemplate
	 * @return XHTML sin procesar */
	private static String readTemplate(String strPathToTemplate)
	{
		String strReturn = "";
		InputStream pInputStream = (TemplateManager.class.getResourceAsStream(strPathToTemplate));
		strReturn = Utils.getStringFromInputStream(pInputStream);
		return strReturn;
	}

	/** Comprueba si es necesario modificar los css para adaptarlos a multientidad
	 * 
	 * @param strXhtml
	 *           Codigo xhtml evaluado hasta entonces
	 * @param pSessionRviaData
	 *           Datos de sesión de ruralvia para el usuario
	 * @return 
	 * @throws Exception */
	private static String adjustCssMultiBank(String strXhtml, SessionRviaData pSessionRviaData) throws Exception
	{
		return CssMultiBankProcessor.processXHTML(strXhtml, pSessionRviaData);
	}
}
