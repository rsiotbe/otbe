package com.rsi.rvia.rest.error.exceptions;

import com.rsi.rvia.rest.error.ErrorManager;

public class ISUMException extends RviaRestException
{
	private static final String MESSAGE_EXCEPTION = "El servicio solicitado no es permitido para este usuario por ISUM.";

	public String getMessage()
	{
		return MESSAGE_EXCEPTION;
	}

	public String getJsonError()
	{
		String strReturn = ErrorManager.getJsonError("403", MESSAGE_EXCEPTION, MESSAGE_EXCEPTION);
		return strReturn;
	}
}
