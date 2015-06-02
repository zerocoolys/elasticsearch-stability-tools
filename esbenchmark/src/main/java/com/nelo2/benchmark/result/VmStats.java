package com.nelo2.benchmark.result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.elasticsearch.action.admin.cluster.node.stats.NodeStats;
import org.elasticsearch.monitor.jvm.JvmStats;
import org.elasticsearch.monitor.jvm.JvmStats.GarbageCollector;

public class VmStats extends Stats {

	public String nodeId;
	private String nodeName;
	long count = 0;
	List<Object> memUsed = new ArrayList<Object>();
	List<Long> nonMemUsed = new ArrayList<Long>();
	Map<String, Long[]> gcdetails = new HashMap<String, Long[]>();
	
	protected String yTitle;
	
	public VmStats(String nodeId,String nodeName) {
		this.nodeId = nodeId;
		this.nodeName = nodeName;
		yTitle = "Memery Used";
	}

	@Override
	public void state(NodeStats nodeStats) {
		JvmStats jvm = nodeStats.getJvm();
		memUsed.add(jvm.mem().getHeapUsed().bytes());
		nonMemUsed.add(jvm.mem().nonHeapUsed().bytes());

		for (GarbageCollector collector : jvm.gc()) {
			String name = collector.getName();

			long count = collector.collectionCount();
			long time = collector.collectionTime().millis();

			Long[] value = gcdetails.get(name);
			if (value == null) {
				value = new Long[] { count, time, 0l, 0l };
				gcdetails.put(name, value);
			}
			value[2] = count;
			value[3] = time;
		}
		count++;
	}

	@Override
	public String parse() {

		String moduleHTML = TemplateFile.getModuleHTML();

		moduleHTML = moduleHTML.replace("%id%", nodeId+"_jvm");
		moduleHTML = moduleHTML.replace("%title%", nodeName + " JVM Heap usage");

		if (yTitle != null) {
			moduleHTML = moduleHTML.replace("%yTitle%", yTitle);
		} else {
			moduleHTML = moduleHTML.replace("%yTitle%", nodeId);
		}

		moduleHTML = moduleHTML.replace("%series%", "memory used");
		
		long xstep = count / 10;
		moduleHTML = moduleHTML.replace("%xstep%", xstep + "");

		moduleHTML = moduleHTML.replace("%model_data%", memUsed.toString());
		StringBuilder module = new StringBuilder();
		module.append("<div id=\"" + nodeId + "_jvm\" style=\"min-width: 400px; height: 400px; margin: 0 auto\"></div>");
		module.append("<table class=\"table\"><thead><tr><th>gc name</th><th>gc count</th><th>gc time</th></tr></thead>");
		module.append("<tbody>");
		for (Entry<String, Long[]> valueEntry : gcdetails.entrySet()) {
			Long[] value = valueEntry.getValue();
			module.append("<tr><td>" + valueEntry.getKey() + "</td><td>" + (value[2] - value[0]) + "</td><td>"
					+ (value[3] - value[1]) + "ms</td></tr>");
		}
		module.append("</tbody>");
		
		module.append("</table>");
		moduleHTML = moduleHTML.replace("%module%",module);
//		templateHTML = templateHTML.replace("%module%", module.toString()).replace("%script%", moduleHTML);
		return moduleHTML;
	}

}
