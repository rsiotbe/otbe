package com.rsi.rvia.rest.DDBB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Interfaz que tiene los métodos minimos de una conexión a base de datos
 *
 */
public interface DDBBConnection
{
	/**
	 * Prepara la ejecución de una consulta sobre la base de datos
	 * @param strSQL Sql a ejecutar
	 * @return Objeto asociado a la consulta
	 * @throws Exception
	 */
	public PreparedStatement prepareStatement(String strSQL) throws Exception;

	public void BBDD_Disconnect() throws Exception;
	
	public void executeUpdate(PreparedStatement pStatement) throws Exception;
		
	public ResultSet executeQuery(PreparedStatement pStatement) throws Exception;
		
	public void closeStatement(PreparedStatement pStatement) throws Exception;
	
}
