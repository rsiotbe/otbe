package com.rsi.rvia.rest.error.exceptions;

public class RviaRestException extends Exception
{
	private static final String MESSAGE_EXCEPTION = "Error Exception RuralVia Rest.";
	
	public String getMessage(){
		return MESSAGE_EXCEPTION;
	}
	
	public String getJsonError(){
		return "{}";
	}
	
}
