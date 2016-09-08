package com.rsi.rvia.translates;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;
import com.rsi.rvia.rest.tool.Utils;

/** Servlet implementation class translateService */
public class TranslateService extends HttpServlet
{
	private static final long		serialVersionUID	= 1L;
	private static final String	IDS_PARAM			= "id";
	private static final String	IDS_PARAM_SEP		= ",";
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
		String strlanguage;
		String[] astrIds = null;
		Hashtable<String, String> htTranslates;
		strIds = pRequest.getParameter(IDS_PARAM);
		strlanguage = pRequest.getParameter(LANGUAGE_PARAM);
		if (IDS_PARAM == null || IDS_PARAM.trim().isEmpty())
		{
			pResponse.sendError(HttpServletResponse.SC_NO_CONTENT);
			return;
		}
		if(strIds != null){
			astrIds = strIds.split(IDS_PARAM_SEP);
		}
		
		htTranslates = TranslateProcessor.processIds(astrIds, strlanguage);
		try
		{
			strJSONReturn = Utils.hashTable2Json(htTranslates);
		}
		catch (JSONException e)
		{
			strJSONReturn = "{\"Error\":\"Fallo al obtener JSON\"}";
		}
		pResponse.setContentType("application/json;charset=UTF-8");
		pResponse.getWriter().print(strJSONReturn);
	}
}
