package org.uwindsor.mac.acc.drivedepot.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class to support date and time manipulations
 * @author 110120950 (Anuj Puri)
 *
 */
public final class DateUtils {
	private static final String TIMESTAMP_FORMAT = "yyyyMMddHHmmss";	
	
	private static SimpleDateFormat timeStampFormatter = new SimpleDateFormat(TIMESTAMP_FORMAT);

	public static final String DATE_FORMAT_yyyyMMdd = "yyyyMMdd";
	public static final String DATE_FORMAT_ddMyyyy = "dd-M-yyyy";
	public static final String DATE_FORMAT_ddMyyyyhhmmss = "dd-M-yyyy hh:mm:ss";
	
	/**
	 * Time 00:01 AM
	 */
	public static final String TIME_MIDNIGHT_hhmmss = " 00:01:00";
	
	/**
	 * Stricly no no, why you need when all the methods are declared as static
	 */
	private DateUtils() {
		
	}
	
	/**
	 * Appends the current timestamp in _yyyymmddhhmmss format e.g _20150722022550 and return the string
	 * @param str
	 * @return
	 */
	public static String appendCurrentTimestamp(String str) {

		return StringUtils.isNullOrEmpty(str) ? (timeStampFormatter.format(new Date())) : (str+"_"+timeStampFormatter.format(new Date()));		
	}


	/**
	 * Returns the current timestamp in yyyymmddhhmmss format e.g 20150722022550
	 * @return
	 */
	public static String getCurrentTimestamp() {
		return appendCurrentTimestamp("");
	}


	/**
	 * Generic utility method for formatting the provided date with the provided pattern
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String format(Date date, String pattern) {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		return formatter.format(date);
	}
	
	
	/**
	 * Generic utility method for formatting the curent date with the provided pattern
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String format(String pattern) {		
		return format(new Date(), pattern);
	}

	
	
	/**
	 * Uitlity method to parse the give date String as per the specified pattern
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static Date parseDate(String date, String pattern) {
		Date d = null;
		try {
			SimpleDateFormat sFormat = new SimpleDateFormat(pattern);
			d = sFormat.parse(date);
		} catch(ParseException ex) {
			ex.printStackTrace();
			return null;
		}
		return d;
	}

	/**
	 * Get current time in millis as {@link String}
	 * @return
	 */
	public static String getCurrentTimeInMillis() {
		return String.valueOf(System.currentTimeMillis());
	}
	
	
}
