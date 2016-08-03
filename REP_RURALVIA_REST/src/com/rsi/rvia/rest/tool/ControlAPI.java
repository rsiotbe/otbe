package com.rsi.rvia.rest.tool;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ControlAPI
{
	private String token;

	public String getToken()
	{
		return token;
	}

	public void setToken(String token)
	{
		this.token = token;
	}
	
}
