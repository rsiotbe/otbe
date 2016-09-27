package com.rsi.rvia.rest.error.exceptions;

public class SessionException extends ApplicationException
{
	private static final long	serialVersionUID	= 1L;

	public SessionException(Integer nHttpErrorCode, Integer nInnerErrorCode, String strMessage, String strDescription,
			Exception ex)
	{
		super(nHttpErrorCode, nInnerErrorCode, strMessage, strDescription, ex);
	}
}
