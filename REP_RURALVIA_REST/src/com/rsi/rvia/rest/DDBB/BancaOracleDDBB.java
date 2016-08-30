package com.rsi.rvia.rest.DDBB;
import java.io.IOException;
import org.slf4j.LoggerFactory;

public class BancaOracleDDBB extends AbstractDDBB
{
	private static DDBBConnection _pDDBB = null;
	
	private BancaOracleDDBB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException
	{
		pLog = LoggerFactory.getLogger(BancaOracleDDBB.class);
		Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
		pAppProperties.load(this.getClass().getResourceAsStream("/Banca.OracleConfig.properties"));		
		pLog.trace("Se crea una nueva instancia de DDBB de Oracle de Banca");
	}
	
	public synchronized static DDBBConnection getInstance() 
	{
		if (_pDDBB == null)
		{
			try
			{
				_pDDBB = new BancaOracleDDBB();	
			}
			catch(Exception ex)
			{
				pLog.error("Error al crear la nueva instancia de DDBB Oracle de Banca. ERROR: " + ex.toString());
			}
		}
		return _pDDBB;
	}
}