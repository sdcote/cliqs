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

import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import coyote.cli.CLI;
import coyote.cli.template.SymbolTable;
import coyote.cli.template.Template;
import coyote.commons.CipherUtil;
import coyote.commons.StringUtil;
import coyote.commons.SystemPropertyUtil;


public abstract class AbstractAction implements Action {
  private static final String OPT_FMT = "fmt";
  private static final String FMT_TXT = "txt";
  /** The database environment to use */
  private static String _env = "DEV";

  /** Flag indicating all output should be suppressed */
  private static boolean QUIET = false;
  /** Flag indicating all messages should be sent to the console */
  private static boolean VERBOSE = false;
  /** Flag indicating debugging messages should be sent to the console */
  private static boolean DEBUG = false;

  /** The format in which our output should be displayed. Text is the default.*/
  protected String _displayFormat = null;

  // The properties stored in the configuration, these will vary by project
  public static final String SAAS_HOST = "saas.host";
  public static final String SAAS_PORT = "saas.port";
  public static final String SAAS_SCHEME = "saas.scheme";
  public static final String SAAS_USER = "saas.user";
  public static final String SAAS_PASS = "saas.pass";

  /** This is what is used for or main output, not logging, but user data. */
  protected static PrintStream OUT = System.out;

  protected static final SymbolTable _symbolTable = new SymbolTable();




  /** Append a right-aligned and zero-padded numeric value to a `StringBuilder`. */
  static private void append( final StringBuilder tgt, final String pfx, final int dgt, final long val ) {
    tgt.append( pfx );
    if ( dgt > 1 ) {
      int pad = ( dgt - 1 );
      for ( long xa = val; ( xa > 9 ) && ( pad > 0 ); xa /= 10 ) {
        pad--;
      }
      for ( int xa = 0; xa < pad; xa++ ) {
        tgt.append( '0' );
      }
    }
    tgt.append( val );
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
   * Display the given message on the error stream and exit with an exit code 
   * of 1.
   * 
   * @param msg The message to display.
   */
  public static void exit( final String msg ) {
    exit( msg, 1 );
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
   * Get the value of the encrypted property
   * 
   * @param key the property to retrieve
   * 
   * @return the value currently set in that property
   */
  public static String getEncryptedProperty( final String key ) {
    final String rawValue = SystemPropertyUtil.getString( _env + "." + key );
    if ( rawValue != null ) {
      try {
        return CipherUtil.decrypt( rawValue );
      } catch ( final Exception e ) {
        error( "Problems getting encrypted property '" + key + "' = '" + rawValue + "' - " + e.toString() );
        e.printStackTrace();
      }
    }
    return null;
  }




  public static String getEnvironment() {
    return _env;
  }




  /**
    * Retrieve the property appropriate for our environment
    * 
    * @param key the property to retrieve
    * 
    * @return the value currently set in that property
    */
  public static String getProperty( final String key ) {
    return SystemPropertyUtil.getString( _env + "." + key );
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




  public static boolean isDebug() {
    return DEBUG;
  }




  public static boolean isQuiet() {
    return QUIET;
  }




  public static boolean isVerbose() {
    return VERBOSE;
  }




  public static void setDebug( final boolean debug ) {
    DEBUG = debug;
  }




  public static void setEnvironment( final String env ) {
    _env = env;
  }




  public static void setQuiet( final boolean quiet ) {
    QUIET = quiet;
  }




  public static void setVerbose( final boolean verbose ) {
    VERBOSE = verbose;
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




  public AbstractAction() {
    // Fill the symbol table with system properties
    _symbolTable.readSystemProperties();
  }




  /**
   * Determine if the argument has been set.
   * 
   * @param name Name of the command line argument to check.
   * 
   * @return true if the argument has been set, false otherwise.
   */
  protected boolean argumentExists( final String name ) {
    return CLI.getCommandLine().hasOption( name );
  }




  /**
   * @see coyote.cli.actions.Action#buildOptions(org.apache.commons.cli.Options)
   */
  @Override
  public void buildOptions( final Options o ) {}




  /**
   * Most actions do not need to close resources.  If they do, they can over-
   * ride this method.
   */
  @Override
  public void close() {}




  /**
   * Formats the given number of milliseconds into hours, minutes and seconds 
   * and if requested the remaining milliseconds.
   * 
   * @param val the interval in milliseconds
   * 
   * @return the time interval in hh:mm:ss format.
   */
  public String formatElapsedMillis( long val, final boolean millis ) {
    final StringBuilder buf = new StringBuilder( 20 );
    String sgn = "";

    if ( val < 0 ) {
      sgn = "-";
      val = Math.abs( val );
    }

    append( buf, sgn, 0, ( val / 3600000 ) );
    append( buf, ":", 2, ( ( val % 3600000 ) / 60000 ) );
    append( buf, ":", 2, ( ( val % 60000 ) / 1000 ) );
    if ( millis ) {
      append( buf, ".", 3, ( val % 1000 ) );
    }

    return buf.toString();
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
        if ( obj != null ) {
          return obj.toString();
        } else {
          return null;
        }
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
   * Retrieve the output format from the command line (defaults to 'TXT').
   *  
   * @return the output format from the command line 
   */
  public String getDisplayFormat() {
    if ( _displayFormat == null ) {
      _displayFormat = getCommandLineValue( OPT_FMT );

      // Default to TXT if no format was given
      if ( ( _displayFormat == null ) || ( _displayFormat.trim().length() < 1 ) ) {
        _displayFormat = FMT_TXT;
      }
    }

    return _displayFormat;
  }




  /**
   * This is the default help message for all actions.
   * 
   * <p>Subclasses are expected to override this method to provide argument
   * details and general usage information specific to the action.</p>
   */
  @Override
  public String getHelp() {
    final StringBuffer b = new StringBuffer();
    b.append( "No additional help is available." );
    return b.toString();
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
  public void output( final Object data ) {
    OUT.print( data );
    OUT.flush();
  }




  /**
   * Same as {@link #output(Object)} except this adds a line terminator after 
   * the the given data.
   * 
   * @param data the data to send to the previously selected output stream.
   */
  public void outputLine( final Object data ) {
    OUT.print( data );
    OUT.print( StringUtil.LINE_FEED );
    OUT.flush();
  }




  public void setDisplayFormat( final String fmt ) {
    _displayFormat = fmt;
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
  public void setOutput( final String filename ) {
    debug( "Setting output to " + filename );
    if ( ( filename != null ) && ( filename.trim().length() > 0 ) ) {

      // Create a template using our current symbol table
      final Template tmplt = new Template( filename, _symbolTable );

      // parse and replace
      final String fname = tmplt.toString();

      final File file = new File( fname );

      if ( file.exists() ) {
        if ( file.isDirectory() ) {
          exit( "File '" + file + "' exists but is a directory", 2 );
        }
        if ( file.canWrite() == false ) {
          exit( "File '" + file + "' exists but cannot be written", 2 );
        }
        debug( "Over-writing existing file '" + file + "'" );
      } else {
        final File parent = file.getParentFile();
        if ( ( parent != null ) && ( parent.exists() == false ) ) {
          if ( parent.mkdirs() == false ) {
            exit( "File '" + file + "' could not be created", 2 );
          }
        }
      }

      try {
        OUT = new PrintStream( file );
      } catch ( final FileNotFoundException e ) {
        // Should not happen since we performed checks above
        exit( "Could not send output to file '" + file.getName() + "' (" + file.getAbsolutePath() + ") reason:" + e.getMessage(), 2 );
      }

    } // filename not blank

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
   * @return the output print stream
   */
  protected static PrintStream getOutStream() {
    return OUT;
  }




  /**
   * @param out the output print stream to use
   */
  protected static void setOutStream( PrintStream out ) {
    OUT = out;
  }
}
