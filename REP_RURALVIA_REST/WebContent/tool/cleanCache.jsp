<%@page import="com.rsi.rvia.multibank.CssMultiBankProcessor"%>
<%@page
	import="com.rsi.rvia.rest.template.TemplateManager,
	com.rsi.rvia.translates.TranslateProcessor,
	com.rsi.rvia.translates.TranslateEntry,
	javax.servlet.http.HttpServletRequest,
	org.slf4j.Logger,org.slf4j.LoggerFactory,
	java.util.Hashtable,
	org.json.JSONObject,
	org.jsoup.nodes.Document;"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<%
	Logger pLog = LoggerFactory.getLogger("CleanCache.jsp");
	boolean fCheckStatus = false;
	JSONObject pJson = new JSONObject();
	String strResponse = "";
	String strCleanAll = (String) request.getParameter("clean");
	pLog.trace("StrCleanAll: " + strCleanAll);
	String[] pStrPartes;
	if (!strCleanAll.trim().isEmpty())
	{
		pStrPartes = strCleanAll.split(",");
		for (String strItem : pStrPartes)
		{
			pLog.trace("strItem: " + strItem);
			if ("all".equals(strItem))
			{
				try
				{
					TranslateProcessor.htCacheData = new Hashtable<String, TranslateEntry>();
					TemplateManager.htCacheTemplate = new Hashtable<String, Document>();
					CssMultiBankProcessor.htCacheData = new Hashtable<String, String>();
					fCheckStatus = true;
					JSONObject pJsonTrans = new JSONObject();
					pJsonTrans.put("clean", fCheckStatus);
					pJsonTrans.put("size", TranslateProcessor.getSizeCache());
					pJson.put("translate", pJsonTrans);
					JSONObject pJsonTemplate = new JSONObject();
					pJsonTemplate.put("clean", fCheckStatus);
					pJsonTemplate.put("size", TemplateManager.getSizeCache());
					pJson.put("template", pJsonTemplate);
					JSONObject pJsonCssMultibank = new JSONObject();
					pJsonCssMultibank.put("clean", fCheckStatus);
					pJsonCssMultibank.put("size", CssMultiBankProcessor.getSizeCache());
					pJson.put("cssMultibank", pJsonCssMultibank);
					JSONObject pJsonAll = new JSONObject();
					pJsonAll.put("clean", fCheckStatus);
					pJsonAll.put("size", TemplateManager.getSizeCache() + TranslateProcessor.getSizeCache());
					pJson.put("all", pJsonAll);
				}
				catch (Exception ex)
				{
					fCheckStatus = false;
				}
			}
			if ("template".equals(strItem))
			{
				try
				{
					TemplateManager.htCacheTemplate = new Hashtable<String, Document>();
					fCheckStatus = true;
					JSONObject pJsonTemplate = new JSONObject();
					pJsonTemplate.put("clean", fCheckStatus);
					pJsonTemplate.put("size", TemplateManager.getSizeCache());
					pJson.put("template", pJsonTemplate);
				}
				catch (Exception ex)
				{
					fCheckStatus = false;
				}
			}
			else if ("translate".equals(strItem))
			{
				try
				{
					TranslateProcessor.htCacheData = new Hashtable<String, TranslateEntry>();
					fCheckStatus = true;
					JSONObject pJsonTrans = new JSONObject();
					pJsonTrans.put("clean", fCheckStatus);
					pJsonTrans.put("size", TranslateProcessor.getSizeCache());
					pJson.put("translate", pJsonTrans);
				}
				catch (Exception ex)
				{
					fCheckStatus = false;
				}
			}
			else if ("cssMultibank".equals(strItem))
			{
				try
				{
					CssMultiBankProcessor.htCacheData = new Hashtable<String, String>();
					fCheckStatus = true;
					JSONObject pJsonTrans = new JSONObject();
					pJsonTrans.put("clean", fCheckStatus);
					pJsonTrans.put("size", CssMultiBankProcessor.getSizeCache());
					pJson.put("cssMultibank", pJsonTrans);
				}
				catch (Exception ex)
				{
					fCheckStatus = false;
				}
			}
		}
	}
	strResponse = pJson.toString();
%><%=strResponse%>
