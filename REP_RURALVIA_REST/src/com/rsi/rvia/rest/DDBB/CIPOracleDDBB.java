package com.rsi.rvia.rest.DDBB;
import java.io.IOException;
import org.slf4j.LoggerFactory;

public class CIPOracleDDBB extends AbstractDDBB
{
	private static DDBBConnection _pDDBB = null;
	
	private CIPOracleDDBB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException
	{
		pLog = LoggerFactory.getLogger(CIPOracleDDBB.class);
		Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
		pAppProperties.load(this.getClass().getResourceAsStream("/CIP.OracleConfig.properties"));		
		pLog.trace("Se crea una nueva instancia de DDBB CIP de Oracle");
	}
	
	public synchronized static DDBBConnection getInstance() 
	{
		if (_pDDBB == null)
		{
			try
			{
				_pDDBB = new CIPOracleDDBB();	
			}
			catch(Exception ex)
			{
				pLog.error("Error al crear la nueva instancia de DDBB CIP Oracle. ERROR: " + ex.toString());
			}
		}
		return _pDDBB;
	}
}