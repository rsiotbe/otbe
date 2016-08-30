package com.rsi.rvia.rest.template;

import java.io.InputStream;
import java.util.Hashtable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.tool.Utils;
import com.rsi.rvia.translates.TranslateProcessor;

/** Clase para administar los templates. Inserta el Json en el template y utiliza el Translator para traducir las
 * etiquetas del template. */
public class TemplateManager
{
	static Logger										pLog					= LoggerFactory.getLogger(TemplateManager.class);
	public static String								JSON_DATA_TAG		= "'__JSONDATA__'";
	public static Hashtable<String, String>	htCacheTemplate	= new Hashtable<String, String>();

	public static String processTemplate(String strPathToTemplate, String strLanguage)
	{
		return processTemplate(strPathToTemplate, strLanguage, "{}");
	}

	/** Busca el template y lo lee. Carga las traducciones. Inyecta el script para ajustar el iframe e inyecta los datos
	 * en json.
	 * 
	 * @param strPathToTemplate
	 *           Ruta del template
	 * @param strLanguage
	 *           Idioma
	 * @param strDataJson
	 *           Datos en formato JSON.
	 * @return Template procesado. */
	public static String processTemplate(String strPathToTemplate, String strLanguage, String strDataJson)
	{
		String strReturn;
		try
		{
			String strCacheKey = htCacheTemplate + "_" + strLanguage;
			pLog.debug("strCacheKey: " + strCacheKey);
			if (htCacheTemplate.containsKey(strCacheKey))
				strReturn = htCacheTemplate.get(strCacheKey);
			else
			{
				strReturn = readTemplate(strPathToTemplate);
				strReturn = translateXhtml(strReturn, strLanguage);
				htCacheTemplate.put(strCacheKey, strReturn);
			}
			strReturn = includeIframeScript(strReturn);
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
		Document pHtml = Jsoup.parse(strReturn);
		pHtml.outputSettings().prettyPrint(false);
		Element pScript = pHtml.createElement("script");
		pScript.attr("src", "http://cdn.jsdelivr.net/iframe-resizer/3.5.3/iframeResizer.contentWindow.min.js");
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
			pLog.info("RESULTADO: " + strJsonData.replace("\"", "\\\""));
			return strXhtml.replace(JSON_DATA_TAG, strJsonData.replaceAll("\"", "\\\""));
		}
	}

	/** Añade el contenido de las traducciones al HTML
	 * 
	 * @param strXhtml
	 * @param strLanguage
	 * @return HTML con las traducciones */
	private static String translateXhtml(String strXhtml, String strLanguage)
	{
		return TranslateProcessor.processXHTML(strXhtml, strLanguage);
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
}
