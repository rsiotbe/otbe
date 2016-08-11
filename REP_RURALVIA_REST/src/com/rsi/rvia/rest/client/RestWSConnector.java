package com.rsi.rvia.rest.client;

import java.net.*;
import java.io.*;
import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.rsi.rvia.rest.DDBB.DDBBConnection;
import com.rsi.rvia.rest.DDBB.DDBBFactory;
import com.rsi.rvia.rest.DDBB.DDBBFactory.DDBBProvider;
import com.rsi.rvia.rest.operation.info.InterrogateRvia;
import com.rsi.rvia.rest.session.SessionRviaData;
import javax.xml.bind.JAXBElement;

// @Path("/hello")
public class RestWSConnector
{
	private static Logger	pLog		= LoggerFactory.getLogger(RestWSConnector.class);
	private static String	RviaXML	= "http://10.1.243.142";

	// @GET
	// @Produces(MediaType.TEXT_PLAIN)
	public static Response getData(HttpServletRequest request, String data, SessionRviaData sesion_rvia,
			String strPrimaryPath) throws Exception
	{
		String ct = "", endp = "";
		int id_miq = 0;
		DDBBConnection p3 = DDBBFactory.getDDBB(DDBBProvider.Oracle);
		// String path=request.getPathInfo();
		String path = strPrimaryPath;
		pLog.debug("Path Rest: " + strPrimaryPath);
		PreparedStatement ps = p3.prepareStatement("select * from bdptb222_miq_quests where path_rest = '" + path + "'");
		ResultSet rs = p3.executeQuery(ps);
		pLog.info("Query Ejecutada!");
		String method = request.getMethod();
		Response resultado = null;
		while (rs.next())
		{
			ct = rs.getString("component_type");
			if(ct != null){
				ct.replace("\t","");
				ct = ct.substring(0,4);
				ct.replace("\t", "");
			}
			endp = rs.getString("end_point");
			id_miq =  rs.getInt("id_miq");
			//operid = rs.getString("end_point");
		}
		pLog.info("Preparando peticion para tipo " + ct + " y endpoint " + endp + " # method: " + method);
		switch (method)
		{
			case "GET":
				resultado = get(request);
				if ("RVIA".equals(ct))
				{
					pLog.info("Derivando peticion a Ruralvía");
					resultado = rviaPost(request, ct, endp, id_miq, sesion_rvia, data);
				}
				else
				{
					pLog.info("Solicitando peticióñn REST");
					resultado = get(request);
				}
				break;
			case "POST":
				if ("RVIA".equals(ct))
				{
					pLog.info("Derivando petición a Ruralvía");
					resultado = rviaPost(request, ct, endp, id_miq, sesion_rvia, data);
				}
				else
				{
					pLog.info("Solicitando peticióñn REST");
					resultado = post(request);
				}
				break;
			case "PUT":
				resultado = put(request);
				break;
			case "PATCH":
				break;
			case "DELETE":
				resultado = delete(request);
				break;
		}
		return resultado;
	}

	private static URI getBaseRviaXML()
	{
		return UriBuilder.fromUri(RviaXML).build();
	}

	private static URI getBaseWSURI()
	{
		return UriBuilder.fromUri("http://localhost:8080/api/").build();
	}

	private static Response performRviaConnection(HttpServletRequest req, String endp, int id_miq, SessionRviaData sesion_rvia, String data)
			throws Exception
	{
		SessionRviaData sesiFoo = sesion_rvia;
		String sesId = sesiFoo.getRviaSessionId();
		String host = sesiFoo.getUriRvia().toString();
		String url = host + "/portal_rvia/ServletDirectorPortal;RVIASESION=" + sesId + "?clavePagina="+ endp; // + "?" + "clave_pagina=" + endp;
		Client client = CustomRSIClient.getClient();
		WebTarget target = client.target(getBaseRviaXML());
		Document doc = InterrogateRvia.getXmlDatAndUserInfo(req, endp);
		NodeList nodos = doc.getElementsByTagName("field");
		Vector<String> sessionParamNames = new Vector();
		MultivaluedMap<String, String> camposDeSession = new MultivaluedHashMap<String, String>();
		// Datos existentes en la sessión
		for (int i = 0; i < nodos.getLength(); i++)
		{
			Element e = (Element) nodos.item(i);
			String value = e.getAttribute("value");
			if (!value.isEmpty())
			{
				pLog.info("--------------------- campo informado: " + e.getAttribute("name").toString() + ": "
						+ e.getAttribute("value").toString());
				camposDeSession.add(e.getAttribute("name"), e.getAttribute("value"));
				sessionParamNames.add(e.getAttribute("name"));
			}
		}
		saveSenssionVarNames(id_miq,sessionParamNames);
		camposDeSession.add("clavePagina", endp);
		//camposDeSession.remove("canal");
		//camposDeSession.add("canal","000001");
		
		// Datos llegados por post
		String[] arr = data.split("&");
		if (!data.trim().isEmpty())
		{
			for (int i = 0; i < arr.length; i++)
			{
				if (arr[i].trim().isEmpty())
					continue;
				String[] arr2 = arr[i].split("=");
				if (arr2.length < 2)
					continue;
				if (arr2[0].trim().isEmpty() || arr2[1].trim().isEmpty())
					continue;
				camposDeSession.add(arr2[0], arr2[1]);
			}
		}
		pLog.info("URL ServletDirectoPortal: " + url.toString());
		target = client.target(UriBuilder.fromUri(url).build());
		Response rp = target.request().post(Entity.form(camposDeSession));
		return rp;
	}

	private static void saveSenssionVarNames(int id_miq, Vector<String> nombres) throws Exception
	{
		int i=0,z=0;
		String in="";

/*		
		CREATE TABLE BEL.BDPTB225_MIQ_SESSION_INPUTS (
				  ID_MIQ_INPUT INTEGER NOT NULL,
				  INPUT_NAME VARCHAR(250)  DEFAULT NULL,
				  INPUT_VALUE VARCHAR(500)  DEFAULT NULL,
				  INPUT_DESC VARCHAR(1000)  DEFAULT NULL,
				  INPUT_TYPE VARCHAR(30)  DEFAULT NULL
				);
				CREATE UNIQUE INDEX NX_1_BELTB225 ON BEL.BDPTB225_MIQ_SESSION_INPUTS (ID_MIQ_INPUT);
				CREATE UNIQUE INDEX NX_2_BELTB225 ON BEL.BDPTB225_MIQ_SESSION_INPUTS (INPUT_NAME);

				CREATE TABLE BEL.BDPTB226_MIQ_QUEST_RL_SESSION (
				  ID_MIQ INTEGER NOT NULL,
				  ID_MIQ_INPUT INTEGER NOT NULL,
				  MIQ_VERB CHAR(10) DEFAULT NULL
				);
				CREATE UNIQUE INDEX NX_1_BELTB226 ON BEL.BDPTB226_MIQ_QUEST_RL_SESSION (ID_MIQ, ID_MIQ_INPUT, MIQ_VERB);
*/		

		
		DDBBConnection p3 = DDBBFactory.getDDBB(DDBBProvider.Oracle);
		PreparedStatement ps;
		ResultSet rs;
		for(i=0; i<nombres.size();i++){
			ps = p3.prepareStatement(	
					" select max() from                          " + 
					"	BEL.BDPTB222_MIQ_QUESTS a,              " + 
					"	BEL.BDPTB226_MIQ_QUEST_RL_SESSION b,    " + 
					"	BEL.BDPTB225_MIQ_SESSION_INPUTS c       " + 
					" where a.id_miq=b.id_miq                    " + 
					" and b.id_miq_input=c.id_miq_input          " + 
					" and a.id_miq=" + id_miq +
					" and c.input_name='" + nombres.get(i) + "';" 
				);
			rs = p3.executeQuery(ps);		
			if(rs.next()){
				continue;
			}
			in = in + ((i==0)?"'":"','") + nombres.get(i);
			ps.close();
		}
		
	
		
	}

	private static Response rviaPost(@Context HttpServletRequest request, String ct, String endp, int id_miq,
			SessionRviaData sesion_rvia, String data) throws Exception
	{
		Client client = CustomRSIClient.getClient();
		URI targetxml = getBaseRviaXML();
		pLog.info("RVIA_POST: " + targetxml.toString());
		WebTarget target = client.target(getBaseRviaXML());
		Response rp = null;
		pLog.info(endp);
		pLog.info(ct);
		pLog.info("xmldat: " + targetxml.toString());
		pLog.info("clave_pagina: " + endp);
		rp = performRviaConnection(request, endp, id_miq, sesion_rvia, data);
		return rp;
	}

	// @GET
	// @Produces(MediaType.TEXT_PLAIN)
	private static Response get(HttpServletRequest request) throws Exception
	{
		Client client = CustomRSIClient.getClient();
		WebTarget target = client.target(getBaseRviaXML()).path("");
		Response rp = target.request().accept(MediaType.APPLICATION_JSON).get();
		pLog.info("GET: " + rp.toString());
		return rp;
	}

	/** Verbo post. Recibe HttpServletRequest de contexto para derivar a RESTfull
	 * 
	 * @return Response con el objeto respuesta */
	// @POST
	// @Produces(MediaType.TEXT_PLAIN)
	private static Response post(@Context HttpServletRequest request) throws Exception
	{
		Client client = CustomRSIClient.getClient();
		WebTarget target = client.target(getBaseRviaXML());
		Response rp = null;
		/*
		 * Response rp = target.path("rest"). path("hello"). request(). accept(MediaType.TEXT_PLAIN). get(Response.class);
		 * pLog.info("POST: " + rp.toString());
		 */
		return rp;
	}

	// @PUT
	// @Produces(MediaType.TEXT_PLAIN)
	private static Response put(@Context HttpServletRequest request) throws Exception
	{
		request.getParameterMap();
		Client client = CustomRSIClient.getClient();
		///??? Modificar con la nueva respuesta.
		///??? Hacer una peticion de tipo PUT al WS enviandole en la cabecera los datos a modificar.
		
		WebTarget target = client.target(getBaseRviaXML());
		//Response rp = target.request().put();
		Response rp = null;
		/*
		 * Response rp = target.path("rest"). path("hello"). request(). accept(MediaType.TEXT_PLAIN). put(Response.class);
		 * pLog.info("PUT: " + rp.toString());
		 */
		return rp;
	}

	// @DELETE
	// @Produces(MediaType.TEXT_PLAIN)
	private static Response delete(@Context HttpServletRequest request) throws Exception
	{
		Client client = CustomRSIClient.getClient();
		WebTarget target = client.target(getBaseRviaXML());
		Response rp = target.path("rest").path("hello").request().accept(MediaType.TEXT_PLAIN).delete(Response.class);
		pLog.info("DELETE: " + rp.toString());
		return rp;
	}
}
