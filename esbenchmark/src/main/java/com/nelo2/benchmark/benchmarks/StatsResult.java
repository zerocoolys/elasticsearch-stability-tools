package com.nelo2.benchmark.benchmarks;

public class StatsResult {

	final String name;
	final long took;

	StatsResult(String name, long took) {
		this.name = name;
		this.took = took;
	}
}
