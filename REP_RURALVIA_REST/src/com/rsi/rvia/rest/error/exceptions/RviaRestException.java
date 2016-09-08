package com.rsi.rvia.rest.error.exceptions;

public class RviaRestException extends Exception
{
	private static final long	serialVersionUID	= 1L;
	protected Exception			pInnerException;
	protected int					nErrorCode;
	protected String				srtMessage;
	protected String				strDescription;

	public Exception getInnerException()
	{
		return pInnerException;
	}

	public int getErrorCode()
	{
		return nErrorCode;
	}

	public String getMessage()
	{
		return srtMessage;
	}

	public String getDescription()
	{
		return strDescription;
	}

	public RviaRestException(int nErrorCode, String strMessage, Exception ex)
	{
		super();
		this.nErrorCode = nErrorCode;
		pInnerException = ex;
		srtMessage = strMessage;
		strDescription = ex.getMessage();
	}

	public RviaRestException(int nErrorCode, String strMessage, String strDescription)
	{
		super();
		this.nErrorCode = nErrorCode;
		srtMessage = strMessage;
		this.strDescription = strDescription;
	}
	

	public RviaRestException(int nErrorCode, String strMessage, String strDescription, Exception ex)
	{
		super();
		this.nErrorCode = nErrorCode;
		pInnerException = ex;
		srtMessage = strMessage;
		this.strDescription = strDescription;
	}	
}
