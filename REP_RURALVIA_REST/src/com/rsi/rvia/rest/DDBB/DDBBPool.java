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
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DDBBPool
{
	protected static Logger	pLog	= LoggerFactory.getLogger(DDBBPool.class);
	public static DataSource setupBancaOracle() throws Exception
	{
		Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
		Properties pDDBBProp = new Properties();
		pDDBBProp.load(DDBBPool.class.getResourceAsStream("/Banca.OracleConfig.properties"));		
		return setupDataSource(pDDBBProp);
	}
	
	public static DataSource setupCIPOracle() throws Exception
	{
		Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
		Properties pDDBBProp = new Properties();
		pDDBBProp.load(DDBBPool.class.getResourceAsStream("/CIP.OracleConfig.properties"));		
		return setupDataSource(pDDBBProp);
	}
	
	public static DataSource setupMySql() throws Exception
	{
		Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
		Properties pDDBBProp = new Properties();
		pDDBBProp.load(DDBBPool.class.getResourceAsStream("/MySqlConfig.properties"));		
		return setupDataSource(pDDBBProp);
	}
	
	private static DataSource setupDataSource(Properties pProps)
	{
		ConnectionFactory pConnFactory;
		PoolableConnectionFactory pPoolableConnFactory;
		GenericObjectPoolConfig pObjectPoolConfig = null;
		ObjectPool<PoolableConnection> pConnPool;		
		PoolingDataSource<PoolableConnection> pDataSource;
		
		/* Se recuperan las propiedades necesarias */
		String strConnectURI = pProps.getProperty("urlDriver");
		int nMaxPoolTotal = 8;
		int nMaxPoolIdle = 8;
		int nMinPoolIdel = 0;
		
		try{
			nMaxPoolTotal = Integer.parseInt(pProps.getProperty("maxPoolTotal"));
		}catch(Exception ex){
			pLog.warn("No se ha podido recuperar el valor de maxPoolTotal del .properties, usando valor por defecto.");
		}
		try{
			nMaxPoolIdle = Integer.parseInt(pProps.getProperty("maxPoolIdle"));
		}catch(Exception ex){
			pLog.warn("No se ha podido recuperar el valor de maxPoolIdle del .properties, usando valor por defecto.");
		}
		try{
			nMinPoolIdel = Integer.parseInt(pProps.getProperty("minPoolIdle"));
		}catch(Exception ex){
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
