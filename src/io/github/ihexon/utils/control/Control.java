package io.github.ihexon.utils.control;

import io.github.ihexon.CommandLine;
import io.github.ihexon.ControlOverrides;
import io.github.ihexon.services.logsystem.Log;
import io.github.ihexon.utils.IncludeExcludeSet;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Control {
    private static Control control = null;
    public Path dir = null;
    public boolean recursive = false;
    public boolean isDebug = true;
    public boolean excludeHidden = false;
    public boolean showHelp = true;
    public File logFile = null;
    public  IncludeExcludeSet<PathMatcher, Path> exclude;


    public final String DIR = "-d";

    private void init(CommandLine commandLine) {

        String LOGFILE = commandLine.LOGFILE;
        String RECURSE = commandLine.RECURSE;
        String EXCLUDES = commandLine.EXCLUDES_HID;
        String regex = commandLine.getKeyPair(commandLine.EXCLUDEREGX);

        this.recursive = commandLine.getSwitchs(RECURSE);
        this.excludeHidden = commandLine.getSwitchs(EXCLUDES);
        this.dir = getMonitorPath(commandLine);
        this.logFile =
                commandLine.getKeyPair(LOGFILE) != null
                        && commandLine.getKeyPair(LOGFILE).length() != 0 ?
                        new File(commandLine.getKeyPair(LOGFILE)) : null;


        // add regex exclude , params -ex [regex]
        if (regex != null && regex.length() != 0) {
            this.exclude = new IncludeExcludeSet<>(PathMatcherSet.class);
            List<String> excludes = new ArrayList<>();
            excludes.add(regex);
            addExcludes(excludes);
        }
    }

    public void addExcludes(List<String> syntaxAndPatterns) {
        for (String syntaxAndPattern : syntaxAndPatterns) {
            addExclude(syntaxAndPattern);
        }
    }

    public void addExclude(final String syntaxAndPattern) {
        if (isDebug)
            Log.getInstance().info("Adding exclude: [{" + syntaxAndPattern + "}]");
        addExclude(dir.getFileSystem().getPathMatcher(syntaxAndPattern));
    }

    public void addExclude(PathMatcher matcher) {
        exclude.exclude(matcher);
    }

    private Path getMonitorPath(CommandLine commandLine) {
        // the params must have a dir to monitor, like -d [dir], than set showHelp to false.
        // otherwise show help message
        if (commandLine.getKeyPair(DIR) == null){
            return null;
        }
        this.showHelp=false;
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
