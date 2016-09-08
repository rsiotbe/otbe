package com.rsi.isum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;
import com.rsi.rvia.rest.error.exceptions.ISUMException;
import com.rsi.rvia.rest.session.SessionRviaData;

public class IsumValidation
{
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
				break;
			}
			pResultSet.close();
			pPreparedStatement.close();
		}
		catch(Exception ex)
		{
			throw new ISUMException(500, "No ha sido posible validar el servicio contra ISUM", "Error al obtener obtener la información de los servicios de ISUM", ex);
		}finally{
			pResultSet.close();
			pPreparedStatement.close();
			pConnection.close();
		}
		return fReturn;
	}
}
