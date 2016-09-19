package com.rsi.rvia.rest.client;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.conector.RestConnector;
import com.rsi.rvia.rest.conector.RestRviaConnector;
import com.rsi.rvia.rest.conector.RestWSConnector;
import com.rsi.rvia.rest.error.exceptions.ApplicationException;
import com.rsi.rvia.rest.operation.MiqQuests;
import com.rsi.rvia.rest.session.SessionRviaData;

/**
 * Clase para manejar la respuesta del RestConnector. Mira si es un error o si no lo es y compone una respuesta JSON
 * Siguiendo la siguiente estructura: { "error": 0, ó 1 (si tiene o si no tiene) "response" : {...} (respuesta en JSON,
 * puede ser el error o la respuesta ya formada) }
 */
public class ResponseManager
{
	private static Logger	pLog	= LoggerFactory.getLogger(ResponseManager.class);

	/**
	 * Procesa una respuesta recibida desde el conector para evaluar si es un error y formatear su contenido
	 * 
	 * @param pSessionRviaData
	 *           Datos de sesión del usuario ruralvia
	 * @param pRestConnector
	 *           Conector al origen de datos
	 * @param pResponseConnector
	 *           Objeto respuesta obtenida del conector
	 * @return
	 * @throws Exception
	 */
	public static String processResponseConnector(SessionRviaData pSessionRviaData, RestConnector pRestConnector,
			Response pResponseConnector, MiqQuests pMiqQuests) throws Exception
	{
		String strJsonData;
		strJsonData = pResponseConnector.readEntity(String.class);
		JSONObject pJsonData;
		/* se comprueba si el conetido de la respuesta es un JSON u otra cosa */
		if (!isDataAJson(strJsonData))
		{
			/* no es un JSON, se evalua por si es un error web de ruralvia */
			if (RestRviaConnector.isRuralviaWebError(strJsonData))
			{
				/* se procede a obtener la infomración del error del interior del html devuelto por ruralvia */
				throw RestRviaConnector.generateLogicalErrorException(strJsonData);
			}
			else if (RestRviaConnector.isRuralviaSessionTimeoutError(strJsonData))
			{
				/* se procede a obtener la infomración del error del interior del html devuelto por ruralvia */
				throw RestRviaConnector.generateLogicalErrorException(strJsonData);
			}
			else
			{
				/*
				 * se procede a generar un error generico ya que si el valor no es un JSON y no es de ruralvia no debería
				 * producirse
				 */
				throw new ApplicationException(500, 99999, "Error al procesar la petición", "Se ha recibido unos datos no válidos", null);
			}
		}
		/* se crea el objeto JSON para ser manejado */
		pJsonData = new JSONObject(strJsonData);
		/* se compreuba si el json contiene un error, si es así se genera una excepción lógica */
		checkLogicalError(pSessionRviaData, pMiqQuests, pResponseConnector, pJsonData);
		/* se formatea la respuesta para estandarizarla y eliminar información que el usuario final no necesita */
		pJsonData = formatResponse(pJsonData, pMiqQuests.getIdMiq(), pRestConnector);
		return pJsonData.toString();
	}

	/**
	 * Comprueba si los adtos obtenidos contienen un error lógico y genera a excepción en dicho caso *
	 * 
	 * @param pSessionRviaData
	 *           Datos de sesión del usuario ruralvia
	 * @param pRestConnector
	 *           Conector al origen de datos
	 * @param pResponse
	 *           Objeto respuesta obtenida del conector
	 * @param pJsonData
	 *           Objeto que contiene la información JSON
	 * @throws ApplicationException
	 */
	private static void checkLogicalError(SessionRviaData pSessionRviaData, MiqQuests pMiqQuests, Response pResponse,
			JSONObject pJsonData) throws ApplicationException
	{
		Integer nHttpErrorCode = null;
		/* se comprueba si la respuesta tiene un codigo http de error para utilizarlo */
		if (pResponse.getStatus() != Status.OK.getStatusCode())
		{
			nHttpErrorCode = pResponse.getStatus();
		}
		/* se comprueba si el mensaje contiene un error generado por el conector WS */
		if (RestWSConnector.isWSError(pJsonData))
		{
			/* se lanza la excepción de tipo lócigo, en caso de no lanzarse se genera una exceción de tipo general */
			if (!RestWSConnector.throwWSError(nHttpErrorCode, pJsonData))
				throw new ApplicationException(500, 999999, "Error al procesar la información", "Error al acceder al contenido de un error de tipo ws", null);
		}
		else if (RestRviaConnector.isRVIAError(pJsonData))
		{
			/* se lanza la excepción de tipo lócigo, en caso de no lanzarse se genera una exceción de tipo general */
			if (!RestRviaConnector.throwRVIAError(pSessionRviaData, pMiqQuests, pJsonData))
				throw new ApplicationException(500, 999999, "Error al procesar la información", "Error al acceder al contenido de un error de tipo ws", null);
		}
		/* si la ejecución ha llegado aqui es que todo parece correcto, se continua */
		pLog.info("No se han detectado errores en el json de respuesta, se continua la ejecución normal");
	}

	/**
	 * Formatea la respuesta recibida por el conector para ajustarla al estadar definido por la aplciación
	 * 
	 * @param pJsonData
	 *           Objeto que contiene la información JSON
	 * @return
	 * @throws Exception
	 */
	private static JSONObject formatResponse(JSONObject pJsonData, int nIdMiq, RestConnector pRestConnector)
			throws Exception
	{
		/* se comprueba si el json pertenece a WS */
		if (RestWSConnector.isWSJson(pJsonData))
		{
			pJsonData = adjustWSJson(pJsonData);
		}
		pJsonData = filterResponseFields(pJsonData, nIdMiq, pRestConnector);
		return pJsonData;
	}

	/**
	 * Indica si la cadena que recibe es un objeto json o no
	 * 
	 * @param strData
	 * @return
	 */
	public static boolean isDataAJson(String strData)
	{
		try
		{
			new JSONObject(strData);
		}
		catch (JSONException ex)
		{
			try
			{
				new JSONArray(strData);
			}
			catch (JSONException ex1)
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Formatea el contenido de la respuesta para que comience por el token 'response' y despues el contenido
	 * 
	 * @param pJsonData
	 *           Objeto que contiene la información JSON
	 * @return Objeto que contiene la información JSON
	 * @throws Exception
	 */
	private static JSONObject adjustWSJson(JSONObject pJsonData) throws Exception
	{
		String strContent;
		JSONObject pResponseObject;
		try
		{
			String strPrimaryKey = "";
			if (pJsonData.keys().hasNext())
			{
				strPrimaryKey = (String) pJsonData.keys().next();
			}
			if (!strPrimaryKey.trim().isEmpty())
			{
				strContent = pJsonData.getJSONObject(strPrimaryKey).getString("Respuesta");
				pResponseObject = new JSONObject();
				pResponseObject.append("response", strContent);
			}
			else
				throw new Exception("No se ha encontrado la raiz del json de WS");
		}
		catch (Exception ex)
		{
			pLog.error("Error al obtener el cuerpo del mensaje de una respuesta WS", ex);
			throw ex;
		}
		return pResponseObject;
	}

	private static JSONObject filterResponseFields(JSONObject pJsonData, int nIdMiq, RestConnector pRestConnector)
			throws Exception
	{
		/* Cargamos en el modelo los parámetros de salida */
		SaveExitHierarchy.process(pJsonData, nIdMiq, pRestConnector.getMethod());
		// TODO: aqui ira el filtrado de campos de salida
		return pJsonData;
	}
}
