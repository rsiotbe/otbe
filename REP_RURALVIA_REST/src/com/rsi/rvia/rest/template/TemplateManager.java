package com.rsi.rvia.rest.template;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.DDBB.OracleDDBB;
import com.rsi.rvia.rest.tool.LogController;
import com.rsi.rvia.translates.TranslateProcessor;

public class TemplateManager
{
	static Logger	pLog	= LoggerFactory.getLogger(OracleDDBB.class);
	private static LogController pLogC = new LogController();
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
			pLogC.addLog("Error", "No ha sido posible procesar la plantilla xhtml: " + ex);
		}
		return strReturn;
	}
	
	private static String includeJsonData(String strXhtml, String strJsonData)
	{
		if(strJsonData == null || strJsonData.trim().isEmpty() )
			return strXhtml;
		else
			return strXhtml.replace(JSON_DATA_TAG, strJsonData);
	}
	
	private static String translateXhtml(String strXhtml, String strLanguage)
	{
		return TranslateProcessor.processXHTML(strXhtml, strLanguage);
	}

	private static String readTemplate(String strPathToTemplate)
	{
		String strReturn = "";
		InputStream pInputStream = (TemplateManager.class.getResourceAsStream(strPathToTemplate));
		strReturn = getStringFromInputStream(pInputStream);
		return strReturn;
	}

	private static String getStringFromInputStream(InputStream is)
	{
		BufferedReader pBufferedReader = null;
		StringBuilder pStringBuilder = new StringBuilder();
		String strLine;
		try
		{
			pBufferedReader = new BufferedReader(new InputStreamReader(is));
			while ((strLine = pBufferedReader.readLine()) != null)
			{
				pStringBuilder.append(strLine);
			}
		}
		catch (Exception ex)
		{
			pLog.error("No es posible leer el contenido del template", ex);
			pLogC.addLog("Error", "No es posible leer el contenido del template" + ex);
		}
		finally
		{
			if (pBufferedReader != null)
			{
				try
				{
					pBufferedReader.close();
				}
				catch (Exception ex)
				{
					pLog.error("No es posible cerrar el StringBuilder", ex);
					pLogC.addLog("Error", "No es posible cerrar el StringBuilder" + ex);
				}
			}
		}
		return pStringBuilder.toString();
	}
}
