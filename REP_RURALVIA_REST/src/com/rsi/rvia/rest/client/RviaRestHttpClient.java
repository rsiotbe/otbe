package com.rsi.rvia.rest.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

public class RviaRestHttpClient
{
	/** Retorna un cliente de Jersey con la configuración de timeOuts
	 * 
	 * @param connectioTimeout
	 *           Timeout de conexión
	 * @param readTimeout
	 *           Timeout de lectura
	 * @return Objeto Client (Jersey) */
	public static Client getClient(int nConnectionTimeout, int nReadTimeout)
	{
		Client pRetorno = null;
		ClientConfig pConfig;
		pConfig = new ClientConfig();
		pRetorno = ClientBuilder.newClient(pConfig);
		pRetorno.property(ClientProperties.CONNECT_TIMEOUT, nConnectionTimeout);
		pRetorno.property(ClientProperties.READ_TIMEOUT, nReadTimeout);
		return pRetorno;
	}

	/** Retorna cliente de Jersey con la configuración de timeOuts por defecto, a 2000 milisegundos para el de conexión,
	 * y a 1000 para el de lectura.
	 * 
	 * @return Objeto Client (Jersey) */
	public static Client getClient()
	{
		return getClient(12000, 10000);
	}
}
