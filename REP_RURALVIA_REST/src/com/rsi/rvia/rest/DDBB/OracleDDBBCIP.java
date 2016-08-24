package com.rsi.rvia.rest.DDBB;
import java.io.IOException;
import org.slf4j.LoggerFactory;

public class OracleDDBBCIP extends AbstractDDBB
{
	private static DDBBConnection _pDDBB = null;
	
	private OracleDDBBCIP() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException
	{
		pLog = LoggerFactory.getLogger(OracleDDBBCIP.class);
		Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
		pAppProperties.load(this.getClass().getResourceAsStream("/OracleConfig.properties"));		
		pLog.trace("Se crea una nueva instancia de DDBB CIP de Oracle");
	}
	
	public synchronized static DDBBConnection getInstance() 
	{
		if (_pDDBB == null)
		{
			try
			{
				_pDDBB = new OracleDDBBCIP();	
			}
			catch(Exception ex)
			{
				pLog.error("Error al crear la nueva instancia de DDBB CIP Oracle. ERROR: " + ex.toString());
			}
		}
		return _pDDBB;
	}
	
	public synchronized static DDBBConnection getInstance(String prefix)
	{
		schemaPrefix = prefix;
		return getInstance();
	}	

}