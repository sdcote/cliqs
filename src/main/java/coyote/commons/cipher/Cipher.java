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
 * The Cipher class models a simple encryption utility.
 */
public interface Cipher {
  /**
   * Returns the decrypted bytes for the given enciphered bytes.
   * 
   * @param data The data to decipher.
   * 
   * @return The decrypted data.
   * 
   * @see #encrypt(byte[])
   */
  public byte[] decrypt( final byte[] data );




  /**
   * Returns the encrypted bytes for the given bytes.
   * 
   * @param bytes The data to encipher.
   * 
   * @return The encrypted data.
   */
  public byte[] encrypt( byte[] bytes );




  /**
   * To help block ciphers receive data with callers need to know the size of
   * blocks to use.
   * 
   * <p>This method allows callers to always send the properly-sized chunks of
   * data to be processed.</p>
   * 
   * @return The current block size of the cipher. May return 0 (zero).
   */
  public int getBlockSize();




  /**
   * Returns the name of the cipher algorithm.
   * 
   * @return the name of the cipher algorithm.
   */
  public String getName();




  /**
   * Get a fresh instance of this cipher.
   * 
   * <p>It is generally not a good idea to share ciphers as they are usually 
   * stateful and prior operations will have left the instance of the cipher in 
   * an unknown state. Get a new cipher instance each time.</p>
   * 
   * @return A new instance of the cipher ready to initialize and use.
   */
  public Cipher getNewInstance();




  /**
   * Initialize the algorithm with a key to be used for the cipher.
   * 
   * @param key The key to use for all operations.
   */
  public void init( final byte[] key );
}
