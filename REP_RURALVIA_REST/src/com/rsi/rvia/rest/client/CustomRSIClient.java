package com.rsi.rvia.rest.client;


import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

public class CustomRSIClient {
	/**
	 * Retorna un cliente de Jersey con la configuración de timeOuts 
	 * @param connectioTimeout Timeout de conexión
	 * @param readTimeout Timeout de lectura
	 * @return Objeto Client (Jersey)
	 */
	public static Client getClient(int connectionTimeout, int readTimeout){
		Client retorno = null;
		ClientConfig config;
	   config = new ClientConfig();
		retorno = ClientBuilder.newClient(config);
		retorno.property(ClientProperties.CONNECT_TIMEOUT, connectionTimeout);
		retorno.property(ClientProperties.READ_TIMEOUT,    readTimeout);	
	   return retorno;
	} 
	/**
	 * Retorna cliente de Jersey con la configuración de timeOuts por defecto, a 2000 milisegundos para el de conexión, y a 1000 para el de lectura.
	 * @return Objeto Client (Jersey)
	 */	
	public static Client getClient(){
		return getClient(12000, 10000);		
	}

}
