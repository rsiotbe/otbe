package com.rsi.rvia.rest.error.exceptions;

public class ISUMException extends RviaRestException
{
	private static final long		serialVersionUID				= 1L;

	public ISUMException(int nErrorCode, String strMessage, Exception ex)
	{
		super(nErrorCode, strMessage, null, ex);
	}

	public ISUMException(int nErrorCode, String strMessage, String strDescription)
	{
		super(nErrorCode, strMessage, strDescription, null);
	}
	

	public ISUMException(int nErrorCode, String strMessage, String strDescription, Exception ex)
	{
		super(nErrorCode, strMessage, strDescription, ex);
	}	
}
