package com.rsi.isum;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.rsi.rvia.rest.DDBB.DDBBConnection;
import com.rsi.rvia.rest.DDBB.DDBBFactory;
import com.rsi.rvia.rest.DDBB.DDBBFactory.DDBBProvider;
import com.rsi.rvia.rest.session.SessionRviaData;

public class IsumValidation
{
	public static boolean IsValidService(SessionRviaData pSessionRviaData) throws Exception
	{
		String strQuery = "select sp.* " + "from ISUM.SERVICES s, ISUM.SERVICES_PROFILES sp, ISUM.PROFILES p "
				+ "where s.ser_id = sp.ser_id and p.prf_id = sp.prf_id "
				+ "and p.prf_code = ? and s.ser_code = ? and sp.SPR_PRF_STATUS='A'";
		boolean fReturn = false;
		String strUserIsumProfile = pSessionRviaData.getIsumUserProfile();
		String strServiceIsumId = pSessionRviaData.getIsumServiceId();
		DDBBConnection pDDBBIsum = DDBBFactory.getDDBB(DDBBProvider.Oracle);
		PreparedStatement pPS = pDDBBIsum.prepareStatement(strQuery);
		pPS.setString(1, strUserIsumProfile);
		pPS.setString(2, strServiceIsumId);
		ResultSet pResult = pPS.executeQuery();
		while (pResult.next())
		{
			fReturn = true;
			break;
		}
		pResult.close();
		pPS.close();
		return fReturn;
	}
}
