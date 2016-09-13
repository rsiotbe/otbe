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
import com.rsi.rvia.rest.error.ErrorManager;
import com.rsi.rvia.rest.error.exceptions.ApplicationException;
import com.rsi.rvia.rest.error.exceptions.LogicalErrorException;
import com.rsi.rvia.rest.session.SessionRviaData;

/** Clase para manejar la respuesta del RestConnector. Mira si es un error o si no lo es y compone una respuesta JSON
 * Siguiendo la siguiente estructura: { "error": 0, ó 1 (si tiene o si no tiene) "response" : {...} (respuesta en JSON,
 * puede ser el error o la respuesta ya formada) } */
public class ResponseManager
{
	private static Logger	pLog	= LoggerFactory.getLogger(ResponseManager.class);

	/**
	 * Procesa una respuesta recibida desde el conector para evaluar si es un error y formatear su contenido
	 * @param pSessionRviaData Datos de sesión del usuario ruralvia
	 * @param pRestConnector Conector al origen de datos
	 * @param pResponseConnector Objeto respuesta obtenida del conector
	 * @return
	 * @throws Exception
	 */
	public static String processResponseConnector(SessionRviaData pSessionRviaData, RestConnector pRestConnector,
			Response pResponseConnector) throws Exception
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
		checkLogicalError(pSessionRviaData, pRestConnector, pResponseConnector, pJsonData);
		/* se formatea la respuesta para estandarizarla y eliminar información que el usuario final no necesita */
		pJsonData = formatResponse(pJsonData);
		return pJsonData.toString();
	}

	/** Comprueba si los adtos obtenidos contienen un error lógico y genera a excepción en dicho caso *
	 * 
	 * @param pSessionRviaData Datos de sesión del usuario ruralvia
	 * @param pRestConnector Conector al origen de datos
	 * @param pResponse Objeto respuesta obtenida del conector
	 * @param pJsonData
	 *           Objeto que contiene la información JSON
	 * @throws ApplicationException
	 */
	private static void checkLogicalError(SessionRviaData pSessionRviaData, RestConnector pRestConnector,
			Response pResponse, JSONObject pJsonData) throws ApplicationException
	{
		Integer nHttpErrorCode = null;
		/* se comprueba si la respuesta tiene un codigo http de error para utilizarlo */
		if (pResponse.getStatus() != Status.OK.getStatusCode())
		{
			nHttpErrorCode = pResponse.getStatus();
		}
		/* se comprueba si el mensaje contiene un error generado por el conector WS */
		if (isWSError(pJsonData))
		{
			/* se lanza la excepción de tipo lócigo, en caso de no lanzarse se genera una exceción de tipo general */
			if (!throwWSError(nHttpErrorCode, pJsonData))
				throw new ApplicationException(500, 999999, "Error al procesar la información", "Error al acceder al contenido de un error de tipo ws", null);
		}
		else if (isRVIAError(pJsonData))
		{
			/* se lanza la excepción de tipo lócigo, en caso de no lanzarse se genera una exceción de tipo general */
			if (!throwRVIAError(pSessionRviaData, pRestConnector, pJsonData))
				throw new ApplicationException(500, 999999, "Error al procesar la información", "Error al acceder al contenido de un error de tipo ws", null);
		}
		/* si la ejecución ha llegado aqui es que todo parece correcto, se continua */
		pLog.info("No se han detectado errores en el json de respuesta, se continua la ejecución normal");
	}

	/** Formatea la respuesta recibida por el conector para ajustarla al estadar definido por la aplciación
	 * 
	 * @param pJsonData
	 *           Objeto que contiene la información JSON
	 * @return
	 * @throws Exception */
	private static JSONObject formatResponse(JSONObject pJsonData) throws Exception
	{
		/* se comprueba si el json pertenece a WS */
		if (isWSJson(pJsonData))
		{
			pJsonData = adjustWSJson(pJsonData);
		}
		pJsonData = filterResponseFields(pJsonData);
		return pJsonData;
	}

	/** Indica si la cadena que recibe es un objeto json o no
	 * 
	 * @param strData
	 * @return */
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

	/** Comprueba si el contenido del JSON es de tipo WS
	 * 
	 * @param pJsonData
	 *           Objeto que contiene la información JSON
	 * @return */
	public static boolean isWSJson(JSONObject pJsonData)
	{
		boolean fReturn = false;
		String strPrimaryKey = "";
		try
		{
			if (pJsonData.keys().hasNext())
			{
				strPrimaryKey = (String) pJsonData.keys().next();
			}
			if (!strPrimaryKey.trim().isEmpty())
			{
				String strStatusResponse = pJsonData.getJSONObject(strPrimaryKey).getString("codigoRetorno");
				if (strStatusResponse != null && strStatusResponse.trim().length() > 0)
				{
					fReturn = true;
				}
			}
		}
		catch (Exception ex)
		{
			pLog.warn("No es un JSON de WS");
			fReturn = false;
		}
		return fReturn;
	}

	/** Comprueba si el contenido del JSON es un error generado por WS
	 * 
	 * @param pJsonData
	 *           Objeto que contiene la información JSON
	 * @return */
	public static boolean isWSError(JSONObject pJsonData)
	{
		boolean fReturn = false;
		String strPrimaryKey = "";
		try
		{
			if (pJsonData.keys().hasNext())
			{
				strPrimaryKey = (String) pJsonData.keys().next();
			}
			if (!strPrimaryKey.trim().isEmpty())
			{
				String strStatusResponse = (String) pJsonData.getJSONObject(strPrimaryKey).getString("codigoRetorno");
				if ("0".equals(strStatusResponse))
				{
					fReturn = true;
				}
			}
		}
		catch (Exception ex)
		{
			pLog.warn("No es un error de WS");
			fReturn = false;
		}
		return fReturn;
	}

	/** genera unaexceción de tipo lógico a partir del mensaje de error de una respuesta WS
	 * 
	 * @param nHttpErrorCode
	 *           Codigo de error obtenido en la cabecera de la respuesta
	 * @param pJsonData
	 *           Objeto que contiene la información JSON
	 * @return Objeto JSON que contiene el cuerpo
	 * @throws LogicalErrorException */
	private static boolean throwWSError(Integer nHttpErrorCode, JSONObject pJsonData) throws LogicalErrorException
	{
		Integer nCode = null;
		String strMessage = null;
		String strDescription = null;
		boolean fProcessed = false;
		try
		{
			String strPrimaryKey = "";
			if (pJsonData.keys().hasNext())
			{
				strPrimaryKey = (String) pJsonData.keys().next();
			}
			if (!strPrimaryKey.trim().isEmpty())
			{
				JSONObject pJsonContent = pJsonData.getJSONObject(strPrimaryKey).getJSONObject("Errores");
				if (pJsonContent == null)
					pLog.error("No se ha encontrado el nodo 'Errores' dentro del contenido del JSON devuelto por el WS");
				else
				{
					nCode = Integer.parseInt(pJsonContent.getString("codigoMostrar"));
					strMessage = pJsonContent.getString("mensajeMostrar");
					strDescription = pJsonContent.getString("solucion");
					fProcessed = true;
				}
			}
		}
		catch (Exception ex)
		{
			pLog.error("Error al obtener el cuerpo del mensaje de error de una respuesta WS", ex);
		}
		if (fProcessed)
			throw new LogicalErrorException(nHttpErrorCode, nCode, strMessage, strDescription, null);
		return true;
	}

	/** Comprueba si el contenido del JSON es un error generado por ruralvia WS
	 * 
	 * @param pJsonData
	 *           Objeto que contiene la información JSON
	 * @return */
	public static boolean isRVIAError(JSONObject pJsonData)
	{
		boolean fReturn = false;
		String strInnerCode;
		try
		{
			strInnerCode = pJsonData.getString("CODERRR");
			fReturn = (strInnerCode != null) && (!strInnerCode.trim().isEmpty());
		}
		catch (Exception ex)
		{
			pLog.error("No es un error de RVIA");
			fReturn = false;
		}
		return fReturn;
	}

	/** @param pSessionRviaData
	 *           Datos de sesión del usuario en ruralvia
	 * @param pRestConnector
	 *           Conector al origen de los datos
	 * @param pJsonData
	 *           Objeto que contiene la información JSON
	 * @return Indica si se ha llegado ha lanzar una excepción de error
	 * @throws LogicalErrorException */
	public static boolean throwRVIAError(SessionRviaData pSessionRviaData, RestConnector pRestConnector,
			JSONObject pJsonData) throws LogicalErrorException
	{
		boolean fReturn = false;
		String strInnerCode;
		Integer nCode = null;
		String strMessage = null;
		String strDescription = null;
		Integer nHttpErrorCode = 400;
		boolean fProcessed = false;
		try
		{
			strInnerCode = pJsonData.getString("CODERRR");
			nCode = Integer.parseInt(strInnerCode);
			strDescription = pJsonData.getString("TXTERRR");
			strMessage = ErrorManager.getFriendlyErrorFromRuralvia(strInnerCode, pSessionRviaData, pRestConnector);
			fProcessed = true;
		}
		catch (Exception ex)
		{
			pLog.error("Error al obtener el cuerpo del mensaje de error de una respuesta RVIA", ex);
		}
		if (fProcessed)
			throw new LogicalErrorException(nHttpErrorCode, nCode, strMessage, strDescription, null);
		return fReturn;
	}

	/** Formatea el contenido de la respuesta para que comience por el token 'respponse' y despues el contenido
	 * 
	 * @param pJsonData
	 *           Objeto que contiene la información JSON
	 * @return Objeto que contiene la información JSON
	 * @throws Exception */
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

	private static JSONObject filterResponseFields(JSONObject pJsonData)
	{
		// TODO: aqui ira el filtrado de campos de salida
		return pJsonData;
	}
}
