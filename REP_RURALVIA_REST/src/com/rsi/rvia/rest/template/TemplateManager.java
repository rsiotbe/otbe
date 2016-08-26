package com.rsi.rvia.rest.template;

import java.io.InputStream;
import java.util.Hashtable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.tool.Utils;
import com.rsi.rvia.translates.TranslateProcessor;

public class TemplateManager
{
	static Logger	pLog	= LoggerFactory.getLogger(TemplateManager.class);
	public static String JSON_DATA_TAG = "'__JSONDATA__'";
	public static Hashtable<String, String> htCacheTemplate = new Hashtable<String, String>();
	
	public static String processTemplate (String strPathToTemplate, String strLanguage)
	{
		return processTemplate(strPathToTemplate, strLanguage, "{}");
	}
	
	public static String processTemplate (String strPathToTemplate, String strLanguage, String strDataJson)
	{
		String strReturn;
		try
		{
			String strCacheKey = htCacheTemplate + "_" + strLanguage;
			pLog.debug("strCacheKey: " + strCacheKey);
			if(htCacheTemplate.containsKey(strCacheKey))
				strReturn = htCacheTemplate.get(strCacheKey);
			else
			{
				strReturn = readTemplate(strPathToTemplate);
				strReturn = translateXhtml(strReturn, strLanguage);
				htCacheTemplate.put(strCacheKey, strReturn);
			}
			strReturn = includeJsonData(strReturn, strDataJson);
		}
		catch (Exception ex)
		{
			strReturn = null;
			pLog.error("No ha sido posible procesar la plantilla xhtml", ex);
		}
		return strReturn;
	}
	
	private static String includeJsonData(String strXhtml, String strJsonData)
	{
		if(strJsonData == null || strJsonData.trim().isEmpty() )
			return strXhtml;
		else{
			pLog.info("RESULTADO: " + strJsonData.replace("\"", "\\\""));
			return strXhtml.replace(JSON_DATA_TAG, strJsonData.replaceAll("\"", "\\\""));
			
		}
	}
	
	private static String translateXhtml(String strXhtml, String strLanguage)
	{
		return TranslateProcessor.processXHTML(strXhtml, strLanguage);
	}

	private static String readTemplate(String strPathToTemplate)
	{
		String strReturn = "";
		InputStream pInputStream = (TemplateManager.class.getResourceAsStream(strPathToTemplate));
		strReturn = Utils.getStringFromInputStream(pInputStream);
		return strReturn;
	}

	
}
