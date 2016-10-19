package com.rsi.rvia.rest.error;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;
import com.rsi.rvia.rest.error.exceptions.ApplicationException;
import com.rsi.rvia.rest.operation.MiqQuests;
import com.rsi.rvia.rest.session.RequestConfigRvia;
import com.rsi.rvia.rest.tool.Utils;

public class ErrorManager
{
	private static Logger		pLog				= LoggerFactory.getLogger(ErrorManager.class);
	public static final String	ERROR_TEMPLATE	= "/error/error.xhtml";

	public static ErrorResponse getErrorResponseObject(Exception pEx)
	{
		ErrorResponse pReturn;
		pLog.info("Se gestiona un error de tipo: " + pEx.getClass().getName());
		/* se evalua que tipo de excepción se ha capturado */
		if (ApplicationException.class.isAssignableFrom(pEx.getClass()))
		{
			ApplicationException pException;
			pException = (ApplicationException) pEx;
			pReturn = new ErrorResponse(pException);
		}
		else
		{
			pReturn = new ErrorResponse(pEx);
		}
		/* se deja traza del error en los log */
		pLog.error("Se ha producido un error: " + pEx.toString() + "\n" + Utils.getExceptionStackTrace(pEx));
		return pReturn;
	}

	/**
	 * Recupera el mensaje amigable que contiene ruralvia del codigo de error generado
	 * 
	 * @param strErrorCode
	 *           Codigo de error
	 * @param RequestConfigRvia
	 *           Objeto que contiene los datos de ususario de ruralvia
	 * @param pRestConnector
	 *           Objeto que contiene la información de la petición realizada, se utilzia para obtener el clave página
	 * @return Texto de error ya traducido
	 * @throws Exception
	 */
	public static String getFriendlyErrorFromRuralvia(String strErrorCode, RequestConfigRvia pSessionRviaData,
			MiqQuests pMiqQuests) throws Exception
	{
		Connection pConnection = null;
		PreparedStatement pPreparedStatement = null;
		ResultSet pResultSet = null;
		String strLanguage;
		String strClavepagina;
		String strReturn = null;
		;
		try
		{
			/* se hace una consulta a la tabla especifa de errores por clave página */
			String strQuery = "SELECT * FROM BDPTB090_ERRORES where CODERR = ? and IDIOMAERR = ? and CLAVE_PAGINA = ?";
			strLanguage = pSessionRviaData.getLanguage();
			strClavepagina = pMiqQuests.getEndPoint();
			pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
			pPreparedStatement = pConnection.prepareStatement(strQuery);
			pPreparedStatement.setInt(1, Integer.parseInt(strErrorCode));
			pPreparedStatement.setString(2, strLanguage);
			pPreparedStatement.setString(3, strClavepagina);
			pResultSet = pPreparedStatement.executeQuery();
			while (pResultSet.next())
			{
				strReturn = pResultSet.getString("TXTERR");
			}
		}
		catch (Exception ex)
		{
			pLog.error("Error al realizar la consulta a la BBDD para obtener el mensaje amigable de error de la tabla personalizada por clave página", ex);
		}
		finally
		{
			DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
		}
		/* si no se ha encontrado el mensaje personalizado por clave página se intenta como genérico */
		if (strReturn == null)
		{
			try
			{
				/* se hace una consulta a la tabla especifa de errores por clave página */
				String strQuery = "SELECT * FROM BELTS105 where CODERR = ? and IDIOMAERR = ?";
				strLanguage = pSessionRviaData.getLanguage();
				strClavepagina = pMiqQuests.getEndPoint();
				pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
				pPreparedStatement = pConnection.prepareStatement(strQuery);
				pPreparedStatement.setInt(1, Integer.parseInt(strErrorCode));
				pPreparedStatement.setString(2, strLanguage);
				pResultSet = pPreparedStatement.executeQuery();
				while (pResultSet.next())
				{
					strReturn = pResultSet.getString("TXTERR");
				}
			}
			catch (Exception ex)
			{
				pLog.error("Error al realizar la consulta a la BBDD para obtener el mensaje amigable de error de la tabla general", ex);
			}
			finally
			{
				DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
			}
		}
		return strReturn;
	}
}
