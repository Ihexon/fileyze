package io.github.ihexon.Loder;

public abstract class AbstractLifeCycle implements LifeCycle{

	private final Object _lock = new Object();

	public static final String STOPPED = "STOPPED";
	public static final String FAILED = "FAILED";
	public static final String STARTING = "STARTING";
	public static final String STARTED = "STARTED";
	public static final String STOPPING = "STOPPING";
	public static final String RUNNING = "RUNNING";

	private volatile int _state = STATE_STOPPED;
	private static final int STATE_FAILED = -1;
	private static final int STATE_STOPPED = 0;
	private static final int STATE_STARTING = 1;
	private static final int STATE_STARTED = 2;
	private static final int STATE_STOPPING = 3;

	@Override
	public boolean isRunning()
	{
		final int state = _state;

		return state == STATE_STARTED || state == STATE_STARTING;
	}


	@Override
	public final void start() throws Exception{
		doStart();
	}

	@Override
	public final void stop() throws Exception{
		doStop();
	}

	protected void doStart() throws Exception {
	}
	protected void doStop() throws Exception {
	}

}
