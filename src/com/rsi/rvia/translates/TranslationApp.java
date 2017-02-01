/************************************************************************
 * CREACION: REFERENCIA: P000008956 FECHA: 02-08-2016 AUTOR: Victor Muñoz Descripción: Clase contenedora de traducciones
 * por idioma MODIFICACIONES:
 ************************************************************************/
package com.rsi.rvia.translates;

import java.util.Enumeration;
import java.util.Hashtable;

/** Clase para guardar las diferentes traducciones en funcion del código de la traducción. */
public class TranslationApp
{
	private final String						strAppName;
	private Hashtable<String, TranslateEntry>	htTranslatesCodes;

	/**
	 * Constructor de la clase
	 * 
	 * @param strCode
	 *            Codigo de la traducción.
	 */
	public TranslationApp(String strAppName)
	{
		this.strAppName = strAppName;
		this.htTranslatesCodes = new Hashtable<String, TranslateEntry>();
	}

	/** @return Codigo de la traducción. */
	public String getAppName()
	{
		return strAppName;
	}

	/** @return Hashtable con las traducciones por idioma. */
	public Hashtable<String, TranslateEntry> getAllTranslations()
	{
		return htTranslatesCodes;
	}

	public TranslateEntry getTranslation(String strCode)
	{
		return htTranslatesCodes.get(strCode);
	}

	public String getTranslation(String strCode, String strLanguage)
	{
		if (htTranslatesCodes.get(strCode) != null)
			return htTranslatesCodes.get(strCode).getTranslation(strLanguage);
		else
			return null;
	}

	public boolean existTranslation(String strCode)
	{
		return htTranslatesCodes.containsKey(strCode);
	}

	public boolean existTranslationInLanguaje(String strCode, String strLang)
	{
		return (htTranslatesCodes.containsKey(strCode) && (this.getTranslation(strCode, strLang) != null));
	}

	public int getCountTanslations()
	{
		int nCnt = 0;
		Enumeration<String> e = htTranslatesCodes.keys();
		while (e.hasMoreElements())
		{
			String strCode = (String) e.nextElement();
			nCnt += htTranslatesCodes.get(strCode).getCountTanslations();
		}
		return nCnt;
	}

	public void addTranslateEntry(String strCode, TranslateEntry pTranslateEntry)
	{
		htTranslatesCodes.put(strCode, pTranslateEntry);
	}

	public String toString()
	{
		StringBuilder pSb = new StringBuilder();
		pSb.append("AppName      :" + strAppName + "\n");
		pSb.append("Translations :" + htTranslatesCodes.size() + "\n");
		return pSb.toString();
	}
}
