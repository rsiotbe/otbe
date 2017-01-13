package com.rsi.rvia.translates;

import java.io.IOException;
import java.util.Hashtable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.rsi.rvia.rest.tool.Utils;

/** Servlet implementation class translateService */
public class TranslateService extends HttpServlet
{
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
		String strlanguage;
		String[] astrIds = null;
		String[] astrApps = null;
		Hashtable<String, String> htTranslates = null;
		Hashtable<String, Hashtable<String, String>> htTranslatesGroup = null;
		strIds = pRequest.getParameter(IDS_PARAM);
		strApps = pRequest.getParameter(APP_PARAM);
		strlanguage = pRequest.getParameter(LANGUAGE_PARAM);
		if (strApps != null && !strApps.trim().isEmpty())
		{
			astrApps = strApps.split(PARAM_SEP);
			if (strlanguage == null)
				htTranslatesGroup = TranslateProcessor.processApps(astrApps);
			else
				htTranslates = TranslateProcessor.processApps(astrApps, strlanguage);
		}
		else if (strIds != null && !strIds.trim().isEmpty())
		{
			astrIds = strIds.split(PARAM_SEP);
			if (strlanguage == null)
				htTranslatesGroup = TranslateProcessor.processIds(astrIds);
			else
				htTranslates = TranslateProcessor.processIds(astrIds, strlanguage);
		}
		else
		{
			pResponse.sendError(HttpServletResponse.SC_NO_CONTENT);
			return;
		}
		try
		{
			if (strlanguage == null)
				strJSONReturn = Utils.objectToJson(htTranslatesGroup);
			else
				strJSONReturn = Utils.objectToJson(htTranslates);
		}
		catch (Exception e)
		{
			strJSONReturn = "{\"Error\":\"Fallo al obtener JSON\"}";
		}
		pResponse.setContentType("application/json;charset=UTF-8");
		pResponse.getWriter().print(strJSONReturn);
	}
}
