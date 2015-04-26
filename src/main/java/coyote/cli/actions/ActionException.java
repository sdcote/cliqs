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
