package com.rsi.rvia.rest.client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
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
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;
import com.rsi.rvia.rest.error.exceptions.RviaRestException;
import com.rsi.rvia.rest.operation.MiqQuests;
import com.rsi.rvia.rest.operation.info.InterrogateRvia;
import com.rsi.rvia.rest.session.SessionRviaData;
import com.rsi.rvia.rest.tool.GettersRequestParams;
import com.rsi.rvia.rest.tool.Utils;

/** Clase que gestiona la conexión y comunicaciñon con el proveedor de datos (Ruralvia o WS) */
public class RestWSConnector
{
	private static Logger	pLog			= LoggerFactory.getLogger(RestWSConnector.class);
	private MiqQuests			pMiqQuests;

	/**
	 * Devuelve el objeto MiqQuests asociado a la petición
	 * @return Objeto MiqQuests
	 */
	public MiqQuests getMiqQuests()
	{
		return this.pMiqQuests;
	}


	/** Realiza la llamada al proveedor de datos para obtener el resultado de la operación
	 * 
	 * @param pRequest
	 *           petición del cliente
	 * @param strData
	 *           datos a enviar al proveedor
	 * @param pSessionRvia
	 *           datos de la petición recibida desde ruralvia
	 * @param strPrimaryPath
	 *           path original de la petición
	 * @param pPathParams
	 *           parámetros asociados al path
	 * @return Respuesta del proveedor de datos
	 * @throws RviaRestException 
	 * @throws Exception */
	public Response getData(HttpServletRequest pRequest, String strData, SessionRviaData pSessionRvia,
			String strPrimaryPath, MultivaluedMap<String, String> pPathParams) throws RviaRestException
	{
		Response pReturn = null;
		try{
		String strMethod = pRequest.getMethod();
		
		/* se obtiene la configuración de la operativa desde base de datos */
		pMiqQuests = getDDBBMiqQuests(strPrimaryPath);

		pLog.info("Se obtiene la configuración de la base de datos. MiqQuest: " + pMiqQuests);
		pLog.info("Se recibe una petición con tipo de metodo : " + strMethod);
		
		/* se invoca al tipo de petición leido desde configuracón */
		switch (strMethod)
		{
			case "GET":
				if ("RVIA".equals(pMiqQuests.getComponentType()))
				{
					pLog.trace("Derivando petición a ruralvía");
					pReturn = performRviaConnection(pRequest, pMiqQuests, pSessionRvia, strData);
				}
				else
				{
					pLog.trace("Solicitando petición REST");
					pReturn = get(pRequest, pMiqQuests, strPrimaryPath, pSessionRvia, pPathParams);
				}
				break;
			case "POST":
				if ("RVIA".equals(pMiqQuests.getComponentType()))
				{
					pLog.trace("Derivando petición a ruralvía");
					pReturn = performRviaConnection(pRequest, pMiqQuests, pSessionRvia, strData);
				}
				else
				{
					pLog.trace("Solicitando petición REST");
					pReturn = post(pRequest, strPrimaryPath, pSessionRvia, strData, pMiqQuests, pPathParams);
				}
				break;
			case "PUT":
				pReturn = put(pRequest, strPrimaryPath, pSessionRvia, strData, pMiqQuests, pPathParams);
				break;
			case "PATCH":
				pLog.warn("No existe nbinguna acción para este método");
				break;
			case "DELETE":
				pReturn = delete(pRequest);
				break;
		}
		}catch(Exception ex){
			throw new RviaRestException(500, "Error al solicitar la información al proveedor de datos.", ex);
		}
		return pReturn;
	}

	/** Realiza la comunicación con RUralvia para obtener los datos necesarios de la operación
	 * 
	 * @param pRequest
	 *           petición del cliente
	 * @param pMiqQuests
	 *           Objeto MiqQuests con la información de la operativa
	 * @param pSessionRvia
	 *           datos de la petición recibida desde ruralvia
	 * @param strData
	 *           datos a enviar al proveedor
	 * @return Respuesta del proveedor de datos
	 * @throws Exception */
	private static Response performRviaConnection(HttpServletRequest pRequest, MiqQuests pMiqQuests,
			SessionRviaData pSessionRvia, String strData) throws Exception
	{
		WebTarget pTarget;
		String strSesId = pSessionRvia.getRviaSessionId();
		String strHost = pSessionRvia.getUriRvia().toString();
		String strClavePagina = pMiqQuests.getEndPoint();
		String strUrl = strHost + "/portal_rvia/ServletDirectorPortal;RVIASESION=" + strSesId + "?clavePagina="
				+ strClavePagina;
		pLog.trace("Se compone la url a invocar a ruralvia: " + strUrl);
		Client pClient = CustomRSIClient.getClient();
		Document pDoc = InterrogateRvia.getXmlDatAndUserInfo(pRequest, strClavePagina);
		pLog.trace("Se obtiene el xml de configuración desde ruralvia y se procede a evaluar su contenido");
		NodeList pNodos = pDoc.getElementsByTagName("field");
		Vector<String> pSessionParamNames = new Vector<String>();
		MultivaluedMap<String, String> pSessionFields = new MultivaluedHashMap<String, String>();
		
		/* Se leen los datos de sesión recibidos en la configuración de la operativa de ruralvia para este usuario */
		for (int i = 0; i < pNodos.getLength(); i++)
		{
			Element pElement = (Element) pNodos.item(i);
			String strValue = pElement.getAttribute("value");
			if (!strValue.isEmpty())
			{
				pLog.info("campo " + pElement.getAttribute("name").toString() + "\t:\t" + strValue.toString());
				pSessionFields.add(pElement.getAttribute("name"), strValue.toString());
				pSessionParamNames.add(pElement.getAttribute("name"));
			}
			else
			{
				pLog.info("campo " + pElement.getAttribute("name").toString() + "\t:\t<<NO INFORMADO>>");
			}
		}
		pLog.trace("Se procede a censar los nombres del los campos de la opereativa");
		saveDDBBSenssionVarNames(pMiqQuests.getIdMiq(), pSessionParamNames);
		pSessionFields.add("clavePagina", strClavePagina);
		
		/* Se evaluan los datos que llegan en ñla parte de datos */
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
				pSessionFields.add(pArr2[0], pArr2[1]);
			}
		}
		pLog.info("Se procede a invocar a ruralvia utilizando la url y los campos obtenidos desde sesión del usuario y por la propia petición.");
		pTarget = pClient.target(UriBuilder.fromUri(strUrl).build());
		Response pReturn = pTarget.request().post(Entity.form(pSessionFields));
		pLog.trace("Respuesra obtenida desde ruralvia: " + pReturn);
		return pReturn;
	}

	/** Guarda los parámetros necesarios, si todavia no lo están, para una invoación a ruralvia en base de datos
	 * 
	 * @param nIdMiq
	 *           Identificador de operativa interno
	 * @param aParamNames
	 *           Array con los nombres de parámetros
	 * @throws Exception */
	private static synchronized void saveDDBBSenssionVarNames(int nIdMiq, Vector<String> aParamNames) throws Exception
	{
		int nIdMiqParam, i;
		String strQuery;
		String strParamName;
		Connection pConnection = null;
		PreparedStatement pPreparedStatement = null;
		ResultSet pResultSet = null;
		try{
			pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
			for (i = 0; i < aParamNames.size(); i++)
			{
				strParamName = aParamNames.get(i);
				if (!strParamName.trim().isEmpty())
				{
					pLog.trace("Se evalua el campo " + strParamName + " para su censo en la operativa con id " + nIdMiq);
					
					/* se comprueba si ya existe este parámetro y está realacionado con la operativa */
					strQuery = "select a.id_miq from  BEL.BDPTB222_MIQ_QUESTS a, "
							+ "BEL.BDPTB226_MIQ_QUEST_RL_SESSION b, BEL.BDPTB225_MIQ_SESSION_PARAMS c "
							+ "where a.id_miq=b.id_miq and b.ID_MIQ_PARAM=c.ID_MIQ_PARAM and a.id_miq=? " + "and c.PARAMNAME=?";
					pPreparedStatement = pConnection.prepareStatement(strQuery);
					pPreparedStatement.setInt(1, nIdMiq);
					pPreparedStatement.setString(2, strParamName);
					pResultSet = pPreparedStatement.executeQuery();
					if (pResultSet.next())
					{
						/* el parametro existe y está relacionado, se pasa al siguiente parámetro */
						pResultSet.close();
						pPreparedStatement.close();
						pLog.trace("El campo " + strParamName + " ya se encuentra censado para esta operativa");
						continue;
					}
					pResultSet.close();
					pPreparedStatement.close();

					/* si el parámetro no existe, se comprueba si está definido como parámetro de culaquier otra opertativa */
					strQuery = "select a.ID_MIQ_PARAM from BEL.BDPTB225_MIQ_SESSION_PARAMS a where a.PARAMNAME = ?";
					pPreparedStatement = pConnection.prepareStatement(strQuery);
					pPreparedStatement.setString(1, strParamName);
					pResultSet = pPreparedStatement.executeQuery();
					if (pResultSet.next())
					{
						/* si el parámetro ya está definido se obtiene su ID */
						nIdMiqParam = pResultSet.getInt("ID_MIQ_PARAM");
						pResultSet.close();
						pPreparedStatement.close();
					}
					else
					{
						/* si el parámetro no está definido todavia se da de alta y se recupera su ID */
						pResultSet.close();
						pPreparedStatement.close();
						
						/* se obtiene el siguiente id de la tabla parámetros */
						strQuery = "select (select * from (select ID_MIQ_PARAM from BEL.BDPTB225_MIQ_SESSION_PARAMS order by ID_MIQ_PARAM desc)	where rownum = 1) + 1 ID_MIQ_PARAM from dual;";
						pPreparedStatement = pConnection.prepareStatement(strQuery);
						pResultSet = pPreparedStatement.executeQuery();
						if (pResultSet.next())
						{
							/* se lee el nuevo id */
							nIdMiqParam = pResultSet.getInt("ID_MIQ_PARAM");
							pResultSet.close();
							pPreparedStatement.close();
						}
						else
						{
							pResultSet.close();
							pPreparedStatement.close();
							throw new Exception("No se ha podido generar un id de secuencia para el campo ID_MIQ_PARAM de la tabla BEL.BDPTB225_MIQ_SESSION_PARAMS");
						}
						/* se procede a dar de alta el nuevo parámetro en la tabla */
						strQuery = "insert into BEL.BDPTB225_MIQ_SESSION_PARAMS values (?, ? ,'','','SESION','','')";
						pPreparedStatement.setInt(1, nIdMiqParam);
						pPreparedStatement.setString(2, strParamName);
						pPreparedStatement = pConnection.prepareStatement(strQuery);
						pPreparedStatement.executeUpdate();
						pPreparedStatement.close();
					}
					/* se añade la realación entre el parámetro y la operación */
					strQuery = " insert into BEL.BDPTB226_MIQ_QUEST_RL_SESSION values(?, ?, '')";
					pPreparedStatement = pConnection.prepareStatement(strQuery);
					pPreparedStatement.setInt(1, nIdMiqParam);
					pPreparedStatement.setString(2, strParamName);
					pPreparedStatement.executeUpdate();
				}
			}
		}catch(Exception ex){
			pLog.error("Error al realizar la consulta a la BBDD.");
		}finally{
			pResultSet.close();
			pPreparedStatement.close();
			pConnection.close();
		}
		
	}

	/** Realiza una petición de tipo get restFull al proveedor de datos (Ruralvia o WS dependiendo de la configuración)
	 * 
	 * @param pRequest
	 *           petición del cliente
	 * @param strPathRest
	 *           path de la petición
	 * @param pMiqQuests
	 *           Objeto MiqQuests con la información de la operativa
	 * @param pSessionRvia
	 *           datos de la petición recibida desde ruralvia
	 * @param strJsonData
	 *           Datos a enviar
	 * @param strEndPoint
	 *           Endpoint del proveedor de datos
	 * @param pPathParams
	 *           Parámetros asociados al path
	 * @return Respuesta del proveedor de datos
	 * @throws Exception */
	private static Response get(HttpServletRequest pRequest, MiqQuests pMiqQuests, String strPathRest,
			SessionRviaData pSessionRvia, MultivaluedMap<String, String> pPathParams) throws Exception
	{
		Client pClient = CustomRSIClient.getClient();
		String strQueryParams = pRequest.getQueryString();
		// Headers
		String strCODSecEnt = GettersRequestParams.getCODSecEnt(pRequest);
		String strCODSecUser = GettersRequestParams.getCODSecUser(pRequest);
		String strCODSecTrans = GettersRequestParams.getCODSecTrans(pRequest);
		String strCODTerminal = GettersRequestParams.getCODTerminal(pRequest);
		String strCODApl = GettersRequestParams.getCODApl(pRequest);
		String strCODCanal = GettersRequestParams.getCODCanal(pRequest);
		String strCODSecIp = GettersRequestParams.getCODSecIp(pRequest);
		String pathQueryParams = "";
		pathQueryParams = Utils.multiValuedMap2QueryString(pPathParams);
		WebTarget pTarget = pClient.target(pMiqQuests.getBaseWSEndPoint() + "?" + strQueryParams + pathQueryParams);
		pLog.info("END_POINT:" + pMiqQuests.getEndPoint());
		Response pReturn = pTarget.request().header("CODSecEnt", strCODSecEnt).header("CODSecUser", strCODSecUser).header("CODSecTrans", strCODSecTrans).header("CODTerminal", strCODTerminal).header("CODApl", strCODApl).header("CODCanal", strCODCanal).header("CODSecIp", strCODSecIp).accept(MediaType.APPLICATION_JSON).get();
		pLog.info("GET: " + pReturn.toString());
		return pReturn;
	}

	/** Realiza una petición de tipo post restFull al proveedor de datos (Ruralvia o WS dependiendo de la configuración)
	 * 
	 * @param pRequest
	 *           petición del cliente
	 * @param strPathRest
	 *           path de la petición
	 * @param pSessionRvia
	 *           Datos de la petición recibida desde ruralvia
	 * @param strJsonData
	 *           Datos a enviar
	 * @param pMiqQuests
	 *           Objeto MiqQuests con la información de la operativa
	 * @param pPathParams
	 *           Parámetros asociados al path
	 * @return Respuesta del proveedor de datos
	 * @throws Exception */
	private static Response post(@Context HttpServletRequest pRequest, String strPathRest, SessionRviaData pSessionRvia,
			String strJsonData, MiqQuests pMiqQuests, MultivaluedMap<String, String> pPathParams) throws Exception
	{
		Hashtable<String, String> htDatesParameters = new Hashtable<String, String>();
		Client pClient = CustomRSIClient.getClient();
		// Headers
		String strCODSecEnt = GettersRequestParams.getCODSecEnt(pRequest);
		String strCODSecUser = GettersRequestParams.getCODSecUser(pRequest);
		String strCODSecTrans = GettersRequestParams.getCODSecTrans(pRequest);
		String strCODTerminal = GettersRequestParams.getCODTerminal(pRequest);
		String strCODApl = GettersRequestParams.getCODApl(pRequest);
		String strCODCanal = GettersRequestParams.getCODCanal(pRequest);
		String strCODSecIp = GettersRequestParams.getCODSecIp(pRequest);
		String strParameters = getDDBBOperationParameters(strPathRest, "paramname");
		pLog.info("Query Params: " + strParameters);
		if (!strParameters.isEmpty())
		{
			htDatesParameters = getParameterRviaSession(strParameters, pSessionRvia);
		}
		ObjectMapper pMapper = new ObjectMapper();
		ObjectNode pJson = (ObjectNode) pMapper.readTree(strJsonData);
		Enumeration<String> pEnum = htDatesParameters.keys();
		while (pEnum.hasMoreElements())
		{
			String strTableKey = (String) pEnum.nextElement();
			pJson.put(strTableKey, (String) htDatesParameters.get(strTableKey).toString());
		}
		Iterator<String> pIterator = pPathParams.keySet().iterator();
		while (pIterator.hasNext())
		{
			String strKey = (String) pIterator.next();
			pJson.put(strKey, (String) pPathParams.get(strKey).toString());
		}
		strJsonData = pJson.toString();
		WebTarget pTarget = pClient.target(pMiqQuests.getBaseWSEndPoint());
		Response pReturn = pTarget.request().header("CODSecEnt", strCODSecEnt).header("CODSecUser", strCODSecUser).header("CODSecTrans", strCODSecTrans)
				.header("CODTerminal", strCODTerminal).header("CODApl", strCODApl).header("CODCanal", strCODCanal).header("CODSecIp", strCODSecIp)
				.post(Entity.json(strJsonData));
		pLog.info("Respose POST: " + pReturn.toString());
		return pReturn;
	}

	/** Realiza una petición de tipo put restFull al proveedor de datos (Ruralvia o WS dependiendo de la configuración)
	 * 
	 * @param pRequest
	 *           petición del cliente
	 * @param strPathRest
	 *           path de la petición
	 * @param pSessionRvia
	 *           Datos de la petición recibida desde ruralvia
	 * @param strJsonData
	 *           Datos a enviar
	 * @param pMiqQuests
	 *           Objeto MiqQuests con la información de la operativa
	 * @param pPathParams
	 *           Parámetros asociados al path
	 * @return Respuesta del proveedor de datos
	 * @throws Exception */
	private static Response put(@Context HttpServletRequest pRequest, String strPathRest, SessionRviaData pSessionRvia,
			String strJsonData, MiqQuests pMiqQuests, MultivaluedMap<String, String> pPathParams) throws Exception
	{
		/*
		 * se reutiliza la petición post puesto que es similar, en caso de una implementación diferente, es necesario
		 * definir este método desde cero
		 */
		pLog.warn("Se recibe un método PUT, pero se trata como si fuera un POST");
		return post(pRequest, strPathRest, pSessionRvia, strJsonData, pMiqQuests, pPathParams);
	}

	/** Realiza una petición de tipo delete restFull al proveedor de datos (Ruralvia o WS dependiendo de la configuración)
	 * 
	 * @param pRequest
	 *           petición del cliente
	 * @return Respuesta del proveedor de datos
	 * @throws Exception */
	private static Response delete(@Context HttpServletRequest pRequest) throws Exception
	{
		// /??? falta por implementar el método delete
		pLog.error("El método delete no está implementado");
		throw new Exception("Se ha recibido una petición de tipo DELETE y no existe ningún método que implemente este tipo de peticiones");
	}

	/** Obtiene los parámetros necesarios para poder ejecutar una operación. Los datos se leen desde la base de datos de
	 * configuración.
	 * 
	 * @param strPathRest
	 *           Path rest de la opreación a realizar
	 * @param strCampo
	 *           campo a consultar de la base de datos
	 * @return Cadena que contiene los campos separados por el carácter ';' 
	 * @throws Exception */
	private static String getDDBBOperationParameters(String strPathRest, String strCampo) throws Exception
	{
		String strReturn = "";
		Connection pConnection = null;
		PreparedStatement pPreparedStatement = null;
		ResultSet pResultSet = null;
		try
		{
			String strQuery = "select c." + strCampo + " campo from " + " BEL.BDPTB222_MIQ_QUESTS a, "
					+ " BEL.BDPTB226_MIQ_QUEST_RL_SESSION b, " + " BEL.BDPTB225_MIQ_SESSION_PARAMS c "
					+ " where a.id_miq=b.id_miq " + " and b.ID_MIQ_PARAM=c.ID_MIQ_PARAM " + " and a.path_rest='" + strPathRest
					+ "' order by c.ID_MIQ_PARAM";
			pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
			pPreparedStatement = pConnection.prepareStatement(strQuery);
			pResultSet = pPreparedStatement.executeQuery();
			pLog.trace("Query BBDD Params bien ejecutada");
			while (pResultSet.next())
			{
				String strInputName = (String) pResultSet.getString(strCampo);
				if (!strReturn.isEmpty())
				{
					strReturn += ";";
				}
				strReturn += strInputName;
			}
		}catch(Exception ex){
			pLog.error("Error al recuperar los nombres de parametros Path_Rest(" + strPathRest + "): " + ex);
			strReturn = "";
		}finally{
			pResultSet.close();
			pPreparedStatement.close();
			pConnection.close();
		}
		return strReturn;
	}
	
	/**
	 * Realiza una conexión a la BBDD para obtener los datos necesarios para crear un objeto MiqQuests y darlo como respuesta.
	 * @param strPath String path primario para la clausula where de la consulta
	 * @return MiqQuests con el id:miq, el component_type, el end_point y el template.
	 * @throws Exception
	 */
	public MiqQuests getDDBBMiqQuests(String strPath) throws Exception{
		MiqQuests pMiqQuests = null;
		Connection pConnection = null;
		PreparedStatement pPreparedStatement = null;
		ResultSet pResultSet = null;
		try
		{
			String strQuery = "select * from bdptb222_miq_quests where path_rest = ?";
			pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
			pPreparedStatement = pConnection.prepareStatement(strQuery);
			pPreparedStatement.setString(1, strPath);
			pResultSet = pPreparedStatement.executeQuery();
			while (pResultSet.next())
			{
				pMiqQuests = new MiqQuests(pResultSet.getInt("id_miq"), pResultSet.getString("component_type"), pResultSet.getString("end_point"), pResultSet.getString("miq_out_template"));
			}
		}catch(Exception ex){
			
		}finally{
			pResultSet.close();
			pPreparedStatement.close();
			pConnection.close();
		}
		return pMiqQuests;
	}

	/** Obtiene los valores de la sesión de ruralvia dao una lista de parámetros. Realiza una invoación a un servlet
	 * específico de ruralvia
	 * 
	 * @param strParameters
	 *           Rarámetros a recuperar separados por el caracter ';'
	 * @param pSessionRvia
	 *           Datos de petición recibida desde ruralvia de Ruralvia
	 * @return Hastable con los parámetros leidos desde ruralvia */
	private static Hashtable<String, String> getParameterRviaSession(String strParameters, SessionRviaData pSessionRvia)
	{
		String strSesId;
		String strHost;
		String url;
		String strHTML = "";
		String[] strDatosParam = null;
		Hashtable<String, String> htReturn;
		org.jsoup.nodes.Document pDocResp;
		/* se obtienen los parametros de la petición a ruralvia */
		strSesId = pSessionRvia.getRviaSessionId();
		strHost = pSessionRvia.getUriRvia().toString();
		htReturn = new Hashtable<String, String>();
		url = strHost + "/portal_rvia/RviaRestInfo;RVIASESION=" + strSesId + "?listAttributes=" + strParameters;
		try
		{
			/* Se fuerza que sea Document el tipo: org.jsoup.nodes.Document */
			pDocResp = Jsoup.connect(url).get();
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
			htReturn = null;
		}
		return htReturn;
	}
}
