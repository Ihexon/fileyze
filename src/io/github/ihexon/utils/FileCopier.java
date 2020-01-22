package io.github.ihexon.utils;

import java.io.*;
import java.nio.channels.FileChannel;

public class FileCopier {
	public FileCopier() {
		super();
	}

	/**
	 * the generic copy method used to copy configure files and log files
	 * This method use {@link #copyLegacy} and new {@link #copyNIO(File, File)} to
	 * do the right work.
	 * @see #copyNIO(File, File)
	 * @see #copyLegacy(File, File)
	 */
	public void copy(File in, File out) throws IOException {
		try {
			copyNIO(in, out);
		} catch (IOException e) {
			// there is a NIO bug causing exception on the above under Debian.
			copyLegacy(in, out);
		}
	}

	/**
	 * old Legacy {@link #copyLegacy(File, File)} method.
	 */
	public void copyLegacy(File in, File out) throws IOException {
		try (FileInputStream inStream = new FileInputStream(in);
		     BufferedInputStream inBuf = new BufferedInputStream(inStream);
		     FileOutputStream outStream = new FileOutputStream(out);
		     BufferedOutputStream outBuf = new BufferedOutputStream(outStream); ) {
			byte[] buf = new byte[10240];
			int len = 1;
			while (len > 0) {
				len = inBuf.read(buf);
				if (len > 0) {
					outBuf.write(buf, 0, len);
				}
			}
		}
	}

	/**
	 * The new NIO {@link #copyNIO(File, File)} method
	 */
	public void copyNIO(File in, File out) throws IOException {
		try (FileInputStream inStream = new FileInputStream(in);
		     FileOutputStream outStream = new FileOutputStream(out);
		     FileChannel sourceChannel = inStream.getChannel();
		     FileChannel destinationChannel = outStream.getChannel(); ) {
			destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
		}
	}
}
