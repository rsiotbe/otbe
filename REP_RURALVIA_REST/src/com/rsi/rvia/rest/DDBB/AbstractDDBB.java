package com.rsi.rvia.rest.DDBB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDDBB implements DDBBConnection
{
	protected static Logger	pLog				= LoggerFactory.getLogger(AbstractDDBB.class);	;
	private Connection		_pConnection	= null;
	protected Properties		pAppProperties	= new Properties();

	private synchronized void BBDD_Connect() throws Exception
	{
		String strURI = null;
		try
		{
			if ((_pConnection == null) || (_pConnection.isClosed()))
			{
				strURI = pAppProperties.getProperty("urlDriver");
				pLog.trace("Se inicia la conexion con la BBDD con el driver:  " + strURI);
				_pConnection = DriverManager.getConnection(strURI, pAppProperties.getProperty("user"), pAppProperties.getProperty("pass"));
				pLog.trace("Se conecta con la BBDD OK");
			}
			else
				pLog.trace("La BBDD ya esto conectada");
		}
		catch (Exception ex)
		{
			pLog.error("Error al conectar con el driver. ERROR: " + ex.toString());
			throw ex;
		}
	}

	public PreparedStatement prepareStatement(String strSQL) throws Exception
	{
		PreparedStatement pReturn;
		BBDD_Connect();
		pLog.trace("Se prepara la consulta: " + strSQL);
		pReturn = _pConnection.prepareStatement(strSQL);
		pLog.trace("Consulta preparada OK");
		return pReturn;
	}

	public synchronized void BBDD_Disconnect() throws Exception
	{
		try
		{
			if ((_pConnection != null) || (!_pConnection.isClosed()))
			{
				pLog.info("Se realiza la desconexion con la BBDD");
				_pConnection.close();
			}
		}
		catch (Exception ex)
		{
			pLog.error("Error al desconectar con la BBDD. ERROR: " + ex.toString());
			throw ex;
		}
	}

	public synchronized void executeUpdate(PreparedStatement pStatement) throws Exception
	{
		try
		{
			pStatement.executeUpdate();
			this.closeStatement(pStatement);
		}
		catch (Exception ex)
		{
			pLog.error("Ha fallado la peticion de Update. Se inicia el reintento. ERROR: " + ex.toString());
			pStatement.executeUpdate();
		}
	}

	public synchronized ResultSet executeQuery(PreparedStatement pStatement) throws Exception
	{
		ResultSet pReturn;
		try
		{
			pReturn = pStatement.executeQuery();
			return pReturn;
		}
		catch (Exception ex)
		{
			pLog.error("Ha fallado la peticin de Query. Se inicia el reintento. ERROR: " + ex.toString());
			return pStatement.executeQuery();
		}
	}

	public synchronized void closeStatement(PreparedStatement pStatement) throws Exception
	{
		try
		{
			pLog.trace("Se invoca al cierre del statment");
			pStatement.close();
			pLog.trace("Cierre OK");
		}
		catch (Exception ex)
		{
			pLog.error("Ha fallado la peticion de cerrar el statment. ERROR: " + ex.toString());
		}
	}
}
