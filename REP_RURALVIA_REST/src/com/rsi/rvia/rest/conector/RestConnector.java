package com.rsi.rvia.rest.conector;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.operation.MiqQuests;
import com.rsi.rvia.rest.session.SessionRviaData;

/** Clase que gestiona la conexión y comunicaciñon con el proveedor de datos (Ruralvia o WS) */
public class RestConnector
{
	private static Logger	pLog				= LoggerFactory.getLogger(RestConnector.class);
	private String				_requestMethod	= "";

	/**
	 * Devuelve el método asociado a la petición
	 * 
	 * @return String método del request
	 */
	public String getMethod()
	{
		return this._requestMethod;
	}

	/**
	 * Realiza la llamada al proveedor de datos para obtener el resultado de la operación
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
	 * @throws Exception
	 */
	public Response getData(HttpServletRequest pRequest, String strData, SessionRviaData pSessionRvia,
			MiqQuests pMiqQuests, MultivaluedMap<String, String> pPathParams) throws Exception
	{
		Response pReturn = null;
		String strMethod = pRequest.getMethod();
		String strComponentType;
		this._requestMethod = strMethod;
		/* se obtiene la configuración de la operativa desde base de datos */
		strComponentType = pMiqQuests.getComponentType();
		pLog.info("Se obtiene la configuración de la base de datos. MiqQuest: " + pMiqQuests);
		pLog.info("Se recibe una petición con tipo de metodo : " + strMethod);
		/* se invoca al tipo de petición leido desde configuracón */
		switch (strMethod)
		{
			case "GET":
				if ("RVIA".equals(strComponentType))
				{
					pLog.trace("Petición de tipo " + strMethod + " a " + strComponentType);
					pReturn = RestRviaConnector.doConnection(pRequest, pMiqQuests, pSessionRvia, strData);
				}
				else if ("WS".equals(strComponentType))
				{
					pLog.trace("Petición de tipo " + strMethod + " a " + strComponentType);
					pReturn = RestWSConnector.get(pRequest, pMiqQuests, pSessionRvia, pPathParams);
				}
				else if ("API".equals(strComponentType))
				{
					pLog.trace("Petición de tipo " + strMethod + " a " + strComponentType);
					pReturn = RestWSConnector.get(pRequest, pMiqQuests, pSessionRvia, pPathParams);
				}
				else
				{
					pLog.warn("No existe tipo de componente definido para esta petición, se devuelve una respuesta ok vacía");
					pReturn = Response.ok("{}").build();
				}
				break;
			case "POST":
				if ("RVIA".equals(strComponentType))
				{
					pLog.trace("Petición de tipo " + strMethod + " a " + strComponentType);
					pReturn = RestRviaConnector.doConnection(pRequest, pMiqQuests, pSessionRvia, strData);
				}
				else if ("WS".equals(strComponentType))
				{
					pLog.trace("Petición de tipo " + strMethod + " a " + strComponentType);
					pReturn = RestWSConnector.post(pRequest, pSessionRvia, strData, pMiqQuests, pPathParams);
				}
				else if ("API".equals(strComponentType))
				{
					pLog.trace("Petición de tipo " + strMethod + " a " + strComponentType);
					pReturn = RestWSConnector.post(pRequest, pSessionRvia, strData, pMiqQuests, pPathParams);
				}
				else
				{
					pLog.warn("No existe tipo de componente definido para esta petición, se devuelve una respuesta ok vacía");
					pReturn = Response.ok("{}").build();
				}
				break;
			case "PUT":
				if ("WS".equals(strComponentType))
				{
					pLog.trace("Petición de tipo " + strMethod + " a " + strComponentType);
					pReturn = RestWSConnector.put(pRequest, pSessionRvia, strData, pMiqQuests, pPathParams);
				}
				else if ("API".equals(strComponentType))
				{
					pLog.trace("Petición de tipo " + strMethod + " a " + strComponentType);
					pReturn = RestWSConnector.put(pRequest, pSessionRvia, strData, pMiqQuests, pPathParams);
				}
				else
				{
					pLog.warn("No existe tipo de componente definido para esta petición, se devuelve una respuesta ok vacía");
					pReturn = Response.ok("{}").build();
				}
				break;
			case "PATCH":
				pLog.warn("No existe ninguna acción para este método");
				break;
			case "DELETE":
				if ("WS".equals(strComponentType))
				{
					pLog.trace("Petición de tipo " + strMethod + " a " + strComponentType);
					pReturn = RestWSConnector.delete(pRequest);
				}
				else if ("API".equals(strComponentType))
				{
					pLog.trace("Petición de tipo " + strMethod + " a " + strComponentType);
					pReturn = RestWSConnector.delete(pRequest);
				}
				else
				{
					pLog.warn("No existe tipo de componente definido para esta petición, se devuelve una respuesta ok vacía");
					pReturn = Response.ok("{}").build();
				}
				break;
		}
		return pReturn;
	}
}
