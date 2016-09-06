package com.rsi.rvia.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.PruebaMetodos;
import com.rsi.rvia.rest.DDBB.DDBBConnection;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.tool.Utils;

public class DDBBPoolTest
{
	private static Logger pLog = LoggerFactory.getLogger(DDBBPoolTest.class);

	public enum QueryType
	{
		QuerySlow, QueryFast
	}

	public static void checkPoolDDBB(int nMax, QueryType pQueryType, ThreadPoolExecutor executor) throws Exception
	{
		
		for (int i = 0; i <= nMax; i++)
		{
			switch (pQueryType)
			{
				case QuerySlow:
					executor.execute(new Runnable() {
						@Override
						public void run()
						{
							String strQueryLong = "SELECT codigo,idioma,traduccion FROM bdptb079_idioma";
							Connection pConection = null;
							ResultSet  pResultSet = null;
							try
							{
								pLog.trace("Entrando en una ejecución de traducciones");
								long time_start, time_end;
								time_start = System.currentTimeMillis();
								pConection = DDBBPoolFactory.getDDBB(com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider.OracleBanca);
								PreparedStatement pPreparedStament = pConection.prepareStatement(strQueryLong);
								pResultSet = pPreparedStament.executeQuery();
								pResultSet.close();
								pConection.close();
								time_end = System.currentTimeMillis();
								pLog.trace("Peticion BBDD Lenta: Completada con exito. La tare ha durado " + (time_end - time_start) + " millisegundos");
							}
							catch (Exception e)
							{
								try
								{
									pResultSet.close();
									pConection.close();
								}
								catch (SQLException e1)
								{
									pLog.error("Error al cerrar las conexiones de la BBDD.");
								}
								
								pLog.error("Error en Thread bbdd.");
							}
						}
					});
					break;
				case QueryFast:
					executor.execute(new Runnable() {
						@Override
						public void run()
						{
							String strQueryFast = "select * from bdptb222_miq_quests";
							Connection pConection = null;
							ResultSet pResultSet = null;
							try
							{
								pLog.trace("Entrando en una ejecución rapida");
								long time_start, time_end;
								time_start = System.currentTimeMillis();
								pConection = DDBBPoolFactory.getDDBB(com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider.OracleBanca);
								PreparedStatement pPreparedStament = pConection.prepareStatement(strQueryFast);
								pResultSet = pPreparedStament.executeQuery();
								pResultSet.close();
								pConection.close();
								time_end = System.currentTimeMillis();
								pLog.trace("Peticion BBDD Fast: Completada con exito. La tare ha durado " + (time_end - time_start) + " millisegundos");
							}
							catch (Exception e)
							{
								try
								{
									pResultSet.close();
									pConection.close();
								}
								catch (SQLException e1)
								{
									pLog.error("Error al cerrar las conexiones de la BBDD.");
								}
								pLog.error("Error en Thread bbdd.");
							}
						}
					});
					break;
			}
		}
		
	}
}
