package coyote.cli.actions;

public class ActionException extends Exception {

	private static final long serialVersionUID = -2613578396611705199L;




	public ActionException() {
	}




	public ActionException(String message) {
		super(message);
	}




	public ActionException(Throwable cause) {
		super(cause);
	}




	public ActionException(String message, Throwable cause) {
		super(message, cause);
	}

}
