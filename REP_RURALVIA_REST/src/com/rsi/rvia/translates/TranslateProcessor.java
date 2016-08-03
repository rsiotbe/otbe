/************************************************************************ CREACION: REFERENCIA: P000008956 FECHA: 02-08-2016 AUTOR: Victor Muñoz Descripción: Clase procesadora de traducciones
 * en HTML MODIFICACIONES: ************************************************************************/
package com.rsi.rvia.translates;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.DDBB.DDBBConnection;
import com.rsi.rvia.rest.DDBB.DDBBFactory;
import com.rsi.rvia.rest.DDBB.DDBBFactory.DDBBProvider;

/** Clase que gestiona el cambio de idioma en el contenido HTML de la web. */
public class TranslateProcessor
{
	private static Logger										pLog			= LoggerFactory.getLogger(TranslateProcessor.class);
	private static Hashtable<String, TranslateEntry>	htCacheData	= new Hashtable<String, TranslateEntry>();

	/** 
	 * Función que recibe una serie de identificadores de traducción e idiona y obtien su traducción
	 * @param processIds Array de String con los identificadores
	 * @param strLanguage String con el idioma (es_ES)
	 * @return hastable con parejas identificador y su traducción. 
	 */
	public static Hashtable<String, String> processIds(String[] astrIds, String strLanguage)
	{
		ArrayList<String> alIdsTrans;
		Hashtable<String, String> htReturn;
		Hashtable<String, TranslateEntry> htTransData;
		htTransData = new Hashtable<String, TranslateEntry>();
		htReturn = new Hashtable<String, String>();
		alIdsTrans = new ArrayList<String>(Arrays.asList(astrIds));
		pLog.debug("Se reciben los siguientes ids para traducir. astrIds:" + astrIds);
		try
		{
			htTransData = getTranslations(alIdsTrans);
			pLog.debug("Traducciones recuperadas correctamente.");
		}
		catch (Exception ex)
		{
			pLog.error("Error al intentar recuperar las Traducciones de la BBDD", ex);
		}
		if (htTransData != null)
		{
			/* si no existe idioma se asigna espaol defecto */
			if (strLanguage == null || strLanguage.trim().isEmpty())
			{
				strLanguage = "es_ES";
				pLog.warn("No se ha definido idioma de la traduccion, se asigna el espanol");
			}
			for(int i =0; i < astrIds.length; i++ )
			{
				String strAuxId = astrIds[i];
				String strAuxTrans = htTransData.get(strAuxId).getTranslate(strLanguage);
				htReturn.put(strAuxId, strAuxTrans);
			}
		}
		pLog.debug("datos a devolver por el metodo. htReturn: " + htReturn);
		return htReturn;
	}

	/** Función Principal, recibe el XHTML y el Idioma y traduce este XHTML
	 * 
	 * @param strXHTML
	 *           String con el XHTML
	 * @param strLanguage
	 *           String con el idioma (es_ES)
	 * @return String con el HTML con la nueva traducción ya aplicada. */
	public static String processXHTML(String strXHTML, String strLanguage)
	{
		Document doc = null;
		String strReturn = null;
		ArrayList<String> alIdsTrans = null;
		Hashtable<String, TranslateEntry> htTransData = new Hashtable<String, TranslateEntry>();
		if (!strXHTML.isEmpty())
		{
			doc = strToDocumentParser(strXHTML);
			pLog.debug("String XHTML parseado a Documento correctamente.");
			if (doc != null)
			{
				alIdsTrans = extractIdsFromDocument(doc);
				pLog.debug("IDs de traducciones extraidos correctamente.");
				pLog.debug("alIdsTrans lenght: " + alIdsTrans.size());
			}
			if (alIdsTrans != null)
			{
				try
				{
					htTransData = getTranslations(alIdsTrans);
					pLog.debug("Traducciones recuperadas correctamente.");
				}
				catch (Exception ex)
				{
					pLog.error("Error al intentar recuperar las Traducciones de la BBDD", ex);
				}
			}
			if (htTransData != null)
			{
				if (strLanguage == null)
				{
					strLanguage = "es_ES";
				}
				pLog.debug("Documento premodificación null: " + (doc == null));
				doc = modifyDocument(doc, htTransData, strLanguage);
				pLog.debug("Documento modificado Correctamente. Tamaño de htTransData: "
						+ htTransData.size());
				if (doc != null)
				{
					strReturn = documentToString(doc);
				}
				else
				{
					pLog.debug("Doc null en último paso.");
				}
			}
		}
		return strReturn;
	}

	/** Función para recuperar las traducciones dada una lista de IDs.
	 * 
	 * @param alIdsTrans
	 *           ArrayList<String> con los IDs de las traducciones.
	 * @return HashTable con las traducciones en todos los idiomas de cada ID de la lista inicial
	 * @throws Exception */
	private static Hashtable<String, TranslateEntry> getTranslations(ArrayList<String> alIdsTrans) throws Exception
	{
		String strQuery = "SELECT codigo,idioma,traduccion FROM bdptb079_idioma where codigo in (";
		String strNewsIds = "";
		Hashtable<String, TranslateEntry> htResult = new Hashtable<String, TranslateEntry>();
		for (String strId : alIdsTrans)
		{
			if (!htCacheData.containsKey(strId))
			{
				if (!strNewsIds.equals(""))
				{
					strNewsIds += ",";
				}
				strNewsIds += "'" + strId + "'";
			}
			else
			{
				htResult.put(strId, (TranslateEntry) htCacheData.get(strId));
			}
		}
		strQuery += strNewsIds + ")";
		if (!strNewsIds.equals(""))
		{
			DDBBConnection pDDBBTranslate = DDBBFactory.getDDBB(DDBBProvider.Oracle);
			PreparedStatement pPS = pDDBBTranslate.prepareStatement(strQuery);
			ResultSet pQueryResult = pPS.executeQuery();
			while (pQueryResult.next())
			{
				String strCodigo = (String) pQueryResult.getString("codigo");
				String strIdioma = (String) pQueryResult.getString("idioma");
				String strTraduccion = (String) pQueryResult.getString("traduccion");
				if (!htResult.containsKey(strCodigo))
				{
					TranslateEntry pTrans = new TranslateEntry(strCodigo);
					htResult.put(strCodigo, pTrans);
					if (!htCacheData.containsKey(strCodigo))
					{
						htCacheData.put(strCodigo, pTrans);
					}
				}
				htResult.get(strCodigo).addTranslate(strIdioma, strTraduccion);
				htCacheData.get(strCodigo).addTranslate(strIdioma, strTraduccion);
			}
		}
		return htResult;
	}

	/** Función que procesa el String que contiene el HTML en un Document(Jsoup)
	 * 
	 * @param strData
	 *           HTML inicial
	 * @return Documento bien formado */
	private static Document strToDocumentParser(String strData)
	{
		Document doc = (Document) Jsoup.parse(strData);
		return doc;
	}

	/** Función que extrae todos los IDs de data-translate dado un Document(Jsoup)
	 * 
	 * @param pDocument
	 *           Document(Jsoup) con el HTML parseado.
	 * @return ArrayList<String> con los IDs de data-translate */
	private static ArrayList<String> extractIdsFromDocument(Document pDocument)
	{
		ArrayList<String> alIdsTranslate = new ArrayList<String>();
		Elements eListDataTranslate = pDocument.getElementsByAttribute("data-translate");
		for (Element eItem : eListDataTranslate)
		{
			String strId = eItem.attr("data-translate");
			if (strId != null)
			{
				alIdsTranslate.add(strId);
			}
		}
		return alIdsTranslate;
	}

	/** Función que modifica sus etiquetas data-translate con las nuevas traducciones.
	 * 
	 * @param doc
	 *           Document(Jsoup) a modificar.
	 * @param htData
	 *           Hashtable con los IDs data.translate y las traducciones.
	 * @param strLanguage
	 *           String con el idioma al que se quiere traducir.
	 * @return Document(Jsoup) con la traducción ya puesta. */
	private static Document modifyDocument(Document doc, Hashtable<String, TranslateEntry> htData, String strLanguage)
	{
		Enumeration<String> enumHTData = htData.keys();
		while (enumHTData.hasMoreElements())
		{
			String strHTKey = (String) enumHTData.nextElement();
			Elements pListIDsTrans = doc.select("[data-translate=\"" + strHTKey + "\"]");
			for (Element eItem : pListIDsTrans)
			{
				if (eItem != null)
				{
					TranslateEntry pTrans = (TranslateEntry) htData.get(strHTKey);
					String strTraduccion = pTrans.getTranslate(strLanguage);
					if (strTraduccion != null)
					{
						eItem.text(strTraduccion);
					}
				}
			}
		}
		return doc;
	}

	/** Función para parsear un Document(Jsoup) a String
	 * 
	 * @param pDoc
	 *           Document(Jsoup) para parsear a string
	 * @return String con el documento HTML */
	private static String documentToString(Document pDoc)
	{
		String strReturn = null;
		if (pDoc != null)
		{
			strReturn = pDoc.toString();
		}
		else
		{
			pLog.error("El documento a convertir en nulo");
		}
		return strReturn;
	}
}
