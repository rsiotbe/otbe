package com.rsi.rvia.mail;

public class EmailAttachObject
{
	private String	strName;
	private String	strMimeType;
	private byte[]	abContent;

	public EmailAttachObject(String strName, String strMimeType, byte[] abContent)
	{
		this.strName = strName;
		this.strMimeType = strMimeType;
		this.abContent = abContent;
	}

	public String getName()
	{
		return strName;
	}

	public void setName(String strName)
	{
		this.strName = strName;
	}

	public String getMimeType()
	{
		return strMimeType;
	}

	public void setMimeType(String strMimeType)
	{
		this.strMimeType = strMimeType;
	}

	public byte[] getContent()
	{
		return abContent;
	}

	public void setContent(byte[] abContent)
	{
		this.abContent = abContent;
	}
}
