package cc.catalysts.asdoclet.generator.velocity;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.catalysts.asdoclet.AsDoclet;

public class SLF4JLogChute implements LogChute {
	private final Logger logger = LoggerFactory.getLogger(AsDoclet.class);

	// --------------------- Interface LogChute ---------------------

	public void init(RuntimeServices rs) throws Exception {

	}

	public void log(int level, String message) {
		switch (level) {
		case DEBUG_ID:
			logger.debug(message);
			break;
		case INFO_ID:
			logger.info(message);
			break;
		case WARN_ID:
			logger.warn(message);
			break;
		case ERROR_ID:
			logger.error(message);
			break;
		}
	}

	public void log(int level, String message, Throwable throwable) {
		switch (level) {
		case DEBUG_ID:
			logger.debug(message, throwable);
			break;
		case INFO_ID:
			logger.info(message, throwable);
			break;
		case WARN_ID:
			logger.warn(message, throwable);
			break;
		case ERROR_ID:
			logger.error(message, throwable);
			break;
		}
	}

	public boolean isLevelEnabled(int level) {
		boolean result = false;
		switch (level) {
		case DEBUG_ID:
			result = logger.isDebugEnabled();
			break;
		case INFO_ID:
			result = logger.isInfoEnabled();
			break;
		case WARN_ID:
			result = logger.isWarnEnabled();
			break;
		case ERROR_ID:
			result = logger.isErrorEnabled();
			break;
		}
		return result;
	}
}
