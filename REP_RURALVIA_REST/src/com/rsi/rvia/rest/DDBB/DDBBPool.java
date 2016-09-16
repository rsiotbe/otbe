package com.rsi.rvia.rest.DDBB;

import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DDBBPool
{
	protected static Logger	pLog	= LoggerFactory.getLogger(DDBBPool.class);

	/** Recupera las propiedades de configuración de Banca Oracle para configurar un pool de conexiones local
	 * 
	 * @return DataSource con la configuración de Banca Oracle ya cargada
	 * @throws Exception */
	public static DataSource getDatasourceFromBancaOracleLocalPool(Properties pDDBBProp) throws Exception
	{
		Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
		return setupLocalPoolDataSource(pDDBBProp);
	}

	/** Recupera las propiedades de configuración de Banca Oracle para configurar un pool de conexiones del servidor
	 * 
	 * @return DataSource con la configuración de Banca Oracle ya cargada
	 * @throws Exception */
	public static DataSource getDatasourceFromBancaOracleServerPool() throws Exception
	{
		Context initCtx = new InitialContext();
		return (DataSource) initCtx.lookup("java:comp/env/jdbc/RVIA_REST_BANCA");
	}

	/** Recupera las propiedades de configuración de CIP Oracle para configurar un pool de conexiones local
	 * 
	 * @return DataSource con la configuración de CIP Oracle ya cargada
	 * @throws Exception */
	public static DataSource getDatasourceFromCIPOracleLocalPool(Properties pDDBBProp) throws Exception
	{
		Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
		return setupLocalPoolDataSource(pDDBBProp);
	}

	/** Recupera las propiedades de configuración de CIP Oracle para configurar un pool de conexiones del servidor
	 * 
	 * @return DataSource con la configuración de CIP Oracle ya cargada
	 * @throws Exception */
	public static DataSource getDatasourceFromCIPOracleServerPool() throws Exception
	{
		Context initCtx = new InitialContext();
		return (DataSource) initCtx.lookup("java:comp/env/jdbc/RVIA_REST_CIP");
	}

	/** Recupera las propiedades de configuración de MySql para configurar un pool de conexiones local
	 * 
	 * @return DataSource con la configuración de MySql ya cargada
	 * @throws Exception */
	public static DataSource getDatasourceFromMySqlLocalPool(Properties pDDBBProp) throws Exception
	{
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		return setupLocalPoolDataSource(pDDBBProp);
	}

	/** Recupera las propiedades de configuración de MySql para configurar un pool de conexiones del servidor
	 * 
	 * @return DataSource con la configuración de MySql ya cargada
	 * @throws Exception */
	public static DataSource getDatasourceFromMySqlServerPool() throws Exception
	{
		Context initCtx = new InitialContext();
		return (DataSource) initCtx.lookup("java:comp/env/jdbc/RVIA_REST_MYSQL");
	}

	/** Carga la configuración del pool de base de datos extrayendo los datos de las propiedades
	 * 
	 * @param pProps
	 *           Propiedades con la configuración de la base de datos especifica.
	 * @return DataSource con la configuración recibida ya cargada. */
	private static DataSource setupLocalPoolDataSource(Properties pProps)
	{
		ConnectionFactory pConnFactory;
		PoolableConnectionFactory pPoolableConnFactory;
		GenericObjectPoolConfig pObjectPoolConfig;
		ObjectPool<PoolableConnection> pConnPool;
		PoolingDataSource<PoolableConnection> pDataSource;
		/* Se recuperan las propiedades necesarias */
		String strConnectURI = pProps.getProperty("urlDriver");
		int nMaxPoolTotal = 8;
		int nMaxPoolIdle = 8;
		int nMinPoolIdel = 0;
		try
		{
			nMaxPoolTotal = Integer.parseInt(pProps.getProperty("maxPoolTotal"));
		}
		catch (Exception ex)
		{
			pLog.warn("No se ha podido recuperar el valor de maxPoolTotal del .properties, usando valor por defecto.");
		}
		try
		{
			nMaxPoolIdle = Integer.parseInt(pProps.getProperty("maxPoolIdle"));
		}
		catch (Exception ex)
		{
			pLog.warn("No se ha podido recuperar el valor de maxPoolIdle del .properties, usando valor por defecto.");
		}
		try
		{
			nMinPoolIdel = Integer.parseInt(pProps.getProperty("minPoolIdle"));
		}
		catch (Exception ex)
		{
			pLog.warn("No se ha podido recuperar el valor de minPoolIdle del .properties, usando valor por defecto.");
		}
		/* se crea la fabrica de conexiones que el pool va a utilizar */
		pConnFactory = new DriverManagerConnectionFactory(strConnectURI, pProps);
		/* se crea la fabrica de conexion de tipo "poolable" */
		pPoolableConnFactory = new PoolableConnectionFactory(pConnFactory, null);
		/* se crea el objeto de configuración del pool */
		pObjectPoolConfig = new GenericObjectPoolConfig();
		pObjectPoolConfig.setMaxTotal(nMaxPoolTotal);
		pObjectPoolConfig.setMaxIdle(nMaxPoolIdle);
		pObjectPoolConfig.setMinIdle(nMinPoolIdel);
		/* se instancia el objeto que funciona como pool */
		pConnPool = new GenericObjectPool<>(pPoolableConnFactory, pObjectPoolConfig);
		/* se asocia el objeto pool con la factoria */
		pPoolableConnFactory.setPool(pConnPool);
		/* se crea el controlador del pool */
		pDataSource = new PoolingDataSource<>(pConnPool);
		return pDataSource;
	}
}
