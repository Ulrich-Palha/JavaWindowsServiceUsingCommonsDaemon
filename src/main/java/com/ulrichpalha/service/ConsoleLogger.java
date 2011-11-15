package com.ulrichpalha.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The Class ConsoleLogger.
 * Simply logs to the console.
 */
public class ConsoleLogger implements Logger {

	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
	
	/* (non-Javadoc)
	 * @see com.ulrichpalha.service.Logger#log(java.lang.String)
	 * 
	 */
	@Override
	public void log(String message) {
		System.out.println(df.format(new Date()) + message);
	}

}
