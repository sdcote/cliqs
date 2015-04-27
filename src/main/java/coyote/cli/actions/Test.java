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

/**
 * This is a simple action which will change over time in the source repository 
 * as it contains logic currently under test.
 */
public class Test extends AbstractAction {

  @Override
  public void execute() {

    outputLine( "Testing..." );

    // get all the tables owned by the CSDB user account
    //getCommonSharedDAO().getAllTables(getProperty(CSDB_DB_USER));

  } // execute




  /**
   * @see coyote.cli.actions.AbstractAction#getHelp()
   */
  @Override
  public String getHelp() {
    final StringBuffer b = new StringBuffer();
    b.append( "Only used during testing of various actions." );
    return b.toString();
  }

}
