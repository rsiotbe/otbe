package com.rsi.rvia.rest.conector;

import java.util.HashMap;
import java.util.Iterator;
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
	private MiqQuests			pMiqQuests;

	
	/** Devuelve el objeto MiqQuests asociado a la petición
	 * 
	 * @return Objeto MiqQuests */
	public MiqQuests getMiqQuests()
	{
		return this.pMiqQuests;
	}	
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
			MiqQuests pMiqQuests, String strPrimaryPath, MultivaluedMap<String, String> pPathParams, HashMap<String, String> pParamsToInject) throws Exception
	{
		Response pReturn = null;
		String strMethod = pRequest.getMethod();
		String strComponentType;
		pMiqQuests = MiqQuests.getMiqQuests(strPrimaryPath);
		this._requestMethod = strMethod;
		/* se obtiene la configuración de la operativa desde base de datos */
		strComponentType = pMiqQuests.getComponentType();
		pLog.info("Se obtiene la configuración de la base de datos. MiqQuest: " + pMiqQuests);
		pLog.info("Se recibe una petición con tipo de metodo : " + strMethod + " a " + strComponentType);
		switch (strComponentType)
		{
			case "RVIA":
				pReturn = RestRviaConnector.doConnection(pRequest, pMiqQuests, pSessionRvia, strData);
				break;
			case "WS":
			case "API":
				switch (strMethod)
				{
					case "GET":
					pReturn = RestWSConnector.get(pRequest, pMiqQuests, strPrimaryPath, pSessionRvia, pPathParams, pParamsToInject);
						break;
					case "POST":
						pReturn = RestWSConnector.post(pRequest, pMiqQuests, pSessionRvia, strData, pPathParams, pParamsToInject);
						break;
					case "PUT":
					pReturn = RestWSConnector.get(pRequest, pMiqQuests, strPrimaryPath, pSessionRvia, pPathParams, pParamsToInject);
						break;
					case "PATCH":
						pLog.warn("No existe ninguna acción para este método");
						break;
					case "DELETE":
						pReturn = RestWSConnector.delete(pRequest);
						break;
					default:
						pLog.warn("No existe tipo de componente definido para esta petición, se devuelve una respuesta ok vacía");
						pReturn = Response.ok("{}").build();
						break;
				}
				break;
		}
		return pReturn;
	}
}
