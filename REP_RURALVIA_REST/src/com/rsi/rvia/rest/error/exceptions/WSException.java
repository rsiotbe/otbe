package com.rsi.rvia.rest.error.exceptions;

public class WSException extends RviaRestException
{
	private static final long		serialVersionUID				= 1L;
	
	public WSException(int nErrorCode, String strMessage, Exception ex)
	{
		super(nErrorCode, strMessage, null, ex);
	}

	public WSException(int nErrorCode, String strMessage, String strDescription)
	{
		super(nErrorCode, strMessage, strDescription, null);
	}
	
	public WSException(int nErrorCode, String strMessage, String strDescription, Exception ex)
	{
		super(nErrorCode, strMessage, strDescription, ex);
	}	
}
