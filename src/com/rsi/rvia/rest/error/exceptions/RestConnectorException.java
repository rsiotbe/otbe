package com.rsi.rvia.rest.error.exceptions;

public class RestConnectorException extends ApplicationException
{
	private static final long	serialVersionUID	= 1L;

	public RestConnectorException(Integer nHttpErrorCode, Integer nInnerErrorCode, String strMessage, String strDescription,
			Exception ex)
	{
		super(nHttpErrorCode, nInnerErrorCode, strMessage, strDescription, ex);
	}
}
