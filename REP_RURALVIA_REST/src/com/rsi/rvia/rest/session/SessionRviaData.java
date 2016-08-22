package com.rsi.rvia.rest.session;

import java.net.URI;
import java.util.Properties;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.tool.LogController;
import com.rsi.rvia.rest.tool.RviaConnectCipher;

public class SessionRviaData
{
	private static Logger		pLog	= LoggerFactory.getLogger(SessionRviaData.class);
	private static LogController pLogC = new LogController();
	private String					strNodeRvia;
	private Cookie[]				pCookiesRviaData;
	private static Properties 	pAddressRviaProp  = new Properties();	
	private URI						pUriRvia = null;
	private String					strRviaSessionId = "";
	private String					strIsumUserProfile = "";
	private String					strIsumServiceId = "";
	private String					strLanguage = "";
	private String					strToken = "";
	
	public String getNodeRvia()
	{
		return strNodeRvia;
	}

	public Cookie[] getCookiesRviaData()
	{
		return pCookiesRviaData;
	}

	public URI getUriRvia()
	{
		return pUriRvia;
	}
	
	public String getRviaSessionId()
	{
		return strRviaSessionId;
	}
	
	public String getIsumUserProfile()
	{
		return strIsumUserProfile;
	}
	
	public String getIsumServiceId()
	{
		return strIsumServiceId;
	}
	
	public String getLanguage()
	{
		return strLanguage;
	}	
	
	public String getToken()
	{
		return strToken;
	}	
	public SessionRviaData(HttpServletRequest request) throws Exception
	{
		String[] strParameters;
		String strDesToken = "";
		pLog.debug("Se procede a cargar la configuraci�n de la conexi�n con ruralvia");
		pLogC.addLog("Debug", "Se procede a cargar la configuraci�n de la conexi�n con ruralvia");
		/* se coprueba si el contenido viene encriptado enel par�metro token */
		strToken = request.getParameter("token");
		if(strToken != null)
		{
			pLog.debug("La informaci�n viene cifrada, se procede a descifrarla");
			pLogC.addLog("Debug", "La informaci�n viene cifrada, se procede a descifrarla");
			/* se desencipta la informaci�n */
			strDesToken = RviaConnectCipher.symmetricDecrypt(strToken,RviaConnectCipher.RVIA_CONNECT_KEY);
			pLog.debug("Contenido descifrado. Token: " + strDesToken);
			pLogC.addLog("Debug", "Contenido descifrado. Token: " + strDesToken);
			/* se obtienen las variables recibidas */
			strParameters = strDesToken.split("&");
			for(int i =0; i < strParameters.length; i++)
			{
				String[] strAux = strParameters[i].split("=");
				String strName = strAux[0];
				String strValue = null;
				if(strAux.length > 1)
					strValue = strAux[1];
				if("node".equals(strName))
				{
					if(strValue != null)
						strNodeRvia = strValue;
				}
				else if("RVIASESION".equals(strName))
				{
					if(strValue != null)
						strRviaSessionId = strValue;
				}
				else if("isumUserProfile".equals(strName))
				{
					if(strValue != null)
						strIsumUserProfile = new String(strValue);
				}		
				else if("isumServiceId".equals(strName))
				{
					if(strValue != null)
						strIsumServiceId = strValue;
				}		
				else if("lang".equals(strName))
				{
					if(strValue != null)
						strLanguage = strValue;
				}	
			}
		}
		else
		{
			pLog.debug("La informaci�n no viene cifrada, se procede a leerla directamente de par�metros");
			pLogC.addLog("Debug", "La informaci�n no viene cifrada, se procede a leerla directamente de par�metros");
			strNodeRvia = request.getParameter("node");
			strRviaSessionId = request.getParameter("RVIASESION");
			strIsumUserProfile = request.getParameter("isumProfile");
			strIsumServiceId = request.getParameter("isumServiceId");
			strLanguage = request.getParameter("lang");
		}
		pCookiesRviaData = request.getCookies();			

		/* se precargan las proiedades de comunicaci�n con RVIA*/
		loadProperties();
	}

	private void loadProperties()
	{
		try
		{
			if(pAddressRviaProp.isEmpty())
				pAddressRviaProp.load(this.getClass().getResourceAsStream("/RuralviaAddress.properties"));
			
			pLog.debug("Se carga el fichero de resoluci�n de direcciones");
			pLogC.addLog("Debug", "Se carga el fichero de resoluci�n de direcciones");
			/* se obtiene la maquina y puerto en la que existe la sesi��on del usuario */
			pUriRvia = new URI(pAddressRviaProp.getProperty(strNodeRvia));
		}
		catch (Exception ex)
		{
			pLog.error("Fallo al cargar las propiedades de conexi�n con ruralvia", ex);
			pLogC.addLog("Error", "Fallo al cargar las propiedades de conexi�n con ruralvia: " + ex);
		}
	}
}
