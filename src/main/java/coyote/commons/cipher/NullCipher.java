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
 * The Cipher class models the basic cipher algorithm for the platform when the 
 * platform is distributed overseas and encryption technologies are not allowed
 * by the government.
 */
public class NullCipher extends AbstractCipher implements Cipher {
  private static final String CIPHER_NAME = "Null";




  /**
   * Standard no-arg constructor.
   */
  public NullCipher() {
    super();
  }




  /**
   * Returns the data passed to this method.
   * 
   * @param data The data to ignore.
   * 
   * @return The data passed to this method.
   */
  @Override
  public byte[] decrypt( final byte[] data ) {
    return data;
  }




  /**
   * Returns the data passed to this method.
   * 
   * @param bytes The data to ignore.
   * 
   * @return The data passed to this method.
   */
  @Override
  public byte[] encrypt( final byte[] bytes ) {
    return bytes;
  }




  /**
   * @see coyote.commons.cipher.Cipher#getBlockSize()
   */
  @Override
  public int getBlockSize() {
    return 8; // pretend to be a block cipher
  }




  /**
   * @see coyote.commons.cipher.Cipher#getName()
   */
  @Override
  public String getName() {
    return NullCipher.CIPHER_NAME;
  }




  /**
   * @see coyote.commons.cipher.Cipher#getNewInstance()
   */
  @Override
  public Cipher getNewInstance() {
    return new NullCipher();
  }




  /**
   * Initialize the algorithm with a key to be used for en/de-cryption.
   * 
   * @param key The key to use for all operations.
   */
  @Override
  public void init( final byte[] key ) {
    // don't care
  }

}