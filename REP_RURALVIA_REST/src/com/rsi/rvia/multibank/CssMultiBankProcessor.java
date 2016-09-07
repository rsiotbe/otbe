package com.rsi.rvia.multibank;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

	/** Funcion que carga la cache desde base de datos
	 * 
	 * @throws Exception */
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

	/** Devuelve el valor de reemplazo del link css y si no lo encuentra devuelve el propio valor pasado
	 * 
	 * @param strNRBE
	 *           Código de entidad
	 * @param strCSSLink
	 *           Link css a convertir
	 * @return */
	private static String getLinkConversion(String strNRBE, String strCSSLink)
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
			strReturn = null;
		}
		return strReturn;
	}

	/** Función Principal, recibe el XHTML y la entidad y realiza las conversiones
	 * 
	 * @param strXHTML
	 *           String con el XHTML
	 * @param strNRBE
	 *           String con el codigo de entidad
	 * @return String con el HTML con los reemplazos de css ya realizados. 
	 * @throws Exception */
	public static String processXHTML(String strXHTML, String strNRBE) throws Exception
	{
		Document pDoc = null;
		String strReturn = null;
		if (strXHTML == null || strXHTML.trim().isEmpty())
			pLog.warn("El contenido de strXHTML es nulo o vacio");
		else
		{
			pDoc = strToDocumentParser(strXHTML);
			pLog.debug("String XHTML parseado a Documento correctamente.");
			if (pDoc != null)
			{
				pLog.debug("Se procede a modificar los enlaces css si es necesario");
				pDoc = adjustCSSLink(pDoc, strNRBE);
				/* se obtiene el String que contiene l documetno final */
				strReturn = documentToString(pDoc);
			}
		}
		return strReturn;
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

	/** Función que modifica los links css de un documento JSOUP.
	 * 
	 * @param doc
	 *           Document(Jsoup) a modificar.
	 * @param htData
	 *           Hashtable con los enlaces a convertir y sus conversiones.
	 * @param strNRBE
	 *           String con el idioma al que se quiere traducir.
	 * @return Document(Jsoup) con la traducción ya puesta. 
	 * @throws Exception */
	private static Document adjustCSSLink(Document pDocument, String strNRBE) throws Exception
	{
		Elements pLinksCss = pDocument.select("link[href]");
		if (htCacheData == null || getSizeCache() < 1)
		{
			pLog.debug("La caché no está inicializada se procede a inicializarla");
			loadCache();
		}
		for (Element pItem : pLinksCss)
		{
			if (pItem != null)
			{
				String strOldLink = pItem.attr("abs:href");
				/* se comprueba si el link es de tipo css */
				if ("stylesheet".equals(pItem.attr("rel")))
				{
					/* se obtiene la traducción del link */
					String strNewLink = getLinkConversion(strNRBE, strOldLink);
					/* se comprueba si el link tiene traducción */
					if (strNewLink != null)
					{
						/* se modifica la propiedad href del link */
						pItem.attr("href", strNewLink);
						pLog.debug("Se modifica el enlace \"" + strOldLink + "\" por \"" + strNewLink + "\"");
					}
				}
			}
		}
		/* se retorna el documetno modificado */
		return pDocument;
	}

	/** Función para parsear un Document(Jsoup) a String
	 * 
	 * @param pDoc
	 *           Document(Jsoup) para parsear a string
	 * @return String con el documento HTML */
	private static String documentToString(Document pDoc)
	{
		String strReturn = null;
		pDoc.outputSettings().escapeMode(EscapeMode.xhtml);
		strReturn = pDoc.toString();
		return strReturn;
	}
}
