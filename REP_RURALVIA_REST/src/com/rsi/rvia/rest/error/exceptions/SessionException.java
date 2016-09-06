package com.rsi.rvia.rest.error.exceptions;

public class SessionException extends RviaRestException
{
	private static final long	serialVersionUID	= 1L;
		
	public SessionException()
	{
		super();
		MESSAGE_EXCEPTION = "Ha ocurrido un problema con la Sesion.";
	}

	public SessionException(Exception ex)
	{
		super();
		MESSAGE_EXCEPTION = "Ha ocurrido un problema con la Sesion.";
		pInnerException = ex;
	}

}
