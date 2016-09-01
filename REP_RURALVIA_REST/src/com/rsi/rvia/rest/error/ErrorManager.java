package com.rsi.rvia.rest.error;

import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorManager
{
	private static Logger pLog = LoggerFactory.getLogger(ErrorManager.class);

	public static String processError(String strEntity, int nStatusCode) throws JSONException
	{
		String strReturn = getJsonError("500", "Error interno en el servidor", "Error en el manejor de la respuesta.");
		if (isWebError(strEntity))
		{
			strReturn = getJsonFormRviaError(strEntity);
		}
		else
		{
			if (isWSError(strEntity))
			{
				JSONObject pJsonReader = new JSONObject(strEntity);
				String strPrimaryKey = "";
				Iterator<String> pKeys = pJsonReader.keys();
				if (pKeys.hasNext())
				{
					strPrimaryKey = (String) pKeys.next();
				}
				if (!strPrimaryKey.trim().isEmpty())
				{
					JSONObject pJsonContent = new JSONObject();
					JSONObject pJsonErrors = new JSONObject();
					pJsonContent = pJsonReader.getJSONObject(strPrimaryKey);
					if (pJsonContent != null)
					{
						pJsonErrors = pJsonContent.getJSONObject("Errores");
					}
					if (pJsonErrors != null)
					{
						String strCode = pJsonErrors.getString("codigoMostrar");
						String strMessage = pJsonErrors.getString("mensajeMostrar");
						String strDescription = pJsonErrors.getString("solucion");
						strReturn = getJsonError(strCode, strMessage, strDescription);
					}
				}
			}
			else
			{
				if (isHTTPError(nStatusCode))
				{
					String strCode = String.valueOf(nStatusCode);
					String strMessage = "Error " + nStatusCode;
					String strDescription = "Error " + nStatusCode;
					strReturn = getJsonError(strCode, strMessage, strDescription);
				}
			}
		}
		return strReturn;
	}

	public static boolean isHTTPError(int nStatusCode)
	{
		return (nStatusCode != 200);
	}

	public static boolean isWSError(String strHtml) throws JSONException
	{
		boolean fReturn = false;
		String strPrimaryKey = "";
		JSONObject pJsonReader = new JSONObject(strHtml);
		Iterator<String> pKeys = pJsonReader.keys();
		if (pKeys.hasNext())
		{
			strPrimaryKey = (String) pKeys.next();
		}
		if (!strPrimaryKey.trim().isEmpty())
		{
			JSONObject pJsonContent = new JSONObject();
			pJsonContent = pJsonReader.getJSONObject(strPrimaryKey);
			String strStatusResponse = (String) pJsonContent.get("codigoRetorno");
			if ("0".equals(strStatusResponse))
			{
				fReturn = true;
			}
		}
		return fReturn;
	}

	public static boolean isWebError(String strHtml)
	{
		boolean fReturn = false;
		Document pDoc = Jsoup.parse(strHtml);
		if ((pDoc.getElementsByClass("txtaviso") != null) && (pDoc.getElementsByClass("txtaviso").size() > 0))
		{
			fReturn = true;
		}
		return fReturn;
	}

	public static String getJsonFormRviaError(String strHtml) throws JSONException
	{
		String strReturn = "";
		String strCode = "";
		String strMessage = "";
		String strDescription = "";
		Element pError = null;
		Document pDoc = Jsoup.parse(strHtml);
		if ((pDoc.getElementsByClass("txtaviso") != null) && (pDoc.getElementsByClass("txtaviso").size() > 0))
		{
			pError = pDoc.getElementsByClass("txtaviso").get(0);
		}
		if (pError != null)
		{
			String[] pStrPartes = pError.text().split(":");
			if ((pStrPartes != null) && (pStrPartes.length > 1))
			{
				strCode = pStrPartes[1].trim();
			}
			else
			{
				strCode = "0";
			}
			pStrPartes = strHtml.split("<!-- TEXTO DE ERROR ORIGINAL ");
			if ((pStrPartes != null) && (pStrPartes.length > 1))
			{
				String[] pStrPartes2 = pStrPartes[1].split("-->");
				if ((pStrPartes2 != null) && (pStrPartes2.length > 1))
				{
					strMessage = pStrPartes2[0];
				}
			}
			else
			{
				strMessage = "Error rvia";
			}
			strDescription = "Error Rvia: " + strMessage;
			strReturn = getJsonError(strCode, strMessage, strDescription);
		}
		else
		{
			strReturn = "{}";
		}
		return strReturn;
	}

	public static String getJsonError(String strCode, String strMessage, String strDescription)
	{
		String strReturn = "";
		JSONObject pJson;
		try
		{
			pJson = new JSONObject();
			pJson.put("code", strCode);
			pJson.put("message", strMessage);
			pJson.put("description", strDescription);
			strReturn = pJson.toString();
		}
		catch (JSONException ex)
		{
			pLog.error("Error al formar el JSON de Error");
			strReturn = "{}";
		}
		return strReturn;
	}
	
	public static boolean isJsonError(String strJson) throws JSONException{
		boolean fReturn = false;
		
		JSONObject pJson = new JSONObject(strJson);
		String strCodeStatus = pJson.getString("code");
		if(strCodeStatus != null){
			fReturn = true;
		}
		return fReturn;
	}
	public static String getCodeError(String strJson) throws JSONException{
		String strReturn = "0";
		
		JSONObject pJson = new JSONObject(strJson);
		String strCodeStatus = pJson.getString("code");
		if(strCodeStatus != null){
			strReturn = strCodeStatus;
		}
		return strReturn;
	}
}
