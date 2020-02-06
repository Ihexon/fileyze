package io.github.ihexon.common;

import java.io.FilterWriter;
import java.io.Writer;

public class QuietWriter extends FilterWriter {

	/**
	 * QuietWriter does not throw exceptions when things go
	 * wrong. Instead, it delegates error handling to System.out and ystem.err
	 */
	public QuietWriter(Writer writer) {
		super(writer);
	}

	@Override
	public void write(String string) {
		if (string != null) {
			try {
				out.write(string);
			} catch (Exception e) {
				System.err.println("Failed to write [" + string + "]." + ErrorCode.WRITE_FAILURE + "\n");
				System.err.println(e.getMessage());
			}
		}
	}

	@Override
	public void flush() {
		try {
			out.flush();
		} catch (Exception e) {
			System.err.println("Failed to flush writer," + ErrorCode.FLUSH_FAILURE);
			System.err.println(e.getMessage());
		}
	}
}
