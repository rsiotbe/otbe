/************************************************************************
 * CREACION: REFERENCIA: P000008956 FECHA: 02-08-2016 AUTOR: Victor Muñoz Descripción: Clase procesadora de traducciones
 * en HTML MODIFICACIONES:
 ************************************************************************/
package com.rsi.rvia.translates;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.Constants;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;
import com.rsi.rvia.rest.session.RequestConfig;
import com.rsi.rvia.rest.session.RequestConfigRvia;
import com.rsi.rvia.rest.tool.Utils;

/** Clase que gestiona el cambio de idioma en el contenido HTML de la web. */
public class TranslateProcessor
{
	private static Logger												pLog					= LoggerFactory.getLogger(TranslateProcessor.class);
	public static Hashtable<String, Hashtable<String, TranslateEntry>>	htTranslateCacheData	= new Hashtable<String, Hashtable<String, TranslateEntry>>();

	/**
	 * Devuelve el tamaño de la cache
	 * 
	 * @return int con el tamaño de la cache
	 */
	public static int getCacheSize()
	{
		int nReturn = 0;
		if (htTranslateCacheData != null)
		{
			Enumeration<String> e = htTranslateCacheData.keys();
			while (e.hasMoreElements())
			{
				String strKey = (String) e.nextElement();
				Hashtable<String, TranslateEntry> htAppTransalates = htTranslateCacheData.get(strKey);
				Enumeration<String> e2 = htAppTransalates.keys();
				while (e2.hasMoreElements())
				{
					String strKey2 = (String) e2.nextElement();
					TranslateEntry pTranslateEntry = htAppTransalates.get(strKey2);
					nReturn += pTranslateEntry.getTranslates().size();
				}
			}
		}
		return nReturn;
	}

	/**
	 * Reinicia la Cache
	 */
	public static void resetCache()
	{
		if (htTranslateCacheData != null)
		{
			htTranslateCacheData = new Hashtable<String, Hashtable<String, TranslateEntry>>();
		}
	}

	/**
	 * Devuelve los datos de la cache en formato texto
	 * 
	 * @return Contenido de la caché
	 * @throws Exception
	 */
	public static String cacheToString() throws Exception
	{
		String strReturn = "";
		strReturn += Utils.hastablePrettyPrintHtml(htTranslateCacheData);
		return strReturn;
	}

	public static Hashtable<String, Hashtable<String, String>> processIds(String[] pStrIds)
	{
		ArrayList<String> alIdsTrans;
		Hashtable<String, Hashtable<String, String>> htReturn = new Hashtable<String, Hashtable<String, String>>();
		Hashtable<String, TranslateEntry> htTransData;
		alIdsTrans = new ArrayList<String>(Arrays.asList(pStrIds));
		pLog.debug("Se reciben los siguientes ids para traducir a todos los idiomas. astrIds:" + pStrIds);
		try
		{
			htTransData = getTranslations(alIdsTrans);
			pLog.debug("Traducciones de los codigos recuperadas correctamente.");
			for (int i = 0; i < pStrIds.length; i++)
			{
				String strAuxId = pStrIds[i];
				if ((strAuxId == null) || (strAuxId.trim().isEmpty()))
				{
					continue;
				}
				TranslateEntry pEntry = htTransData.get(strAuxId);
				Hashtable<String, String> htTransaltes = pEntry.getTranslates();
				Enumeration<String> e = htTransaltes.keys();
				while (e.hasMoreElements())
				{
					String strLang = (String) e.nextElement();
					String strTranslate = htTransaltes.get(strLang);
					if (!htReturn.contains(strLang))
					{
						htReturn.put(strLang, new Hashtable<String, String>());
					}
					Hashtable<String, String> htlang = htReturn.get(strLang);
					htlang.put(strAuxId, strTranslate);
				}
			}
		}
		catch (Exception ex)
		{
			pLog.error("Error al procesar la petición", ex);
		}
		pLog.debug("datos a devolver por el metodo. htReturn: " + htReturn);
		return htReturn;
	}

	public static Hashtable<String, String> processIds(String[] pStrIds, String strLanguage)
	{
		ArrayList<String> alIdsTrans;
		Hashtable<String, String> htReturn = new Hashtable<String, String>();
		Hashtable<String, TranslateEntry> htTransData;
		alIdsTrans = new ArrayList<String>(Arrays.asList(pStrIds));
		/* si no existe idioma se asigna español defecto */
		if (strLanguage == null || strLanguage.trim().isEmpty())
		{
			strLanguage = Constants.DEFAULT_LANGUAGE;
			pLog.warn("No se ha definido idioma de la traduccion, se asigna el español");
		}
		pLog.debug("Se reciben los siguientes ids para traducir a idioma " + strLanguage + ". astrIds:" + pStrIds);
		try
		{
			htTransData = getTranslations(alIdsTrans);
			pLog.debug("Traducciones de los codigos recuperadas correctamente.");
			for (int i = 0; i < pStrIds.length; i++)
			{
				String strAuxId = pStrIds[i];
				if ((strAuxId == null) || (strAuxId.trim().isEmpty()))
				{
					continue;
				}
				TranslateEntry pEntry = htTransData.get(strAuxId);
				htReturn.put(pEntry.getCode(), pEntry.getTranslate(strLanguage));
			}
		}
		catch (Exception ex)
		{
			pLog.error("Error al procesar la petición", ex);
		}
		pLog.debug("datos a devolver por el metodo. htReturn: " + htReturn);
		return htReturn;
	}

	public static Hashtable<String, Hashtable<String, String>> processIds(String strApp, String[] pStrIds)
	{
		ArrayList<String> alIdsTrans;
		Hashtable<String, Hashtable<String, String>> htReturn = new Hashtable<String, Hashtable<String, String>>();
		Hashtable<String, TranslateEntry> htTransData;
		alIdsTrans = new ArrayList<String>(Arrays.asList(pStrIds));
		pLog.debug("Se reciben los siguientes ids de la apliación " + strApp
				+ " para traducir a todos los idiomas. astrIds:" + pStrIds);
		try
		{
			htTransData = getTranslations(strApp, alIdsTrans);
			pLog.debug("Traducciones de los codigos recuperadas correctamente.");
			for (int i = 0; i < pStrIds.length; i++)
			{
				String strAuxId = pStrIds[i];
				if ((strAuxId == null) || (strAuxId.trim().isEmpty()))
				{
					continue;
				}
				TranslateEntry pEntry = htTransData.get(strAuxId);
				Hashtable<String, String> htTransaltes = pEntry.getTranslates();
				Enumeration<String> e = htTransaltes.keys();
				while (e.hasMoreElements())
				{
					String strLang = (String) e.nextElement();
					String strTranslate = htTransaltes.get(strLang);
					if (!htReturn.contains(strLang))
					{
						htReturn.put(strLang, new Hashtable<String, String>());
					}
					Hashtable<String, String> htlang = htReturn.get(strLang);
					htlang.put(strAuxId, strTranslate);
				}
			}
		}
		catch (Exception ex)
		{
			pLog.error("Error al procesar la petición", ex);
		}
		pLog.debug("datos a devolver por el metodo. htReturn: " + htReturn);
		return htReturn;
	}

	/**
	 * Función que recibe una serie de identificadores de traducción e idioma y obtiene su traducción.
	 * 
	 * @param processIds
	 *            Array de String con los identificadores
	 * @param strLanguage
	 *            String con el idioma (es_ES)
	 * @return hastable con parejas identificador y su traducción.
	 */
	public static Hashtable<String, String> processIds(String strApp, String[] pStrIds, String strLanguage)
	{
		ArrayList<String> alIdsTrans;
		Hashtable<String, String> htReturn = new Hashtable<String, String>();
		Hashtable<String, TranslateEntry> htTransData;
		alIdsTrans = new ArrayList<String>(Arrays.asList(pStrIds));
		/* si no existe idioma se asigna español defecto */
		if (strLanguage == null || strLanguage.trim().isEmpty())
		{
			strLanguage = Constants.DEFAULT_LANGUAGE;
			pLog.warn("No se ha definido idioma de la traduccion, se asigna el español");
		}
		pLog.debug("Se reciben los siguientes ids de la apliación " + strApp + " para traducir a idioma " + strLanguage
				+ ". astrIds:" + pStrIds);
		try
		{
			htTransData = getTranslations(strApp, alIdsTrans);
			pLog.debug("Traducciones de los codigos recuperadas correctamente.");
			for (int i = 0; i < pStrIds.length; i++)
			{
				String strAuxId = pStrIds[i];
				if ((strAuxId == null) || (strAuxId.trim().isEmpty()))
				{
					continue;
				}
				TranslateEntry pEntry = htTransData.get(strAuxId);
				htReturn.put(pEntry.getCode(), pEntry.getTranslate(strLanguage));
			}
		}
		catch (Exception ex)
		{
			pLog.error("Error al procesar la petición", ex);
		}
		pLog.debug("datos a devolver por el metodo. htReturn: " + htReturn);
		return htReturn;
	}

	public static Hashtable<String, Hashtable<String, String>> processApps(String[] aStrApps)
	{
		Hashtable<String, Hashtable<String, String>> htReturn = new Hashtable<String, Hashtable<String, String>>();
		Hashtable<String, TranslateEntry> htTransData;
		pLog.debug("Se reciben los siguientes nombres de apliación " + aStrApps
				+ " para obtener las traducciones a todos los idiomas");
		try
		{
			htTransData = getTranslationsByApp(aStrApps);
			pLog.debug("Traducciones de los codigos recuperadas correctamente.");
			for (int i = 0; i < aStrApps.length; i++)
			{
				String strAuxId = aStrApps[i];
				if ((strAuxId == null) || (strAuxId.trim().isEmpty()))
				{
					continue;
				}
				TranslateEntry pEntry = htTransData.get(strAuxId);
				Hashtable<String, String> htTransaltes = pEntry.getTranslates();
				Enumeration<String> e = htTransaltes.keys();
				while (e.hasMoreElements())
				{
					String strLang = (String) e.nextElement();
					String strTranslate = htTransaltes.get(strLang);
					if (!htReturn.contains(strLang))
					{
						htReturn.put(strLang, new Hashtable<String, String>());
					}
					Hashtable<String, String> htlang = htReturn.get(strLang);
					htlang.put(strAuxId, strTranslate);
				}
			}
		}
		catch (Exception ex)
		{
			pLog.error("Error al procesar la petición", ex);
		}
		pLog.debug("datos a devolver por el metodo. htReturn: " + htReturn);
		return htReturn;
	}

	public static Hashtable<String, String> processApps(String[] pStrApps, String strLanguage)
	{
		Hashtable<String, String> htReturn = new Hashtable<String, String>();
		Hashtable<String, TranslateEntry> htTransData;
		/* si no existe idioma se asigna español defecto */
		if (strLanguage == null || strLanguage.trim().isEmpty())
		{
			strLanguage = Constants.DEFAULT_LANGUAGE;
			pLog.warn("No se ha definido idioma de la traduccion, se asigna el español");
		}
		pLog.debug("Se reciben los siguientes ids de las apliaciónes " + pStrApps + " para traducir a idioma "
				+ strLanguage);
		try
		{
			htTransData = getTranslationsByApp(pStrApps);
			pLog.debug("Traducciones de los codigos recuperadas correctamente.");
			for (int i = 0; i < pStrApps.length; i++)
			{
				String strAuxId = pStrApps[i];
				if ((strAuxId == null) || (strAuxId.trim().isEmpty()))
				{
					continue;
				}
				TranslateEntry pEntry = htTransData.get(strAuxId);
				htReturn.put(pEntry.getCode(), pEntry.getTranslate(strLanguage));
			}
		}
		catch (Exception ex)
		{
			pLog.error("Error al procesar la petición", ex);
		}
		pLog.debug("datos a devolver por el metodo. htReturn: " + htReturn);
		return htReturn;
	}

	/**
	 * Función Principal, recibe el XHTML y el idioma y lo traduce
	 * 
	 * @param strHtmlt
	 *            Texto html
	 * @param pRequestConfigRvia
	 *            Dattos de sessión de usuario de ruralvia
	 * @return Documento jsoup con el HTML con la nueva traducción ya aplicada.
	 */
	public static Document processXHTML(String strHtmlt, RequestConfigRvia pRequestConfigRvia, String strApp)
	{
		return processXHTML(new Document(strHtmlt), pRequestConfigRvia);
	}

	/**
	 * Función Principal, recibe el documento Jsoup y el idioma y lo traduce
	 * 
	 * @param pDocument
	 *            Documento jsoup que contien el html
	 * @param pRequestConfig
	 *            Dattos de sessión de usuario de ruralvia
	 * @return Documento jsoup con el HTML con la nueva traducción ya aplicada.
	 */
	public static Document processXHTML(Document pDocument, RequestConfig pRequestConfig)
	{
		ArrayList<String> alIdsTrans = null;
		String strLanguage = null;
		Hashtable<String, TranslateEntry> htTransData = new Hashtable<String, TranslateEntry>();
		strLanguage = pRequestConfig.getLanguage();
		if (strLanguage == null || strLanguage.trim().isEmpty())
		{
			strLanguage = null;
		}
		pLog.debug("String XHTML parseado a Documento correctamente.");
		if (pDocument != null)
		{
			alIdsTrans = extractIdsFromDocument(pDocument);
			pLog.debug("IDs de traducciones extraidos correctamente.");
			pLog.debug("alIdsTrans lenght: " + alIdsTrans.size());
		}
		if (alIdsTrans != null && !alIdsTrans.isEmpty())
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
		if (htTransData != null && !htTransData.isEmpty())
		{
			if (strLanguage == null)
				strLanguage = Constants.DEFAULT_LANGUAGE;
			pLog.debug("Documento premodificación es nulo?: " + (pDocument == null));
			pDocument = modifyDocument(pDocument, htTransData, strLanguage);
			pLog.debug("Documento modificado Correctamente. Tamaño de htTransData: " + htTransData.size());
		}
		return pDocument;
	}

	private static Hashtable<String, TranslateEntry> getTranslations(ArrayList<String> alIdsTrans) throws Exception
	{
		String strNewsIds = "";
		Hashtable<String, TranslateEntry> htResult = new Hashtable<String, TranslateEntry>();
		Enumeration<String> e = htTranslateCacheData.keys();
		while (e.hasMoreElements())
		{
			String strAppName = (String) e.nextElement();
			for (String strId : alIdsTrans)
			{
				if (!htTranslateCacheData.get(strAppName).containsKey(strId))
				{
					if (!strNewsIds.equals(""))
					{
						strNewsIds += ",";
					}
					strNewsIds += "'" + strId + "'";
				}
				else
				{
					htResult.put(strId, (TranslateEntry) htTranslateCacheData.get(strAppName).get(strId));
				}
			}
		}
		if (!strNewsIds.isEmpty())
		{
			Connection pConnection = null;
			PreparedStatement pPreparedStatement = null;
			ResultSet pResultSet = null;
			try
			{
				String strQuery = "SELECT codigo,idioma,traduccion, aplicativo FROM bdptb079_idioma where codigo in (?)";
				pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
				pPreparedStatement = pConnection.prepareStatement(strQuery);
				pPreparedStatement.setString(1, strNewsIds);
				pResultSet = pPreparedStatement.executeQuery();
				while (pResultSet.next())
				{
					String strApp = (String) pResultSet.getString("aplicativo");
					String strCode = (String) pResultSet.getString("codigo");
					String strLang = (String) pResultSet.getString("idioma");
					String strTranslate = (String) pResultSet.getString("traduccion");
					if (!htResult.containsKey(strCode))
					{
						TranslateEntry pTrans = new TranslateEntry(strCode, strApp);
						htResult.put(strCode, pTrans);
						if (!htTranslateCacheData.contains(strApp))
						{
							htTranslateCacheData.put(strApp, new Hashtable<String, TranslateEntry>());
						}
						if (!htTranslateCacheData.get(strApp).containsKey(strCode))
						{
							htTranslateCacheData.get(strApp).put(strCode, pTrans);
						}
					}
					htResult.get(strCode).addTranslate(strLang, strTranslate);
					htTranslateCacheData.get(strApp).get(strCode).addTranslate(strLang, strTranslate);
				}
			}
			catch (Exception ex)
			{
				pLog.error("Error al realizar la consulta a la BBDD.");
			}
			finally
			{
				DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
			}
		}
		return htResult;
	}

	/**
	 * Función para recuperar las traducciones dada una lista de IDs.
	 * 
	 * @param alIdsTrans
	 *            ArrayList<String> con los IDs de las traducciones.
	 * @return HashTable con las traducciones en todos los idiomas de cada ID de la lista inicial
	 * @throws Exception
	 */
	private static Hashtable<String, TranslateEntry> getTranslations(String strAppName, ArrayList<String> alIdsTrans)
			throws Exception
	{
		String strNewsIds = "";
		Hashtable<String, TranslateEntry> htResult = new Hashtable<String, TranslateEntry>();
		if (!htTranslateCacheData.contains(strAppName))
		{
			htTranslateCacheData.put(strAppName, new Hashtable<String, TranslateEntry>());
		}
		for (String strId : alIdsTrans)
		{
			if (!htTranslateCacheData.get(strAppName).containsKey(strId))
			{
				if (!strNewsIds.equals(""))
				{
					strNewsIds += ",";
				}
				strNewsIds += "'" + strId + "'";
			}
			else
			{
				htResult.put(strId, (TranslateEntry) htTranslateCacheData.get(strAppName).get(strId));
			}
		}
		if (!strNewsIds.isEmpty())
		{
			Connection pConnection = null;
			PreparedStatement pPreparedStatement = null;
			ResultSet pResultSet = null;
			try
			{
				String strQuery = "SELECT codigo,idioma,traduccion, aplicativo FROM bdptb079_idioma where codigo in (?) and aplicativo = ?";
				pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
				pPreparedStatement = pConnection.prepareStatement(strQuery);
				pPreparedStatement.setString(1, strNewsIds);
				pPreparedStatement.setString(2, strAppName);
				pResultSet = pPreparedStatement.executeQuery();
				while (pResultSet.next())
				{
					String strApp = (String) pResultSet.getString("aplicativo");
					String strCode = (String) pResultSet.getString("codigo");
					String strLang = (String) pResultSet.getString("idioma");
					String strTranslate = (String) pResultSet.getString("traduccion");
					if (!htResult.containsKey(strCode))
					{
						TranslateEntry pTrans = new TranslateEntry(strCode, strApp);
						htResult.put(strCode, pTrans);
						if (!htTranslateCacheData.get(strAppName).containsKey(strCode))
						{
							htTranslateCacheData.get(strAppName).put(strCode, pTrans);
						}
					}
					htResult.get(strCode).addTranslate(strLang, strTranslate);
					htTranslateCacheData.get(strAppName).get(strCode).addTranslate(strLang, strTranslate);
				}
			}
			catch (Exception ex)
			{
				pLog.error("Error al realizar la consulta a la BBDD.");
			}
			finally
			{
				DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
			}
		}
		return htResult;
	}

	private static Hashtable<String, TranslateEntry> getTranslationsByApp(String[] aStrApps) throws Exception
	{
		Hashtable<String, TranslateEntry> htResult = new Hashtable<String, TranslateEntry>();
		Connection pConnection = null;
		PreparedStatement pPreparedStatement = null;
		ResultSet pResultSet = null;
		try
		{
			for (String strAppName : aStrApps)
			{
				if (!htTranslateCacheData.contains(strAppName))
				{
					htTranslateCacheData.put(strAppName, new Hashtable<String, TranslateEntry>());
				}
				String strQuery = "SELECT codigo,idioma,traduccion, aplicativo FROM bdptb079_idioma whereand aplicativo = ?";
				pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
				pPreparedStatement = pConnection.prepareStatement(strQuery);
				pPreparedStatement.setString(1, strAppName);
				pResultSet = pPreparedStatement.executeQuery();
				while (pResultSet.next())
				{
					String strApp = (String) pResultSet.getString("aplicativo");
					String strCode = (String) pResultSet.getString("codigo");
					String strLang = (String) pResultSet.getString("idioma");
					String strTranslate = (String) pResultSet.getString("traduccion");
					if (!htResult.containsKey(strCode))
					{
						TranslateEntry pTrans = new TranslateEntry(strCode, strApp);
						htResult.put(strCode, pTrans);
						if (!htTranslateCacheData.get(strAppName).containsKey(strCode))
						{
							htTranslateCacheData.get(strAppName).put(strCode, pTrans);
						}
					}
					htResult.get(strCode).addTranslate(strLang, strTranslate);
					htTranslateCacheData.get(strAppName).get(strCode).addTranslate(strLang, strTranslate);
				}
			}
		}
		catch (Exception ex)
		{
			pLog.error("Error al realizar la consulta a la BBDD.");
		}
		finally
		{
			DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
		}
		return htResult;
	}

	/**
	 * Función que extrae todos los IDs de data-translate dado un Document(Jsoup)
	 * 
	 * @param pDocument
	 *            Document(Jsoup) con el HTML parseado.
	 * @return ArrayList<String> con los IDs de data-translate
	 */
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

	/**
	 * Función que modifica sus etiquetas data-translate con las nuevas traducciones.
	 * 
	 * @param doc
	 *            Document(Jsoup) a modificar.
	 * @param htData
	 *            Hashtable con los IDs data.translate y las traducciones.
	 * @param strLanguage
	 *            String con el idioma al que se quiere traducir.
	 * @return Document(Jsoup) con la traducción ya puesta.
	 */
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
}
