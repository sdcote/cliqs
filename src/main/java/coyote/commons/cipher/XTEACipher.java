package coyote.commons.cipher;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * The XTEACipher class models the Extended Tiny Encryption Algorithm (XTEA).
 */
public class XTEACipher extends AbstractCipher implements Cipher {
  private static final int ROUNDS = 32;// iteration count (cycles)
  private static final int BLOCK_SIZE = 8; // bytes in a data block (64 bits)
  private static final int DELTA = 0x9E3779B9;
  private static final int D_SUM = 0xC6EF3720;
  private static final byte[] EMPTY_BYTES = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
  private static final String UTF16 = "UTF-16";
  private static final String CIPHER_NAME = "XTEA";




  /**
   * Decrypt one block of data with XTEA algorithm.
   * 
   * @param data the byte array from which to read the enciphered data.
   * @param offset offset in the array to retrieve the next block to decrypt.
   * @param subkeys The subkeys to use in the decryption.
   * 
   * @return a block of clear data.
   */
  public static byte[] decrypt( final byte[] data, int offset, final int[] subkeys ) {
    // Pack bytes into integers
    int v0 = ( ( data[offset++] ) << 24 ) | ( ( data[offset++] & 0xFF ) << 16 ) | ( ( data[offset++] & 0xFF ) << 8 ) | ( ( data[offset++] & 0xFF ) );

    int v1 = ( ( data[offset++] ) << 24 ) | ( ( data[offset++] & 0xFF ) << 16 ) | ( ( data[offset++] & 0xFF ) << 8 ) | ( ( data[offset] & 0xFF ) );

    int n = XTEACipher.ROUNDS, sum;

    sum = XTEACipher.D_SUM;

    while ( n-- > 0 ) {
      v1 -= ( ( ( v0 << 4 ) ^ ( v0 >>> 5 ) ) + v0 ) ^ ( sum + subkeys[( sum >>> 11 ) & 3] );
      sum -= XTEACipher.DELTA;
      v0 -= ( ( ( v1 << 4 ) ^ ( v1 >>> 5 ) ) + v1 ) ^ ( sum + subkeys[sum & 3] );
    }

    // Unpack and return
    int outOffset = 0;
    final byte[] out = new byte[XTEACipher.BLOCK_SIZE];
    out[outOffset++] = (byte)( v0 >>> 24 );
    out[outOffset++] = (byte)( v0 >>> 16 );
    out[outOffset++] = (byte)( v0 >>> 8 );
    out[outOffset++] = (byte)( v0 );

    out[outOffset++] = (byte)( v1 >>> 24 );
    out[outOffset++] = (byte)( v1 >>> 16 );
    out[outOffset++] = (byte)( v1 >>> 8 );
    out[outOffset] = (byte)( v1 );

    return out;
  }




  /**
   * Returns the decrypted bytes for the given enciphered data and key string.
   * 
   * <p>First, the key is converted to UTF-16 encoding and passed through the
   * Virtual Machines MD5 message digest and the first 16 bytes of the digest 
   * are used to represent the key.<p>
   *  
   * <p>Next, the resulting blocked data is run through 32-round Feistel cipher 
   * which uses operations from mixed (orthogonal) algebraic groups - XORs and 
   * additions in this case. It decrypts 64 data bits at a time using the 
   * 128-bit key.</p>
   * 
   * <p>Finally, the data is stripped of padding using a PKCS5 DES CBC padding 
   * scheme described in section 1.1 of RFC-1423.</p>
   * 
   * @param data The data to decipher.
   * @param key The key to use in the generation of the deciphered text.
   * 
   * @return The decrypted data.
   * 
   * @see #encrypt(byte[], String)
   */

  public static byte[] decrypt( final byte[] data, final String key ) {
    byte[] retval = new byte[data.length];
    final int[] subKeys = XTEACipher.generateSubKeys( XTEACipher.getKeyBytes( key ) );

    for ( int x = 0; x < data.length; x += XTEACipher.BLOCK_SIZE ) {
      final byte[] block = XTEACipher.decrypt( data, x, subKeys );
      System.arraycopy( block, 0, retval, x, block.length );
    }

    final int padding = retval[retval.length - 1];

    if ( ( padding > 0 ) && ( padding < 9 ) ) {
      final byte[] tmp = new byte[retval.length - padding];
      System.arraycopy( retval, 0, tmp, 0, tmp.length );
      retval = tmp;
    }

    return retval;
  }




  /**
   * Decrypt the given encrypted data using the given key.
   * 
   * <p>This method will decrypt the given data and parse the resulting 
   * decrypted bytes into a string assuming UTF16 encoding.
   * 
   * @param data The encrypted data.
   * @param key The key to use during decryption.
   * 
   * @return The string resulting from the decrypted bytes.
   */
  public static String decryptString( final byte[] data, final String key ) {
    try {
      return new String( XTEACipher.decrypt( data, key ), XTEACipher.UTF16 );
    } catch ( final UnsupportedEncodingException e ) {
      e.printStackTrace();
    }
    return new String( data );
  }




  /**
   * Encrypt one block of data with XTEA algorithm.
   * 
   * @param data the byte array from which to read the clear data.
   * @param offset offset in the array to retrieve the next block to encrypt. 
   * @param subkeys The subkeys to use in the encryption.
   * 
   * @return a block of enciphered data.
   */
  public static byte[] encrypt( final byte[] data, int offset, final int[] subkeys ) {
    // Pack bytes into integers
    int v0 = ( ( data[offset++] ) << 24 ) | ( ( data[offset++] & 0xFF ) << 16 ) | ( ( data[offset++] & 0xFF ) << 8 ) | ( ( data[offset++] & 0xFF ) );
    int v1 = ( ( data[offset++] ) << 24 ) | ( ( data[offset++] & 0xFF ) << 16 ) | ( ( data[offset++] & 0xFF ) << 8 ) | ( ( data[offset] & 0xFF ) );

    int n = XTEACipher.ROUNDS, sum;

    sum = 0;
    while ( n-- > 0 ) {
      v0 += ( ( ( v1 << 4 ) ^ ( v1 >>> 5 ) ) + v1 ) ^ ( sum + subkeys[sum & 3] );
      sum += XTEACipher.DELTA;
      v1 += ( ( ( v0 << 4 ) ^ ( v0 >>> 5 ) ) + v0 ) ^ ( sum + subkeys[( sum >>> 11 ) & 3] );
    }

    // Unpack and return
    int outOffset = 0;
    final byte[] out = new byte[XTEACipher.BLOCK_SIZE];
    out[outOffset++] = (byte)( v0 >>> 24 );
    out[outOffset++] = (byte)( v0 >>> 16 );
    out[outOffset++] = (byte)( v0 >>> 8 );
    out[outOffset++] = (byte)( v0 );

    out[outOffset++] = (byte)( v1 >>> 24 );
    out[outOffset++] = (byte)( v1 >>> 16 );
    out[outOffset++] = (byte)( v1 >>> 8 );
    out[outOffset] = (byte)( v1 );

    return out;
  }




  /**
   * Returns the encrypted bytes for the given string.
   * 
   * <p>First, the key is converted to UTF-16 encoding and passed through the
   * Virtual Machines MD5 message digest and the first 16 bytes of the digest 
   * are used to represent the key.<p>
   *  
   * <p>Next the data is padded to 8-byte blocks of data using a PKCS5 DES CBC 
   * encryption padding scheme described in section 1.1 of RFC-1423.</p>
   * 
   * <p>Finally, the resulting blocked data is run through 32-round Feistel 
   * cipher which uses operations from mixed (orthogonal) algebraic groups - 
   * XORs and additions in this case. It encrypts 64 data bits at a time using 
   * the key.</p>
   * 
   * @param bytes The data to encipher.
   * @param key The key to use in the generation of the enciphered text.
   * 
   * @return The encrypted text as a byte array.
   */
  public static byte[] encrypt( final byte[] bytes, final String key ) {

    final XTEACipher cipher = new XTEACipher();

    // initialize with the key string
    cipher.init( XTEACipher.getKeyBytes( key ) );

    // Have the cipher encrypt the data
    return cipher.encrypt( bytes );

  }




  /**
   * Returns the encrypted bytes for the given data and key strings.
   * 
   * @param data The data to encipher.
   * @param key The key to use in the generation of the enciphered text.
   * 
   * @return The encrypted text as a byte array.
   * 
   * @see #encrypt(byte[], String)
   */
  public static byte[] encryptString( final String data, final String key ) {
    byte[] bytes = null;
    try {
      bytes = data.getBytes( XTEACipher.UTF16 );
    } catch ( final UnsupportedEncodingException e ) {
      e.printStackTrace();
    }

    return XTEACipher.encrypt( bytes, key );
  }




  /**
   * Generate XTEA subkeys for the cipher alogrithm.
   * 
   * @param key the key to use as the seed for subkey generation.
   * 
   * @return a 4-element integer array containing the generated keys. 
   */
  private static int[] generateSubKeys( final byte[] key ) {
    final int[] retval = new int[4];
    for ( int off = 0, i = 0; i < 4; i++ ) {
      retval[i] = ( ( key[off++] & 0xFF ) << 24 ) | ( ( key[off++] & 0xFF ) << 16 ) | ( ( key[off++] & 0xFF ) << 8 ) | ( ( key[off++] & 0xFF ) );
    }
    return retval;
  }




  /**
   * Generate a key from the given string.
   * 
   * <p>Return the first 16 bytes of the MD5 digest of the given key string 
   * encoded using UTF-16.</p>
   * 
   * @param key The string to use as the key.
   * 
   * @return Bytes suitable for use as an encryption key.
   */
  public static byte[] getKeyBytes( final String key ) {
    final byte[] retval = XTEACipher.EMPTY_BYTES;
    try {
      final MessageDigest md = MessageDigest.getInstance( "MD5" );
      md.update( key.getBytes( XTEACipher.UTF16 ) );
      final byte[] result = md.digest();
      for ( int x = 0; x < retval.length; x++ ) {
        retval[x] = result[x];
        if ( ( x + 1 ) > result.length ) {
          break;
        }
      }
    } catch ( final NoSuchAlgorithmException e ) {
      e.printStackTrace();
    } catch ( final UnsupportedEncodingException e ) {
      e.printStackTrace();
    }
    return retval;
  }

  private int[] subKeys = null;




  public XTEACipher() {

  }




  /**
   * @see coyote.commons.security.Cipher#decrypt(byte[])
   */
  @Override
  public byte[] decrypt( final byte[] data ) {
    final byte[] retval = new byte[data.length];

    for ( int x = 0; x < data.length; x += XTEACipher.BLOCK_SIZE ) {
      final byte[] block = XTEACipher.decrypt( data, x, subKeys );
      System.arraycopy( block, 0, retval, x, block.length );
    }

    return AbstractCipher.trim( retval );
  }




  /**
   * Encrypt the given data.
   * 
   * <p>This instance MUST be initialized prior to making this call.</p>
   * 
   * @see coyote.commons.security.Cipher#encrypt(byte[])
   */
  @Override
  public byte[] encrypt( final byte[] bytes ) {

    // pad the data using RFC-1423 scheme
    final byte[] data = AbstractCipher.pad( bytes );

    // create our return value
    final byte[] retval = new byte[data.length];

    // encrypt the data
    for ( int x = 0; x < bytes.length; x += XTEACipher.BLOCK_SIZE ) {
      final byte[] block = XTEACipher.encrypt( data, x, subKeys );
      System.arraycopy( block, 0, retval, x, block.length );
    }
    return retval;
  }




  /**
   * @see coyote.commons.security.Cipher#getBlockSize()
   */
  @Override
  public int getBlockSize() {
    return XTEACipher.BLOCK_SIZE;
  }




  /**
   * @see coyote.commons.security.Cipher#getName()
   */
  @Override
  public String getName() {
    return XTEACipher.CIPHER_NAME;
  }




  /**
   * @see coyote.commons.cipher.Cipher#getNewInstance()
   */
  @Override
  public Cipher getNewInstance() {
    return new XTEACipher();
  }




  /**
   * <p>The most common method for initializing this cipher is to pick a string 
   * and an encoding and convert the string to bytes using that encoding.</p>
   * 
   * @see coyote.commons.security.Cipher#init(byte[])
   */
  @Override
  public void init( final byte[] key ) {
    subKeys = XTEACipher.generateSubKeys( key );
  }

}
