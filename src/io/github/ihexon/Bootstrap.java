package io.github.ihexon;

import io.github.ihexon.common.ErrorCode;
import io.github.ihexon.common.FileAppender;
import io.github.ihexon.common.PrintUtils;
import io.github.ihexon.other.HelpClass;
import io.github.ihexon.services.logsystem.Log;
import io.github.ihexon.utils.control.Control;

import java.io.File;
import java.io.IOException;

public class Bootstrap extends AbstractBootstrap {

    public Bootstrap(CommandLine cmdLine) {
        super(cmdLine);
    }

    protected void initControl() {
        Control.initSingleton(getControlOverrides(), getCommandLine());
    }

    protected void initLogger() throws IOException {
        Control control = Control.getSingleton();
        if (control != null) {
            if (control.logFile != null
                    && control.logFile.toString().length() != 0) {
                File file = control.logFile;
                Log.getInstance().addAppender(new FileAppender(file.toString()));
            }
        } else {
            Log.getInstance().info("Control should not be null, there is some " +
                    "problem with Bootstrap, Dangerous operation! exit now");
            System.exit(ErrorCode.CONTROL_NULL);
        }
    }

    @Override
    public int start() throws IOException {
        int rc = super.start();
        if (rc != 0) return rc;
        initControl();
        initLogger();
        if (!Control.getSingleton().showHelp) {
            PathWatcher pathWatcher = new PathWatcher();
            pathWatcher.processEvents();
        } else {
            new HelpClass();
            System.exit(1);
        }
        return rc;
    }
}
