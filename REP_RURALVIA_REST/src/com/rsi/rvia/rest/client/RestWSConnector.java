package com.rsi.rvia.rest.client;

import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.rsi.rvia.rest.DDBB.DDBBConnection;
import com.rsi.rvia.rest.DDBB.DDBBFactory;
import com.rsi.rvia.rest.DDBB.DDBBFactory.DDBBProvider;
import com.rsi.rvia.rest.DDBB.OracleDDBB;
import com.rsi.rvia.rest.operation.info.InterrogateRvia;
import com.rsi.rvia.rest.session.SessionRviaData;

public class RestWSConnector
{
	private static Logger	pLog		= LoggerFactory.getLogger(RestWSConnector.class);
	private static String	strRviaXML	= "http://localhost:8080";
	private static String	strTemplate;

	public String getTemplate()
	{
		return this.strTemplate;
	}

	private static URI getBaseRviaXML()
	{
		return UriBuilder.fromUri(strRviaXML).build();
	}

	private static URI getBaseWSURI()
	{
		return UriBuilder.fromUri("http://localhost:8080/api/").build();
	}

	private static URI getBaseWSEndPoint(String endp)
	{
		return UriBuilder.fromUri(endp).build();
	}
	
	public static Response getData(HttpServletRequest request, String strData, SessionRviaData sesion_rvia,
			String strPrimaryPath) throws Exception
	{
		String strComponentType = "";
		String strEndPoint = "";
		int nId_miq = 0;
		DDBBConnection p3 = DDBBFactory.getDDBB(DDBBProvider.Oracle);
		String strPath = strPrimaryPath;
		pLog.debug("Path Rest: " + strPrimaryPath);
		PreparedStatement ps = p3.prepareStatement("select * from bdptb222_miq_quests where path_rest = '" + strPath + "'");
		ResultSet rs = p3.executeQuery(ps);
		pLog.info("Query Ejecutada!");
		String method = request.getMethod();
		Response pReturn = null;
		while (rs.next())
		{
			strComponentType = rs.getString("component_type");
			pLog.debug("strComponentType lenght: " + strComponentType.length());
			if (strComponentType != null)
			{
				strComponentType = strComponentType.substring(0, 4);
			}
			strEndPoint = rs.getString("end_point");
			strTemplate = rs.getString("miq_out_template");
			nId_miq = rs.getInt("id_miq");
		}
		pLog.info("Preparando peticion para tipo " + strComponentType + " y endpoint " + strEndPoint + " # method: " + method);
		switch (method)
		{
			case "GET":
				if ("RVIA".equals(strComponentType))
				{
					pLog.info("Derivando peticion a Ruralvía");
					pReturn = rviaPost(request, strComponentType, strEndPoint, nId_miq, sesion_rvia, strData);
				}
				else
				{
					pLog.info("Solicitando peticióñn REST");
					pReturn = get(request, strEndPoint);
				}
				break;
			case "POST":
				if ("RVIA".equals(strComponentType))
				{
					pLog.info("Derivando petición a Ruralvía");
					pReturn = rviaPost(request, strComponentType, strEndPoint, nId_miq, sesion_rvia, strData);
				}
				else
				{
					pLog.info("Solicitando peticióñn REST");
					pReturn = post(request, strEndPoint, strData);
				}
				break;
			case "PUT":
				pReturn = put(request, strPath, sesion_rvia);
				break;
			case "PATCH":
				break;
			case "DELETE":
				pReturn = delete(request);
				break;
		}
		return pReturn;
	}


	private static Response performRviaConnection(HttpServletRequest req, String endp, int id_miq,
			SessionRviaData sesion_rvia, String data) throws Exception
	{
		SessionRviaData sesiFoo = sesion_rvia;
		String sesId = sesiFoo.getRviaSessionId();
		String host = sesiFoo.getUriRvia().toString();
		String url = host + "/portal_rvia/ServletDirectorPortal;RVIASESION=" + sesId + "?clavePagina=" + endp;
																																					
		Client client = CustomRSIClient.getClient();
		WebTarget target = client.target(getBaseRviaXML());
		Document doc = InterrogateRvia.getXmlDatAndUserInfo(req, endp);
		NodeList nodos = doc.getElementsByTagName("field");
		Vector<String> sessionParamNames = new Vector();
		MultivaluedMap<String, String> camposDeSession = new MultivaluedHashMap<String, String>();
		// Datos existentes en la session
		for (int i = 0; i < nodos.getLength(); i++)
		{
			Element e = (Element) nodos.item(i);
			String value = e.getAttribute("value");
			if (!value.isEmpty())
			{
				pLog.info("----------------- campo informado: " + e.getAttribute("name").toString() + ": "
						+ e.getAttribute("value").toString());
				camposDeSession.add(e.getAttribute("name"), e.getAttribute("value"));
				sessionParamNames.add(e.getAttribute("name"));
			}
		}
		saveSenssionVarNames(id_miq, sessionParamNames);
		camposDeSession.add("clavePagina", endp);
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
		int id_miq_param, i;
		String q;
		DDBBConnection p3 = OracleDDBB.getInstance();
		PreparedStatement ps;
		ResultSet rs;
		for (i = 0; i < nombres.size(); i++)
		{
			q = 	"select a.id_miq from " +
					"BEL.BDPTB222_MIQ_QUESTS a, " +
					"BEL.BDPTB226_MIQ_QUEST_RL_SESSION b, " +
					"BEL.BDPTB225_MIQ_SESSION_PARAMS c " +
					"where a.id_miq=b.id_miq " + 
					"and b.ID_MIQ_PARAM=c.ID_MIQ_PARAM " +
					"and a.id_miq=" + id_miq + 
					"and c.PARAMNAME='" + nombres.get(i) + "'";
			pLog.info(q);
			ps = p3.prepareStatement(q);
			rs = ps.executeQuery();
			if (rs.next())
			{
				ps.close();
				rs.close();
				continue;
			}
			ps.close();
			rs.close();
			q = "select a.ID_MIQ_PARAM from BEL.BDPTB225_MIQ_SESSION_PARAMS a where a.PARAMNAME = '" + nombres.get(i)
					+ "'";
			pLog.info(q);
			ps = p3.prepareStatement(q);
			rs = ps.executeQuery();
			if (!rs.next())
			{
				ps.close();
				rs.close();
				q = " insert into BEL.BDPTB225_MIQ_SESSION_PARAMS" + " select"
						+ "  (select count(*) from BEL.BDPTB225_MIQ_SESSION_PARAMS) +1  " + " , '" + nombres.get(i) + "' "
						+ " , ''" + " , ''" + " , 'SESION'" + " from dual ";
				pLog.info(q);
				ps = p3.prepareStatement(q);
				rs = ps.executeQuery();
			}
			ps.close();
			rs.close();
			q = " select h.ID_MIQ_PARAM from BEL.BDPTB225_MIQ_SESSION_PARAMS h where h.PARAMNAME='" + nombres.get(i) + "'";
			pLog.info(q);
			ps = p3.prepareStatement(q);
			rs = ps.executeQuery();
			rs.next();
			id_miq_param = rs.getInt("ID_MIQ_PARAM");
			ps.close();
			rs.close();
			q = " insert into BEL.BDPTB226_MIQ_QUEST_RL_SESSION values(" + id_miq + ", " + id_miq_param + " , '')";
			pLog.info(q);
			ps = p3.prepareStatement(q);
			rs = ps.executeQuery();
			ps.close();
			rs.close();
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

	/** Verbo get. Recibe HttpServletRequest de contexto para derivar a RESTfull
	 * 
	 * @return Response con el objeto respuesta */
	private static Response get(HttpServletRequest request, String endp) throws Exception
	{
		Client client = CustomRSIClient.getClient();
		String strQueryParams = request.getQueryString();
		WebTarget target = client.target(getBaseWSEndPoint(endp) + "?" + strQueryParams);
		pLog.info("END_POINT:" + endp + "?" + strQueryParams);
		Response rp = target.request()
								  .header("CODSecEnt", "18")
								  .header("CODSecUser", "")
								  .header("CODSecTrans", "")
								  .header("CODTerminal", "18")
								  .header("CODApl", "BDP")
								  .header("CODCanal", "18")
								  .header("CODSecIp", "10.1.245.2")
								  .accept(MediaType.APPLICATION_JSON).get();
		pLog.info("GET: " + rp.toString());
		return rp;
	}

	/** Verbo post. Recibe HttpServletRequest de contexto para derivar a RESTfull
	 * 
	 * @return Response con el objeto respuesta */
	private static Response post(@Context HttpServletRequest request, String endp, String data) throws Exception
	{
		Client client = CustomRSIClient.getClient();
		MultivaluedMap<String, String> pDataPost = new MultivaluedHashMap<String, String>();
		if (data != null)
		{
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
					pDataPost.add(arr2[0], arr2[1]);
				}
			}
		}
		WebTarget target = client.target(getBaseWSEndPoint(endp));
		Response rp = target.request().post(Entity.form(pDataPost));
		pLog.info("Respose POST: " + rp.toString());
		return rp;
	}

	private static Response put(@Context HttpServletRequest request, String strPathRest, SessionRviaData sesion_rvia)
			throws Exception
	{
		// /??? Comprobar funcionamiento real
		// /??? Creado el servicio rest /putprueba, queda a�adir los parametros necesarios en la BBDD
		Hashtable<String, String> htDatesParameters = new Hashtable<String, String>();
		Client client = CustomRSIClient.getClient();
		String strParameters = getOperationParameters(strPathRest);
		if (!strParameters.isEmpty())
		{
			htDatesParameters = getParameterRviaSession(strParameters, sesion_rvia);
		}
		MultivaluedMap<String, String> camposDeSession = new MultivaluedHashMap<String, String>();
		for (Map.Entry<String, String> entry : htDatesParameters.entrySet())
		{
			camposDeSession.add(entry.getKey(), entry.getValue());
		}
		WebTarget target = client.target(getBaseRviaXML());
		Response rp = target.request().put(Entity.form(camposDeSession));
		pLog.info("Respose PUT: " + rp.toString());
		return rp;
	}

	private static Response delete(@Context HttpServletRequest request) throws Exception
	{
		Client client = CustomRSIClient.getClient();
		WebTarget target = client.target(getBaseRviaXML());
		Response rp = target.path("rest").path("hello").request().accept(MediaType.TEXT_PLAIN).delete(Response.class);
		pLog.info("DELETE: " + rp.toString());
		return rp;
	}

	private static String getOperationParameters(String strPathRest)
	{
		String strReturn = "";
		String strQuery = "select c.paramname from " + 
								"BEL.BDPTB222_MIQ_QUESTS a, " + 
								"BEL.BDPTB226_MIQ_QUEST_RL_SESSION b, " +
								"BEL.BDPTB225_MIQ_SESSION_PARAMS c " + 
								"where a.id_miq=b.id_miq " + 
								"and b.ID_MIQ_PARAM=c.ID_MIQ_PARAM " +
								"and a.path_rest=" + strPathRest;
		DDBBConnection pDDBBTranslate = DDBBFactory.getDDBB(DDBBProvider.Oracle);
		PreparedStatement pPS;
		try
		{
			pPS = pDDBBTranslate.prepareStatement(strQuery);
			ResultSet pQueryResult = pPS.executeQuery();
			pLog.debug("Query BBDD Params bien ejecutada");
			while (pQueryResult.next())
			{
				String strInputName = (String) pQueryResult.getString("paramname");
				if (!strReturn.isEmpty())
				{
					strReturn += ";";
				}
				strReturn += strInputName;
			}
		}
		catch (Exception ex)
		{
			pLog.error("Error al recuperar los nombres de parametros Path_Rest(" + strPathRest + "): " + ex);
			strReturn = "";
		}
		return strReturn;
	}

	private static Hashtable<String, String> getParameterRviaSession(String strParameters, SessionRviaData sesion_rvia)
	{
		SessionRviaData pSesiFoo = sesion_rvia;
		String strSesId = pSesiFoo.getRviaSessionId();
		String strHost = pSesiFoo.getUriRvia().toString();
		String strHTML = "";
		String[] strDatosParam = null;
		Hashtable<String, String> htReturn = new Hashtable();
		String url = strHost + "/portal_rvia/RviaRestInfo;RVIASESION=" + strSesId + "?listAttributes=" + strParameters;
		try
		{
			// Forzamos que sea Document del tipo: org.jsoup.nodes.Document
			org.jsoup.nodes.Document docResp = Jsoup.connect(url).get();
			strHTML = docResp.html();
			strDatosParam = strHTML.split(";");
			if (strDatosParam != null)
			{
				for (String strParam : strDatosParam)
				{
					String[] strPartesParam = strParam.split("#-#");
					if ((strPartesParam != null) && (strPartesParam.length >= 2))
					{
						htReturn.put(strPartesParam[0], strPartesParam[1]);
					}
				}
			}
		}
		catch (Exception ex)
		{
			pLog.error("Error al recuperar parametros de la sesion de Rvia: " + ex);
			htReturn = null;
		}
		return htReturn;
	}
}
