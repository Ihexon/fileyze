package io.github.ihexon.commandline;

import java.util.Hashtable;

public class CommandLine {
	public static final String HELP = "--help";
	public static final String HELP2 = "-h";
	public static final String DIR = "--dir";
	public static final String DIR2 = "-d";
	public static final String DAEMON = "--daemon";
	public static final String DAEMON2 = "-D";
	public static  boolean reportVersion = false;

	public static final String VERSION = "--version";
	public static final String VERSION2 = "-v";

	// We do not have gui yet,so set this field false
	private boolean GUI = false;
	// We do not have daemon yet,so set this field false
	public static boolean daemon = false;
	public  static boolean isWeb = false;



	public final static Hashtable<String,String> keyParis = new Hashtable<>();

	private static String[] args = null;

	/**
	 * the {@link io.github.ihexon.commandline.CommandLine} class used to collecting the args
	 * and parase those parameters, set the flags. eg --dir --daemon,
	 * and save those parameters to keyParis
	 */
	public  CommandLine(String[] args) throws Exception {
		this.args = (args == null ? new String[0]:args);
		parseFirst(this.args);
	}

	public boolean isGUI() {
		return GUI;
	}
	public boolean isDaemon() {
		return daemon;
	}


	/**
	 * parseFirst use to parse the commandline args.
	 * @param args the ArrayList of args
	 * @throws Exception any of {@link Exception}
	 *
	 * @see #checkSwitch(String[], int)
	 * @see #checkKeyPairs(String[], int)
	 */
	private void parseFirst(String[] args) throws Exception {
		for (int i = 0; i < args.length; i++) {
			String z = args[i];
			if (checkSwitch(args,i)) continue;
			if (checkKeyPairs(args,i)) continue;
		}
	}

	/**
	 * {@link #checkSwitch(String[], int)} use to check the single flag in args .eg: --version, -v.
	 * @param args the ArrayList of args
	 * @param i the string[i] of the args ArrayList
	 * @return boolean
	 * @throws Exception any of {@link Exception}
	 */
	private  boolean checkSwitch(String[] args,int i) throws  Exception{
		boolean result = false;
		if (args[i] == null) return false;
		if (args[i].equalsIgnoreCase(HELP) || args[i].equalsIgnoreCase(HELP2)) {
			keyParis.put(args[i],"");
			args[i] = null;
			result = true;
		}else if (args[i].equalsIgnoreCase(VERSION) || args[i].equalsIgnoreCase(VERSION2)) {
			reportVersion = true;
			keyParis.put(args[i],"");
			args[i] = null;
			result = true;
		}

		return result;
	}

	/**
	 * {@link #checkKeyPairs(String[], int)} use to check the keyParis flag in args .eg: --dir, -d, --daemon, -D.
	 * @param args the ArrayList of args
	 * @param i the string[i] of the args ArrayList
	 * @return boolean
	 * @throws Exception any of {@link Exception}
	 */
	private boolean checkKeyPairs(String[] args,int i) throws Exception{
		boolean result = false;
		if (args[i] == null) return false;
		if (args[i].equalsIgnoreCase(DIR) || args[i].equalsIgnoreCase(DIR2)) {
			keyParis.put(DIR, args[i+1]);
			args[i+1] = args[i] = null;
			result = true;
		}else  if (args[i].equalsIgnoreCase(DAEMON) || args[i].equalsIgnoreCase(DAEMON2)){
			daemon = true;
			keyParis.put(DIR, args[i+1]);
			args[i+1] = args[i] = null;
			result = true;
		}
		return result;
	}


}
