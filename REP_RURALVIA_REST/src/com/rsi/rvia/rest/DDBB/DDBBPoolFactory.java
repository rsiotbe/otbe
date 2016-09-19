package com.rsi.rvia.rest.DDBB;

import java.sql.Connection;
import java.util.Properties;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Clase que gestiona la solicitud de una instancia de base de datos */
public class DDBBPoolFactory
{
	/** Enumeración que contiene los diferentes tipos de Pool de BBDD implementados en la aplicación */
	public enum DDBBProvider
	{
		MySql, OracleBanca, OracleCIP;
	}

	private static Logger		pLog					= LoggerFactory.getLogger(DDBBProvider.class);
	private static DataSource	pOracleBanca		= null;
	private static DataSource	pOracleCIP			= null;
	private static DataSource	pMySql				= null;
	private static Properties	pPropOracleBanca	= null;
	private static Properties	pPropOracleCIP		= null;
	private static Properties	pPropMySql			= null;

	/** Obtiene la clase que gestiona la conexión con base de datos
	 * 
	 * @param pDDBBProvider
	 *           Tipo de base de datos a instanciar
	 * @return Conexión con la base de datos seleccionar
	 * @throws Exception */
	public static Connection getDDBB(DDBBProvider pDDBBProvider) throws Exception
	{
		Connection pReturn = null;
		boolean fUseServerPool;
		try
		{
			switch (pDDBBProvider)
			{
				case OracleBanca:
					if (pOracleBanca == null)
					{
						pLog.debug("Se procede a conectar con la base de datos de Oracle-Banca");
						/* se leen las propiedaddes de de esta conexión */
						if (pPropOracleBanca == null)
						{
							pPropOracleBanca = new Properties();
							pPropOracleBanca.load(DDBBPoolFactory.class.getResourceAsStream("/Banca.OracleConfig.properties"));
						}
						fUseServerPool = Boolean.parseBoolean(pPropOracleBanca.getProperty("useServerPool"));
						if (fUseServerPool)
						{
							/* por conexión con el pool del servidor */
							pLog.debug("Se utiliza el pool de base de datos del servidor");
							pOracleBanca = DDBBPool.getDatasourceFromBancaOracleServerPool();
						}
						else
						{
							/* por conexión al pool interno generado por la aplicación */
							pLog.debug("Se utiliza el pool local de base de datos");
							pOracleBanca = DDBBPool.getDatasourceFromBancaOracleLocalPool(pPropOracleBanca);
						}
					}
					pReturn = pOracleBanca.getConnection();
					break;
				case OracleCIP:
					if (pOracleCIP == null)
					{
						pLog.debug("Se procede a conectar con la base de datos de Oracle-CIP");
						/* se leen las propiedaddes de de esta conexión */
						if (pPropOracleCIP == null)
						{
							pPropOracleCIP = new Properties();
							pPropOracleCIP.load(DDBBPoolFactory.class.getResourceAsStream("/CIP.OracleConfig.properties"));
						}
						fUseServerPool = Boolean.parseBoolean(pPropOracleCIP.getProperty("useServerPool"));
						if (fUseServerPool)
						{
							/* por conexión con el pool del servidor */
							pLog.debug("Se utiliza el pool de base de datos del servidor");
							pOracleBanca = DDBBPool.getDatasourceFromCIPOracleServerPool();
						}
						else
						{
							/* por conexión al pool interno generado por la aplicación */
							pLog.debug("Se utiliza el pool local de base de datos");
							pOracleBanca = DDBBPool.getDatasourceFromCIPOracleLocalPool(pPropOracleCIP);
						}
					}
					pReturn = pOracleCIP.getConnection();
					break;
				case MySql:
					if (pMySql == null)
					{
						pLog.debug("Se procede a conectar con la base de datos de MySql");
						/* se leen las propiedaddes de de esta conexión */
						if (pPropMySql == null)
						{
							pPropMySql = new Properties();
							pPropMySql.load(DDBBPoolFactory.class.getResourceAsStream("/MySqlConfig.properties"));
						}
						fUseServerPool = Boolean.parseBoolean(pPropMySql.getProperty("useServerPool"));
						if (fUseServerPool)
						{
							/* por conexión con el pool del servidor */
							pLog.debug("Se utiliza el pool de base de datos del servidor");
							pMySql = DDBBPool.getDatasourceFromMySqlServerPool();
						}
						else
						{
							/* por conexión al pool interno generado por la aplicación */
							pLog.debug("Se utiliza el pool local de base de datos");
							pMySql = DDBBPool.getDatasourceFromMySqlLocalPool(pPropMySql);
						}
					}
					pReturn = pMySql.getConnection();
					break;
			}
		}
		catch (Exception ex)
		{
			pLog.error("Error al obtener la conexión con la base de datos", ex);
		}
		return pReturn;
	}
}
