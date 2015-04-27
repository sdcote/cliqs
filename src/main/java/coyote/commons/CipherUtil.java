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

  private static final String DEFAULT_SECRET = "coyotecliqs";
  private static final String DEFAULT_CIPHER = "blowfish";
  private static String defaultSecret = DEFAULT_SECRET;
  private static String defaultCipherName = DEFAULT_CIPHER;

  private static final Map<String, Cipher> cipherMap = new HashMap<String, Cipher>();

  static {
    register( new BlowfishCipher() );
    register( new NullCipher() );
    register( new XTEACipher() );
  }




  /**
   * Decrypt the given string.
   * 
   * @param ciphertext the encrypted string to decrypt
   * 
   * @return The decrypted string
   */
  public static String decrypt( final String ciphertext ) {

    // Create a new instance of the Cipher to decrypt our data
    final Cipher cipher = CipherUtil.getCipher( defaultCipherName );

    // Initialize the cipher with our secret key
    cipher.init( StringUtil.getBytesUtf8( DEFAULT_SECRET ) );

    // decode the bytes
    final byte[] cipherdata = new Base32().decode( ciphertext );

    // Decrypt the data  
    final byte[] cleardata = cipher.decrypt( cipherdata );

    // return the UTF16 encoded string
    return StringUtil.newStringUtf16( cleardata );
  }




  /**
   * Encrypt the given string.
   * 
   * @param token the string to encrypt
   * 
   * @return encrypted text string suitable for use in any text file
   */
  public static String encrypt( final String token ) {

    // Create a new instance of the Cipher
    final Cipher cipher = CipherUtil.getCipher( defaultCipherName );

    // Initialize the cipher with our secret key here we just use the UTF16 
    // encoding of our key string
    cipher.init( StringUtil.getBytesUtf8( DEFAULT_SECRET ) );

    // Encrypt the text with the UTF16 encoded bytes our our clear text string 
    final byte[] cipherdata = cipher.encrypt( StringUtil.getBytesUtf16( token ) );

    // Return the base32 encoded ciphertext
    return new Base32().encodeAsString( cipherdata );
  }




  public static Cipher getCipher( final String name ) {
    if ( name != null ) {
      final Cipher retval = cipherMap.get( name.toLowerCase() );
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
   * @return the default secret used as an initialization vector or key
   */
  public static String getDefaultSecret() {
    return defaultSecret;
  }




  /**
   * @param cipher
   */
  public static void register( final Cipher cipher ) {
    if ( ( cipher != null ) && ( cipher.getName() != null ) ) {
      cipherMap.put( cipher.getName().toLowerCase(), cipher );
    }
  }




  /**
   * @param name the cipher name to set as the default
   */
  public static void setDefaultCipherName( final String name ) {
    CipherUtil.defaultCipherName = name;
  }




  /**
   * @param token the default secret to set as an initialization vector or key
   */
  public static void setDefaultSecret( final String token ) {
    CipherUtil.defaultSecret = token;
  }

}
