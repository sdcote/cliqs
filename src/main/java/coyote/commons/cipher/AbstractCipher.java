/*
 * Copyright (c) 2014 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and initial implementation
 */
package coyote.commons.cipher;

/**
 * This class contains some useful utilities for our ciphers.
 */
public abstract class AbstractCipher implements Cipher {

  /**
   * Pad the given the data to the given block size according to RFC 1423.
   * 
   * <p>First the data is padded to blocks of data using a PKCS5 DES CBC
   * encryption padding scheme described in section 1.1 of RFC-1423.</p>
   * 
   * <p>The last byte of the stream is ALWAYS the number of bytes added to the 
   * end of the data. If the data ends on a boundary, then there will be eight
   * bytes of padding:<code><pre>
   * 88888888 - all of the last block is padding.
   * X7777777 - the last seven bytes are padding.
   * XX666666 - the last six bytes are padding.
   * XXX55555 - etc.
   * XXXX4444 - etc.
   * XXXXX333 - etc.
   * XXXXXX22 - etc.
   * XXXXXXX1 - only the last byte is padding.</pre></code></p>
   * 
   * <p>According to RFC1423 section 1.1:<blockquote>The input to the DES CBC
   * encryption process shall be padded to a multiple of 8 octets, in the
   * following manner. Let n be the length in octets of the input. Pad the 
   * input by appending 8-(n mod 8) octets to the end of the message, each 
   * having the value 8-(n mod 8), the number of octets being added. In 
   * hexadecimal, the possible paddings are: 01, 0202, 030303, 04040404, 
   * 0505050505, 060606060606, 07070707070707, and 0808080808080808. All input 
   * is padded with 1 to 8 octets to produce a multiple of 8 octets in length. 
   * The padding can be removed unambiguously after decryption.</blockquote></p>
   *  
   * @param data The source data
   * 
   * @return a new array of data containing the original data and the padding
   * 
   * @see #trim(byte[])
   */
  public static byte[] pad( final byte[] data ) {
    // pad the data as necessary using a PKCS5 (or RFC1423) padding scheme
    int padding = 8 - ( data.length % 8 );

    // There is alway padding even it it is not needed
    if ( padding == 0 )
      padding = 8;

    // create the return value
    final byte[] retval = new byte[data.length + padding];

    // copy the original data
    System.arraycopy( data, 0, retval, 0, data.length );

    // add the padding
    for ( int x = data.length; x < retval.length; retval[x++] = (byte)padding );

    return retval;
  }




  /**
   * Remove padding that is at the end of the data using RFC 1423.
   * 
   * @param data the byte array to trim.
   * 
   * @return The trimmed array.
   * 
   * @see #pad(byte[])
   */
  public static byte[] trim( final byte[] data ) {
    if ( data.length > 0 ) {
      final int padding = data[data.length - 1];

      if ( ( padding > 0 ) && ( padding < 9 ) ) {
        final byte[] retval = new byte[data.length - padding];
        System.arraycopy( data, 0, retval, 0, retval.length );
        return retval;
      }
    }

    return data;
  }

}
