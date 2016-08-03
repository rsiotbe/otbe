/************************************************************************ 
 * CREACION: REFERENCIA: P000008956 
 * FECHA: 02-08-2016 
 * AUTOR: Victor Mu�oz Descripci�n: 
 * Clase contenedora de traducciones por idioma 
 * 
 * MODIFICACIONES: 
 * ************************************************************************/
package com.rsi.rvia.translates;

import java.util.Hashtable;

/** Clase para guardar las diferentes traducciones en funcion del c�digo de la traducci�n. */
public class TranslateEntry
{
	private final String						strCode;
	private Hashtable<String, String>	htTranslates;

	/** Constructor de la clase.
	 * 
	 * @param strCode
	 *           Codigo de la traducci�n. */
	public TranslateEntry(String strCode)
	{
		this.strCode = strCode;
		this.htTranslates = new Hashtable<String, String>();
	}

	/** @return Codigo de la traducci�n. */
	public String getCode()
	{
		return strCode;
	}

	/** @return Hashtable con las traducciones por idioma. */
	public Hashtable<String, String> getTranslates()
	{
		return htTranslates;
	}

	/** Devuelve una traducci�n concreta dado un idioma.
	 * 
	 * @param strLanguage
	 *           Idioma del que se quiere la traducci�n.
	 * @return String con la traducci�n en el idioma dado. */
	public String getTranslate(String strLanguage)
	{
		if (this.htTranslates.containsKey(strLanguage))
			return htTranslates.get(strLanguage);
		else
			return null;
	}

	/** A�ade una nueva traducci�n al Hashtable de traducciones.
	 * 
	 * @param strIdioma
	 *           Idioma de la nueva traducci�n.
	 * @param strTraduccion
	 *           Traducci�n nueva. */
	public void addTranslate(String strLanguage, String strTranslate)
	{
		if (!htTranslates.containsKey(strLanguage))
			this.htTranslates.put(strLanguage, strTranslate);
		else
		{
			this.htTranslates.remove(strLanguage);
			this.htTranslates.put(strLanguage, strTranslate);
		}
	}
}
