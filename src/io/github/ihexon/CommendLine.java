package io.github.ihexon;

import java.util.Hashtable;

public class CommendLine {

    public static final String HELP = "-help";
    public static final String HELP2 = "-h";
    public static final String DIR = "-dir";
    public static final String VERSION = "-version";

    private  final Hashtable<String,String> keywords  = new Hashtable<String,String>();

    private String[] args;


    public CommandLine(String[] args) throws Exception {
        this.args = args == null ? new String[0] : args;
        parseFirst(this.args);
    }

    private void parseFirst(String[] args) throws Exception {

        for (int i = 0; i < args.length; i++) {

            if (parseSwitchs(args, i)) {
                continue;
            }
            if (parseKeywords(args, i)) {
                continue;
            }
        }
    }



    /********************** parse Switch **********************/
    private boolean parseSwitchs(String[] args, int i) throws Exception {
        boolean result = false;
        if (checkSwitch(args ,HELP,i)){
            result = true;
        }else if (checkSwitch(args, HELP2 ,i)) {
            result = true;
        }
        return result;
    }
    private boolean checkSwitch(String[] args, String paramName, int i){
        String key = args[i];
        if (key == null) {
            return  false;
        }
        if (key.equalsIgnoreCase(HELP)){
            keywords.put(key, "");
            args[i] = null;
            return true;
        }
        return  false;
    }
    /************************* parse Switch end *************************/




    /********************** parse Keypair **********************/
    private boolean parseKeywords(String[] args,int i) throws Exception{
        boolean result = false;
        if (checkPair(args,DIR,i)){
            return true;
        }
        return result;
    }
    private boolean checkPair(String[] args ,String paramName,int i) throws Exception {
        String key = args[i];
        String value = null;
        if (key == null) {
            return false;
        }

        if (args[i].equalsIgnoreCase(DIR)) {
            value = args[i+1];
            if (value == null) {
                throw new Exception("Missing parameter for keyword '\" + paramName + \"'.");
            }
            keywords.put(key, value);
            args[i] = null;
            args[i+1] = null;
            return true;
        }


        return false;
    }


}
