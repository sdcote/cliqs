package coyote.commons.codec;

/**
 * Provides the highest level of abstraction for Encoders. 
 * 
 * <p>This is the sister interface of {@link Decoder}.</p>
 * 
 * <p>Every implementation of Encoder provides this common generic interface 
 * which allows a user to pass a generic Object to any Encoder implementation 
 * in the codec package.</p>
 */
public interface Encoder {

  /**
   * Encodes an "Object" and returns the encoded content as an Object.
   * 
   * <p>The Objects here may just be {@code byte[]} or {@code String}s 
   * depending on the implementation used.</p>
   *   
   * @param source An object to encode
   * 
   * @return An "encoded" Object
   * 
   * @throws EncoderException an encoder exception is thrown if the encoder 
   * experiences a failure condition during the encoding process.
   */
  Object encode( Object source ) throws EncoderException;
}
