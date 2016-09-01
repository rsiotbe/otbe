package com.rsi.rvia.rest.error.exceptions;

import org.json.JSONObject;
import com.rsi.rvia.rest.error.ErrorManager;
import com.rsi.rvia.rest.tool.Utils;

public class SessionException extends RviaRestException
{
	private static final String MESSAGE_EXCEPTION = "Ha ocurrido un problema con la Sesion.";
	
	public String getMessage(){
		return MESSAGE_EXCEPTION;
	}
	public String getJsonError(){
		String strReturn = ErrorManager.getJsonError("500",MESSAGE_EXCEPTION, MESSAGE_EXCEPTION);

		return strReturn;
	}

}
