package com.rsi.rvia.rest.client;

import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Hashtable;
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
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
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
import com.rsi.rvia.rest.tool.LogController;

public class RestWSConnector
{
	private static Logger	pLog		= LoggerFactory.getLogger(RestWSConnector.class);
	private static String	strRviaXML	= "http://localhost:8080";
	private static String	strTemplate = "";
	private static LogController pLogC = new LogController();
	
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

	private static URI getBaseWSEndPoint(String strEndPoint)
	{
		return UriBuilder.fromUri(strEndPoint).build();
	}
	
	public static Response getData(HttpServletRequest pRequest, String strData, SessionRviaData pSessionRvia,
			String strPrimaryPath) throws Exception
	{
		String strComponentType = "";
		String strEndPoint = "";
		String strPath = strPrimaryPath;
		String strMethod = pRequest.getMethod();
		int nIdMiq = 0;
		Response pReturn = null;
		DDBBConnection pDBConection = DDBBFactory.getDDBB(DDBBProvider.Oracle);
		pLog.debug("Path Rest: " + strPrimaryPath);
		pLogC.addLog("Info","Path Rest: " + strPrimaryPath);
		PreparedStatement pPreparedStament = pDBConection.prepareStatement("select * from bdptb222_miq_quests where path_rest = '" + strPath + "'");
		ResultSet pResultSet = pDBConection.executeQuery(pPreparedStament);
		pLog.info("Query Ejecutada!");
		pLogC.addLog("Info","Query Ejecutada!");
		
		
		while (pResultSet.next())
		{
			strComponentType = pResultSet.getString("component_type");
			strEndPoint = pResultSet.getString("end_point");
			strTemplate = pResultSet.getString("miq_out_template");
			nIdMiq = pResultSet.getInt("id_miq");
		}
		pLog.info("Template: " + strTemplate);
		pLog.info("Preparando peticion para tipo " + strComponentType + " y endpoint " + strEndPoint + " # method: " + strMethod);
		pLogC.addLog("Info","Preparando peticion para tipo " + strComponentType + " y endpoint " + strEndPoint + " # method: " + strMethod);
		switch (strMethod)
		{
			case "GET":
				if ("RVIA".equals(strComponentType))
				{
					pLog.info("Derivando peticion a Ruralvía");
					pLogC.addLog("Info","Derivando peticion GET a Ruralvía");
					pReturn = rviaPost(pRequest, strComponentType, strEndPoint, nIdMiq, pSessionRvia, strData);
				}
				else
				{
					pLog.info("Solicitando peticiÃ³Ã±n REST");
					pLogC.addLog("Info","Solicitando petición GET REST");
					pReturn = get(pRequest, strEndPoint, strPath, pSessionRvia);
				}
				break;
			case "POST":
				if ("RVIA".equals(strComponentType))
				{
					pLog.info("Derivando petición a Ruralvía");
					pLogC.addLog("Info","Derivando petición POST a Ruralvía");
					pReturn = rviaPost(pRequest, strComponentType, strEndPoint, nIdMiq, pSessionRvia, strData);
				}
				else
				{
					pLog.info("Solicitando peticiÃ³Ã±n REST");
					pLogC.addLog("Info","Solicitando petición POST REST");
					pReturn = post(pRequest, strPath, pSessionRvia, strData, strEndPoint);
				}
				break;
			case "PUT":
				pLogC.addLog("Info","Solicitando petición PUT REST");
				pReturn = put(pRequest, strPath, pSessionRvia, strData, strEndPoint);
				break;
			case "PATCH":
				pLogC.addLog("Info","Solicitando petición PATCH REST");
				break;
			case "DELETE":
				pLogC.addLog("Info","Solicitando petición DELETE REST");
				pReturn = delete(pRequest);
				break;
		}
		return pReturn;
	}


	private static Response performRviaConnection(HttpServletRequest pRequest, String strEndPoint, int nIdMiq,
			SessionRviaData pSessionRvia, String strData) throws Exception
	{
		SessionRviaData pSesiFoo = pSessionRvia;
		String strSesId = pSesiFoo.getRviaSessionId();
		String strHost = pSesiFoo.getUriRvia().toString();
		String strUrl = strHost + "/portal_rvia/ServletDirectorPortal;RVIASESION=" + strSesId + "?clavePagina=" + strEndPoint;
																																					
		Client pClient = CustomRSIClient.getClient();
		WebTarget pTarget = pClient.target(getBaseRviaXML());
		Document pDoc = InterrogateRvia.getXmlDatAndUserInfo(pRequest, strEndPoint);
		NodeList pNodos = pDoc.getElementsByTagName("field");
		Vector<String> pSessionParamNames = new Vector();
		MultivaluedMap<String, String> pCamposDeSession = new MultivaluedHashMap<String, String>();
		// Datos existentes en la session
		for (int i = 0; i < pNodos.getLength(); i++)
		{
			Element pElement = (Element) pNodos.item(i);
			String strValue = pElement.getAttribute("value");
			if (!strValue.isEmpty())
			{
				pLog.info("----------------- campo informado: " + pElement.getAttribute("name").toString() + ": "
						+ strValue.toString());
				pCamposDeSession.add(pElement.getAttribute("name"), strValue.toString());
				pSessionParamNames.add(pElement.getAttribute("name"));
			}
			else{
				pLog.info("----------------- campo NO informado: " + pElement.getAttribute("name").toString() + ": "
					+ strValue.toString());
			}
		}
		saveSenssionVarNames(nIdMiq, pSessionParamNames);
		pCamposDeSession.add("clavePagina", strEndPoint);
		// Datos llegados por post
		String[] pArr = strData.split("&");
		if (!strData.trim().isEmpty())
		{
			for (int i = 0; i < pArr.length; i++)
			{
				if (pArr[i].trim().isEmpty())
					continue;
				String[] pArr2 = pArr[i].split("=");
				if (pArr2.length < 2)
					continue;
				if (pArr2[0].trim().isEmpty() || pArr2[1].trim().isEmpty())
					continue;
				pCamposDeSession.add(pArr2[0], pArr2[1]);
			}
		}
		pLog.info("URL ServletDirectoPortal: " + strUrl.toString());
		pTarget = pClient.target(UriBuilder.fromUri(strUrl).build());
		Response pReturn = pTarget.request().post(Entity.form(pCamposDeSession));
		
		return pReturn;
	}

	private static void saveSenssionVarNames(int nIdMiq, Vector<String> pNombres) throws Exception
	{
		int nIdMiqParam, i;
		String strQuery;
		DDBBConnection pDBConnection = OracleDDBB.getInstance();
		PreparedStatement pPreparedStatement;
		ResultSet pResultSet;
		for (i = 0; i < pNombres.size(); i++)
		{
			strQuery = 	"select a.id_miq from " +
					"BEL.BDPTB222_MIQ_QUESTS a, " +
					"BEL.BDPTB226_MIQ_QUEST_RL_SESSION b, " +
					"BEL.BDPTB225_MIQ_SESSION_PARAMS c " +
					"where a.id_miq=b.id_miq " + 
					"and b.ID_MIQ_PARAM=c.ID_MIQ_PARAM " +
					"and a.id_miq=" + nIdMiq + 
					"and c.PARAMNAME='" + pNombres.get(i) + "'";
			pLog.info(strQuery);
			pPreparedStatement = pDBConnection.prepareStatement(strQuery);
			pResultSet = pPreparedStatement.executeQuery();
			if (pResultSet.next())
			{
				pPreparedStatement.close();
				pResultSet.close();
				continue;
			}
			pPreparedStatement.close();
			pResultSet.close();
			strQuery = "select a.ID_MIQ_PARAM from BEL.BDPTB225_MIQ_SESSION_PARAMS a where a.PARAMNAME = '" + pNombres.get(i)
					+ "'";
			pLog.info(strQuery);
			pPreparedStatement = pDBConnection.prepareStatement(strQuery);
			pResultSet = pPreparedStatement.executeQuery();
			if (!pResultSet.next())
			{
				pPreparedStatement.close();
				pResultSet.close();
				strQuery = " insert into BEL.BDPTB225_MIQ_SESSION_PARAMS" + " select"
						+ "  (select count(*) from BEL.BDPTB225_MIQ_SESSION_PARAMS) +1  " + " , '" + pNombres.get(i) + "' "
						+ " , ''" + " , ''" + " , 'SESION'" + " from dual ";
				pLog.info(strQuery);
				pPreparedStatement = pDBConnection.prepareStatement(strQuery);
				pResultSet = pPreparedStatement.executeQuery();
			}
			pPreparedStatement.close();
			pResultSet.close();
			strQuery = " select h.ID_MIQ_PARAM from BEL.BDPTB225_MIQ_SESSION_PARAMS h where h.PARAMNAME='" + pNombres.get(i) + "'";
			pLog.info(strQuery);
			pPreparedStatement = pDBConnection.prepareStatement(strQuery);
			pResultSet = pPreparedStatement.executeQuery();
			pResultSet.next();
			nIdMiqParam = pResultSet.getInt("ID_MIQ_PARAM");
			pPreparedStatement.close();
			pResultSet.close();
			strQuery = " insert into BEL.BDPTB226_MIQ_QUEST_RL_SESSION values(" + nIdMiq + ", " + nIdMiqParam + " , '')";
			pLog.info(strQuery);
			pPreparedStatement = pDBConnection.prepareStatement(strQuery);
			pResultSet = pPreparedStatement.executeQuery();
			pPreparedStatement.close();
			pResultSet.close();
		}
	}

	private static Response rviaPost(@Context HttpServletRequest pRequest, String strComponentType, String strEndPoint, int nIdMiq,
			SessionRviaData pSessionRvia, String strData) throws Exception
	{
		Client pClient = CustomRSIClient.getClient();
		URI pTargetXML = getBaseRviaXML();
		pLog.info("RVIA_POST: " + pTargetXML.toString());
		pLogC.addLog("Info","RVIA_POST: " + pTargetXML.toString());
		WebTarget pTarget = pClient.target(getBaseRviaXML());
		Response pReturn = null;
		pLog.info(strEndPoint);
		pLogC.addLog("Info",strEndPoint);
		pLog.info(strComponentType);
		pLogC.addLog("Info",strComponentType);
		pLog.info("xmldat: " + pTargetXML.toString());
		pLogC.addLog("Info","xmldat: " + pTargetXML.toString());
		pLog.info("clave_pagina: " + strEndPoint);
		pLogC.addLog("Info","clave_pagina: " + strEndPoint);
		pReturn = performRviaConnection(pRequest, strEndPoint, nIdMiq, pSessionRvia, strData);
		return pReturn;
	}

	/** Verbo get. Recibe HttpServletRequest de contexto para derivar a RESTfull
	 * 
	 * @return Response con el objeto respuesta */
	private static Response get(HttpServletRequest pRequest, String strEndPoint, String strPathRest, SessionRviaData pSessionRvia) throws Exception
	{
		Hashtable<String, String> htDatesParameters = new Hashtable<String, String>();
		Client pClient = CustomRSIClient.getClient();
		String strQueryParams = pRequest.getQueryString();
		String strAliasNames = getOperationParameters(strPathRest,"aliasname");
		String strSessionNames = getOperationParameters(strPathRest,"paramname");
		
		//htDatesParameters = getParameterRviaSession(strSessionNames, sesion_rvia);

		
		MultivaluedMap<String, String> pCamposDeSession = new MultivaluedHashMap<String, String>();
		pCamposDeSession.add("idInternoPe", "104955");
		pCamposDeSession.add("codEntidad", "3076");
		
		WebTarget pTarget = pClient.target(getBaseWSEndPoint(strEndPoint) + "?" + strQueryParams) ;
		
		pTarget = pTarget.queryParam("idInternoPe", "104955");
		pTarget = pTarget.queryParam("codEntidad", "3076");
		//target=target.queryParam("token", strQueryParams);
		
		pLog.info("END_POINT:" + strPathRest );
		Response pReturn = pTarget.request()
								  .header("CODSecEnt", "18")
								  .header("CODSecUser", "")
								  .header("CODSecTrans", "")
								  .header("CODTerminal", "18")
								  .header("CODApl", "BDP")
								  .header("CODCanal", "18")
								  .header("CODSecIp", "10.1.245.2")							
								  .accept(MediaType.APPLICATION_JSON)
								  .get();
		
		pLog.info("GET: " + pReturn.toString());
		return pReturn;
	}

	/** Verbo post. Recibe HttpServletRequest de contexto para derivar a RESTfull
	 * 
	 * @return Response con el objeto respuesta */
	private static Response post(@Context HttpServletRequest pRequest, String strPathRest, SessionRviaData pSessionRvia, String strJsonData, String strEndPoint) throws Exception
	{
		Hashtable<String, String> htDatesParameters = new Hashtable<String, String>();
		Client pClient = CustomRSIClient.getClient();
		String strParameters = getOperationParameters(strPathRest,"paramname");
		pLog.info("Query Params: " + strParameters);
		pLogC.addLog("Info","Query Params: " + strParameters);
		if (!strParameters.isEmpty())
		{
			htDatesParameters = getParameterRviaSession(strParameters, pSessionRvia);
		}
		
		ObjectMapper pMapper = new ObjectMapper();
		ObjectNode pJson = (ObjectNode) pMapper.readTree(strJsonData);
		Enumeration pEnum = htDatesParameters.keys();
		while (pEnum.hasMoreElements())
		{
			String strTableKey = (String) pEnum.nextElement();
			pJson.put(strTableKey,(String) htDatesParameters.get(strTableKey).toString());
		}
		strJsonData = pJson.toString();
		WebTarget pTarget = pClient.target(getBaseWSEndPoint(strEndPoint));
		
		Response pReturn = pTarget.request()
				.header("CODSecEnt","3008")
				.header("CODSecTrans", "")
				.header("CODSecUser", "")
				.header("CODApl", "BPC")
				.header("CODTerminal", "")
				.header("CODSecIp", "111.11.11.1")
				.post(Entity.json(strJsonData));
		pLog.info("Respose POST: " + pReturn.toString());
		pLogC.addLog("Info","Respose POST: " + pReturn.toString());
		return pReturn;
	}

	private static Response put(@Context HttpServletRequest pRequest, String strPathRest, SessionRviaData pSessionRvia, String strJsonData, String strEndPoint)
			throws Exception
	{
		Hashtable<String, String> htDatesParameters = new Hashtable<String, String>();
		Client pClient = CustomRSIClient.getClient();
		String strParameters = getOperationParameters(strPathRest,"paramname");
		pLog.info("Query Params: " + strParameters);
		pLogC.addLog("Info","Query Params: " + strParameters);
		if (!strParameters.isEmpty())
		{
			htDatesParameters = getParameterRviaSession(strParameters, pSessionRvia);
		}
		
		ObjectMapper pMapper = new ObjectMapper();
		ObjectNode pJson = (ObjectNode) pMapper.readTree(strJsonData);
		Enumeration pEnum = htDatesParameters.keys();
		while (pEnum.hasMoreElements())
		{
			String strTableKey = (String) pEnum.nextElement();
			pJson.put(strTableKey,(String) htDatesParameters.get(strTableKey).toString());
		}
		strJsonData = pJson.toString();
		WebTarget pTarget = pClient.target(getBaseWSEndPoint(strEndPoint));
		Response pReturn = pTarget.request()
				.header("CODSecEnt","3008")
				.header("CODSecTrans", "")
				.header("CODSecUser", "")
				.header("CODApl", "BPC")
				.header("CODTerminal", "")
				.header("CODSecIp", "111.11.11.1")
				.put(Entity.json(strJsonData));
		pLog.info("Respose PUT: " + pReturn.toString());
		pLogC.addLog("Info","Respose PUT: " + pReturn.toString());
		return pReturn;
	}

	private static Response delete(@Context HttpServletRequest pRequest) throws Exception
	{
		Client pClient = CustomRSIClient.getClient();
		WebTarget pTarget = pClient.target(getBaseRviaXML());
		Response pReturn = pTarget.path("rest").path("hello").request().accept(MediaType.TEXT_PLAIN).delete(Response.class);
		pLog.info("DELETE: " + pReturn.toString());
		pLogC.addLog("Info","DELETE: " + pReturn.toString());
		return pReturn;
	}

	private static String getOperationParameters(String strPathRest, String strCampo)
	{
		String strReturn = "";
		String strQuery = "select c." + strCampo + " campo from " + 
								" BEL.BDPTB222_MIQ_QUESTS a, " + 
								" BEL.BDPTB226_MIQ_QUEST_RL_SESSION b, " +
								" BEL.BDPTB225_MIQ_SESSION_PARAMS c " + 
								" where a.id_miq=b.id_miq " + 
								" and b.ID_MIQ_PARAM=c.ID_MIQ_PARAM " +
								" and a.path_rest='" + strPathRest + "' order by c.ID_MIQ_PARAM";
		DDBBConnection pDDBBConnection = DDBBFactory.getDDBB(DDBBProvider.Oracle);
		PreparedStatement pPreparedStatement;
		try
		{
			pPreparedStatement = pDDBBConnection.prepareStatement(strQuery);
			ResultSet pResultSet = pPreparedStatement.executeQuery();
			pLog.debug("Query BBDD Params bien ejecutada");
			while (pResultSet.next())
			{
				String strInputName = (String) pResultSet.getString("campo");
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

	private static Hashtable<String, String> getParameterRviaSession(String strParameters, SessionRviaData pSessionRvia)
	{
		SessionRviaData pSesiFoo = pSessionRvia;
		String strSesId = pSesiFoo.getRviaSessionId();
		String strHost = pSesiFoo.getUriRvia().toString();
		String strHTML = "";
		String[] strDatosParam = null;
		Hashtable<String, String> htReturn = new Hashtable();
		String url = strHost + "/portal_rvia/RviaRestInfo;RVIASESION=" + strSesId + "?listAttributes=" + strParameters;
		try
		{
			// Forzamos que sea Document del tipo: org.jsoup.nodes.Document
			org.jsoup.nodes.Document pDocResp = Jsoup.connect(url).get();
			strHTML = pDocResp.html();
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
			pLogC.addLog("Error","Error al recuperar parametros de la sesion de Rvia: " + ex);
			htReturn = null;
		}
		return htReturn;
	}
}
