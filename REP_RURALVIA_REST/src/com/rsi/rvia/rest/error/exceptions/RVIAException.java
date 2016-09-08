package com.rsi.rvia.rest.error.exceptions;


public class RVIAException extends RviaRestException
{
	private static final long		serialVersionUID				= 1L;

	public RVIAException(int nErrorCode, String strMessage, Exception ex)
	{
		super(nErrorCode, strMessage, null, ex);
	}

	public RVIAException(int nErrorCode, String strMessage, String strDescription)
	{
		super(nErrorCode, strMessage, strDescription, null);
	}
	

	public RVIAException(int nErrorCode, String strMessage, String strDescription, Exception ex)
	{
		super(nErrorCode, strMessage, strDescription, ex);
	}	
}
