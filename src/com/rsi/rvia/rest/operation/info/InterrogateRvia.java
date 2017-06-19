package com.rsi.rvia.rest.operation.info;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.glassfish.jersey.client.ClientConfig;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import com.rsi.rvia.rest.client.RviaRestHttpClient;

/**
 * Clase que gestiona el interrogatoria a ruralvia para conocer los datos de la operativa y los valores que el usuario
 * tiene para ellos dentro de al sesión
 */
public class InterrogateRvia
{
    public static final String URI_INTERROGATE_SERVICE       = "/portal_rvia/rviaRestInfo";
    public static final String INTERROGATE_PARAM_CLAVEPAGINA = "clave_pagina";
    private static Logger      pLog                          = LoggerFactory.getLogger(InterrogateRvia.class);

    /**
     * Obtiene un documento de tipo XML con la información, es simmilar al fichero xml.dat de la operativa
     * 
     * @param pRequestConfigRvia
     *            Objeto que contiene la información extraida de la sesión de ruralvia
     * @param strClavepagina
     *            Clave pagina de la operativa a preguntar
     * @return Documento Xml que contiene toda la info
     * @throws Exception
     */
    public static Document getXmlDatAndUserInfo(String strHostRvia, String strRviaSessionId, String strClavepagina)
            throws Exception
    {
        String strURL;
        Client pClient;
        WebTarget pWebTarget;
        Response pResponseService = null;
        String strXmlResponse;
        Document pXmlDoc = null;
        DocumentBuilderFactory pDocumentBuilderFactory;
        DocumentBuilder pDocumentBuilder;
        try
        {
            /* se compone la url a invocar, para ello se accede a la inforamción de la sesión */
            strURL = strHostRvia + URI_INTERROGATE_SERVICE;
            /* si existe sesión de ruralvia asociada al usuario */
            if (strRviaSessionId != null && !strRviaSessionId.isEmpty())
            {
                strURL += ";RVIASESION=" + strRviaSessionId;
            }
            strURL += "?" + INTERROGATE_PARAM_CLAVEPAGINA + "=" + strClavepagina;
            pLog.info("se compone la URL para interrogar a RVIA. URL: " + strURL);
            /* se utiliza el objeto cliente de peticiones http de Jersey */
            pClient = ClientBuilder.newClient(new ClientConfig());
            pWebTarget = pClient.target(strURL);
            pResponseService = pWebTarget.request().get();
            pLog.debug("El servidor ha respondido");
        }
        catch (Exception ex)
        {
            pLog.error("Error al realizar la petición al servicio de intearrogar ruralvia", ex);
        }
        /* en caso que el servidor no haya respondido una contenido correcto se lanza una excepción */
        // if (pResponseService == null || pResponseService.getStatus() != 200)
        if (pResponseService == null)
        {
            pLog.error("No se ha podido procesar el objeto ResponseService que devuelve la invocación, el elemento es nulo");
            throw new Exception("No se ha podido obtener la información del xml.dat y la información del usuario de ruralvia");
        }
        else if (pResponseService.getStatus() != 200)
        {
            pLog.error("El servidor ha respondido un codigo http " + pResponseService.getStatus()
                    + " al realizar la petición al servicio de intearrogar ruralvia");
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

    /**
     * Obtiene los valores de la sesión de ruralvia dada una lista de parámetros. Realiza una invocación a un servlet
     * específico de ruralvia
     * 
     * @param strParameters
     *            Rarámetros a recuperar separados por el caracter ';'
     * @param pSessionRvia
     *            Datos de petición recibida desde ruralvia de Ruralvia
     * @return Hastable con los parámetros leidos desde ruralvia
     */
    public static HashMap<String, String> getParameterFromSession(String strHostRvia, String strRviaSessionId,
            String strParameters)
    {
        String strUrl;
        String strHTML = "";
        HashMap<String, String> htReturn = new HashMap<String, String>();
        /* se obtienen los parametros de la petición a ruralvia */
        strUrl = strHostRvia + "/portal_rvia/rviaRestInfo;RVIASESION=" + strRviaSessionId + "?listAttributes="
                + strParameters;
        try
        {
            pLog.debug("Se procede a obtener de ruralvia los datos necesario para gnerar el token JWT");
            Client pClient = RviaRestHttpClient.getClient();
            WebTarget pTarget = pClient.target(UriBuilder.fromUri(strUrl).build());
            Response pResponse = pTarget.request().get();
            strHTML = pResponse.readEntity(String.class);
            JSONObject pData = new JSONObject(strHTML);
            Iterator<String> aKeys = pData.keys();
            while (aKeys.hasNext())
            {
                String strKey = aKeys.next();
                htReturn.put(strKey, pData.getString(strKey).toString());
            }
            pLog.debug("Datos recuperado: " + htReturn);
        }
        catch (Exception ex)
        {
            pLog.error("Error al recuperar parametros de la sesion de Rvia: " + ex);
            htReturn = new HashMap<String, String>();
        }
        return htReturn;
    }
}
