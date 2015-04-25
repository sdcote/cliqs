package coyote.cli;

import java.util.Map;

import coyote.cli.actions.Action;

public class Help {

	public static void display(CLI cli) {
		final StringBuffer b = new StringBuffer();
		// _______----:----1----:----2----:----3----:----4----:----5----:----6----:----7----:----8
		b.append("GET ");
		b.append(cli.getVersion());
		b.append("\r\n\r\n");
		b.append("This utility gets data from a ServiceNow instance.\r\n\r\n");
		b.append("Example usage:  get USER -id x123 -env DEV\r\n");
		b.append("The first argument is always a noun describing what to get. In the above, the\r\n");
		b.append("command will get USER records.\r\n");
		b.append(" -id   The identifier of the user to get.\r\n");
		b.append(" -env  The environment against which to run. Supported values are PROD, ST, UAT,\r\n");
		b.append("       and DEV (default).\r\n");
		b.append("\r\n");
		b.append("Not all nouns support all options. Usually if no options are provided the\r\n");
		b.append("supported options for that noun will be displayed (i.e. help for that noun).\r\n");
		b.append("\r\n");
		b.append("Controlling message output:\r\n");
		b.append(" -v toggles verbose messages to give more feedback on operations and results.\r\n");
		b.append(" -d toggles debug messages helpful in determining what is happening.\r\n");
		b.append(" -q toggles quiet mode, only the result is displayed. This overrides debug and\r\n");
		b.append("    verbose output.\r\n");
		b.append("\r\n");
		b.append("All gets are executed against PROD unless another environment is\r\n");
		b.append("specified with the environment argument:\r\n");
		b.append(" -env  The environment against which to run. Supported values are PROD, ST, UAT,\r\n");
		b.append("       and DEV (default).\r\n");
		b.append("\r\n");
		b.append("Use the -o option to send output to a file. If argument of 'default' is specified,\r\n");
		b.append(" a name of current action and date ([#$Action#]_[#$nowDate#].txt) will be used.\r\n");
		b.append("\r\n");
		b.append("The following nouns are supported:\r\n");
		Map<String,Action> map = cli.getActionMap();

		for (String noun : map.keySet()) {
			b.append("\r\n");
			b.append(noun.toUpperCase()+"\r\n");
			b.append(map.get(noun).getHelp()+"\r\n");
		}
    b.append("\r\n");

		b.append(cli.getVersion());
		
		System.out.println(b);
	}

}
