package com.demshape.dempred.loggingtools;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class StdOutHandler extends Handler {

	public StdOutHandler() {
		super();
	}

	public void publish(LogRecord record) {
		if (!isLoggable(record))
			return;
		System.out.print(getFormatter().format(record));
	}

	public void flush() {
		System.out.flush();
	}

	public void close() {
		System.out.close();
	}
}
