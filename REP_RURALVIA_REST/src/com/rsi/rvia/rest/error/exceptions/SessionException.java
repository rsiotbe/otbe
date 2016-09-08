package com.rsi.rvia.rest.error.exceptions;

public class SessionException extends RviaRestException
{
	private static final long		serialVersionUID				= 1L;
	
	public SessionException(int nErrorCode, String strMessage, Exception ex)
	{
		super(nErrorCode, strMessage, ex);
	}

	public SessionException(int nErrorCode, String strMessage, String strDescription)
	{
		super(nErrorCode, strMessage, strDescription);
	}
	
	public SessionException(int nErrorCode, String strMessage, String strDescription, Exception ex)
	{
		super(nErrorCode, strMessage, strDescription, ex);
	}	
}
