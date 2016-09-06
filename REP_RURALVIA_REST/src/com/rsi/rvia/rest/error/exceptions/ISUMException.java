package com.rsi.rvia.rest.error.exceptions;


public class ISUMException extends RviaRestException
{
	private static final long	serialVersionUID	= 1L;
	
	public ISUMException ()
	{
		super();
		MESSAGE_EXCEPTION = "El servicio solicitado no es permitido para este usuario por ISUM.";
	}
	
	public ISUMException (Exception ex)
	{
		super();
		MESSAGE_EXCEPTION = "El servicio solicitado no es permitido para este usuario por ISUM.";
		pInnerException = ex;
	}
}
