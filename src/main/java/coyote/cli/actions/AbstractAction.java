/*
 * Copyright (c) 2014 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial API and implementation
 */
package coyote.cli.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import coyote.cli.CLI;
import coyote.cli.template.SymbolTable;
import coyote.cli.template.Template;
import coyote.commons.CipherUtil;


public abstract class AbstractAction implements Action {
  private static final String OPT_FMT = "fmt";
  private static final String FMT_TXT = "txt";
  private static final String FMT_CSV = "csv";
  private static final String FMT_TAB = "tab";

  /** The database access object for the common shared database */
  //private static CsdbDAO _commonSharedDAO = null;

  /** Configuration properties loaded into the system */
  private static Configuration _config = null;

  /** The database environment to use */
  private static String _env = "DEV";

  /** Flag indicating all output should be suppressed */
  private static boolean QUIET = false;
  /** Flag indicating all messages should be sent to the console */
  private static boolean VERBOSE = false;
  /** Flag indicating debugging messages should be sent to the console */
  private static boolean DEBUG = false;

  /** used to format dates into strings */
  private static final DateFormat _DATETIME_FORMAT = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );
  private static final DateFormat _DATE_FORMAT = new SimpleDateFormat( "yyyy/MM/dd" );
  private static final DateFormat _TIME_FORMAT = new SimpleDateFormat( "HH:mm:ss" );

  /** The format in which our output should be displayed. Text is the default.*/
  protected String _displayFormat = null;

  // The properties stored in the configuration
  public static final String CSDB_DB_USER = "csdb.db.user";
  public static final String CSDB_DB_PASSWORD = "csdb.db.pass";
  public static final String CSDB_DB_URL = "csdb.db.url";
  public static final String CMDB_DB_USER = "cmdb.db.user";
  public static final String CMDB_DB_PASSWORD = "cmdb.db.pass";
  public static final String CMDB_DB_URL = "cmdb.db.url";
  public static final String OPER_LOGIN = "opr.user";
  public static final String OPER_PASSWORD = "oper.pass";
  public static final String SAAS_HOST = "saas.host";
  public static final String SAAS_PORT = "saas.port";
  public static final String SAAS_SCHEME = "saas.scheme";
  public static final String SAAS_USER = "saas.user";
  public static final String SAAS_PASS = "saas.pass";

  public static final String LF = System.getProperty( "line.separator" );
  static DecimalFormat MILLIS = new DecimalFormat( "000" );
  protected static final DecimalFormat NUMBER_FORMAT = new DecimalFormat( "###,###,###,###,###" );
  private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat( "#,###,##0.00" );

  private static final String PROPERTY_FILE_NAME = "snowstorm.properties";

  /** Represents 1 Kilo Byte ( 1024 ). */
  public final static long ONE_KB = 1024L;

  /** Represents 1 Mega Byte ( 1024^2 ). */
  public final static long ONE_MB = ONE_KB * 1024L;

  /** Represents 1 Giga Byte ( 1024^3 ). */
  public final static long ONE_GB = ONE_MB * 1024L;

  /** Represents 1 Tera Byte ( 1024^4 ). */
  public final static long ONE_TB = ONE_GB * 1024L;

  /** This is what is used for or main output, not logging, but user data. */
  protected static PrintStream OUT = System.out;

  protected static final SymbolTable _symbolTable = new SymbolTable();




  public AbstractAction() {
    // Fill the symbol table with system properties
    _symbolTable.readSystemProperties();
  }




  /**
   * Get the configuration for this application
   * 
   * @return The configuration object for this application
   */
  public static Configuration getConfiguration() {

    if ( _config == null ) {
      // Read in our properties
      try {
        _config = new PropertiesConfiguration( PROPERTY_FILE_NAME );
      } catch ( final ConfigurationException e ) {
        throw new IllegalArgumentException( e.getMessage() );
      }
    }
    return _config;
  }




  /**
   * This sets the destination of our output.
   * 
   * <p>This method uses template processing to perform substitutions based 
   * on the currently set symbol table. This table contains all the system 
   * properties and several common command line arguments using the name of 
   * the argument name as the symbol and the argument itself as the value.</p>
   * 
   * <p>This is expected to be a filename, and this method will perform 
   * processing necessary to ensure that it is valid and writable before 
   * continuing. If not, it will exit with an appropriate message.</p>
   * 
   * <p>If the filename is null or empty, this method will use STDOUT of output
   * in a manner similar to {@code System.out}. In fact, it is 
   * expected most output will be sent to the console (STDOUT) and not a 
   * file.</p>
   * 
   * @param filename The name of the file to sent output.
   */
  public void setOutput( String filename ) {
    debug( "Setting output to " + filename );
    if ( filename != null && filename.trim().length() > 0 ) {

      // Create a template using our current symbol table
      Template tmplt = new Template( filename, _symbolTable );

      // parse and replace
      String fname = tmplt.toString();

      File file = new File( fname );

      if ( file.exists() ) {
        if ( file.isDirectory() ) {
          exit( "File '" + file + "' exists but is a directory", 2 );
        }
        if ( file.canWrite() == false ) {
          exit( "File '" + file + "' exists but cannot be written", 2 );
        }
        debug( "Over-writing existing file '" + file + "'" );
      } else {
        File parent = file.getParentFile();
        if ( parent != null && parent.exists() == false ) {
          if ( parent.mkdirs() == false ) {
            exit( "File '" + file + "' could not be created", 2 );
          }
        }
      }

      try {
        OUT = new PrintStream( file );
      } catch ( FileNotFoundException e ) {
        // Should not happen since we performed checks above
        exit( "Could not send output to file '" + file.getName() + "' (" + file.getAbsolutePath() + ") reason:" + e.getMessage(), 2 );
      }

    } // filename not blank

  }




  /**
   * This sends data to the chosen (or default) output stream.
   * 
   * <p>This essentially performs a {@code print(Object)} on the output stream,
   * so line feeds and returns should be embedded in the data if they are 
   * desired.</p>
   * 
   * <p>This method is a convenience wrapper around {@code OUT.print(data)}. 
   * For slightly more control over output, the {@code OUT} attribute can be 
   * used directly. It is a {@link PrintWriter} connected to the desired output
   * stream.</p>
   * 
   * <p>The output flushes which each call to this method.</p> 
   * 
   * @param data the data to send to the previously selected output stream.
   */
  public void output( Object data ) {
    OUT.print( data );
    OUT.flush();
  }




  /**
   * Same as {@link #output(Object)} except this adds a line terminator after 
   * the the given data.
   * 
   * @param data the data to send to the previously selected output stream.
   */
  public void outputLine( Object data ) {
    OUT.print( data );
    OUT.print( LF );
    OUT.flush();
  }




  public static String getEnvironment() {
    return _env;
  }




  public static void setEnvironment( final String env ) {
    _env = env;
  }




  public static boolean isQuiet() {
    return QUIET;
  }




  public static void setQuiet( final boolean quiet ) {
    QUIET = quiet;
  }




  public static boolean isVerbose() {
    return VERBOSE;
  }




  public static void setVerbose( final boolean verbose ) {
    VERBOSE = verbose;
  }




  public static boolean isDebug() {
    return DEBUG;
  }




  public static void setDebug( final boolean debug ) {
    DEBUG = debug;
  }




  //	public static CsdbDAO getCommonSharedDAO() {
  //
  //		if (_commonSharedDAO == null) {
  //			debug("Connecting as " + getProperty(CSDB_DB_USER) + " to " + getProperty(CSDB_DB_URL));
  //			_commonSharedDAO = new CsdbDAO(getProperty(CSDB_DB_URL), getProperty(CSDB_DB_USER), getEncryptedProperty(CSDB_DB_PASSWORD));
  //		}
  //		return _commonSharedDAO;
  //	}

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




  /**
   * Get a formatted string representing the difference between the two times.
   * 
   * @param startDate Required start date
   * @param endDate Optional end date; current time is assumed otherwise.
   *  
   * @return formatted string representing weeks, days, hours minutes and seconds.
   */
  public static String formatElapsed( Date startDate, Date endDate ) {
    if ( startDate == null )
      return "not started";

    Date end = endDate;

    if ( endDate == null )
      end = new Date();

    return formatElapsed( end.getTime() - startDate.getTime() );
  }




  /**
   * Format the number consistently across action.
   * 
   * @param number the number to format
   * 
   * @return zero suppressed, comma separated number.
   */
  public static String formatNumber( long number ) {
    return NUMBER_FORMAT.format( number );
  }




  public static String formatDecimal( double number ) {
    return DECIMAL_FORMAT.format( number );
  }




  /**
   * Formats the size as a most significant number of bytes.
   * 
   * @param size in bytes
   * 
   * @return the size formatted for display
   */
  public static String formatSizeBytes( final double size ) {
    final StringBuffer buf = new StringBuffer( 16 );
    String text;
    double divider;

    if ( size < ONE_KB ) {
      text = "bytes";
      divider = 1.0;
    } else if ( size < ONE_MB ) {
      text = "KB";
      divider = ONE_KB;
    } else if ( size < ONE_GB ) {
      text = "MB";
      divider = ONE_MB;
    } else if ( size < ONE_TB ) {
      text = "GB";
      divider = ONE_GB;
    } else {
      text = "TB";
      divider = ONE_TB;
    }

    final double d = ( (double)size ) / divider;
    DECIMAL_FORMAT.format( d, buf, new FieldPosition( 0 ) ).append( ' ' ).append( text );

    return buf.toString();
  }




  /**
   * Formats the given number as a most significant number of bytes.
   * 
   * @param number the number to format
   * 
   * @return the number formatted for display
   */
  public static String formatSizeBytes( final Number number ) {
    return formatSizeBytes( number.doubleValue() );
  }




  /**
   * Get a formatted string representing the difference between the two times.
   * 
   * @param millis number of elapsed milliseconds.
   * 
   * @return formatted string representing weeks, days, hours minutes and seconds.
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
    b.append( elapsedSeconds );
    if ( millis > 0 ) {
      b.append( "." );
      b.append( MILLIS.format( millis ) );
    }
    b.append( " sec" );

    return b.toString();
  }




  /**
    * Retrieve the property appropriate for our environment
    * 
    * @param key the property to retrieve
    * 
    * @return the value currently set in that property
    */
  public static String getProperty( final String key ) {
    return getConfiguration().getString( _env + "." + key );
  }




  /**
   * Get the value of the encrypted property
   * 
   * @param key the property to retrieve
   * 
   * @return the value currently set in that property
   */
  public static String getEncryptedProperty( final String key ) {
    String rawValue = getConfiguration().getString( _env + "." + key );
    if ( rawValue != null ) {
      try {
        return CipherUtil.decrypt( rawValue );
      } catch ( Exception e ) {
        error( "Problems getting encrypted property '" + key + "' = '" + rawValue + "' - " + e.toString() );
        e.printStackTrace();
      }
    }
    return null;
  }




  /**
   * Write an informational message to the console.
   * 
   * @param msg The message to write.
   */
  public static void info( final Object msg ) {
    if ( !QUIET ) {
      System.out.println( msg );
    }
  }




  /**
   * Write a detailed message relating to this classes operation to the 
   * console.
   * 
   * @param msg The message to write.
   */
  public static void trace( final Object msg ) {

    if ( !QUIET && VERBOSE ) {
      System.out.println( msg );
    }
  }




  /**
   * Write a debug message to the console.
   * 
   * @param msg The message to write.
   */
  public static void debug( final Object msg ) {
    if ( !QUIET && DEBUG ) {
      System.out.println( msg );
    }
  }




  /**
   * Write an error message to the console.
   * 
   * @param msg The message to write.
   */
  public static void error( final Object msg ) {
    if ( !QUIET ) {
      System.err.println( msg );
    }
  }




  /**
   * Display the given message on the error stream and exit with the given
   * code.
   * 
   * @param msg The message to display.
   * @param code The exit code.
   */
  public static void exit( final String msg, final int code ) {
    System.err.println( msg );
    System.exit( code );
  }




  /**
   * Display the given message on the error stream and exit with an exit code 
   * of 1.
   * 
   * @param msg The message to display.
   */
  public static void exit( final String msg ) {
    exit( msg, 1 );
  }




  /**
   * Get a named value from the command line.
   * 
   * <p>This method is only successful if the application was called by the 
   * command line interface (i.e. CLI class).</p>
   * 
   * @param name name of the value to return
   * 
   * @return The value or null if there were problems or the value does not exist.
   */
  protected String getCommandLineValue( final String name ) {
    try {
      if ( CLI.getCommandLine() != null ) {
        final Object obj = CLI.getCommandLine().getParsedOptionValue( name );
        if ( obj != null )
          return obj.toString();
        else
          return null;
      } else {
        // We were not called from the command line and no argument
        // parsing was performed
        return null;
      }
    } catch ( final ParseException e ) {
      debug( "Error retrieving Command Line value '" + name + "' - " + e.getMessage() );
      return null;
    }

  }




  /**
   * Determine if the argument has been set.
   * 
   * @param name Name of the command line argument to check.
   * 
   * @return true if the argument has been set, false otherwise.
   */
  protected boolean argumentExists( String name ) {
    return CLI.getCommandLine().hasOption( name );
  }




  /**
   * @see coyote.cli.actions.Action#buildOptions(org.apache.commons.cli.Options)
   */
  @Override
  public void buildOptions( Options o ) {}




  /**
   * This is the default help message for all actions.
   * 
   * <p>Subclasses are expected to override this method to provide argument
   * details and general usage information specific to the action.</p>
   */
  @Override
  public String getHelp() {
    StringBuffer b = new StringBuffer();
    b.append( "No additional help is available." );
    return b.toString();
  }




  /**
   * Performs any validation on the set parameters before execution.
   * 
   * @see coyote.cli.actions.Action#validate()
   */
  @Override
  public void validate() throws ActionException {

  }




  /**
   * Retrieve the output format from the command line (defaults to 'TXT').
   *  
   * @return the output format from the command line 
   */
  public String getDisplayFormat() {
    if ( _displayFormat == null ) {
      _displayFormat = getCommandLineValue( OPT_FMT );

      // Default to TXT if no format was given
      if ( _displayFormat == null || _displayFormat.trim().length() < 1 )
        _displayFormat = FMT_TXT;
    }

    return _displayFormat;
  }




  public void setDisplayFormat( String fmt ) {
    this._displayFormat = fmt;
  }




  /**
   * Formats the given number of milliseconds into hours, minutes and seconds 
   * and if requested the remaining milliseconds.
   * 
   * @param val the interval in milliseconds
   * 
   * @return the time interval in hh:mm:ss format.
   */
  public String formatElapsedMillis( long val, boolean millis ) {
    StringBuilder buf = new StringBuilder( 20 );
    String sgn = "";

    if ( val < 0 ) {
      sgn = "-";
      val = Math.abs( val );
    }

    append( buf, sgn, 0, ( val / 3600000 ) );
    append( buf, ":", 2, ( ( val % 3600000 ) / 60000 ) );
    append( buf, ":", 2, ( ( val % 60000 ) / 1000 ) );
    if ( millis )
      append( buf, ".", 3, ( val % 1000 ) );

    return buf.toString();
  }




  /** Append a right-aligned and zero-padded numeric value to a `StringBuilder`. */
  static private void append( StringBuilder tgt, String pfx, int dgt, long val ) {
    tgt.append( pfx );
    if ( dgt > 1 ) {
      int pad = ( dgt - 1 );
      for ( long xa = val; xa > 9 && pad > 0; xa /= 10 ) {
        pad--;
      }
      for ( int xa = 0; xa < pad; xa++ ) {
        tgt.append( '0' );
      }
    }
    tgt.append( val );
  }




  /**
   * Most actions do not need to close resources.  If they do, they can over-
   * ride this method.
   */
  @Override
  public void close() {}
}
