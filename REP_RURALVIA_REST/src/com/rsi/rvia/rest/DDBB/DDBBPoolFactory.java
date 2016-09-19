package com.rsi.rvia.rest.DDBB;

import java.sql.Connection;
import javax.sql.DataSource;

/**
 * Clase que gestiona la solicitud de una instancia de base de datos
 *
 */
public class DDBBPoolFactory
{
	/**
	 * Enumeración que contiene los diferentes tipos de Pool de BBDD implementados en la aplicación
	 *
	 */
	public enum DDBBProvider
	{
		MySql, OracleBanca, OracleCIP;
	}

	private static DataSource pOracleBanca = null;
	private static DataSource pOracleCIP = null;
	private static DataSource pMySql = null;
	
	/**
	 * Obtiene la clase que gestiona la conexión con base de datos
	 * @param pDDBBProvider Tipo de base de datos a instanciar
	 * @return Conexión con la base de datos seleccionar
	 * @throws Exception 
	 */
	public static Connection getDDBB(DDBBProvider pDDBBProvider) throws Exception
	{
		Connection pReturn = null;
		if (pDDBBProvider != null)
		{
			switch (pDDBBProvider)
			{
				case OracleBanca:
					if(pOracleBanca == null)
						pOracleBanca = DDBBPool.setupBancaOracle();
					pReturn = pOracleBanca.getConnection();
					break;
				case OracleCIP:
					if(pOracleCIP == null)
						pOracleCIP = DDBBPool.setupCIPOracle();
					pReturn = pOracleCIP.getConnection();
					break;
				case MySql:
					if(pMySql == null)
						pMySql = DDBBPool.setupBancaOracle();
					pReturn = pMySql.getConnection();
					break;
			}
		}
		return pReturn;
	}
}
