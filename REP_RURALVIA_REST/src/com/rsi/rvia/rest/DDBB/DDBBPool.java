package com.rsi.rvia.rest.DDBB;

import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DDBBPool
{
	protected static Logger	pLog	= LoggerFactory.getLogger(DDBBPool.class);

	public static DataSource setupBancaOracle() throws Exception
	{
		String strConnectURI;
		Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
		Properties pDDBBProp = new Properties();
		pDDBBProp.load(DDBBPool.class.getResourceAsStream("/Banca.OracleConfig.properties"));		
		strConnectURI = pDDBBProp.getProperty("urlDriver");
		return setupDataSource(strConnectURI, pDDBBProp);
	}
	
	public static DataSource setupCIPOracle() throws Exception
	{
		String strConnectURI;
		Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
		Properties pDDBBProp = new Properties();
		pDDBBProp.load(DDBBPool.class.getResourceAsStream("/CIP.OracleConfig.properties"));		
		strConnectURI = pDDBBProp.getProperty("urlDriver");
		return setupDataSource(strConnectURI, pDDBBProp);
	}
	
	public static DataSource setupMySql() throws Exception
	{
		String strConnectURI;
		Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
		Properties pDDBBProp = new Properties();
		pDDBBProp.load(DDBBPool.class.getResourceAsStream("/MySqlConfig.properties"));		
		strConnectURI = pDDBBProp.getProperty("urlDriver");
		
		return setupDataSource(strConnectURI, pDDBBProp);
	}
	
	private static DataSource setupDataSource(String strConnectURI, Properties pProps)
	{
		ConnectionFactory pConnFactory;
		PoolableConnectionFactory pPoolableConnFactory;
		ObjectPool<PoolableConnection> pConnPool;		
		PoolingDataSource<PoolableConnection> pDataSource;
	
		/* se crea la fabrica de conexiones que el pool va a utilizar */
		pConnFactory = new DriverManagerConnectionFactory(strConnectURI, pProps);
		
		/* se crea la fabrica de conexion de tipo "poolable" */
		pPoolableConnFactory = new PoolableConnectionFactory(pConnFactory, null);
		
		/* se instancia el objeto que funciona como pool */
		pConnPool = new GenericObjectPool<>(pPoolableConnFactory);
		
		/* se asocia el objeto pool con la factoria */
		pPoolableConnFactory.setPool(pConnPool);
		
		/* se crea el controlador del pool */
		pDataSource = new PoolingDataSource<>(pConnPool);
		return pDataSource;
	}
}
