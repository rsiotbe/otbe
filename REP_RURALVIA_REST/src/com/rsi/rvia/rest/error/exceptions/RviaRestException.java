package com.rsi.rvia.rest.error.exceptions;

public class RviaRestException extends Exception
{
	protected final static String	DEFAULT_MESSAGE_EXCEPTION	= "Error Exception RuralVia Rest.";
	private static final long		serialVersionUID				= 1L;
	protected Exception				pInnerException;
	protected int						nErrorCode;
	protected String					srtMessage;
	

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
	
	
	public RviaRestException(int nErrorCode)
	{
		super();
		pInnerException = null;
	}

	public RviaRestException(int nErrorCode, Exception ex)
	{
		super();
		this.nErrorCode = nErrorCode;
		pInnerException = ex;
		srtMessage = DEFAULT_MESSAGE_EXCEPTION;
	}

	public RviaRestException(int nErrorCode, String strMessage, Exception ex)
	{
		super();
		this.nErrorCode = nErrorCode;
		pInnerException = ex;
		srtMessage = strMessage;

	}

	public RviaRestException(int nErrorCode, String strMessage)
	{
		super();
		this.nErrorCode = nErrorCode;
		srtMessage = strMessage;
	}


}
