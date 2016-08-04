package com.rsi.rvia.rest.operation;

import javax.servlet.http.HttpServletRequest;
import com.rsi.isum.IsumValidation;
import com.rsi.rvia.rest.session.SessionRviaData;

public class OperationManager
{
	public static void proccesFromRvia(HttpServletRequest pRequest) throws Exception
	{
		SessionRviaData pSessionRviaData = new SessionRviaData(pRequest);
		if(!IsumValidation.IsValidService(pSessionRviaData))
			throw new Exception("EL servicio solicitado no es permitido para este usuario por ISUM");
		
		/* comprueba si la operativa es de tipo WS o RVIA-JSON */
		/*if(operativa es de tipo WS) 
				recuerar pametros necesarios
				*/
	}
	
}
