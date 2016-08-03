package com.rsi.rvia.rest.operation.info;

import java.io.StringReader;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import com.rsi.rvia.rest.session.SessionRviaData;

/**
 * Clase que gestiona el interrogatoria a ruralvia para conocer los datos de la operativa y 
 * los valores que el usuario tiene para ellos dentro de al sesión
 *
 */
public class InterrogateRvia
{
	public static final String URI_INTERROGATE_SERVICE = "/portal_rvia/rviaRestInfo?clave_pagina=";
	private static Logger pLog = LoggerFactory.getLogger(InterrogateRvia.class);
	
	/**
	 * Obtiene un documento de tipo XML con la información, es simmilar al fichero xml.dat de la operativa
	 * @param pRequest Petición original
	 * @param strClavepagina Clave pagina de la operativa a preguntar
	 * @return Documento Xml que contiene toda la info
	 * @throws Exception
	 */
	public static Document getXmlDatAndUserInfo(HttpServletRequest pRequest, String strClavepagina) throws Exception
	{
		return getXmlDatAndUserInfo(new SessionRviaData(pRequest), strClavepagina); 
	}
	
	/**
	 * Obtiene un documento de tipo XML con la información, es simmilar al fichero xml.dat de la operativa
	 * @param pSessionRviaData Objeto que contiene la información extraida de la sesión de ruralvia
	 * @param strClavepagina Clave pagina de la operativa a preguntar
	 * @return Documento Xml que contiene toda la info
	 * @throws Exception
	 */
	public static Document getXmlDatAndUserInfo(SessionRviaData pSessionRviaData, String strClavepagina) throws Exception
	{
		String strURL;
		Client pClient;
		WebTarget pWebTarget;
		Cookie pCookieRvia;
		Response pResponseService = null;
		String strXmlResponse;
		Document pXmlDoc = null;
		DocumentBuilderFactory pDocumentBuilderFactory;
		DocumentBuilder pDocumentBuilder;
		try
		{
			/* se compone la url a invocar, para ello se accede a la inforamción de la sesiión */
			strURL = pSessionRviaData.getUriRvia() + URI_INTERROGATE_SERVICE + strClavepagina;
			pLog.info("se compone la URL para interrogar a RVIA. URL: " + strURL);
			
			/* se utiliza el objeto cliente de peticiones http de Jersey */
			pClient = ClientBuilder.newClient(new ClientConfig());
			pWebTarget = pClient.target(strURL);
		   
			/* se añade la cookie recuperada de la sesión para poder obtener los datos del usuario */
		   pCookieRvia= new Cookie("RVIASESION", pSessionRviaData.getRviaSessionId());
			pLog.debug("se procede a invocar a la url con los datos de sesión");	   
		   pResponseService = pWebTarget.request().cookie(pCookieRvia).get();
			pLog.debug("El servidor ha respondido");	   
		}
		catch (Exception ex) 
		{
			pLog.error("Error al realizar la petición al servicio de intearrogar ruralvia", ex);			
		}
		/* en caso que el servidor no haya respondido una contenido correcto se lanza una excepción */
		if(pResponseService == null || pResponseService.getStatus() != 200)
		{
			if (pResponseService == null)
				pLog.error("No se ha podido procesar el objeto ResponseService que devuelve la invocación, el elemento es nulo");			
			else
				pLog.error("El servidor ha respondido un codigo http " + pResponseService.getStatus() + 
						" al realizar la petición al servicio de intearrogar ruralvia");			
			throw new Exception("No se ha podido obtener la información del xml.dat y la información del usuario de ruralvia");
		}
		try
		{		
			/* se obtiene la cadena que contien el XML */
			strXmlResponse = pResponseService.readEntity(String.class);
			pLog.debug("El servidor responde: " + strXmlResponse);			
			
			/* se monta el objeto xmldocument */ 
			pDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
			pDocumentBuilder = pDocumentBuilderFactory.newDocumentBuilder();
			pXmlDoc = pDocumentBuilder.parse(new InputSource(new StringReader(strXmlResponse)));
			pLog.info("Documento xml generado correctamente");			

		}
		catch (Exception ex) 
		{
			throw new Exception("No se ha podido procesar el contenido xml recibido por el servicio de intearrogar ruralvia");
		}		
		return pXmlDoc;
	}
}
