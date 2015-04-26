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

import java.io.UnsupportedEncodingException;


/**
 * Simply a set of useful string utilities for this project.
 */
public class StringUtil {

  /** Eight-bit Unicode Transformation Format. */
  public static final String UTF_8 = "UTF-8";

  /** Sixteen-bit Unicode Transformation Format, The byte order specified by a mandatory initial byte-order mark (either order accepted on input, big-endian used on output) */
  public static final String UTF_16 = "UTF-16";




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
    return !StringUtil.isBlank( str );
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




  /**
   * Constructs a new {@code String} by decoding the specified array of bytes 
   * using the given charset.
   * 
   * <p>This method catches {@link UnsupportedEncodingException} and re-throws 
   * it as {@link IllegalStateException}, which should never happen for a 
   * required charset name. Use this method when the encoding is required to be 
   * in the JRE.</p>
   * 
   * @param bytes The bytes to be decoded into characters, may be {@code null}
   * @param charsetName The name of a required {@link java.nio.charset.Charset}
   * 
   * @return A new {@code String} decoded from the specified array of bytes 
   * using the given charset, or {@code null} if the input byte array was 
   * {@code null}.
   * 
   * @throws IllegalStateException Thrown when a {@link UnsupportedEncodingException} is caught, which should never happen for a required charset name.
   * 
   * @see String#String(byte[], String)
   */
  public static String newString( byte[] bytes, String charsetName ) {
    if ( bytes == null ) {
      return null;
    }
    try {
      return new String( bytes, charsetName );
    } catch ( UnsupportedEncodingException e ) {
      throw StringUtil.newIllegalStateException( charsetName, e );
    }
  }




  /**
   * Constructs a new {@code String} by decoding the specified array of 
   * bytes using the UTF-8 charset.
   * 
   * @param bytes The bytes to be decoded into characters
   * 
   * @return A new {@code String} decoded from the specified array of bytes 
   * using the UTF-8 charset, or {@code null} if the input byte array was 
   * {@code null}</p>.
   * 
   * @throws IllegalStateException Thrown when a {@link UnsupportedEncodingException} is caught, which should never happen since the charset is required.
   */
  public static String newStringUtf8( byte[] bytes ) {
    return StringUtil.newString( bytes, StringUtil.UTF_8 );
  }




  /**
   * Constructs a new {@code String} by decoding the specified array of bytes 
   * using the UTF-16 charset.
   * 
   * @param bytes The bytes to be decoded into characters
   * 
   * @return A new {@code String} decoded from the specified array of bytes 
   * using the UTF-16 charset or {@code null} if the input byte array was 
   * {@code null}.
   * 
   * @throws IllegalStateException Thrown when a {@link UnsupportedEncodingException} is caught, which should never happen since the charset is required.
   */
  public static String newStringUtf16( byte[] bytes ) {
    return StringUtil.newString( bytes, StringUtil.UTF_16 );
  }




  private static IllegalStateException newIllegalStateException( String charsetName, UnsupportedEncodingException e ) {
    return new IllegalStateException( charsetName + ": " + e );
  }




  /**
   * Encodes the given string into a sequence of bytes using the UTF-16 charset, 
   * storing the result into a new byte array.
   * 
   * @param string the String to encode, may be {@code null}
   * 
   * @return encoded bytes, or {@code null} if the input string was {@code null}
   * 
   * @throws IllegalStateException Thrown when the charset is missing, which should be never according the the Java specification.
   */
  public static byte[] getBytesUtf16( String string ) {
    return StringUtil.getBytesUnchecked( string, StringUtil.UTF_16 );
  }




  /**
   * Encodes the given string into a sequence of bytes using the UTF-8 charset, 
   * storing the result into a new byte array.
   * 
   * @param string the String to encode, may be {@code null}
   * 
   * @return encoded bytes, or {@code null} if the input string was {@code null}
   * 
   * @throws IllegalStateException Thrown when the charset is missing, which should be never according the the Java specification.
   */
  public static byte[] getBytesUtf8( String string ) {
    return StringUtil.getBytesUnchecked( string, StringUtil.UTF_8 );
  }




  /**
   * Encodes the given string into a sequence of bytes using the named charset, 
   * storing the result into a new byte array.
   * 
   * <p>This method catches {@link UnsupportedEncodingException} and rethrows 
   * it as {@link IllegalStateException}, which should never happen for a 
   * required charset name. Use this method when the encoding is required to be 
   * in the JRE.</p>
   * 
   * @param string the String to encode, may be {@code null}
   * @param charsetName The name of a required {@link java.nio.charset.Charset}
   * 
   * @return encoded bytes, or {@code null} if the input string was {@code null}
   * 
   * @throws IllegalStateException Thrown when a {@link UnsupportedEncodingException} is caught, which should never happen for a required charset name.
   */
  public static byte[] getBytesUnchecked( String string, String charsetName ) {
    if ( string == null ) {
      return null;
    }
    try {
      return string.getBytes( charsetName );
    } catch ( UnsupportedEncodingException e ) {
      throw StringUtil.newIllegalStateException( charsetName, e );
    }
  }

}
