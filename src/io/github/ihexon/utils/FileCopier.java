package io.github.ihexon.utils;

import com.sun.media.sound.SoftFilter;

import java.io.*;
import java.nio.channels.FileChannel;

public class FileCopier {
	public FileCopier() {
		super();
	}

	public void copy(File in, File out) throws IOException {
		try {
			copyNIO(in, out);
		} catch (IOException e) {
			// there is a NIO bug causing exception on the above under Debian.
			copyLegacy(in, out);
		}
	}

	private void copyLegacy(File in, File out) throws IOException {
		try (FileInputStream inputStream = new FileInputStream(in);
		     BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
		     FileOutputStream outputStream = new FileOutputStream(out);
		     BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
		){
			byte[] buf = new byte[10240];
			int len = 1;
			while (len > 0){
				len = bufferedInputStream.read(buf);
				if (len > 0){
					bufferedOutputStream.write(buf, 0,len);
				}
			}
		}
	}


	private void copyNIO(File in, File out) throws IOException {
		try (
				FileInputStream inStream = new FileInputStream(in);
				FileOutputStream outStream = new FileOutputStream(out);
				FileChannel sourceChannel = inStream.getChannel();
				FileChannel destinationChannel = outStream.getChannel();
		) {
			destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
		}
	}
}
