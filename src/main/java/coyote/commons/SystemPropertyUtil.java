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
   * Load the system properties using the given name as the base name of the 
   * properties file for which to search and load.
   * 
   * <p>The given base name will be appended with {@code .properties} and 
   * several common locations searched.</p>
   * 
   * @param name
   */
  public static void load( String name ) {
    load( name, false );
  }




  /**
   * Load the system properties using the given name as the base name of the 
   * properties file for which to search and load and treat the proxy password 
   * (if found) as an encrypted string.
   * 
   * <p>The given base name will be appended with {@code .properties} and 
   * several common locations searched.</p>
   * 
   * @param name
   */
  public static void loadSecure( String name ) {
    load( name, true );
  }




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
   * <p>If the secure flag is set to true, the system will treat the proxy 
   * password as encrypted text. In this case the value will be read in, 
   * decrypted and reset in the runtime properties in it decrypted form (plain 
   * text). If the secure flag is set to false, the proxy password will be used 
   * as is.</p>
   * 
   * @param name base name of the file to load
   * @param secure flag indicating the proxy password is to be treated as 
   *        encrypted text
   */
  private static void load( String name, boolean secure ) {

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
    installProxyAuthenticatorIfNeeded( secure );

  }




  /**
   * Return the configuration path set in the system properties.
   * 
   * @return the path to the configuration property
   */
  public static String getConfigPath() {
    // Check for ending file separator and remove it if found
    String retval = System.getProperty( CONFIG_DIR );
    if ( isBlank( retval ) ) {
      LOG.debug( String.format( "No configuration override path found in '%s' system property", CONFIG_DIR ) );
      return "";
    }
    retval = retval.trim();
    if ( retval.endsWith( File.separator ) ) {
      retval = retval.substring( 0, retval.lastIndexOf( File.separator ) );
    }

    return retval;
  }




  /**
   * Searches for the property with the specified key in this property list. 
   * 
   * <p>If the key is not found in this property list, the default property 
   * list, and its defaults, recursively, are then checked. The method returns 
   * {@code null] if the property is not found.</p>

   * @param key the property key
   * 
   * @return the system property with the given key, or its default if it is set.
   */
  public static String getString( final String key ) {
    return System.getProperties().getProperty( key );
  }




  /**
   * Get the value of the encrypted property
   * 
   * @param key the property to retrieve
   * 
   * @return the value currently set in that property
   */
  public static String getEncryptedString( final String key ) {
    String rawValue = getString( key );
    if ( isNotBlank( rawValue ) ) {
      try {
        return CipherUtil.decrypt( rawValue );
      } catch ( Exception e ) {
        e.printStackTrace();
      }
    }
    return null;
  }




  /**
     * Get an encrypted value suitable for placing in an encrypted property
     * 
     * @param value The value to encrypt
     * 
     * @return cipher text suitable for placing in a properties file, or null if 
     *         the passed value is null or encryption errors have occurred.
     */
  public static String encryptString( final String value ) {
    if ( isNotBlank( value ) ) {
      try {
        return CipherUtil.encrypt( value );
      } catch ( Exception e ) {
        e.printStackTrace();
      }
    }
    return null;
  }




  /**
   * Load the Java proxy authenticator if the are system properties specifying
   * a proxy host and user name.
   * 
   * @param secure if true, the password will be treated as an encrypted 
   *        string, if false the string will be read in as plain text;
   */
  private static void installProxyAuthenticatorIfNeeded( boolean secure ) {
    final String user = System.getProperty( PROXY_USER );
    final String password = secure ? getEncryptedString( PROXY_PASSWORD ) : getString( PROXY_PASSWORD );
    final String host = System.getProperty( PROXY_HOST );

    if ( secure && isNotBlank( password ) ) {
      System.setProperty( PROXY_PASSWORD, password );
    }

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
   * Checks if a string is not null, empty ("") and not only whitespace.
   * 
   * @param str the String to check, may be null
   * 
   * @return {@code true} if the String is not empty and not null and not
   *         whitespace
   */
  public static boolean isBlank( final String str ) {
    int strLen;
    if ( ( str == null ) || ( ( strLen = str.length() ) == 0 ) ) {
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
  public static boolean isNotBlank( final String str ) {
    return !isBlank( str );
  }




  private static void loadPropertiesFromClasspath( final String name ) {
    final Properties props = new Properties();
    final String resourcename = name + ".properties";
    try {
      props.load( SystemPropertyUtil.class.getClassLoader().getResourceAsStream( resourcename ) );
      LOG.debug( "Loading {} properties from classpath resource '{}'", props.size(), resourcename );
      System.getProperties().putAll( props );
    } catch ( final Exception e ) {
      LOG.debug( "Could not find properties file named '{}' on classpath", resourcename );
    }

  }




  private static void loadPropertiesIntoSystem( final String fileName, final boolean errIfMissing, final String pathName ) {
    if ( isNotBlank( pathName ) ) {
      final String filename = pathName + File.separator + fileName + ".properties";
      LOG.debug( String.format( "Trying to load properties from %s into system", filename ) );
      final Properties props = new Properties();
      try {
        props.load( new FileInputStream( filename ) );
        LOG.debug( "Loading {} properties from '{}'", props.size(), filename );
        System.getProperties().putAll( props );
      } catch ( final IOException e ) {
        final String msg = String.format( "Failed to read from %s", filename );
        LOG.debug( String.format( "%s - Reason: %s", msg, e.getMessage() ) );
        if ( errIfMissing ) {
          throw new IllegalStateException( msg, e );
        }
      }
    }
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
  public static void setProperty( final String name, final String value ) {
    if ( name != null ) {
      if ( value != null ) {
        System.getProperties().setProperty( name, value );
      } else {
        System.getProperties().remove( name );
      }
    }
  }

}
