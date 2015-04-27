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




  public ActionException() {}




  public ActionException( final String message ) {
    super( message );
  }




  public ActionException( final String message, final Throwable cause ) {
    super( message, cause );
  }




  public ActionException( final Throwable cause ) {
    super( cause );
  }

}
