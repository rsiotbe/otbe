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
	private static Logger	pLog	= LoggerFactory.getLogger(RestConnector.class);
	private MiqQuests			pMiqQuests;

	/** Devuelve el objeto MiqQuests asociado a la petición
	 * 
	 * @return Objeto MiqQuests */
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
	 * @throws Exception */
	public Response getData(HttpServletRequest pRequest, String strData, SessionRviaData pSessionRvia,
			String strPrimaryPath, MultivaluedMap<String, String> pPathParams) throws Exception
	{
		Response pReturn = null;
		String strMethod = pRequest.getMethod();
		/* se obtiene la configuración de la operativa desde base de datos */
		pMiqQuests = MiqQuests.getMiqQuests(strPrimaryPath);
		pLog.info("Se obtiene la configuración de la base de datos. MiqQuest: " + pMiqQuests);
		pLog.info("Se recibe una petición con tipo de metodo : " + strMethod);
		/*
		 * se comprueba si la infomración asociada a la petición enviada por el cliente viene vacia o nulo se inicializa a
		 * un json vacio
		 */
		if (strData == null || strData.trim().isEmpty())
			strData = "{}";
		/* se invoca al tipo de petición leido desde configuracón */
		switch (strMethod)
		{
			case "GET":
				if ("RVIA".equals(pMiqQuests.getComponentType()))
				{
					pLog.trace("Derivando petición GET a ruralvía");
					pReturn = RestRviaConnector.doConnection(pRequest, pMiqQuests, pSessionRvia, strData);
				}
				else
				{
					pLog.trace("Solicitando petición GET a WS");
					pReturn = RestWSConnector.get(pRequest, pMiqQuests, strPrimaryPath, pSessionRvia, pPathParams);
				}
				break;
			case "POST":
				if ("RVIA".equals(pMiqQuests.getComponentType()))
				{
					pLog.trace("Derivando petición POST a ruralvía");
					pReturn = RestRviaConnector.doConnection(pRequest, pMiqQuests, pSessionRvia, strData);
				}
				else
				{
					pLog.trace("Solicitando petición POST a WS");
					pReturn = RestWSConnector.post(pRequest, strPrimaryPath, pSessionRvia, strData, pMiqQuests, pPathParams);
				}
				break;
			case "PUT":
				pLog.trace("Solicitando petición PUT a WS");
				pReturn = RestWSConnector.put(pRequest, strPrimaryPath, pSessionRvia, strData, pMiqQuests, pPathParams);
				break;
			case "PATCH":
				pLog.warn("No existe ninguna acción para este método");
				break;
			case "DELETE":
				pReturn = RestWSConnector.delete(pRequest);
				break;
		}
		return pReturn;
	}
}
