package coyote.commons.codec;

/**
 * Thrown when there is a failure condition during the decoding process. 
 * 
 * <p>This exception is thrown when a {@link Decoder} encounters a decoding 
 * specific exception such as invalid data, or characters outside of the 
 * expected range.</p>
 */
public class DecoderException extends Exception {

  private static final long serialVersionUID = -4661162681768236603L;




  /**
     * Constructs a new exception with <code>null</code> as its detail message. 
     * 
     * <p>The cause is not initialized, and may subsequently be initialized by 
     * a call to {@link #initCause}.</p>
     */
  public DecoderException() {
    super();
  }




  /**
   * Constructs a new exception with the specified detail message. 
   * 
   * <p>The cause is not initialized, and may subsequently be initialized by 
   * a call to {@link #initCause}.</p>
   * 
   * @param message The detail message which is saved for later retrieval by 
   * the {@link #getMessage()} method.
   */
  public DecoderException( final String message ) {
    super( message );
  }




  /**
   * Constructs a new exception with the specified detail message and cause.
   * 
   * <p>Note that the detail message associated with <code>cause</code> is 
   * not automatically incorporated into this exception's detail message.</p>
   * 
   * @param message The detail message which is saved for later retrieval by 
   * the {@link #getMessage()} method.
   * @param cause The cause which is saved for later retrieval by the 
   * {@link #getCause()} method. A <code>null</code> value is permitted, and 
   * indicates that the cause is nonexistent or unknown.
   */
  public DecoderException( final String message, final Throwable cause ) {
    super( message, cause );
  }




  /**
   * Constructs a new exception with the specified cause and a detail message 
   * of <code>(cause==null ? null : cause.toString())</code> (which typically 
   * contains the class and detail message of <code>cause</code>).
   * 
   * <p>This constructor is useful for exceptions that are little more than 
   * wrappers for other throwables.</p>
   * 
   * @param cause The cause which is saved for later retrieval by the 
   * {@link #getCause()} method. A <code>null</code> value is permitted, and 
   * indicates that the cause is nonexistent or unknown.
   */
  public DecoderException( final Throwable cause ) {
    super( cause );
  }
}
