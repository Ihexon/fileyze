package io.github.ihexon.common;

import sun.tools.tree.InstanceOfExpression;

import java.io.*;

public class FileAppender extends WriterAppender {
	protected boolean append = true;
	protected String fileName = null;
	protected boolean bufferedIO = false;
	protected int bufferSize = 8 * 1024;

	public FileAppender() {
	}

	public FileAppender(String filename, boolean fileAppend, boolean bufferedIO,
	                    int bufferSize) throws IOException {
		this.setFile(filename, append, bufferedIO, bufferSize);
	}

	public FileAppender(String filename) throws IOException {
		this(filename, true);
	}

	public FileAppender(String filename, boolean append)
			throws IOException {
		this.setFile(filename, append, false, bufferSize);
	}

	public synchronized void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize)
			throws IOException {
		if (bufferedIO) {
			setImmediateFlush(false);
		}
		reset();
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(fileName, append);
		} catch (FileNotFoundException ex) {
			String parentName = new File(fileName).getParent();
			if (parentName != null) {
				File parentDir = new File(parentName);
				if (! parentDir.exists() && parentDir.mkdir()) {
					outputStream = new FileOutputStream(fileName, append);
				} else {
					System.err.println("Make directory fail !! Please make sure you have right premission with dir with output logfile.");
					System.err.println(fileName);
					throw ex;
				}
			} else {
				throw ex;
			}
		}

		Writer fw = createWriter(outputStream);
		if (bufferedIO)
			fw = new BufferedWriter(fw, bufferSize);
		this.qw = new QuietWriter(fw);
		this.fileName = fileName;
		this.append = append;
		this.bufferedIO = bufferedIO;
		this.bufferSize = bufferSize;
		writeHeader();
	}

	// DO NOT USE !!!
	public void activateOptions() {
		if(fileName != null) {
			try {
				setFile(fileName, append, bufferedIO, bufferSize);
			} catch(java.io.IOException e) {
				System.err.println("setFile("+fileName+","+append+") call failed."+" "+ErrorCode.FILE_OPEN_FAILURE);
				e.printStackTrace();
			}
		}else {
			String  s1= "File option not set for appender ["+name+"].";
			String s2="Are you using FileAppender instead of ConsoleAppender?";
			System.err.println(s1);
			System.err.println(s2);
		}
	}


		/**
		 * Closes the previously opened file.
		 */
		private void closeFile () {
			if (this.qw != null) {
				try {
					this.qw.close();
				} catch (IOException e) {
					if (e instanceof InterruptedIOException)
						Thread.currentThread().interrupt();
				}
			}
		}

		protected void reset () {
			closeFile();
			this.fileName = null;
			super.reset();
		}

		public void setFile (String file){
			String val = file.trim();
			this.fileName = val;
		}


		public String getFile () {
			return fileName;
		}

		public boolean getAppend () {
			return append;
		}


	}
