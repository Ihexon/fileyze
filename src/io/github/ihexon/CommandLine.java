package io.github.ihexon;

import java.util.Hashtable;

public class CommandLine {

	public static final String DIR = "-d";
	public static final String RECURSE = "-r";
	public static final String EXCLUDEHIDDEN = "--exclude-hidden";
	public static final String HELP0x00 ="-h";
	public static final String HELP0x01 ="--help";
	public static final String VERSION0x00 =  "--version";
	public static final String VERSION0x01 =  "-v";

	private String[] args;
	private final Hashtable<String, String> keywords = new Hashtable<>();
	public CommandLine(String[] args) throws Exception {
		this.args = args == null ? new String[0] : args;

		// 开始解析参数
		parseArgs(this.args);
	}

	private void parseArgs(String[] args)  throws Exception{
		for (int i = 0; i < args.length; i++) {
			if (parseSwitchs(args, i)) continue;
			if (parseKeywords(args, i)) continue;
		}
	}

	// 解析配对参数
	// 如 -dir xxx是配对参数
	private boolean parseKeywords(String[] args, int i) throws Exception {
		boolean result = false;
		if (checkPair(args, DIR, i)) {
			result = true;
		}else if(checkPair(args, EXCLUDEHIDDEN, i) ){
			result = true;
		}
		return result;
	}

	// 解析CMD开关参数
	// 如--help -r -v --version是开关参数
	private boolean parseSwitchs(String[] args, int i) throws Exception {
		boolean result = false;
		if (checkSwitch(args, RECURSE, i)) {
			result = true;
		}else if (checkSwitch(args, HELP0x00, i)) {
			result = true;
		}if (checkSwitch(args, HELP0x01, i)) {
			result = true;
		}
		return  result;
	}

	// 解析配对参数
	private boolean checkPair(String[] args, String paramName, int i) throws Exception {
		String key = args[i];
		String value = null;
		if (key == null) {
			return false;
		}
		if (key.equalsIgnoreCase(paramName)) {
			value = args[i + 1];
			if (value == null) {
				throw new Exception("Missing parameter for keyword '" + paramName + "'.");
			}
			keywords.put(paramName, value);
			args[i] = null;
			args[i + 1] = null;
			return true;
		}
		return false;
	}

	// 解析CMD开关参数
	private boolean checkSwitch(String[] args, String paramName, int i) throws Exception {
		String key = args[i];
		if (key == null) {
			return false;
		}

		if (key.equalsIgnoreCase(paramName)) {
				keywords.put(paramName, "true");
			args[i] = null;
			return true;
		}
		return false;
	}

	public String getArgument(String keyword) {
		return keywords.get(keyword);
	}
}
