package com.rsi.rvia.multibank;

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

/** Clase que gestiona el los CSS de multientidad para adaptar el estilo de la web. */
public class CssMultiBankProcessor
{
	private static Logger							pLog			= LoggerFactory.getLogger(CssMultiBankProcessor.class);
	public static Hashtable<String, String>	htCacheData	= new Hashtable<String, String>();

	/** Devuelve el tamaño de la cache
	 * 
	 * @return int con el tamaño de la cache */
	public static int getSizeCache()
	{
		int nReturn = 0;
		if (htCacheData != null)
		{
			nReturn = htCacheData.size();
		}
		return nReturn;
	}

	/**
	 * Funcion que carga la cache desde base de datos
	 * @throws Exception
	 */
	private static void loadCache() throws Exception
	{
		String strQuery = "SELECT * from bel.bdptb229_css_multibank";
		DDBBConnection pDDBBCssMultibank = DDBBFactory.getDDBB(DDBBProvider.OracleBanca);
		PreparedStatement pPreparedStatement = pDDBBCssMultibank.prepareStatement(strQuery);
		ResultSet pResultSet = pPreparedStatement.executeQuery();
		while (pResultSet.next())
		{
			String strLinkRvia = (String) pResultSet.getString("RURALVIA");
			String strNRBE = (String) pResultSet.getString("NRBE");
			String strNewLink = (String) pResultSet.getString("VALUE");
			String strKey = strNRBE + "_" + strLinkRvia;
			if (!htCacheData.containsKey(strKey))
				htCacheData.put(strKey, strNewLink);
		}
		pLog.debug("Se carga la cache de CssMultiBank con " + getSizeCache() + " elementos");
	}
	
	/**
	 * Devuelve el valor de reemplazo del link css y si no lo encuentra devuelve el propio valor pasado
	 * @param strNRBE Código de entidad
	 * @param strCSSLink Link css a convertir
	 * @return
	 */
	private static String getLinkConversion (String strNRBE, String strCSSLink)
	{
		String strReturn;
		String strKey = strNRBE + "_" + strCSSLink;
		pLog.debug("Se solicita la key " + strKey);
		if (htCacheData.containsKey(strKey))
		{
			strReturn = htCacheData.get(strKey);
			pLog.debug("Se recupera el dato de la caché");
		}
		else
		{
			pLog.debug("No se encuentra el dato en caché");
			strReturn = strCSSLink;
		}
		pLog.debug("Se reemplaza el link \"" + strCSSLink + "\" por \"" + strReturn + "\"");		
		return strReturn;
	}

	/** Función que recibe una serie de enlaces a CSS y busca su conversión en función de la entidad
	 * 
	 * @param processIds
	 *           Array de String con los enlaces a CSS
	 * @param strNRBE
	 *           Código de entidad
	 * @return hastable con parejas identificador y su traducción. */
	public static Hashtable<String, String> processIds(String[] aOriginalCssLinks, String strNRBE)
	{
		Hashtable<String, String> htReturn = new Hashtable<String, String>();
		pLog.debug("Se reciben los siguientes enlaces para traducir. aOriginalCssLinks:" + aOriginalCssLinks);
		try
		{
			if(htCacheData == null || getSizeCache()<1)
			{
				pLog.debug("La caché no está inicializada se procede a inicializarla");
				loadCache();
			}
			for (String strOldLink : aOriginalCssLinks)
			{
				htReturn.put(strOldLink, getLinkConversion(strNRBE, strOldLink));
			}
		}
		catch (Exception ex)
		{
			pLog.error("Error al intentar recuperar las Traducciones de la BBDD", ex);
		}
		pLog.debug("Se retorna el hashtable con la infomación de conversiones");
		return htReturn;
	}

	/** Función Principal, recibe el XHTML y la entidad y realiza las conversiones
	 * 
	 * @param strXHTML
	 *           String con el XHTML
	 * @param strNRBE
	 *           String con el codigo de entidad
	 * @return String con el HTML con los reemplazos de css ya realizados. */
	public static String processXHTML(String strXHTML, String strNRBE)
	{
		Document pDoc = null;
		String strReturn = null;
		ArrayList<String> alOldLinks = null;
		Hashtable<String, String> htConversionData;
		if (strXHTML == null || strXHTML.trim().isEmpty())
			pLog.warn("El contenido de XHTML es nulo o vacio");
		else 
			pDoc = strToDocumentParser(strXHTML);
			pLog.debug("String XHTML parseado a Documento correctamente.");
			if (pDoc != null)
			{
				alOldLinks = extractLinksFromDocument(pDoc);
				pLog.debug("Links css extraidos correctamente.");
				pLog.debug("alIdsTrans lenght: " + alOldLinks.size());
			}
			if (alOldLinks != null)
			{
				try
				{
					htConversionData = getNewCssLinks(alOldLinks);
					pLog.debug("Traducciones recuperadas correctamente.");
				}
				catch (Exception ex)
				{
					pLog.error("Error al intentar recuperar las Traducciones de la BBDD", ex);
				}
			}
			if (htConversionData != null)
			{
				if (strLanguage == null)
				{
					strLanguage = "es_ES";
				}
				pLog.debug("Documento premodificaci�n null: " + (pDoc == null));
				pDoc = modifyDocument(pDoc, htConversionData, strLanguage);
				pLog.debug("Documento modificado Correctamente. Tamaño de htTransData: " + htConversionData.size());
				if (pDoc != null)
				{
					strReturn = documentToString(pDoc);
				}
				else
				{
					pLog.debug("Doc null en último paso.");
				}
			}
		}
		return strReturn;
	}

	/** Función para recuperar los enlaces a los nuevos CSS.
	 * 
	 * @param alOriginalCss
	 *           ArrayList con los enlaces originales
	 * @return HashTable con los elnaces originales y su conversión
	 * @throws Exception */
	private static Hashtable<String, String> getNewCssLinks(ArrayList<String> alOriginalCss) throws Exception
	{

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
		pDoc.outputSettings().prettyPrint(false);
		return pDoc;
	}

	/** Función que extrae todos los links de tipo css dado un Document(Jsoup)
	 * 
	 * @param pDocument
	 *           Document(Jsoup) con el HTML parseado.
	 * @return ArrayList<String> con los IDs de data-translate */
	private static ArrayList<String> extractLinksFromDocument(Document pDocument)
	{
		ArrayList<String> alCssLinks = new ArrayList<String>();
		Elements pLinksCss = pDocument.select("link[href]");
		for (Element pItem : pLinksCss)
		{
			/* se comprueba si el link es de tipo css */
			if("stylesheet".equals(pItem.attr("rel")))
			{
				String strLink = pItem.attr("abs:href");
				if (strLink != null)
				{
					alCssLinks.add(strLink);
				}
			}
		}
		return alCssLinks;
	}

	/** Función que modifica los links css de un documento JSOUP.
	 * 
	 * @param doc
	 *           Document(Jsoup) a modificar.
	 * @param htData
	 *           Hashtable con los enlaces a convertir y sus conversiones.
	 * @param strNRBE
	 *           String con el idioma al que se quiere traducir.
	 * @return Document(Jsoup) con la traducción ya puesta. */
	private static Document adjustCSSLink(Document pDocument, String strNRBE, Hashtable<String, String> htData)
	{
		Enumeration<String> pEnumHTData = htData.keys();
		while (pEnumHTData.hasMoreElements())
		{
			String strHTKey = (String) pEnumHTData.nextElement();
			Elements pLinksCss = pDocument.select("link[href]");
			for (Element pItem : pLinksCss)
			{
				if (pItem != null)
				{
					/* se comprueba si el link es de tipo css */
					if("stylesheet".equals(pItem.attr("rel")))
					{
						/* se comprueba si el link tiene traducción */

					}
	
					
					String pNewLink = (String) htData.get(strHTKey);
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

	/** Funci�n para parsear un Document(Jsoup) a String
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
		}
		return strReturn;
	}
}
