package com.rsi.rvia.rest.DDBB;
import java.io.IOException;
import org.slf4j.LoggerFactory;

public class OracleDDBBBDES extends AbstractDDBB
{
	private static DDBBConnection _pDDBB = null;
	
	private OracleDDBBBDES() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException
	{
		pLog = LoggerFactory.getLogger(OracleDDBB.class);
		Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
		pAppProperties.load(this.getClass().getResourceAsStream("/OracleConfig{TEST}.properties"));		
		pLog.trace("Se crea una nueva instancia de DDBB DES de Oracle");
	}
	
	public synchronized static DDBBConnection getInstance() 
	{
		if (_pDDBB == null)
		{
			try
			{
				_pDDBB = new OracleDDBBBDES();	
			}
			catch(Exception ex)
			{
				pLog.error("Error al crear la nueva instancia de DDBB DES Oracle. ERROR: " + ex.toString());
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