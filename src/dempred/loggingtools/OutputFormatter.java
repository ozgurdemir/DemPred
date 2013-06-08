package dempred.loggingtools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class OutputFormatter extends Formatter {

	private long lastLogging;
	private long startTime;
	private SimpleDateFormat dateFormat;
	
	public OutputFormatter() {
		super();
		startTime = System.currentTimeMillis();
		lastLogging = System.currentTimeMillis();;
		dateFormat = new SimpleDateFormat( "dd.MM.yy_HH:mm:ss" ); 
	}

	public String format(LogRecord record) {
		String lineseperator = System.getProperty("line.separator");
		StringBuffer sb = new StringBuffer();
		if(record.getLevel().intValue()>=Level.CONFIG.intValue()){
			long currentTime = record.getMillis();
			long pastTime = currentTime - lastLogging;
			long totalTime = currentTime - startTime;
			lastLogging = currentTime; 
			Date date = new Date(record.getMillis());
			sb.append("--> ");
			sb.append(dateFormat.format(date) );
			sb.append(" sinceLastLog: ");
			sb.append(msec2String(pastTime));
			sb.append(" total: ");
			sb.append(msec2String(totalTime));
			sb.append(lineseperator);
			sb.append(formatMessage(record));
			sb.append(lineseperator);
		}
		else{
			sb.append(formatMessage(record));
			sb.append(lineseperator);
		}
		return sb.toString();
	}

	private String msec2String(long msec) {
		long seconds = msec / 1000;
		int sec = (int) seconds % 60;
		int min = (int) (seconds / 60) % 60;
		int hour = (int) (seconds / 60 / 60) % 24;
		return String.format("%02d:%02d:%02d", hour, min, sec);
	}
	
}
