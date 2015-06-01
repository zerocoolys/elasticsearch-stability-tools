package com.nelo2.benchmark.factory;

import java.util.ArrayList;
import java.util.List;

import com.nelo2.benchmark.AbstractEsBenchmark;
import com.nelo2.benchmark.core.QueryEsBenchmark;

public class EsBenchmarkFactory {
	
	public static List<AbstractEsBenchmark> getEsBenchmarkTasks(){
		List<AbstractEsBenchmark> tasks = new ArrayList<AbstractEsBenchmark>();
		QueryEsBenchmark task1 = new QueryEsBenchmark();
		tasks.add(task1);
		return tasks;
	}

}
