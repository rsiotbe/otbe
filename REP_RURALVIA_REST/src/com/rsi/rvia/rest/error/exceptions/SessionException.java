package com.rsi.rvia.rest.error.exceptions;

public class SessionException extends RviaRestException
{

	protected final static String	DEFAULT_MESSAGE_EXCEPTION	= "Ha ocurrido un problema con la Sesion.";
	private static final long		serialVersionUID				= 1L;
	protected Exception				pInnerException;
	protected int						nErrorCode;
	protected String					srtMessage;
	protected String					strDescription;

	public String getDescription()
	{
		return strDescription;
	}
	
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
	
	public SessionException(int nErrorCode)
	{
		super();
		pInnerException = null;
		this.nErrorCode = nErrorCode;
		srtMessage = DEFAULT_MESSAGE_EXCEPTION;
		strDescription = DEFAULT_MESSAGE_EXCEPTION;
	}

	public SessionException(int nErrorCode, Exception ex)
	{
		super();
		this.nErrorCode = nErrorCode;
		pInnerException = ex;
		srtMessage = DEFAULT_MESSAGE_EXCEPTION;
		strDescription = ex.getMessage();
	}

	public SessionException(int nErrorCode, String strMessage, Exception ex)
	{
		super();
		this.nErrorCode = nErrorCode;
		pInnerException = ex;
		srtMessage = strMessage;
		strDescription = ex.getMessage();
	}

	public SessionException(int nErrorCode, String strMessage)
	{
		super();
		this.nErrorCode = nErrorCode;
		srtMessage = strMessage;
		strDescription = DEFAULT_MESSAGE_EXCEPTION;
	}
	
	public SessionException(int nErrorCode, String strMessage, String strDescription)
	{
		super();
		this.nErrorCode = nErrorCode;
		srtMessage = strMessage;
		this.strDescription = strDescription;
	}
}
