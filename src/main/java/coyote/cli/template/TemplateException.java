package coyote.cli.template;

/**
 * Exception thrown when there is a problem with parsing template operations.
 */
public final class TemplateException extends Exception {
	/** */
	private static final long serialVersionUID = -7397711944655415190L;
	private String context = null;




	/**
	 * Constructor
	 */
	public TemplateException() {
		super();
	}




	/**
	 * Constructor
	 *
	 * @param message Error message
	 */
	public TemplateException(String message) {
		super(message);
	}




	/**
	 * Constructor TemplateException
	 *
	 * @param message
	 * @param context
	 */
	public TemplateException(String message, String context) {
		super(message);

		this.context = context;
	}




	/**
	 * Constructor
	 *
	 * @param message Error message
	 * @param excptn
	 */
	public TemplateException(String message, Throwable excptn) {
		super(message, excptn);
	}




	/**
	 * Constructor
	 *
	 * @param excptn
	 */
	public TemplateException(Throwable excptn) {
		super(excptn);
	}




	/**
	 * Method getContext
	 *
	 * @return the area surrounding where the exception occurred
	 */
	public String getContext() {
		return context;
	}

}