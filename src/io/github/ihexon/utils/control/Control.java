package io.github.ihexon.utils.control;

import io.github.ihexon.CommandLine;
import io.github.ihexon.ControlOverrides;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Control {
    private static Control control = null;
    public Path dir = null;
    public boolean recursive = false;
    public boolean isDebug = true;
    public boolean excludeHidden = false;
    public boolean showHelp = true;
    public File logFile = null;

    public final String DIR = "-d";
    public final String RECURSE = "-r";
    public final String EXCLUDEHIDDEN = "--exclude-hidden";
    public final String HELP0x00 = "-h";
    public final String HELP0x01 = "--help";
    public final String VERSION0x00 = "--version";
    public final String VERSION0x01 = "-v";
    public final String LOGFILE = "--log";

    private void init(CommandLine commandLine) {
        this.recursive = commandLine.getSwitchs(RECURSE);
        this.excludeHidden = commandLine.getSwitchs(EXCLUDEHIDDEN);
        this.dir = getMonitorPath(commandLine);
        this.logFile =
                commandLine.getKeyPair(LOGFILE) != null
                        && commandLine.getKeyPair(LOGFILE).length() != 0 ?
                        new File(commandLine.getKeyPair(LOGFILE)) : null;
        this.showHelp = commandLine.getSwitchs(HELP0x00);
        this.showHelp = commandLine.getSwitchs(HELP0x01);
    }


    private Path getMonitorPath(CommandLine commandLine) {
        // the params must have a dir to monitor, like -d [dir], than set showHelp to false.
        // otherwise show help message
        this.showHelp = false;
        return Paths.get(commandLine.getKeyPair(DIR));
    }

    public static Control getSingleton() {
        return control;
    }


    public static void initSingleton(ControlOverrides overrides, CommandLine commandLine) {
        control = new Control();
        // int CommandLine args
        control.init(commandLine);
        // DO NOTHING
        control.init(overrides);
    }


    /**
     * since release 2 , the {@link ControlOverrides} do nothing,so
     * the {@link #init(ControlOverrides)} do nothing too.
     * We still need this method in someday so it will not be delete.
     *
     * @param overrides the {@link ControlOverrides}'s reference
     */
    private void init(ControlOverrides overrides) {
    }


}
