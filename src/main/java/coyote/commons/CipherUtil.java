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

import java.util.HashMap;
import java.util.Map;

import coyote.commons.cipher.BlowfishCipher;
import coyote.commons.cipher.Cipher;
import coyote.commons.cipher.NullCipher;
import coyote.commons.cipher.XTEACipher;
import coyote.commons.codec.Base32;


/**
 * This is a collection of utilities to assist in obfuscating data.
 * 
 * <p>This is not intended to be state of the art encryption, but a set of 
 * portable algorithms which can assist in obfuscating text in a reasonably 
 * secure manner. This is adequate for securing data in configuration files.</p>
 * 
 * <p>This class come in handy when the application may be exported overseas as 
 * this class uses well-known algorithms with key sizes which fall under the 
 * limit established by the government.</p> 
 */
public class CipherUtil {

  private static final String SECRET = "coyotecliqs";
  private static final String DEFAULT = "default";
  private static String defaultCipherName = DEFAULT;

  private static final Map<String, Cipher> cipherMap = new HashMap<String, Cipher>();

  static {
    register( new BlowfishCipher() );
    register( new NullCipher() );
    register( new XTEACipher() );
  }




  /**
   * @param cipher
   */
  public static void register( Cipher cipher ) {
    if ( cipher != null && cipher.getName() != null ) {
      cipherMap.put( cipher.getName().toLowerCase(), cipher );
    }
  }




  /**
   * Encrypt the given string.
   * 
   * @param token the string to encrypt
   * 
   * @return encrypted text string suitable for use in any text file
   */
  public static String encrypt( String token ) {

    // Create a new instance of the Cipher
    Cipher cipher = CipherUtil.getCipher( defaultCipherName );

    // Initialize the cipher with our secret key here we just use the UTF16 
    // encoding of our key string
    cipher.init( StringUtil.getBytesUtf8( SECRET ) );

    // Encrypt the text with the UTF16 encoded bytes our our clear text string 
    byte[] cipherdata = cipher.encrypt( StringUtil.getBytesUtf16( token ) );

    // Return the base32 encoded ciphertext
    return new Base32().encodeAsString( cipherdata );
  }




  /**
   * Decrypt the given string.
   * 
   * @param ciphertext the encrypted string to decrypt
   * 
   * @return The decrypted string
   */
  public static String decrypt( String ciphertext ) {

    // Create a new instance of the Cipher to decrypt our data
    Cipher cipher = CipherUtil.getCipher( defaultCipherName );

    // Initialize the cipher with our secret key
    cipher.init( StringUtil.getBytesUtf8( SECRET ) );

    // decode the bytes
    byte[] cipherdata = new Base32().decode( ciphertext );

    // Decrypt the data  
    byte[] cleardata = cipher.decrypt( cipherdata );

    // return the UTF16 encoded string
    return StringUtil.newStringUtf16( cleardata );
  }




  public static Cipher getCipher( String name ) {
    if ( name != null ) {
      Cipher retval = cipherMap.get( name.toLowerCase() );
      if ( retval != null ) {
        return retval.getNewInstance();
      }
    }
    return null;
  }




  /**
   * @return the name of the default cipher
   */
  public static String getDefaultCipherName() {
    return defaultCipherName;
  }




  /**
   * @param name the cipher name to set as the default
   */
  public static void setDefaultCipherName( String name ) {
    CipherUtil.defaultCipherName = name;
  }

}
