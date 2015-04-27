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
package coyote.cli;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import coyote.cli.actions.AbstractAction;
import coyote.cli.actions.Action;
import coyote.cli.actions.ActionException;
import coyote.commons.DateUtil;
import coyote.commons.StringUtil;
import coyote.commons.SystemPropertyUtil;


/**
 * CLI - This is the command line interface to the load actions.
 * 
 * <p>All supported actions are to be place in CoreContext.xml</p>
 * 
 * <p>This class is responsible for parsing input from the user, setting up the 
 * environment and determining which action to call to satisfy the user 
 * request. It is intended to codify complex operations for easy replication.</p>
 * 
 */
public class CLI extends AbstractAction {

  private static final Logger LOG = LoggerFactory.getLogger( CLI.class );

  private static String VERSION = "0.1 dev - build 11 Mar 2015";
  private static CommandLine _cmd = null;
  private static String appname = "cliqs"; // name of the properties file to load
  private static String NOUN = null;

  private static final String[] CONFIG_FILES = new String[] { "CoreContext.xml" };

  private static final Map<String, Action> actions = new HashMap<String, Action>();

  // Command Line Option Names
  private static final String OPT_ENV = "env";
  private static final String OPT_FMT = "fmt";
  private static final String OPT_QUIET = "q";
  private static final String OPT_VERBOSE = "v";
  private static final String OPT_DEBUG = "d";
  private static final String OPT_OUT = "o";

  // Environments Supported
  private static final String DEV = "DEV";
  private static final String TEST = "TEST";
  private static final String UAT = "UAT";
  private static final String PROD = "PROD";




  public static CommandLine getCommandLine() {
    return _cmd;
  }




  /**
   * This is the main entry point into the utility.
   * 
   * @param args command line arguments to parse.
   */
  public static void main( final String[] args ) {

    // Load system properties from conventional locations
    SystemPropertyUtil.load( appname );

    // Load configuration context from the class path
    @SuppressWarnings("resource")
    final ApplicationContext applicationContext = new ClassPathXmlApplicationContext( CONFIG_FILES );

    // Now get the configured command line interface object
    final CLI loader = applicationContext.getBean( "cli", CLI.class );

    try {

      // process the command line arguments
      loader.processArgs( args );

      // validate the arguments and select the appropriate action to
      // handle the request
      loader.validate();

      final long start = System.currentTimeMillis();
      loader.execute();
      final long elapsed = System.currentTimeMillis() - start;

      debug( "Action completed - execution time " + DateUtil.formatElapsed( elapsed ) );

      // send a tone to the console if the command took longer than 30
      // seconds and we are not being quiet...just to let you know the
      // action is done
      if ( !isQuiet() && ( elapsed > 30000 ) ) {
        System.out.println( (char)7 );
      }

      System.exit( 0 );

    } catch ( final Exception ex ) {
      ex.printStackTrace();
    }
    finally {
      if ( OUT != System.out ) {
        debug( "Closing output stream." );
        OUT.close();
      }
    }

  }

  // Actions Supported
  private final String ACTION_HELP = "Help";

  private final String ACTION_VERSION = "Version";

  // That which does what was asked
  Action action = null;




  /**
   * Default Constructor
   */
  public CLI() {

  }




  /**
   * Builds the options supported in the command line to be used by parser.
   * 
   * @return Options for the parser
   */
  @SuppressWarnings("static-access")
  private Options buildOptions() {

    // go through all the actions and determine the options supported

    final Options o = new Options();
    o.addOption( new Option( OPT_QUIET, "surpress messages (quiet) just the facts." ) );
    o.addOption( new Option( OPT_VERBOSE, "verbose output, more object data displayed." ) );
    o.addOption( new Option( OPT_DEBUG, "debugging messages, describes what's happening." ) );

    o.addOption( OptionBuilder.hasArg().isRequired( false ).withArgName( "environment" ).withType( String.class ).withDescription( "The environment (e.g. DEV, TEST, PROD) to use." ).create( OPT_ENV ) );
    o.addOption( OptionBuilder.hasArg().isRequired( false ).withArgName( "CSV,TAB" ).withType( String.class ).withDescription( "The format of the output. (Default is text)" ).create( OPT_FMT ) );
    o.addOption( OptionBuilder.hasArg().isRequired( false ).withArgName( "filename" ).withType( String.class ).withDescription( "Output results to file (try 'default')" ).create( OPT_OUT ) );

    // use a visitor pattern to build the options the actions expect
    for ( final Action action : actions.values() ) {
      action.buildOptions( o );
    }

    return o;
  }




  /**
   * Based on the contents of the system-wide arguments, perform some manner
   * of processing.
   */
  @Override
  public void execute() {

    // Now execute the appropriate action

    if ( action != null ) {
      try {
        // Have the action validate the arguments
        action.validate();

        try {
          // Now try to execute the action with the validated
          // arguments
          action.execute();

        } catch ( final Exception e ) {
          error( "Unexpected issues executing action: " + e.getMessage() );
          error( e.getMessage() );

          if ( isDebug() || isVerbose() ) {
            e.printStackTrace( System.err );
          }

        }
      } catch ( final Exception e ) {
        error( "Problems validating action: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")" );
        if ( isDebug() || isVerbose() ) {
          e.printStackTrace( System.err );
        }
      }
      finally {
        action.close();
      }

    } else {
      exit( "No action to perform", 2 );
    }

  }




  /**
   * @return the map of Nouns and their associated Actions
   */
  public Map<String, Action> getActionMap() {
    return actions;
  }




  /**
   * @return a collection of all the actions configured in this CLI
   */
  public Collection<Action> getActions() {
    return actions.values();
  }




  public String getVersion() {
    return VERSION;
  }




  /**
   * Populates the symbol table with the set arguments.
   */
  @SuppressWarnings("unchecked")
  private void populateSymbolTable() {

    // First put all the arguments in the table
    for ( final Iterator<Option> it = _cmd.iterator(); it.hasNext(); ) {
      final Option option = it.next();

      try {
        final Object value = _cmd.getParsedOptionValue( option.getOpt() );
        if ( value != null ) {
          _symbolTable.put( option.getOpt(), value.toString() );
        }
      } catch ( final ParseException e ) {
        error( e.getMessage() );
      }
    }

    // Put the environment in the symbol table
    _symbolTable.put( CLI.OPT_ENV, getEnvironment() );

    // Place date and time values in the symbol table
    final Calendar cal = Calendar.getInstance();

    // Add current 'now' Date, Time and DateTime
    final Date date = new Date();
    if ( date != null ) {
      cal.setTime( date );
      _symbolTable.put( "nowDate", DateUtil.formatDate( date ) );
      _symbolTable.put( "nowTime", DateUtil.formatTime( date ) );
      _symbolTable.put( "nowDateTime", DateUtil.formatDateTime( date ) );
      _symbolTable.put( "nowMonth", String.valueOf( cal.get( Calendar.MONTH ) + 1 ) );
      _symbolTable.put( "nowDay", String.valueOf( cal.get( Calendar.DAY_OF_MONTH ) ) );
      _symbolTable.put( "nowYear", String.valueOf( cal.get( Calendar.YEAR ) ) );
      _symbolTable.put( "nowHour", String.valueOf( cal.get( Calendar.HOUR ) ) );
      _symbolTable.put( "nowMinute", String.valueOf( cal.get( Calendar.MINUTE ) ) );
      _symbolTable.put( "nowSecond", String.valueOf( cal.get( Calendar.SECOND ) ) );
      _symbolTable.put( "nowMillisecond", String.valueOf( cal.get( Calendar.MILLISECOND ) ) );
    }

  }




  /**
   * Parse the arguments into system-wide arguments.
   * 
   * @param args The arguments to parse
   */
  @SuppressWarnings("unchecked")
  private void processArgs( final String[] args ) {
    String[] nargs = new String[0];

    final CommandLineParser parser = new PosixParser();
    final Options options = buildOptions();
    final HelpFormatter help = new HelpFormatter();

    // First, get the noun or action we are to perform, it is always the
    // first argument
    if ( ( args != null ) && ( args.length > 0 ) ) {
      NOUN = args[0];

      // shift all the arguments to the left
      nargs = new String[args.length - 1];
      System.arraycopy( args, 1, nargs, 0, nargs.length );
    }

    try {
      _cmd = parser.parse( options, nargs );

      // populate the symbol table with values based on command line
      // arguments
      populateSymbolTable();
      _symbolTable.put( "Action", NOUN );

      setQuiet( _cmd.hasOption( OPT_QUIET ) );
      setVerbose( _cmd.hasOption( OPT_VERBOSE ) );
      setDebug( _cmd.hasOption( OPT_DEBUG ) );

      // setup our output file, support a default naming
      if ( _cmd.hasOption( OPT_OUT ) ) {
        String filename = getCommandLineValue( OPT_OUT );
        if ( filename.equalsIgnoreCase( "default" ) ) {
          filename = "[#$Action#]_[#$nowYear#]-[#$nowMonth#]-[#$nowDay#].txt";
        }
        setOutput( filename );
      }

      // Set the environment
      final String environ = (String)_cmd.getParsedOptionValue( OPT_ENV );

      if ( environ != null ) {
        setEnvironment( environ.toUpperCase() );

        // Make sure it is one of the expected values
        if ( DEV.equals( getEnvironment() ) || TEST.equals( getEnvironment() ) || UAT.equals( getEnvironment() ) || PROD.equals( getEnvironment() ) ) {
          info( "Using the '" + getEnvironment() + "' environment." );
        } else {
          exit( "Unsupported environment '" + environ + "'", 1 );
        }
      }
    } catch ( final Exception e ) {
      System.err.println( "Wrong parameters:" + e.getMessage() );
      help.printHelp( "Get NOUN [options]" + StringUtil.LINE_FEED + "Try Get HELP to get more help." + StringUtil.LINE_FEED + "Here are some command line options:", options );
      System.exit( 1 );
    }

    if ( NOUN == null ) {
      exit( "Get NOUN [options]" + StringUtil.LINE_FEED + "Try Get HELP to get more help.", 1 );
    }

  }




  /**
   * Set all the actions supported by this CLI mapped by their "noun"
   * 
   * @param map the map of actions this CLI is to support
   */
  public void setActionMap( final Map<String, Action> map ) {
    synchronized( actions ) {
      for ( final String name : map.keySet() ) {
        actions.put( name.toLowerCase(), map.get( name ) );
      }
    }

  }




  /**
   * Based on the contents of the system-wide arguments, perform some manner
   * of processing.
   */
  @Override
  public void validate() throws ActionException {
    if ( ACTION_VERSION.equalsIgnoreCase( NOUN ) ) {
      System.out.println( VERSION );
      System.exit( 0 );
    } else if ( ACTION_HELP.equalsIgnoreCase( NOUN ) ) {
      // Display the help page for this CLI
      Help.display( this );
      System.exit( 0 );
    } else {
      // lookup the noun
      action = actions.get( NOUN.toLowerCase() );
    }

    // If we could not determine the action, exit with an error
    if ( action == null ) {
      exit( "Unsupported noun '" + NOUN + "'\r\nTry 'Load HELP'", 1 );
    }
  }

}
