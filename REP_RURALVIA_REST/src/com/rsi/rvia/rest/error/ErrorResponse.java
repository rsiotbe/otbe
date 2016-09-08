package com.rsi.rvia.rest.error;

public class ErrorResponse
{
	private int nHttpCode;
	private int nErrorCode;
	private String strMessage;
	private String strDescriptción;
	
	public int getHttpCode()
	{
		return nHttpCode;
	}
	public void setHttpCode(int nHttpCode)
	{
		this.nHttpCode = nHttpCode;
	}
	public int getErrorCode()
	{
		return nErrorCode;
	}
	public void setErrorCode(int nErrorCode)
	{
		this.nErrorCode = nErrorCode;
	}	
	public String getMessage()
	{
		return strMessage;
	}
	public void setMessage(String strMessage)
	{
		this.strMessage = strMessage;
	}
	public String getDescriptción()
	{
		return strDescriptción;
	}
	public void setDescriptción(String strDescriptción)
	{
		this.strDescriptción = strDescriptción;
	}
}
