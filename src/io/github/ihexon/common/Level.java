package io.github.ihexon.common;

public class Level extends Priority {

    public final static int INFO_INT  = 20000;
    public final static int WARN_INT  = 30000;
    public final static int ERROR_INT = 40000;



    final static public Level WARN  = new Level(WARN_INT, "WARN",  4);
    final static public Level INFO  = new Level(INFO_INT, "INFO",  6);
    final static public Level ERROR = new Level(ERROR_INT, "ERROR", 3);



    /**
     Instantiate a Level object.
     */
    protected
    Level(int level, String levelStr, int syslogEquivalent) {
        super(level, levelStr, syslogEquivalent);
    }





}
