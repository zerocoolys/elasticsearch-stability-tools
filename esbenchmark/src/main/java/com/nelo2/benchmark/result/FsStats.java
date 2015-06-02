package com.nelo2.benchmark.result;

import org.elasticsearch.action.admin.cluster.node.stats.NodeStats;

public class FsStats extends Stats {

	@Override
	public void state(NodeStats nodeStats) {
		
	}

	@Override
	public String parse() {
		return "";
	}

}
