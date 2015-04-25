package coyote.cli.actions;

/**
 * This is a simple action which will change over time in the source repository 
 * as it contains logic currently under test.
 */
public class Test extends AbstractAction {

	/**
	 * @see coyote.cli.actions.AbstractAction#getHelp()
	 */
	@Override
	public String getHelp() {
		StringBuffer b = new StringBuffer();
		b.append("Only used during testing of various actions.");
		return b.toString();
	}




	@Override
	public void execute() {

		outputLine("Testing...");

		// get all the tables owned by the CSDB user account
		//getCommonSharedDAO().getAllTables(getProperty(CSDB_DB_USER));

	} // execute

}
