package com.rsi.rvia.rest.session;

import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.Constants;

/**
 * Clase que contiene la información asoiada ala petición del usuario y que se utiliza para configurar el comportamiento
 * del resultado
 */
public class RequestConfig
{
	private static Logger	pLog	= LoggerFactory.getLogger(RequestConfig.class);
	protected String		strLanguage;
	protected String		strNRBE;

	public String getLanguage()
	{
		return strLanguage;
	}

	public String getNRBE()
	{
		return strNRBE;
	}

	/**
	 * Constructor pasando parámetros
	 * 
	 * @param strLang
	 *            String con el lenguaje, por defecto si viene vacio o a null se pondra es_ES
	 * @param strNRBE
	 *            String con el NRBE, por defecto si viene vacio o a null se pondre 0198
	 */
	public RequestConfig(String strLang, String strNRBE)
	{
		pLog.debug("Se procede a cargar la configuración de la petición leyendo parámetros");
		setValues(strLang, strNRBE);
	}

	/**
	 * Constructor pasando objeto request
	 * 
	 * @param request
	 *            Objeto request recibido
	 * @throws Exception
	 */
	public RequestConfig(HttpServletRequest request) throws Exception
	{
		pLog.debug("Se procede a cargar la configuración de la petición leyendo objeto request");
		setValues(request.getParameter(Constants.PARAM_LANG), request.getParameter(Constants.PARAM_NRBE));
	}

	/**
	 * Constructor pasando objeto JSOn
	 * 
	 * @param request
	 *            Objeto JSON recibido
	 * @throws Exception
	 */
	public RequestConfig(JSONObject pJSONObject) throws Exception
	{
		pLog.debug("Se procede a cargar la configuración de la petición leyendo objeto JSON");
		setValues(pJSONObject.optString(Constants.PARAM_LANG), pJSONObject.optString(Constants.PARAM_NRBE));
	}

	/**
	 * Constructor pasando parámetros
	 * 
	 * @param strLang
	 *            String con el lenguaje, por defecto si viene vacio o a null se pondra es_ES
	 * @param strNRBE
	 *            String con el NRBE, por defecto si viene vacio o a null se pondre 0198
	 * @param strAppName
	 *            String con el nombre del aplicativo que responde a la petición
	 */
	private void setValues(String strLang, String strNRBE)
	{
		if (strLang == null || strLang.trim().isEmpty())
		{
			this.strLanguage = Constants.DEFAULT_LANGUAGE;
			pLog.warn("No se recibe parámetro de configuración de idioma, se coge por defecto español (es_ES)");
		}
		else
		{
			this.strLanguage = strLang;
		}
		if (strNRBE == null || strNRBE.trim().isEmpty())
		{
			this.strNRBE = Constants.CODIGO_BANCO_COOPERATIVO_ESPANOL;
			pLog.warn("No se recibe parámetro de configuración de entidad, se coge por defecto Banco cooperativo (0198)");
		}
		else
		{
			this.strNRBE = strNRBE;
		}
		pLog.info("Valores cargados. strLanguage: " + strLanguage + " - strNRBE: " + strNRBE);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuilder pSb = new StringBuilder();
		pSb.append("Language        :" + strLanguage + "\n");
		pSb.append("NRBE            :" + strNRBE + "\n");
		return pSb.toString();
	}
}
