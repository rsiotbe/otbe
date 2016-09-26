<%@page import="org.json.JSONArray"%>
<%@page import="com.rsi.rvia.multibank.CssMultiBankProcessor"%>
<%@page
	import="com.rsi.rvia.rest.template.TemplateManager,
	com.rsi.rvia.translates.TranslateProcessor,
	com.rsi.rvia.translates.TranslateEntry,
	com.rsi.rvia.multibank.CssMultiBankProcessor,
	javax.servlet.http.HttpServletRequest,
	org.slf4j.Logger,org.slf4j.LoggerFactory,
	java.util.Hashtable,
	org.json.JSONObject,
	org.json.JSONArray,
	java.util.Enumeration,
	org.jsoup.nodes.Document;"%>
<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%
	/* Para dar de alta una nueva Cache, añadir al Hashtable htCaches su identificador y la ejecución de su funcion 
	 getSizeCache(). Y añadir en el punto de liberar laches, marcado con un comentario [**] el reinicio de la cache */
	Logger pLog = LoggerFactory.getLogger("CleanCache.jsp");
	boolean fCheckStatus = false;
	Hashtable<String, String> htCaches = new Hashtable<String, String>();
	JSONObject pJson = new JSONObject();
	JSONArray pJsonArray = new JSONArray();
	String strResponse = "";
	String strCleanAll = (String) request.getParameter("clean");
	String strRefresh = (String) request.getParameter("refresh");
	String[] pStrPartes;
	/* se libera la cache */
	if (strCleanAll != null && !strCleanAll.trim().isEmpty())
	{
		pLog.info("Liberando Caches: " + strCleanAll);
		pStrPartes = strCleanAll.split(";");
		for (String strItem : pStrPartes)
		{
			pLog.trace("Libero cache: " + strItem);
			/* [**] */
			if ("Plantillas HTML".equals(strItem))
			{
				TemplateManager.restartCache();
			}
			else if ("Traducciones".equals(strItem))
			{
				TranslateProcessor.restartCache();
			}
			else if ("CSS Multientidad".equals(strItem))
			{
				CssMultiBankProcessor.restartCache();
			}
		}
		strRefresh = "true";
	}
	/* Se añaden las caches */
	htCaches.put("Plantillas HTML", (String) String.valueOf(TemplateManager.getSizeCache()));
	htCaches.put("Traducciones", (String) String.valueOf(TranslateProcessor.getSizeCache()));
	htCaches.put("CSS Multientidad", (String) String.valueOf(CssMultiBankProcessor.getSizeCache()));
	/* Se refresca la Cache */
	if (strRefresh != null && !strRefresh.trim().isEmpty())
	{
		pLog.info("Refrescando la página de caches.");
		for (Enumeration e = htCaches.keys(); e.hasMoreElements();)
		{
			String strKey = (String) e.nextElement();
			JSONObject pJsonObject = new JSONObject();
			pJsonObject.put("cacheName", strKey);
			pJsonObject.put("clean", fCheckStatus);
			pJsonObject.put("size", htCaches.get(strKey));
			pJsonArray.put(pJsonObject);
		}
		pJson.put("caches", pJsonArray);
		pLog.trace("Json Respuesta: " + pJson.toString());
	}
	strResponse = pJson.toString();
%><%=strResponse%>