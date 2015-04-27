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
package coyote.cli.template;

import java.io.Reader;


/**
 * SimpleReader is a scaled-down version, slightly faster version of
 * StringReader.
 */
public final class SimpleReader extends Reader {

  /** Field string */
  String string;

  /** Field length */
  int length;

  /** Field next */
  int next;

  /** Field mark */
  int mark;




  /**
   * Constructor FastReader
   *
   * @param text The string to be read by the reader
   */
  public SimpleReader( final String text ) {
    next = 0;
    mark = 0;
    string = text;
    length = text.length();
  }




  /**
   * Method close
   */
  @Override
  public void close() {
    string = null;
  }




  /**
   * Method mark
   *
   * @param limit
   */
  @Override
  public void mark( final int limit ) {
    mark = next;
  }




  /**
   * Method markSupported
   *
   * @return if the reader support marking (always true)
   */
  @Override
  public boolean markSupported() {
    return true;
  }




  /**
   * Method read
   *
   * @return the next character
   */
  @Override
  public int read() {
    return ( next < length ) ? string.charAt( next++ ) : -1;
  }




  /**
   * Method read
   *
   * @param buffer the destination character array
   * @param offset
   * @param length
   *
   * @return the character at the offset
   */
  @Override
  public int read( final char buffer[], final int offset, final int length ) {
    if ( length == 0 ) {
      return 0;
    }

    if ( next >= length ) {
      return -1;
    } else {
      final int bytesToRead = Math.min( length - next, length );
      string.getChars( next, next + bytesToRead, buffer, offset );

      next += bytesToRead;

      return bytesToRead;
    }
  }




  /**
   * Method ready
   *
   * @return if the reader is read to read (always true)
   */
  @Override
  public boolean ready() {
    return true;
  }




  /**
   * Method reset
   */
  @Override
  public void reset() {
    next = mark;
  }




  /**
   * Method skip
   *
   * @param amount
   *
   * @return The number skipped
   */
  @Override
  public long skip( final long amount ) {
    if ( next >= length ) {
      return 0L;
    } else {
      final long skipped = Math.min( length - next, amount );
      next += skipped;

      return skipped;
    }
  }
}
