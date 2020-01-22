package io.github.ihexon.Loder;

public interface LifeCycle {
	void start() throws Exception;
	void stop() throws Exception;

	/**
	 * @return true if the component is starting or has been started.
	 */
	boolean isRunning();
}
