package io.github.ihexon.output;

import java.io.IOException;
import java.io.OutputStream;

public class NullOutputStream extends OutputStream {
	/**
	 * A singleton.
	 */
	public static final NullOutputStream NULL_OUTPUT_STREAM = new NullOutputStream();

	/**
	 * Does nothing - output to <code>/dev/null</code>.
	 * @param b The bytes to write
	 * @param off The start offset
	 * @param len The number of bytes to write
	 */
	@Override
	public void write(final byte[] b, final int off, final int len) {
		//to /dev/null
	}

	/**
	 * Does nothing - output to <code>/dev/null</code>.
	 * @param b The byte to write
	 */
	@Override
	public void write(final int b) {
		//to /dev/null
	}

	/**
	 * Does nothing - output to <code>/dev/null</code>.
	 * @param b The bytes to write
	 * @throws IOException never
	 */
	@Override
	public void write(final byte[] b) throws IOException {
		//to /dev/null
	}
}
