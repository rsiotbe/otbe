package com.rsi.rvia.rest.error.exceptions;

import com.rsi.rvia.rest.error.ErrorManager;

public class RVIAException extends RviaRestException
{
	private static final String MESSAGE_EXCEPTION = "La petición a RuralVia no ha funcionado correctamente.";

	public String getMessage()
	{
		return MESSAGE_EXCEPTION;
	}

	public String getJsonError()
	{
		String strReturn = ErrorManager.getJsonError("406", MESSAGE_EXCEPTION, MESSAGE_EXCEPTION);
		return strReturn;
	}
}
