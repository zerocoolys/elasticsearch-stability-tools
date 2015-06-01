package com.nelo2.benchmark;

import java.io.PrintStream;

public class Logging {

	public static void log(String log) {
		System.out.println("DEBUG >" + log);
	}

	public static void err(String log) {
		System.err.println("ERR >" + log);
	}

	public static void log(String log, PrintStream ps) {
		ps.println("--> " + log);
	}

}
