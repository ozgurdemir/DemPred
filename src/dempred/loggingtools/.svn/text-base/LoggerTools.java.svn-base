package dempred.loggingtools;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerTools {
	 public static Level getLevel(Logger logger) {
	        Level level = logger.getLevel();
	        while (level == null && logger.getParent() != null) {
	            logger = logger.getParent();
	            level = logger.getLevel();
	        }
	        return level;
	     }
}
