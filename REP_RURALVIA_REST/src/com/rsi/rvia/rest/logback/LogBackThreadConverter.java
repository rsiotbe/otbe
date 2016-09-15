package com.rsi.rvia.rest.logback;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class LogBackThreadConverter extends ClassicConverter {

	@Override
	public String convert(ILoggingEvent pEvent)
	{
		String strThreadName = pEvent.getThreadName();
		String strIdThread = strThreadName.substring(strThreadName.lastIndexOf('-'))
													 .replace("]","")
													 .replace("-", "");
		return strIdThread;
	}
}