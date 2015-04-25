package coyote.commons.codec;

/**
 * <p>Provides the highest level of abstraction for Decoders. 
 * 
 * <p>This is the sister interface of {@link Encoder}. All Decoders implement 
 * this common generic interface.</p>
 * 
 * <p>Allows a user to pass a generic Object to any Decoder implementation in 
 * the codec package.</p>
 * 
 * <p>One of the two interfaces at the center of the codec package.</p>
 */
public interface Decoder {

  /**
   * Decodes an "encoded" Object and returns a "decoded" Object.  
   * 
   * <p>Note that the implementation of this interface will try to cast the 
   * Object parameter to the specific type expected by a particular Decoder 
   * implementation. If a {@link ClassCastException} occurs this decode method 
   * will throw a DecoderException.</p>
   * 
   * @param source the object to decode
   * 
   * @return a 'decoded" object
   * 
   * @throws DecoderException a decoder exception can be thrown for any number 
   * of reasons. Some good candidates are that the parameter passed to this 
   * method is null, a param cannot be cast to the appropriate type for a 
   * specific encoder.
   */
  Object decode( Object source ) throws DecoderException;
}
