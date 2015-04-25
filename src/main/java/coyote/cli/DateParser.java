package coyote.cli;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This is a convenience class which parses a string into a date using a 
 * variety of common date formats.
 * 
 * <p>Particularly useful in command line argument processing, this class 
 * supports several shorthand 'tokens' which are interpreted into different 
 * dates. For example, 'now' means the present time and 'noon' means today at 
 * noon.</p>
 */
public class DateParser {

	private static final DateFormat _DATE_TIME_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private static final DateFormat _DATE_TIME_FORMAT_US = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	private static final DateFormat _DATE_TIME_MILLIS_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss,SSS");
	private static final DateFormat _DATE_TIME_FORMAT_MILLIS_US = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
	private static final DateFormat _DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
	private static final DateFormat _DATE_FORMAT_US = new SimpleDateFormat("MM/dd/yyyy");
	private static final DateFormat _TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
	private static final DateFormat _TIME_FORMAT_MILLIS = new SimpleDateFormat("HH:mm:ss.SSS");
	private static final DateFormat _TIME_FORMAT_MILLIS_EU = new SimpleDateFormat("HH:mm:ss,SSS");




	/**
	 * Parse the given string into a Date object.
	 *  
	 * <p>This allows for the uniform specification and handling of date/time 
	 * strings in all actions.</p>
	 * 
	 * <p>Supports the following tokens:<ul>
	 * <li>now - the current instant in time.</li>
	 * <li>noon - Today at 12:00:00.000</li>
	 * <li>today or startoftoday - the time representing today at 00:00:00.000</li></ul>
	 * <li>yesterday - the time representing yesterday at 00:00:00.000</li></ul>
	 * <li>5,10...60ago - increments of 5 minutes in the past</li></ul>
	 * <li>5,10...60fromnow - increments of 5 minutes in the future</li></ul>
	 * </p> 
	 * 
	 * @param token The string to parse
	 * 
	 * @return The date represented by the string, or null of there were problems
	 *         parsing the token
	 */
	public static Date parse(final String token) {
		Date retval = null;

		// the current point in time
		if ("now".equalsIgnoreCase(token)) {
			return new Date();
		}

		// the start of today
		if ("startoftoday".equalsIgnoreCase(token) || "today".equalsIgnoreCase(token)) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			return cal.getTime();
		} else

		// the end of today
		if ("endtoftoday".equalsIgnoreCase(token) || "tomorrow".equalsIgnoreCase(token) || "startoftomorrow".equalsIgnoreCase(token)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			return cal.getTime();
		} else

		// TODO: parse 'ago' from the token and use the prefix as minutes

		// one hour ago
		if ("hourago".equalsIgnoreCase(token) || "60ago".equalsIgnoreCase(token)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR_OF_DAY, -1);
			return cal.getTime();
		} else

		// 55 minutes ago
		if ("55ago".equalsIgnoreCase(token)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, -55);
			return cal.getTime();
		} else

		// 50 minutes ago
		if ("50ago".equalsIgnoreCase(token)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, -50);
			return cal.getTime();
		} else

		// 45 minutes ago
		if ("45ago".equalsIgnoreCase(token)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, -45);
			return cal.getTime();
		} else

		// 40 minutes ago
		if ("40ago".equalsIgnoreCase(token)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, -40);
			return cal.getTime();
		} else

		// 35 minutes ago
		if ("35ago".equalsIgnoreCase(token)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, -35);
			return cal.getTime();
		} else

		// 30 minutes ago
		if ("30ago".equalsIgnoreCase(token)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, -30);
			return cal.getTime();
		} else

		// 25 minutes ago
		if ("25ago".equalsIgnoreCase(token)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, -25);
			return cal.getTime();
		} else

		// 20 minutes ago
		if ("20ago".equalsIgnoreCase(token)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, -20);
			return cal.getTime();
		} else

		// 15 minutes ago
		if ("15ago".equalsIgnoreCase(token)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, -15);
			return cal.getTime();
		} else

		// 10 minutes ago
		if ("10ago".equalsIgnoreCase(token)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, -10);
			return cal.getTime();
		} else

		// 5 minutes ago
		if ("5ago".equalsIgnoreCase(token)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, -5);
			return cal.getTime();
		} else

		// TODO: parse 'fromnow' from the token and use the prefix as minutes

		// one hour from now
		if ("hourfromnow".equalsIgnoreCase(token) || "60fromnow".equalsIgnoreCase(token)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR_OF_DAY, +1);
			return cal.getTime();
		} else

		// 55 minutes from now
		if ("55fromnow".equalsIgnoreCase(token)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 55);
			return cal.getTime();
		} else

		// 50 minutes from now
		if ("50fromnow".equalsIgnoreCase(token)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 50);
			return cal.getTime();
		} else

		// 45 minutes from now
		if ("45fromnow".equalsIgnoreCase(token)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 45);
			return cal.getTime();
		} else

		// 40 minutes from now
		if ("40fromnow".equalsIgnoreCase(token)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 40);
			return cal.getTime();
		} else

		// 35 minutes from now
		if ("35fromnow".equalsIgnoreCase(token)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 35);
			return cal.getTime();
		} else

		// 30 minutes from now
		if ("30fromnow".equalsIgnoreCase(token)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 30);
			return cal.getTime();
		} else

		// 25 minutes from now
		if ("25fromnow".equalsIgnoreCase(token)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 25);
			return cal.getTime();
		} else

		// 20 minutes from now
		if ("20fromnow".equalsIgnoreCase(token)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 20);
			return cal.getTime();
		} else

		// 15 minutes from now
		if ("15fromnow".equalsIgnoreCase(token)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 15);
			return cal.getTime();
		} else

		// 10 minutes from now
		if ("10fromnow".equalsIgnoreCase(token)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 10);
			return cal.getTime();
		} else

		// 5 minutes from now
		if ("5fromnow".equalsIgnoreCase(token)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 5);
			return cal.getTime();
		} else

		// Today at noon
		if ("noon".equalsIgnoreCase(token)) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 12);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			return cal.getTime();
		} else

		// the start of yesterday morning
		if ("yesterday".equalsIgnoreCase(token) || "1dayago".equalsIgnoreCase(token)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -1);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			return cal.getTime();
		} else

		if ("2daysago".equalsIgnoreCase(token)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -2);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			return cal.getTime();
		} else

		if ("3daysago".equalsIgnoreCase(token)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -3);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			return cal.getTime();
		} else

		if ("4daysago".equalsIgnoreCase(token)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -4);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			return cal.getTime();
		} else

		// yesterday at noon
		if ("yesterdaynoon".equalsIgnoreCase(token)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -1);
			cal.set(Calendar.HOUR_OF_DAY, 12);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			return cal.getTime();
		}

		// No tokens were found, try to parse the string provided into a date
		else {

			// try to determine if it is a date, a time, or both
			boolean timeformat = token.indexOf(':') > -1;
			boolean dateformat = token.indexOf('/') > -1;

			if (timeformat && !dateformat) {
				// Looks like only a time format

				// Check for milliseconds
				if (token.indexOf('.') > -1) {
					try {
						retval = _TIME_FORMAT_MILLIS.parse(token);
					} catch (final Exception e) {
						retval = null;
					}
				} else if (token.indexOf(',') > -1) {
					try {
						retval = _TIME_FORMAT_MILLIS_EU.parse(token);
					} catch (final Exception e) {
						retval = null;
					}
				} else {
					// Normal time format
					try {
						retval = _TIME_FORMAT.parse(token);
					} catch (final Exception e) {
						retval = null;
					}
				}
			} else if (dateformat && !timeformat) {
				// Looks like only a date format
				try {
					// Try the normal, sortable format
					retval = _DATE_FORMAT.parse(token);
				} catch (final Exception e) {
					try {
						// try the US format
						retval = _DATE_FORMAT_US.parse(token);
					} catch (final Exception ex) {
						retval = null;
					}
				}

			} else {
				// It looks like both, try common date-time formats
				if (token.indexOf(',') > -1) {
					// looks like milliseconds are specified (EU)
					try {
						// try the US format
						retval = _DATE_TIME_MILLIS_FORMAT.parse(token);
					} catch (final Exception ex) {
						retval = null;
					}
				} else if (token.indexOf('.') > -1) {
					// looks like milliseconds are specified (US)
					try {
						// try the US format
						retval = _DATE_TIME_FORMAT_MILLIS_US.parse(token);
					} catch (final Exception ex) {
						retval = null;
					}
				} else {
					// Looks like just a date-time without milliseconds
					try {
						// Try the normal, sortable format
						retval = _DATE_TIME_FORMAT.parse(token);
					} catch (final Exception e) {
						try {
							// try the US format
							retval = _DATE_TIME_FORMAT_US.parse(token);
						} catch (final Exception ex) {
							retval = null;
						}
					}
				}
			}
		}
		return retval;
	}

}
