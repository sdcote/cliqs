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

import org.apache.commons.cli.Options;

public interface Action {

	/**
	 * This method is called by the CLI to validate arguments and prepare the 
	 * action for execution.
	 * 
	 * <p>All implementations of this method can assume the CLI has parsed the
	 * command line argument into the CommandLine reference and use it as the 
	 * primary source of arguments.</p>
	 * 
	 * @throws ActionException of the execution encountered an error in processing. 
	 */
	public void validate() throws ActionException;




	/**
	 * This method is called by the CLI to execute the action requested by the
	 * user.
	 * 
	 * <p>All implementations of this method can assume the CLI has parsed the
	 * command line argument into the CommandLine reference and use it as the 
	 * primary source of arguments.</p>
	 * 
	 * @throws ActionException of the execution encountered an error in execution. 
	 */
	public void execute() throws ActionException;




	/**
	 * Called to display detailed help for this particular action.
	 * 
	 * <p>Keep in mind that most DOS windows are 80x24 and Unix terminals can 
	 * be any size.</p>
	 * 
	 * @return Help text to be sent to SYSOUT
	 */
	public String getHelp();




	/**
	 * Close the action and clean-up any resources it may have allocated.
	 * 
	 * <p>No exceptions are thrown as all actions handle all exceptions during 
	 * closing.</p>
	 */
	public void close();






	/**
	 * Add supported options to the given options collection.
	 * 
	 * <p>This uses the visitor pattern to give all registered actions the 
	 * ability to register options with the CLI argument processor.</p>
	 * 
	 * <p>It is strongly recommended that all actions first query the options 
	 * argument to see if the argument is already there. Common arguments like 
	 * "id" or "file" may have already been added. Also, consider generic 
	 * descriptions so other actions can use the argument option.</p>
	 * 
	 * @param o The options object to which options are contributed
	 */
	public void buildOptions(Options o);
	
}
