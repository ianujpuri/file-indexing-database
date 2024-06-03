package org.uwindsor.mac.acc.drivedepot.logger;

import org.apache.log4j.FileAppender;
import org.uwindsor.mac.acc.drivedepot.util.DateUtils;

/**
 * Primarily for logging purpose.
 * @author Anuj Puri (110120950)
 *
 */
public class LoggingFileAppender extends FileAppender {

	@Override
	public void setFile(String file)
	{
		String loggingFile = file;
		if (loggingFile.indexOf("%timestamp") > 0) {
			loggingFile = file.replaceAll("%timestamp", DateUtils.getCurrentTimestamp());
		}

		super.setFile(loggingFile);
	}
}
