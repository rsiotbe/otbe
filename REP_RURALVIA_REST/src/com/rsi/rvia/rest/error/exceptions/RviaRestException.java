package com.rsi.rvia.rest.error.exceptions;

public class RviaRestException extends Exception
{
	protected final static String	DEFAULT_MESSAGE_EXCEPTION	= "Error Exception RuralVia Rest.";
	private static final long		serialVersionUID				= 1L;
	protected Exception				pInnerException;
	protected int						nErrorCode;
	protected String					srtMessage;
	protected String					strDescription;

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
	
	public RviaRestException(){
		super();
		pInnerException = null;
	}

	public RviaRestException(int nErrorCode)
	{
		super();
		pInnerException = null;
		this.nErrorCode = nErrorCode;
		srtMessage = DEFAULT_MESSAGE_EXCEPTION;
		strDescription = DEFAULT_MESSAGE_EXCEPTION;
	}

	public RviaRestException(int nErrorCode, Exception ex)
	{
		super();
		this.nErrorCode = nErrorCode;
		pInnerException = ex;
		srtMessage = DEFAULT_MESSAGE_EXCEPTION;
		strDescription = ex.getMessage();
	}

	public RviaRestException(int nErrorCode, String strMessage, Exception ex)
	{
		super();
		this.nErrorCode = nErrorCode;
		pInnerException = ex;
		srtMessage = strMessage;
		strDescription = ex.getMessage();

	}

	public RviaRestException(int nErrorCode, String strMessage)
	{
		super();
		this.nErrorCode = nErrorCode;
		srtMessage = strMessage;
		strDescription = DEFAULT_MESSAGE_EXCEPTION;
	}
	
	public RviaRestException(int nErrorCode, String strMessage, String strDescription)
	{
		super();
		this.nErrorCode = nErrorCode;
		srtMessage = strMessage;
		this.strDescription = strDescription;
	}


}
