package com.rsi.rvia.rest.client;

import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.error.ErrorManager;
import com.rsi.rvia.rest.error.exceptions.RviaRestException;

/** Clase para manejar la respuesta del RestWSConnector. Mira si es un error o si no lo es y compone una respuesta JSON
 * Siguiendo la siguiente estructura: { "error": 0, ó 1 (si tiene o si no tiene) "response" : {...} (respuesta en JSON,
 * puede ser el error o la respuesta ya formada) } */
public class ResponseManager
{
	private static Logger pLog = LoggerFactory.getLogger(ResponseManager.class);

	public static String processResponse(String strJsonData, int nStatus) throws Exception
	{
		String strReturn = "{}";
		try
		{
			JSONObject pJson = new JSONObject();
			if ((nStatus != 200) || (ErrorManager.isWebError(strJsonData)))
			{
				pLog.info("Respuesta con errores.");
				strReturn = ErrorManager.processError(strJsonData, nStatus);
			}
			else
			{
				/* Respuesta sin errores */
				pLog.info("Respuesta sin errores.");
				if (!strJsonData.trim().isEmpty())
				{
					JSONObject pJsonReader = new JSONObject(strJsonData);
					String strPrimaryKey = "";
					Iterator<String> pKeys = pJsonReader.keys();
					if (pKeys.hasNext())
					{
						strPrimaryKey = (String) pKeys.next();
					}
					if (!strPrimaryKey.trim().isEmpty())
					{
						if ("response".equals(strPrimaryKey))
						{
							strReturn = strJsonData;
						}
						else
						{
							JSONObject pJsonContent = new JSONObject();
							pJsonContent = pJsonReader.getJSONObject(strPrimaryKey);
							String strStatusResponse = (String) pJsonContent.get("codigoRetorno");
							if ("1".equals(strStatusResponse))
							{
								pJson.put("response", pJsonContent.get("Respuesta"));
								strReturn = pJson.toString();
							}
							else
							{
								strReturn = ErrorManager.processError(strJsonData, nStatus);
							}
						}
					}
				}
			}
		}
		catch (Exception ex)
		{
			throw new RviaRestException(500, "Error al procesar el mensaje de respuesta del conector", ex);
		}
		return strReturn;
	}
}
