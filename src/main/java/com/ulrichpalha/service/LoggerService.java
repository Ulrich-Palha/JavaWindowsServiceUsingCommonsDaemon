package com.ulrichpalha.service;


import java.io.File;
import java.io.IOException;
import java.util.Random;


/*
 * A Modified version of Commons Daemon provided sample ProcrunService
 * The orginal can be found here
 * http://svn.apache.org/viewvc/commons/proper/daemon/trunk/src/samples/ProcrunService.java?view=markup
 */

/*

 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

/**
 * Sample service implementation for use with Windows Procrun.
 * <p>
 * Use the main() method for running as a Java (external) service. Use the
 * start() and stop() methods for running as a jvm (in-process) service
 */
public class LoggerService implements Runnable {

	/** The Constant MS_PER_SEC. */
	private static final long MS_PER_SEC = 1000L; // Milliseconds in a second

	/** The logger thread. */
	private static volatile Thread loggerThread; // start and stop are called
													// from different threads
	/** The stop file. */
	private final File stopFile;

	/** The Constant random. */
	private static final Random random = new Random();

	/** The Constant logger. */
	private final static Logger logger = new ConsoleLogger();

	/**
	 * Instantiates a new procrun service.
	 * 
	 * @param file
	 *            the file
	 */
	private LoggerService(File file) {
		this.stopFile = file;

	}

	/**
	 * Tmp file.
	 * 
	 * @param filename
	 *            the filename
	 * @return the file
	 */
	private static File tmpFile(String filename) {
		return new File(System.getProperty("java.io.tmpdir"),
				filename != null ? filename : "LoggerService.tmp");
	}

	/**
	 * Usage.
	 */
	private static void usage() {
		System.err.println("Must supply the argument 'start' or 'stop'");
	}

	/**
	 * Helper method for process args with defaults.
	 * 
	 * @param args
	 *            array of string arguments, may be empty
	 * @param argnum
	 *            which argument to extract
	 * @return the argument or null
	 */
	private static String getArg(String[] args, int argnum) {
		if (args.length > argnum) {
			return args[argnum];
		} else {
			return null;
		}
	}

	/**
	 * Common entry point for start and stop service functions. To allow for use
	 * with Java mode, a temporary file is created by the start service, and a
	 * deleted by the stop service.
	 * 
	 * @param args
	 *            [start [pause time] | stop]
	 * @throws IOException
	 *             if there are problems creating or deleting the temporary file
	 */
	public static void main(String[] args) throws IOException {

		final int argc = args.length;

		logger.log("LoggerService called with " + argc
				+ " arguments from thread: " + Thread.currentThread());
		for (int i = 0; i < argc; i++) {
			System.out.println("[" + i + "] " + args[i]);
		}
		String mode = getArg(args, 0);
		if ("start".equals(mode)) {
			File f = tmpFile(getArg(args, 1));
			logger.log("Creating file: " + f.getPath());
			f.createNewFile();
			startThread(f);
		} else if ("stop".equals(mode)) {
			final File tmpFile = tmpFile(getArg(args, 1));
			logger.log("Deleting file: " + tmpFile.getPath());
			tmpFile.delete();
		} else {
			usage();
		}
	}

	/**
	 * Start the jvm version of the service, and waits for it to complete.
	 * 
	 * @param args
	 *            optional, arg[0] = timeout (seconds)
	 */
	public static void start(String[] args) {

		startThread(null);
		while (loggerThread.isAlive()) {
			try {
				loggerThread.join();
			} catch (InterruptedException ie) {
				// Ignored
			}
		}
	}

	/**
	 * Start thread.
	 * 
	 * @param file
	 *            the file
	 */
	private static void startThread(File file) {

		logger.log("Starting the thread");
		loggerThread = new Thread(new LoggerService(file));
		loggerThread.start();
	}

	/**
	 * Stop the JVM version of the service.
	 * 
	 * @param args
	 *            ignored
	 */
	public static void stop(String[] args) {
		if (loggerThread != null) {
			logger.log("Interrupting the thread");
			loggerThread.interrupt();
		} else {
			logger.log("No thread to interrupt");
		}
	}

	/**
	 * This method simulates performing the work of the service. In this case, it just logs
	 * a message any time between 1-5 seconds.
	 * A real logging application would get its log messages from a queue or socket etc.
	 */
	public void run() {
		long sleepTime;
		
		logger.log("Started thread in " + System.getProperty("user.dir"));
		
		while (stopFile == null || stopFile.exists()) {
			sleepTime = random.nextInt(4) + 1;
			
			try {
				logger.log("pausing " + sleepTime + " seconds");
				Thread.sleep(sleepTime * MS_PER_SEC);
			} catch (InterruptedException e) {
				logger.log("Exiting");
				break;
			}
		}
	}

}
