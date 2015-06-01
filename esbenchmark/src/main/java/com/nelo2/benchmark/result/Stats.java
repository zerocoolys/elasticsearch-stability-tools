package com.nelo2.benchmark.result;

import org.elasticsearch.action.admin.cluster.node.stats.NodeStats;

public abstract class Stats {

	public abstract void state(NodeStats nodeStats);

	public abstract String parse();

}
