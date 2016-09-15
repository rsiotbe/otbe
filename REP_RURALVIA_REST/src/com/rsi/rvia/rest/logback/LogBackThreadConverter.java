package com.rsi.rvia.rest.logback;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class LogBackThreadConverter extends ClassicConverter {

	@Override
	public String convert(ILoggingEvent arg0)
	{
		String strIdThread = String.valueOf(Thread.currentThread().getId());
		
		return strIdThread;
	}
}