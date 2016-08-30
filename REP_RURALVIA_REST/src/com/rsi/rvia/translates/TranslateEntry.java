/************************************************************************ CREACION: REFERENCIA: P000008956 FECHA: 02-08-2016 AUTOR: Victor Muñoz Descripción: Clase contenedora de traducciones
 * por idioma MODIFICACIONES: ************************************************************************/
package com.rsi.rvia.translates;

import java.util.Hashtable;

/** Clase para guardar las diferentes traducciones en funcion del código de la traducción. */
public class TranslateEntry
{
	private final String						strCode;
	private Hashtable<String, String>	htTranslates;

	/** Constructor de la clase.
	 * 
	 * @param strCode
	 *           Codigo de la traducción. */
	public TranslateEntry(String strCode)
	{
		this.strCode = strCode;
		this.htTranslates = new Hashtable<String, String>();
	}

	/** @return Codigo de la traducción. */
	public String getCode()
	{
		return strCode;
	}

	/** @return Hashtable con las traducciones por idioma. */
	public Hashtable<String, String> getTranslates()
	{
		return htTranslates;
	}

	/** Devuelve una traducción concreta dado un idioma.
	 * 
	 * @param strLanguage
	 *           Idioma del que se quiere la traducción.
	 * @return String con la traducción en el idioma dado. */
	public String getTranslate(String strLanguage)
	{
		if (this.htTranslates.containsKey(strLanguage))
			return htTranslates.get(strLanguage);
		else
			return null;
	}

	/** Añade una nueva traducción al Hashtable de traducciones.
	 * 
	 * @param strIdioma
	 *           Idioma de la nueva traducción.
	 * @param strTraduccion
	 *           Traducción nueva. */
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
