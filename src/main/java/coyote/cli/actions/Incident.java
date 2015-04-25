/*
 *
 */
package coyote.cli.actions;

import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

/**
 * 
 */
public class Incident extends RestAction {
	public static final String OPT_ID = "id";

	String scheme = null;
	String host = null;
	int port = 80;
	String uri = null;
	String user = null;
	String password = null;

	/**
	 * Basic constructor
	 */
	public Incident() {

		// This class need a specific URI
		uri = "/api/now/v1/table/incident";
	}

	/**
	 * @see coyote.cli.actions.AbstractAction#getHelp()
	 */
	@Override
	public String getHelp() {
		StringBuffer b = new StringBuffer();
		// _______----:----1----:----2----:----3----:----4----:----5----:----6----:----7----:----8
		b.append("Get an incident record.\r\n");
		b.append("    Specify an (-id) of an incident to retrieve.\r\n");
		return b.toString();
	}

	/**
	 * @see coyote.cli.actions.AbstractAction#buildOptions(org.apache.commons.cli.Options)
	 */
	@Override
	@SuppressWarnings("static-access")
	public void buildOptions(Options o) {

		// If there is not an ID option, create one for the CLI to use when
		// parsing command arguments
		if (!o.hasOption("id")) {
			o.addOption(OptionBuilder.hasArg().isRequired(false)
					.withArgName("text").withType(String.class)
					.withDescription("The identifier to query.").create(OPT_ID));
		}
	}

	/**
	 * @see coyote.cli.actions.AbstractAction#validate()
	 */
	@Override
	public void validate() throws ActionException {

		// Get our configured values from the system properties
		scheme = getProperty(SAAS_SCHEME);
		host = getProperty(SAAS_HOST);
		port = Integer.parseInt(getProperty(SAAS_PORT));
		user = getProperty(SAAS_USER);
		password = getEncryptedProperty(SAAS_PASS);

		final String incidentId = getCommandLineValue(OPT_ID);

		// if no arguments are given, just display a help message
		if (incidentId == null) {
			exit(getHelp(), 1);
		}

		// This is where we would use the ID from the command line to create
		// the proper URI e.g. "/api/now/v1/table/incident/4399945";
		// https://nationwide1pov.service-now.com/api/now/table/{tableName}/{sys_id}

		if (isDebug()) {
			debug("Getting Incident " + incidentId);
			debug("Using a SaaS user of: " + user);
			// debug("Pass: " + password); // Never do this!
		}
	}

	/**
	 * This executes the action with the currently set attributes.
	 * 
	 * @throws ActionException
	 * 
	 * @see coyote.cli.actions.Action#execute()
	 */
	@Override
	public void execute() throws ActionException {

		// All we do now is call the base class method to get the body of the
		// response
		try {
			String result = super.httpGet(scheme, host, port, uri, user,
					password);
			System.out.println("Result==> " + result);
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

}
