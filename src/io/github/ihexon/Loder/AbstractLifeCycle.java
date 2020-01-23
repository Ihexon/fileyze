package io.github.ihexon.Loder;

import io.github.ihexon.common.DebugUtils;
import io.github.ihexon.utils.control.Control;

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
		synchronized (_lock){
			try{
				if(_state == STATE_STARTED || _state == STATE_STARTING ) return;
				setStarting();
				doStart();
				setStarted();
			}catch (Throwable e){
				setFailed(e);
				DebugUtils.ErrPrintln(e.getMessage());
			}
		}
	}

	private void setFailed(Throwable th)
	{
		_state = STATE_FAILED;
//		if (LOG.isDebugEnabled())
//			LOG.warn(FAILED + " " + this + ": " + th, th);
//		for (Listener listener : _listeners)
//		{
//			listener.lifeCycleFailure(this, th);
//		}
	}

	private void setStarted(){
		_state = STATE_STARTED;
//		if (Control.isDebug)
//			DebugUtils.stdPrintln(STARTED + " @{"+Uptime.getUptime()+"}ms {"+this+"}");

	}

	private void setStarting()
	{
		if (Control.isDebug)
			DebugUtils.stdPrintln("starting {"+this+"}");
		_state = STATE_STARTING;
	}




	@Override
	public final void stop() throws Exception{
		doStop();
	}

	protected void doStart() throws Exception {
	}
	protected void doStop() throws Exception {
	}

	@Override
	public String toString(){
		Class<?> clazz = getClass();
		String name = clazz.getSimpleName();
		if ((name == null || name.length() == 0) && clazz.getSuperclass() != null){
			clazz = clazz.getSuperclass();
			name = clazz.getSimpleName();
		}
		return String.format("%s@%x{%s}", name, hashCode(), getState());
	}

	public String getState()
	{
		switch (_state)
		{
			case STATE_FAILED:
				return FAILED;
			case STATE_STARTING:
				return STARTING;
			case STATE_STARTED:
				return STARTED;
			case STATE_STOPPING:
				return STOPPING;
			case STATE_STOPPED:
				return STOPPED;
		}
		return null;
	}
}
