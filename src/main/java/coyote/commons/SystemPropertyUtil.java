/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Simple set of utilities to interact with System Properties.
 */
public class SystemPropertyUtil {

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

  private static final Logger LOG = LoggerFactory.getLogger( SystemPropertyUtil.class );




  /**
   * Load the property file with the given name into the system properties.
   * 
   * <p>The name is appended with '.properties' to come up with the full file 
   * name. Therefore passing this the name of 'app' will result in a file named
   * 'app.properties' being used.</p>
   * 
   * <p>This method will search for the named file in 4 locations, each 
   * subsequent found file being used to augment and over write the properties 
   * of previously loaded properties files:<ol>
   * <li>currently set class path</li>
   * <li>home directory of the user running the JVM</li>
   * <li>directory specified by the {@code cfg.dir} system property</li>
   * <li>current working directory</li></ol></p>
   * 
   * @param name root name of the file to search
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
      props.load( SystemPropertyUtil.class.getClassLoader().getResourceAsStream( resourcename ) );
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
   * @return {@code true} if the String is not empty and not null and not
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
   * @return {@code true} if the String is not empty and not null and not
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




  /**
   * Set a system property.
   * 
   * <p>If the value is null, then the property will be removed from the system 
   * properties.</p>
   * 
   * @param name the name of the property to set
   * @param value the value of the property to set
   */
  public static void setProperty( String name, String value ) {
    if ( name != null ) {
      if ( value != null ) {
        System.getProperties().setProperty( name, value );
      } else {
        System.getProperties().remove( name );
      }
    }
  }




  /**
   * @param key
   * 
   * @return the system property with the given key, or its default if it is set.
   */
  public static String getString( String key ) {
    return System.getProperties().getProperty( key );
  }

}
