package com.rsi.rvia.rest.error;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.error.exceptions.ApplicationException;

public class ErrorResponse
{
	private static Logger	pLog	= LoggerFactory.getLogger(ErrorManager.class);
	private Integer			nHttpCode;
	private Integer			nInnerErrorCode;
	private String				strMessage;
	private String				strDescription;

	public Integer getHttpCode()
	{
		return nHttpCode;
	}

	public void setHttpCode(int nHttpCode)
	{
		this.nHttpCode = nHttpCode;
	}

	public Integer getInnerErrorCode()
	{
		return nInnerErrorCode;
	}

	public void setInnerErrorCode(int nErrorCode)
	{
		this.nInnerErrorCode = nErrorCode;
	}

	public String getMessage()
	{
		return strMessage;
	}

	public void setMessage(String strMessage)
	{
		this.strMessage = strMessage;
	}

	public String getDescription()
	{
		return strDescription;
	}


	public void setDescription(String strDescriptcion)
	{
		this.strDescription = strDescriptcion;
	}

	public ErrorResponse(ApplicationException ex)
	{
		if (ex.getHttpErrorCode() != null)
			nHttpCode = ex.getHttpErrorCode();
		else
			nHttpCode = 500;
		if (ex.getInnerErrorCode() != null)
			nInnerErrorCode = ex.getInnerErrorCode();
		else
			nInnerErrorCode = 9999999;
		if (ex.getMessage() != null && !ex.getMessage().trim().isEmpty())
			strMessage = ex.getMessage();
		if (ex.getDescription() != null && !ex.getDescription().trim().isEmpty())
			strDescription = ex.getDescription();
	}

	public ErrorResponse(Exception ex)
	{
		nHttpCode = 500;
		nInnerErrorCode = 9999999;
		strMessage = "Error de la aplicación";
		strDescription = "Error no controlado de la aplicación";
		if (ex.getDescription() != null && !ex.getDescription().trim().isEmpty())
			strDescription = ex.getDescription();
	}
	public ErrorResponse(Exception ex)
	{
		nHttpCode = 500;
		nInnerErrorCode = 9999999;
		strMessage = "Error de la aplicación";
		strDescription = "Error no controlado de la aplicación";
	}

	/**
	 * Devuelve el objeto ErrorResponse como un json
	 * 
	 * @return String con el JSON de respuesta.
	 */
	public String getJsonError()
	{
		String strReturn = "";
		JSONObject pJson;
		try
		{
			pJson = new JSONObject();
			pJson.put("code", this.getInnerErrorCode());
			pJson.put("httpCode", this.getHttpCode());
			pJson.put("message", this.getMessage());
			pJson.put("description", this.getDescription());
			strReturn = pJson.toString();
		}
		catch (JSONException ex)
		{
			pLog.error("Error al generar el JSON de Error", ex);
			strReturn = "{" + "\"code\":999999," + "\"httpCode\":500," + "\"message\":\"Error de la aplicación\","
					+ "\"description\":\"Error no controlado de la aplicación\"" + "}";
		}
		return strReturn;
	}
}
