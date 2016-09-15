package com.rsi.isum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;
import com.rsi.rvia.rest.error.exceptions.ISUMException;
import com.rsi.rvia.rest.session.SessionRviaData;

public class IsumValidation
{
	private static Logger	pLog	= LoggerFactory.getLogger(IsumValidation.class);

	/** Comprueba si el servicio solicitado por el usuario es accesible para el perfil del usuario
	 * 
	 * @param pSessionRviaData
	 *           Datos de sesión de la apliación de ruralvia
	 * @return Booleano indicando si está disponible el servicio
	 * @throws Exception */
	public static boolean IsValidService(SessionRviaData pSessionRviaData) throws Exception
	{
		boolean fReturn = false;
		Connection pConnection = null;
		PreparedStatement pPreparedStatement = null;
		ResultSet pResultSet = null;
		try
		{
			String strQuery = "select sp.* " + "from ISUM.SERVICES s, ISUM.SERVICES_PROFILES sp, ISUM.PROFILES p "
					+ "where s.ser_id = sp.ser_id and p.prf_id = sp.prf_id "
					+ "and p.prf_code = ? and s.ser_code = ? and sp.SPR_PRF_STATUS='A'";
			pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
			pPreparedStatement = pConnection.prepareStatement(strQuery);
			String strUserIsumProfile = pSessionRviaData.getIsumUserProfile();
			String strServiceIsumId = pSessionRviaData.getIsumServiceId();
			pPreparedStatement.setString(1, strUserIsumProfile);
			pPreparedStatement.setString(2, strServiceIsumId);
			pResultSet = pPreparedStatement.executeQuery();
			while (pResultSet.next())
			{
				fReturn = true;
				pLog.info("El servicio está permitido para este usuario");
				break;
			}
			pResultSet.close();
			pPreparedStatement.close();
		}
		catch (Exception ex)
		{
			pLog.error("El servicio NO está permitido para este usuario");
			throw new ISUMException(500, null, "No ha sido posible validar el servicio contra ISUM", "Error al obtener obtener la información de los servicios de ISUM", ex);
		}
		finally
		{
			try
			{
				if (pResultSet != null)
					pResultSet.close();
				if (pPreparedStatement != null)
					pPreparedStatement.close();
				if (pConnection != null)
					pConnection.close();
			}
			catch (Exception ex)
			{
				pLog.error("Error al cerrar los objetos de base de datos", ex);
			}
		}
		return fReturn;
	}
}
