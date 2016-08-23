package com.rsi.rvia.rest.DDBB;
import java.io.IOException;
import org.slf4j.LoggerFactory;

public class OracleDDBBBTEST extends AbstractDDBB
{
	private static DDBBConnection _pDDBB = null;
	
	private OracleDDBBBTEST() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException
	{
		pLog = LoggerFactory.getLogger(OracleDDBB.class);
		Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
		pAppProperties.load(this.getClass().getResourceAsStream("/OracleConfig.properties"));		
		pLog.trace("Se crea una nueva instancia de DDBB TEST de Oracle");
	}
	
	public synchronized static DDBBConnection getInstance() 
	{
		if (_pDDBB == null)
		{
			try
			{
				_pDDBB = new OracleDDBBBTEST();	
			}
			catch(Exception ex)
			{
				pLog.error("Error al crear la nueva instancia de DDBB TEST Oracle. ERROR: " + ex.toString());
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