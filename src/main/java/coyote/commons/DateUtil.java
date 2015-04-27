/*
 * Copyright (c) 2004 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and initial implementation
 */
package coyote.commons;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 
 */
public class DateUtil {

  private static DecimalFormat MILLIS = new DecimalFormat( "000" );

  /** used to format dates into strings */
  private static final DateFormat _DATETIME_FORMAT = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );
  private static final DateFormat _DATE_FORMAT = new SimpleDateFormat( "yyyy/MM/dd" );
  private static final DateFormat _TIME_FORMAT = new SimpleDateFormat( "HH:mm:ss" );




  /**
   * Get a formatted string representing the difference between the two times.
   * 
   * <p>The output is in the format of X wks X days X hrs X min X.XXX sec.</p>
   * 
   * @param millis number of elapsed milliseconds.
   * 
   * @return formatted string representing weeks, days, hours minutes and seconds .
   */
  public static String formatElapsed( long millis ) {
    if ( millis < 0 || millis == Long.MAX_VALUE ) {
      return "?";
    }

    long secondsInMilli = 1000;
    long minutesInMilli = secondsInMilli * 60;
    long hoursInMilli = minutesInMilli * 60;
    long daysInMilli = hoursInMilli * 24;
    long weeksInMilli = daysInMilli * 7;
    double monthsInMilli = weeksInMilli * 4.33333333;
    long yearsInMilli = weeksInMilli * 52;

    long elapsedyears = millis / yearsInMilli;
    millis = millis % yearsInMilli;

    long elapsedmonths = millis / (long)monthsInMilli;
    millis = millis % (long)monthsInMilli;

    long elapsedWeeks = millis / weeksInMilli;
    millis = millis % weeksInMilli;

    long elapsedDays = millis / daysInMilli;
    millis = millis % daysInMilli;

    long elapsedHours = millis / hoursInMilli;
    millis = millis % hoursInMilli;

    long elapsedMinutes = millis / minutesInMilli;
    millis = millis % minutesInMilli;

    long elapsedSeconds = millis / secondsInMilli;
    millis = millis % secondsInMilli;

    StringBuilder b = new StringBuilder();

    if ( elapsedyears > 0 ) {
      b.append( elapsedyears );
      if ( elapsedyears > 1 )
        b.append( " yrs " );
      else
        b.append( " yr " );
    }
    if ( elapsedmonths > 0 ) {
      b.append( elapsedmonths );
      if ( elapsedmonths > 1 )
        b.append( " mths " );
      else
        b.append( " mth " );
    }
    if ( elapsedWeeks > 0 ) {
      b.append( elapsedWeeks );
      if ( elapsedWeeks > 1 )
        b.append( " wks " );
      else
        b.append( " wk " );
    }
    if ( elapsedDays > 0 ) {
      b.append( elapsedDays );
      if ( elapsedDays > 1 )
        b.append( " days " );
      else
        b.append( " day " );

    }
    if ( elapsedHours > 0 ) {
      b.append( elapsedHours );
      if ( elapsedHours > 1 )
        b.append( " hrs " );
      else
        b.append( " hr " );
    }
    if ( elapsedMinutes > 0 ) {
      b.append( elapsedMinutes );
      b.append( " min " );
    }
    if ( elapsedSeconds > 0 ) {

      b.append( elapsedSeconds );
      if ( millis > 0 ) {
        b.append( "." );
        b.append( MILLIS.format( millis ) );
      }
      b.append( " sec" );
    }

    return b.toString();
  }




  /**
   * Format the string using the default formatting for all actions.
   * 
   * @param date The date to format.
   * 
   * @return The formatted date string.
   */
  public static String formatDateTime( Date date ) {
    if ( date == null )
      return "null";
    else
      return _DATETIME_FORMAT.format( date );
  }




  /**
   * Format the date only returning the date portion of the date  (i.e. no time representation).
   * 
   * @param date the date/time to format
   * 
   * @return only the date portion formatted
   */
  public static String formatDate( Date date ) {
    if ( date == null )
      return "null";
    else
      return _DATE_FORMAT.format( date );
  }




  /**
   * Format the date returning only the time portion of the date (i.e. no month, day or year).
   * 
   * @param date the date/time to format
   * 
   * @return only the time portion formatted
   */
  public static String formatTime( Date date ) {
    if ( date == null )
      return "null";
    else
      return _TIME_FORMAT.format( date );
  }

}
