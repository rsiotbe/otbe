package com.rsi.rvia.rest.conector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;
import com.rsi.rvia.rest.client.RviaRestHttpClient;
import com.rsi.rvia.rest.operation.MiqQuests;
import com.rsi.rvia.rest.operation.info.InterrogateRvia;
import com.rsi.rvia.rest.session.SessionRviaData;
import com.rsi.rvia.rest.tool.GettersRequestParams;
import com.rsi.rvia.rest.tool.Utils;

/** Clase que gestiona la conexión y comunicaciñon con el proveedor de datos (Ruralvia o WS) */
public class RestWSConnector
{
	private static Logger	pLog			= LoggerFactory.getLogger(RestWSConnector.class);

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
	public static Response get(HttpServletRequest pRequest, MiqQuests pMiqQuests, String strPathRest,
			SessionRviaData pSessionRvia, MultivaluedMap<String, String> pPathParams) throws Exception
	{
		Client pClient = RviaRestHttpClient.getClient();
		String strQueryParams = pRequest.getQueryString();
		/* se obtienen lso header necesarios para realizar la petición al WS */
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
	public static Response post(@Context HttpServletRequest pRequest, String strPathRest, SessionRviaData pSessionRvia,
			String strJsonData, MiqQuests pMiqQuests, MultivaluedMap<String, String> pPathParams) throws Exception
	{
		Hashtable<String, String> htDatesParameters = new Hashtable<String, String>();
		Client pClient = RviaRestHttpClient.getClient();
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
			htDatesParameters = InterrogateRvia.getParameterFromSession(strParameters, pSessionRvia);
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
	public static Response put(@Context HttpServletRequest pRequest, String strPathRest, SessionRviaData pSessionRvia,
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
	public static Response delete(@Context HttpServletRequest pRequest) throws Exception
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
				String strInputName = (String) pResultSet.getString("campo");
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
	
}
