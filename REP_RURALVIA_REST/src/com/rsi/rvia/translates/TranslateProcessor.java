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
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.DDBB.DDBBConnection;
import com.rsi.rvia.rest.DDBB.DDBBFactory;
import com.rsi.rvia.rest.DDBB.DDBBFactory.DDBBProvider;
import com.rsi.rvia.rest.tool.LogController;

/** Clase que gestiona el cambio de idioma en el contenido HTML de la web. */
public class TranslateProcessor
{
	private static Logger										pLog			= LoggerFactory.getLogger(TranslateProcessor.class);
	private static LogController pLogC = new LogController();
	public static Hashtable<String, TranslateEntry>	htCacheData	= new Hashtable<String, TranslateEntry>();

	/** Función que recibe una serie de identificadores de traducción e idiona y obtien su traducción
	 * 
	 * @param processIds
	 *           Array de String con los identificadores
	 * @param strLanguage
	 *           String con el idioma (es_ES)
	 * @return hastable con parejas identificador y su traducción. */
	public static Hashtable<String, String> processIds(String[] pStrIds, String strLanguage)
	{
		ArrayList<String> alIdsTrans;
		Hashtable<String, String> htReturn;
		Hashtable<String, TranslateEntry> htTransData;
		htTransData = new Hashtable<String, TranslateEntry>();
		htReturn = new Hashtable<String, String>();
		alIdsTrans = new ArrayList<String>(Arrays.asList(pStrIds));
		pLog.debug("Se reciben los siguientes ids para traducir. astrIds:" + pStrIds);
		pLogC.addLog("Debug", "Se reciben los siguientes ids para traducir. astrIds:" + pStrIds);
		try
		{
			htTransData = getTranslations(alIdsTrans);
			pLog.debug("Traducciones recuperadas correctamente.");
			pLogC.addLog("Debug", "Traducciones recuperadas correctamente.");
		}
		catch (Exception ex)
		{
			pLog.error("Error al intentar recuperar las Traducciones de la BBDD", ex);
			pLogC.addLog("Error", "Error al intentar recuperar las Traducciones de la BBDD"+ ex);
		}
		if (htTransData != null)
		{
			/* si no existe idioma se asigna espaol defecto */
			if (strLanguage == null || strLanguage.trim().isEmpty())
			{
				strLanguage = "es_ES";
				pLog.warn("No se ha definido idioma de la traduccion, se asigna el espanol");
				pLogC.addLog("Warning", "No se ha definido idioma de la traduccion, se asigna el espanol");
			}
			for (int i = 0; i < pStrIds.length; i++)
			{
				String strAuxId = pStrIds[i];
				if ((strAuxId == null) || (strAuxId.trim().isEmpty()))
				{
					continue;
				}
				String strAuxTrans = htTransData.get(strAuxId).getTranslate(strLanguage);
				htReturn.put(strAuxId, strAuxTrans);
			}
		}
		pLog.debug("datos a devolver por el metodo. htReturn: " + htReturn);
		pLogC.addLog("Debug", "Datos a devolver por el metodo. htReturn: " + htReturn);
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
		Document pDoc = null;
		String strReturn = null;
		ArrayList<String> alIdsTrans = null;
		Hashtable<String, TranslateEntry> htTransData = new Hashtable<String, TranslateEntry>();
		if (!strXHTML.isEmpty())
		{
			pDoc = strToDocumentParser(strXHTML);
			pLog.debug("String XHTML parseado a Documento correctamente.");
			pLogC.addLog("Info", "String XHTML parseado a Documento correctamente.");
			if (pDoc != null)
			{
				alIdsTrans = extractIdsFromDocument(pDoc);
				pLog.debug("IDs de traducciones extraidos correctamente.");
				pLogC.addLog("Info", "IDs de traducciones extraidos correctamente.");
				pLog.debug("alIdsTrans lenght: " + alIdsTrans.size());
				pLogC.addLog("Info", "alIdsTrans lenght: " + alIdsTrans.size());
			}
			if (alIdsTrans != null)
			{
				try
				{
					htTransData = getTranslations(alIdsTrans);
					pLog.debug("Traducciones recuperadas correctamente.");
					pLogC.addLog("Info", "Traducciones recuperadas correctamente.");
					
				}
				catch (Exception ex)
				{
					pLog.error("Error al intentar recuperar las Traducciones de la BBDD", ex);
					pLogC.addLog("Error", "Error al intentar recuperar las Traducciones de la BBDD: " + ex);
				}
			}
			if (htTransData != null)
			{
				if (strLanguage == null)
				{
					strLanguage = "es_ES";
				}
				pLog.debug("Documento premodificación null: " + (pDoc == null));
				pLogC.addLog("Info", "Documento premodificación null: " + (pDoc == null));
				pDoc = modifyDocument(pDoc, htTransData, strLanguage);
				pLog.debug("Documento modificado Correctamente. Tamaño de htTransData: " + htTransData.size());
				pLogC.addLog("Info", "Documento modificado Correctamente. Tamaño de htTransData: " + htTransData.size());
				if (pDoc != null)
				{
					strReturn = documentToString(pDoc);
				}
				else
				{
					pLog.debug("Doc null en último paso.");
					pLogC.addLog("Info", "Doc null en último paso.");
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
			PreparedStatement pPreparedStatement = pDDBBTranslate.prepareStatement(strQuery);
			ResultSet pResultSet = pPreparedStatement.executeQuery();
			while (pResultSet.next())
			{
				String strCode = (String) pResultSet.getString("codigo");
				String strIdiom = (String) pResultSet.getString("idioma");
				String strTraduction = (String) pResultSet.getString("traduccion");
				if (!htResult.containsKey(strCode))
				{
					TranslateEntry pTrans = new TranslateEntry(strCode);
					htResult.put(strCode, pTrans);
					if (!htCacheData.containsKey(strCode))
					{
						htCacheData.put(strCode, pTrans);
					}
				}
				htResult.get(strCode).addTranslate(strIdiom, strTraduction);
				htCacheData.get(strCode).addTranslate(strIdiom, strTraduction);
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
		Document pDoc = (Document) Jsoup.parse(strData, "", Parser.xmlParser());
		return pDoc;
	}

	/** Función que extrae todos los IDs de data-translate dado un Document(Jsoup)
	 * 
	 * @param pDocument
	 *           Document(Jsoup) con el HTML parseado.
	 * @return ArrayList<String> con los IDs de data-translate */
	private static ArrayList<String> extractIdsFromDocument(Document pDocument)
	{
		ArrayList<String> alIdsTranslate = new ArrayList<String>();
		Elements pListDataTranslate = pDocument.getElementsByAttribute("data-translate");
		for (Element pItem : pListDataTranslate)
		{
			String strId = pItem.attr("data-translate");
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
	private static Document modifyDocument(Document pDoc, Hashtable<String, TranslateEntry> htData, String strLanguage)
	{
		Enumeration<String> pEnumHTData = htData.keys();
		while (pEnumHTData.hasMoreElements())
		{
			String strHTKey = (String) pEnumHTData.nextElement();
			Elements pListIDsTrans = pDoc.select("[data-translate=\"" + strHTKey + "\"]");
			for (Element pItem : pListIDsTrans)
			{
				if (pItem != null)
				{
					TranslateEntry pTrans = (TranslateEntry) htData.get(strHTKey);
					String strTraduccion = pTrans.getTranslate(strLanguage);
					if (strTraduccion != null)
					{
						pItem.text(strTraduccion);
					}
				}
			}
		}
		
		/* se añade el atributo lang a la etiqueta html para poder manejar el idioma dentro de la página */
		pDoc.getElementsByTag("html").attr("lang", strLanguage.replace("_", "-"));	
		return pDoc;
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
			pDoc.outputSettings().escapeMode(EscapeMode.xhtml);
			strReturn = pDoc.toString();
		}
		else
		{
			pLog.error("El documento a convertir en nulo");
			pLogC.addLog("Error", "El documento a convertir en nulo");
		}
		return strReturn;
	}
}
