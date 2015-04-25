package coyote.cli.template;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Simply a table of named string values.
 * 
 * <p>This table has some utility functions to manage the data in the table 
 * such as placing system properties in and removing them from the table.</p>
 */
public class SymbolTable extends HashMap {

	/** */
	private static final long serialVersionUID = -3448311765253950903L;




	/**
	   * Constructor SymbolTable
	   */
	public SymbolTable() {
	}




	/**
	 * Read all the System properties into the SymbolTable.
	 */
	public synchronized void readSystemProperties() {
		for (Enumeration en = System.getProperties().propertyNames(); en.hasMoreElements();) {
			String name = (String) en.nextElement();
			put(name, System.getProperty(name));
		}
	}




	/**
	 * Remove all the System properties from the SymbolTable.
	 */
	public synchronized void removeSystemProperties() {
		for (Enumeration en = System.getProperties().propertyNames(); en.hasMoreElements();) {
			String name = (String) en.nextElement();
			remove(name);
		}
	}




	/**
	 * Return the String value of the named symbol from the table.
	 *
	 * @param key the key to look-up the value 
	 *
	 * @return the string for the given key
	 */
	public synchronized String getString(String key) {
		if (key != null) {
			if (containsKey(key)) {
				return get(key).toString();
			} else if (key.equals("currentMilliseconds")) {
				return Long.toString(System.currentTimeMillis());
			} else if (key.equals("currentSeconds")) {
				return Long.toString(System.currentTimeMillis() / 1000);
			} else if (key.equals("epocTime")) {
				return Long.toString(System.currentTimeMillis() / 1000);
			} else if (key.equals("symbolDump")) {
				return dump();
			}
		}

		return "null";
	}




	/**
	 * Go through all the symbols in the given table and add/replace them to our
	 * table.
	 *
	 * @param symbols the Hashtable of name value pairs to merge.
	 */
	public synchronized void merge(HashMap symbols) {
		for (Iterator it = symbols.keySet().iterator(); it.hasNext();) {
			try {
				String key = (String) it.next();
				put(key, symbols.get(key));
			} catch (Exception ex) {
				// key was not a String?
				// value was null?
			}
		}
	}




	/**
	 * Method dump
	 *
	 * @return the contents of the table
	 */
	public synchronized String dump() {
		StringBuffer retval = new StringBuffer();

		for (Iterator it = keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			retval.append("'" + key + "' = '" + get(key).toString() + "'\r\n");
		}

		return retval.toString();
	}

}