package coyote.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This allows for the population of the System properties from a variety of
 * locations.
 * 
 * <p>The first place searched for the named properties file is the class path 
 * then the home directory of the user running the Java runtime. Next, the 
 * system property of {@code cfg.dir} is checked for a directory location. If 
 * found, the named property file in that directory is read and over-writes any 
 * existing properties with values from that location. Finally, the current 
 * directory is checked and all those properties overwrites any existing 
 * properties.</p>
 */
public class SystemPropertiesLoader {

  /** System property which specifies the user name for the proxy server */
  public static final String PROXY_USER = "http.proxyUser";

  /** System property which specifies the user password for the proxy server */
  public static final String PROXY_PASSWORD = "http.proxyPassword";

  /** System property which specifies the proxy server host name */
  public static final String PROXY_HOST = "http.proxyHost";

  /**
   * System property which specifies the configuration directory for the
   * application
   */
  public static final String CONFIG_DIR = "cfg.dir";

  private static final Logger LOG = LoggerFactory.getLogger( SystemPropertiesLoader.class );




  /**
   * 
   * @param name
   */
  public static void load( String name ) {

    // Start with loading the property file from the classpath
    loadPropertiesFromClasspath( name );

    // then load from the user's home directory
    loadPropertiesIntoSystem( name, false, System.getProperty( "user.home" ) );

    // Load specific property files from the configuration directory which
    // over-rides those properties previously loaded
    loadPropertiesIntoSystem( name, false, getConfigPath() );

    // Next load specific property files from the current working directory
    loadPropertiesIntoSystem( name, false, System.getProperty( "user.dir" ) );

    // Load the Java proxy authenticator if system properties contained the
    // necessary data
    installProxyAuthenticatorIfNeeded();
  }




  private static void loadPropertiesFromClasspath( String name ) {
    Properties props = new Properties();
    String resourcename = name + ".properties";
    try {
      props.load( SystemPropertiesLoader.class.getClassLoader().getResourceAsStream( resourcename ) );
      LOG.debug( "Loading {} properties from classpath resource '{}'", props.size(), resourcename );
      System.getProperties().putAll( props );
    } catch ( Exception e ) {
      LOG.debug( "Could not find properties file named '{}' on classpath", resourcename );
    }

  }




  private static void loadPropertiesIntoSystem( String fileName, boolean errIfMissing, String pathName ) {
    if ( isNotBlank( pathName ) ) {
      String filename = pathName + File.separator + fileName + ".properties";
      LOG.debug( String.format( "Trying to load properties from %s into system", filename ) );
      Properties props = new Properties();
      try {
        props.load( new FileInputStream( filename ) );
        LOG.debug( "Loading {} properties from '{}'", props.size(), filename );
        System.getProperties().putAll( props );
      } catch ( IOException e ) {
        String msg = String.format( "Failed to read from %s", filename );
        LOG.debug( String.format( "%s - Reason: %s", msg, e.getMessage() ) );
        if ( errIfMissing ) {
          throw new IllegalStateException( msg, e );
        }
      }
    }
  }




  /**
   * Load the Java proxy authenticator if the are system properties specifying
   * a proxy host and user name.
   */
  private static void installProxyAuthenticatorIfNeeded() {
    final String user = System.getProperty( PROXY_USER );
    final String password = System.getProperty( PROXY_PASSWORD );
    final String host = System.getProperty( PROXY_HOST );

    if ( isNotBlank( user ) && isNotBlank( password ) && isNotBlank( host ) ) {
      LOG.debug( String.format( "Detected http proxy settings (%s@%s), will setup authenticator", user, host ) );
      Authenticator.setDefault( new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication( user, password.toCharArray() );
        }
      } );
    }
  }




  /**
   * Return the configuration path set in the system properties.
   * 
   * @return the path to the configuration property
   */
  public static String getConfigPath() {
    // Check for ending file separator and remove it if found
    String retval = System.getProperty( CONFIG_DIR );
    if ( retval == null ) {
      LOG.debug( String.format( "No configuration override path found in '%s' system property", CONFIG_DIR ) );
      return "";
    }
    retval = retval.trim();
    if ( retval.endsWith( File.separator ) )
      retval = retval.substring( 0, retval.lastIndexOf( File.separator ) );

    return retval;
  }




  /**
   * Checks if a string is not null, empty ("") and not only whitespace.
   * 
   * <p>This is a convenience wrapper around isBlank(String) to make code 
   * slightly more readable.</p>
   * 
   * @param str the String to check, may be null
   * 
   * @return <code>true</code> if the String is not empty and not null and not
   *         whitespace
   * 
   * @see #isBlank(String)
   */
  public static boolean isNotBlank( String str ) {
    return !isBlank( str );
  }




  /**
   * Checks if a string is not null, empty ("") and not only whitespace.
   * 
   * @param str the String to check, may be null
   * 
   * @return <code>true</code> if the String is not empty and not null and not
   *         whitespace
   */
  public static boolean isBlank( String str ) {
    int strLen;
    if ( str == null || ( strLen = str.length() ) == 0 ) {
      return true;
    }
    for ( int i = 0; i < strLen; i++ ) {
      if ( ( Character.isWhitespace( str.charAt( i ) ) == false ) ) {
        return false;
      }
    }
    return true;
  }

}
