package com.rsi.rvia.rest.DDBB;


import java.io.IOException;
import org.slf4j.LoggerFactory;


public class MySqlDDBB extends AbstractDDBB
{
	private static DDBBConnection _pDDBB = null;

	private MySqlDDBB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException
	{
		pLog = LoggerFactory.getLogger(MySqlDDBB.class);
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		pAppProperties.load(this.getClass().getResourceAsStream("/MySqlConfig.properties"));		
		pLog.trace("Se crea una nueva instancia de DDBB de MySql");
	}
	
	public synchronized static DDBBConnection getInstance()
	{
		if (_pDDBB == null)
		{
			try
			{
				_pDDBB = new MySqlDDBB();	
			}
			catch(Exception ex)
			{
				pLog.error("Error al crear la nueva instancia de DDBB MySql. ERROR: " + ex.toString());
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