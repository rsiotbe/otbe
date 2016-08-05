package com.rsi.rvia.rest.DDBB;
import java.io.IOException;
import org.slf4j.LoggerFactory;

public class OracleDDBB extends AbstractDDBB
{
	private static DDBBConnection _pDDBB = null;
	
	private OracleDDBB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException
	{
		pLog = LoggerFactory.getLogger(OracleDDBB.class);
		Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
		pAppProperties.load(this.getClass().getResourceAsStream("/OracleConfig.properties"));		
		pLog.trace("Se crea una nueva instancia de DDBB de Oracle");
	}
	
	public synchronized static DDBBConnection getInstance()
	{
		if (_pDDBB == null)
		{
			try
			{
				_pDDBB = new OracleDDBB();	
			}
			catch(Exception ex)
			{
				pLog.error("Error al crear la nueva instancia de DDBB Oracle. ERROR: " + ex.toString());
			}
		}
		return _pDDBB;
	}
	

}