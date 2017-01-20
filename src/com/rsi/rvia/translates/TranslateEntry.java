/************************************************************************
 * CREACION: REFERENCIA: P000008956 FECHA: 02-08-2016 AUTOR: Victor Muñoz Descripción: Clase contenedora de traducciones
 * por idioma MODIFICACIONES:
 ************************************************************************/
package com.rsi.rvia.translates;

import java.util.Hashtable;

/** Clase para guardar las diferentes traducciones en funcion del código de la traducción. */
public class TranslateEntry
{
	private final String				strAppName;
	private final String				strCode;
	private Hashtable<String, String>	htTranslations;

	/**
	 * Constructor de la clase
	 * 
	 * @param strCode
	 *            Codigo de la traducción.
	 */
	public TranslateEntry(String strCode, String strAppName)
	{
		this.strAppName = strCode;
		this.strCode = strCode;
		this.htTranslations = new Hashtable<String, String>();
	}

	/** @return Codigo de la traducción. */
	public String getCode()
	{
		return strCode;
	}

	/** @return Codigo de la traducción. */
	public String getAppName()
	{
		return strAppName;
	}

	/** @return Hashtable con las traducciones por idioma. */
	public Hashtable<String, String> getAllTranslations()
	{
		return htTranslations;
	}

	/**
	 * Devuelve una traducción concreta dado un idioma.
	 * 
	 * @param strLanguage
	 *            Idioma del que se quiere la traducción.
	 * @return String con la traducción en el idioma dado.
	 */
	public String getTranslation(String strLanguage)
	{
		if (this.htTranslations.containsKey(strLanguage))
			return htTranslations.get(strLanguage);
		else
			return null;
	}

	/**
	 * Añade una nueva traducción al Hashtable de traducciones.
	 * 
	 * @param strIdioma
	 *            Idioma de la nueva traducción.
	 * @param strTraduccion
	 *            Traducción nueva.
	 */
	public void addTranslation(String strLanguage, String strTranslate)
	{
		if (!htTranslations.containsKey(strLanguage))
			this.htTranslations.put(strLanguage, strTranslate);
		else
		{
			this.htTranslations.remove(strLanguage);
			this.htTranslations.put(strLanguage, strTranslate);
		}
	}

	public int getCountTanslations()
	{
		return htTranslations.size();
	}

	public String toString()
	{
		StringBuilder pSb = new StringBuilder();
		pSb.append("AppName      :" + strAppName + "\n");
		pSb.append("Code         :" + strCode + "\n");
		pSb.append("Translations :" + htTranslations + "\n");
		return pSb.toString();
	}
}
