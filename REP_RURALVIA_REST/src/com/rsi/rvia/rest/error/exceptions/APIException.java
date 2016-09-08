package com.rsi.rvia.rest.error.exceptions;

public class APIException extends RviaRestException
{
	private static final long		serialVersionUID				= 1L;
	
	public APIException(int nErrorCode, String strMessage, Exception ex)
	{
		super(nErrorCode, strMessage, null, ex);
	}

	public APIException(int nErrorCode, String strMessage, String strDescription)
	{
		super(nErrorCode, strMessage, strDescription, null);
	}
	
	public APIException(int nErrorCode, String strMessage, String strDescription, Exception ex)
	{
		super(nErrorCode, strMessage, strDescription, ex);
	}	
}
