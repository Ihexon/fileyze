package io.github.ihexon.common;

public class Priority {
    transient int level;
    transient String levelStr;
    transient int syslogEquivalent;
    public final static int DEBUG_INT = 10000;

    public
    final
    int getSyslogEquivalent() {
        return syslogEquivalent;
    }

    public
    boolean isGreaterOrEqual(Priority r) {
        return level >= r.level;
    }

    final
    public
    String toString() {
        return levelStr;
    }

    public
    final
    int toInt() {
        return level;
    }



    public
    boolean equals(Object o) {
        if(o instanceof Priority) {
            Priority r = (Priority) o;
            return (this.level == r.level);
        } else {
            return false;
        }
    }

    protected Priority() {
        level = DEBUG_INT;
        levelStr = "DEBUG";
        syslogEquivalent = 7;
    }

    /**
     Instantiate a level object.
     */
    protected
    Priority(int level, String levelStr, int syslogEquivalent) {
        this.level = level;
        this.levelStr = levelStr;
        this.syslogEquivalent = syslogEquivalent;
    }
}
