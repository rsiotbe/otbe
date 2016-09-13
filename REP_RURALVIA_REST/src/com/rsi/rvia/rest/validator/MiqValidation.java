package com.rsi.rvia.rest.validator;

import org.json.JSONException;
import org.json.JSONObject;

/** Objeto que representa una operativa o operación definida en la aplicación */
public class MiqValidation
{
	private int		nParamLong;
	private int		nParamMin;
	private int		nParamMax;
	private String	strParamMask;
	private String	strParamDataType;
	private String	strParamName;
	private String	strParamAliasName;

	public int getParamLong()
	{
		return nParamLong;
	}

	public int getParamMin()
	{
		return nParamMin;
	}

	public int getParamMax()
	{
		return nParamMax;
	}

	public String getParamMask()
	{
		return strParamMask;
	}

	public String getParamDataType()
	{
		return strParamDataType;
	}

	public String getParamName()
	{
		return strParamName;
	}

	public String getParamAliasName()
	{
		return strParamAliasName;
	}

	public MiqValidation(int nParamLong, String strParamMin, String strParamMax, String strParamMask,
			String strParamDataType, String strParamName, String strParamAliasName)
	{
		this.nParamLong = nParamLong;
		if (strParamMin != null)
		{
			try
			{
				this.nParamMin = Integer.parseInt(strParamMin);
			}
			catch (Exception ex)
			{
				this.nParamMin = -1;
			}
		}
		else
		{
			this.nParamMin = -1;
		}
		if (strParamMax != null)
		{
			try
			{
				this.nParamMax = Integer.parseInt(strParamMax);
			}
			catch (Exception ex)
			{
				this.nParamMax = -1;
			}
		}
		else
		{
			this.nParamMax = -1;
		}
		this.strParamMask = strParamMask;
		this.strParamDataType = strParamDataType;
		this.strParamName = strParamName;
		this.strParamAliasName = strParamAliasName;
	}

	public JSONObject getJson() throws JSONException
	{
		JSONObject pJson = new JSONObject();
		pJson.put("Longitud", this.nParamLong);
		pJson.put("Minimo", this.nParamMin);
		pJson.put("Maximo", this.nParamMin);
		pJson.put("Mascara Fecha", this.strParamMask);
		pJson.put("Nombre", this.strParamName);
		pJson.put("Tipo de Dato", this.strParamDataType);
		pJson.put("Alias", this.strParamAliasName);
		return pJson;
	}
}
