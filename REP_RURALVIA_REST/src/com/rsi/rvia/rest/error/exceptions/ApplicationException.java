package com.rsi.rvia.rest.error.exceptions;

import com.rsi.rvia.rest.tool.Utils;

public class ApplicationException extends Exception
{
	private static final long	serialVersionUID	= 1L;
	protected Exception			pInnerException;
	protected Integer				nHttpErrorCode;
	protected Integer				nInnerErrorCode;
	protected String				srtMessage;
	protected String				strDescription;

	public Exception getInnerException()
	{
		return pInnerException;
	}

	public Integer getHttpErrorCode()
	{
		return nHttpErrorCode;
	}

	public Integer getInnerErrorCode()
	{
		return nInnerErrorCode;
	}
	
	public String getMessage()
	{
		return srtMessage;
	}

	public String getDescription()
	{
		return strDescription;
	}

	/**
	 * Constructor de la clase
	 * @param nHttpErrorCode Codigo de error http
	 * @param nInnerErrorCode Codigo de error interno
	 * @param strMessage Mensaje principal del error
	 * @param strDescription Descripción del error
	 * @param ex Excepción interna que genera el error
	 */
	public ApplicationException(Integer nHttpErrorCode, Integer nInnerErrorCode, String strMessage, String strDescription, Exception ex)
	{
		super();
		this.nHttpErrorCode = nHttpErrorCode;
		this.nInnerErrorCode = nInnerErrorCode;
		this.pInnerException = ex;
		this.srtMessage = strMessage;
		this.strDescription = strDescription;
	}	
	
	/* (non-Javadoc)
	 * @see java.lang.Throwable#toString()
	 */
	public String toString()
	{
		StringBuilder pSb = new StringBuilder();
		if(srtMessage != null)
			pSb.append(this.srtMessage).append(" - ");
		if(nHttpErrorCode != null)
			pSb.append("(http: " + nHttpErrorCode + ")").append(" - ");
		if(nInnerErrorCode != null)
			pSb.append("Error interno: " + nInnerErrorCode).append(" - ");
		if(strDescription != null)
			pSb.append("Detalle: " + strDescription).append(" - ");
		if(pInnerException != null)
			pSb.append("Inner Exception: " + Utils.getExceptionStackTrace(pInnerException)).append(" - ");;
		pSb.append("StackTrace: " + Utils.getExceptionStackTrace(this));
		return pSb.toString();
	}	
}

