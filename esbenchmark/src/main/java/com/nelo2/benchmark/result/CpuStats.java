package com.nelo2.benchmark.result;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.admin.cluster.node.stats.NodeStats;


public class CpuStats extends Stats {

	List<Short> percents = new ArrayList<Short>();
	public String nodeId;
	private String nodeName;
	private String yTitle = "cpu usage";

	private long count = 0;

	public CpuStats(String nodeId, String nodeName) {
		this.nodeId = nodeId;
		this.nodeName = nodeName;
	}

	@Override
	public void state(NodeStats nodeStats) {
		short percent = nodeStats.getProcess().cpu().percent();
		percents.add(percent);
		count++;
	}

	@Override
	public String parse() {
		String moduleHTML = TemplateFile.getModuleHTML();

		moduleHTML = moduleHTML.replace("%id%", nodeId+"_cpu");
		moduleHTML = moduleHTML.replace("%title%", nodeName + " cpu usage");

		if (yTitle != null) {
			moduleHTML = moduleHTML.replace("%yTitle%", yTitle);
		} else {
			moduleHTML = moduleHTML.replace("%yTitle%", nodeId);
		}

		moduleHTML = moduleHTML.replace("%series%", "cpu usage");

		long xstep = count / 10;
		moduleHTML = moduleHTML.replace("%xstep%", xstep + "");

		moduleHTML = moduleHTML.replace("%model_data%", percents.toString());
		StringBuilder module = new StringBuilder();
		module.append("<div id=\"" + nodeId + "_cpu\" style=\"min-width: 400px; height: 400px; margin: 0 auto\"></div>");
		moduleHTML = moduleHTML.replace("%module%", module);
		return moduleHTML;
	}

}
