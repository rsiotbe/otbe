package com.rsi.rvia.rest.error.exceptions;


public class RVIAException extends RviaRestException
{
	private static final long	serialVersionUID	= 1L;
	
	public RVIAException()
	{
		super();
		MESSAGE_EXCEPTION = "La petición a RuralVia no ha funcionado correctamente.";
	}

	public RVIAException(Exception ex)
	{
		super();
		MESSAGE_EXCEPTION = "La petición a RuralVia no ha funcionado correctamente.";
		pInnerException = ex;
	}

}
