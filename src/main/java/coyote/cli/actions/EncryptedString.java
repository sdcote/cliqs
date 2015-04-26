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

import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import coyote.commons.CipherUtil;


/**
 * Example of a simple action - encrypt a token.
 * 
 * Use this action to generate encrypted values for the property files.
 */
public class EncryptedString extends AbstractAction {

	private static final String OPT_TOKEN = "token";

	String token = null;




	/**
	 * @see coyote.cli.actions.AbstractAction#buildOptions(org.apache.commons.cli.Options)
	 */
	@Override
	@SuppressWarnings("static-access")
	public void buildOptions(Options o) {

		// If there is not an ID option, create one for the CLI to use when
		// parsing command arguments
		if (!o.hasOption(OPT_TOKEN)) {
			o.addOption(OptionBuilder.hasArg().isRequired(false).withArgName("text").withType(String.class).withDescription("The string token to parse or process.").create(OPT_TOKEN));
		}
	}




	/**
	 * @see coyote.cli.actions.AbstractAction#getHelp()
	 */
	@Override
	public String getHelp() {
		StringBuffer b = new StringBuffer();
		b.append("Encrypt a token for the properties file.");
		return b.toString();
	}




	@Override
	public void validate() throws ActionException {
		token = getCommandLineValue(OPT_TOKEN);
	}




	@Override
	public void execute() {

		String cipherText = null;

		if (token == null) {
			exit("No arguments specified" + LF + getHelp(), 1);
		} else {
			try {
				cipherText = CipherUtil.encrypt(token);
			} catch (Exception e) {
				error(e);
			}
		}
		outputLine("Token '" + token + "' encrypts to '" + cipherText + "'");
	}

}
