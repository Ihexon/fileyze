package io.github.ihexon;

import io.github.ihexon.common.PrintUtils;
import io.github.ihexon.other.HelpClass;
import io.github.ihexon.utils.control.Control;

import java.io.IOException;

public class Bootstrap extends AbstractBootstrap {

	public Bootstrap(CommandLine cmdLine) {
		super(cmdLine);
	}

	protected void initControl() {
		Control.initSingleton(getControlOverrides());
	}

	protected void initPrintUtils() throws IOException {
		if (Control.getSingleton() != null)
			PrintUtils.initSingleton(Control.getSingleton());
		else {
			System.err.println("Logging System init fail !!");
			System.exit(1);
		}
	}

	@Override
	public void start() throws IOException {
		super.start();
		initControl();
		initPrintUtils();
		if (!Control.getSingleton().showHelp){
			PathWatcher pathWatcher = new PathWatcher();
			pathWatcher.processEvents();
		}else {
			new HelpClass();
			System.exit(1);
		}
	}
}
