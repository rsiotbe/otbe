package com.rsi.rvia.translates;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.tool.Utils;

/** Servlet implementation class translateService */
public class TranslateService extends HttpServlet
{
	private static Logger		pLog				= LoggerFactory.getLogger(TranslateService.class);
	private static final long	serialVersionUID	= 1L;
	private static final String	IDS_PARAM			= "id";
	private static final String	APP_PARAM			= "app";
	private static final String	PARAM_SEP			= ",";
	private static final String	LANGUAGE_PARAM		= "lang";

	/** @see HttpServlet#HttpServlet() */
	public TranslateService()
	{
		super();
	}

	/*
	 * Servlet dado una serie de IDs de parametros busca sus traducciones y las devuelve en formato JSON La respuesta
	 * vendra como {idTraduccion:traduccion}
	 */
	protected void service(HttpServletRequest pRequest, HttpServletResponse pResponse) throws ServletException,
			IOException
	{
		String strJSONReturn;
		String strIds;
		String strApps;
		String strLanguage;
		String[] astrIds = null;
		String[] astrApps = null;
		Hashtable<?, ?> htReturn = null;
		try
		{
			strIds = pRequest.getParameter(IDS_PARAM);
			strApps = pRequest.getParameter(APP_PARAM);
			strLanguage = pRequest.getParameter(LANGUAGE_PARAM);
			if (strApps != null && !strApps.trim().isEmpty())
			{
				astrApps = strApps.split(PARAM_SEP);
				if (strLanguage == null)
				{
					if (astrApps.length > 1)
					{
						pLog.info("Se recibe una petición para traducir las aplicaiones "
								+ astrApps.toString().replace("[", "").replace("]", "") + " a todos los idiomas");
						htReturn = processAppsAllLanguages(astrApps);
					}
					else
					{
						pLog.info("Se recibe una petición para traducir la aplicaión " + astrApps[0]
								+ " a todos los idiomas");
						htReturn = processOneAppAllLanguages(astrApps[0]);
					}
				}
				else
				{
					if (astrApps.length > 1)
					{
						pLog.info("Se recibe una petición para traducir las aplicaiones "
								+ astrApps.toString().replace("[", "").replace("]", "") + " a " + strLanguage);
						htReturn = processAppsOneLanguages(astrApps, strLanguage);
					}
					else
					{
						pLog.info("Se recibe una petición para traducir la aplicaión " + astrApps[0] + " a "
								+ strLanguage);
						htReturn = processOneAppOneLanguages(astrApps[0], strLanguage);
					}
				}
			}
			else if (strIds != null && !strIds.trim().isEmpty())
			{
				astrIds = strIds.split(PARAM_SEP);
				if (strLanguage == null)
				{
					pLog.info("Se recibe una petición para traducir ids a todos los idiomas");
					htReturn = processIdsAllLanguages(astrIds);
				}
				else
				{
					pLog.info("Se recibe una petición para traducir ids a " + strLanguage);
					htReturn = processIdsOneLanguages(astrIds, strLanguage);
				}
			}
			else
			{
				pResponse.sendError(HttpServletResponse.SC_NO_CONTENT);
				return;
			}
			strJSONReturn = Utils.objectToJson(htReturn);
		}
		catch (Exception ex)
		{
			strJSONReturn = "{\"Error\":\"Fallo al obtener JSON\",\"desc\":\""
					+ Utils.replaceIlegalCharactersInJSON(Utils.getExceptionStackTrace(ex)) + "\"}";
		}
		pResponse.setContentType("application/json;charset=UTF-8");
		pResponse.getWriter().print(strJSONReturn);
	}

	private Hashtable<String, Hashtable<String, String>> processIdsAllLanguages(String[] astrIds) throws Exception
	{
		Hashtable<String, Hashtable<String, String>> htReturn;
		Hashtable<String, TranslateEntry> htData;
		htData = TranslateProcessor.getTranslationsById(astrIds);
		htReturn = regroupHastableByLanguage(htData);
		return htReturn;
	}

	private Hashtable<String, String> processIdsOneLanguages(String[] astrIds, String strLanguage) throws Exception
	{
		return TranslateProcessor.getTranslationsById(astrIds, strLanguage);
	}

	private Hashtable<String, Hashtable<String, String>> processOneAppAllLanguages(String strAppName) throws Exception
	{
		Hashtable<String, Hashtable<String, String>> htReturn;
		Hashtable<String, TranslateEntry> htData;
		htData = TranslateProcessor.getTranslationsByApp(strAppName);
		htReturn = regroupHastableByLanguage(htData);
		return htReturn;
	}

	private Hashtable<String, String> processOneAppOneLanguages(String strAppName, String strLanguage) throws Exception
	{
		Hashtable<String, String> htReturn;
		htReturn = TranslateProcessor.getTranslationsByApp(strAppName, strLanguage);
		return htReturn;
	}

	private Hashtable<String, Hashtable<String, Hashtable<String, String>>> processAppsAllLanguages(String[] astrAppName)
			throws Exception
	{
		Hashtable<String, Hashtable<String, Hashtable<String, String>>> htReturn = new Hashtable<String, Hashtable<String, Hashtable<String, String>>>();
		Hashtable<String, TranslateEntry> htData;
		Hashtable<String, Hashtable<String, String>> htAux;
		for (String strAppName : astrAppName)
		{
			htData = TranslateProcessor.getTranslationsByApp(strAppName);
			htAux = regroupHastableByLanguage(htData);
			/* se añade la aplicación al resultado */
			htReturn.put(strAppName, htAux);
		}
		return htReturn;
	}

	private Hashtable<String, Hashtable<String, String>> processAppsOneLanguages(String[] astrAppName,
			String strLanguage) throws Exception
	{
		Hashtable<String, Hashtable<String, String>> htReturn = new Hashtable<String, Hashtable<String, String>>();
		Hashtable<String, String> htData;
		for (String strAppName : astrAppName)
		{
			htData = TranslateProcessor.getTranslationsByApp(strAppName, strLanguage);
			htReturn.put(strAppName, htData);
		}
		return htReturn;
	}

	private Hashtable<String, Hashtable<String, String>> regroupHastableByLanguage(
			Hashtable<String, TranslateEntry> htData)
	{
		Hashtable<String, Hashtable<String, String>> htReturn = new Hashtable<String, Hashtable<String, String>>();
		Enumeration<String> pEnumHtData = htData.keys();
		while (pEnumHtData.hasMoreElements())
		{
			String strHtCode = (String) pEnumHtData.nextElement();
			TranslateEntry pTranslations = htData.get(strHtCode);
			Enumeration<String> pEnumHtTranslates = pTranslations.getAllTranslations().keys();
			while (pEnumHtTranslates.hasMoreElements())
			{
				String strHtLanguage = (String) pEnumHtTranslates.nextElement();
				/* si no existe todavia el idioma en el hashtable de salida se añade una nueva entrada */
				if (!htReturn.containsKey(strHtLanguage))
				{
					htReturn.put(strHtLanguage, new Hashtable<String, String>());
				}
				Hashtable<String, String> htTranslationsLang = htReturn.get(strHtLanguage);
				/* se añade la traducción al resultado */
				htTranslationsLang.put(pTranslations.getCode(), pTranslations.getTranslation(strHtLanguage));
			}
		}
		return htReturn;
	}
}
