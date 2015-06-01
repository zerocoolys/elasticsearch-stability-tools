package com.nelo2.benchmark;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;

import org.elasticsearch.action.admin.cluster.node.info.NodeInfo;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoRequestBuilder;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse;
import org.elasticsearch.action.admin.cluster.node.stats.NodeStats;
import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsRequest;
import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsRequestBuilder;
import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.SizeValue;

import com.nelo2.benchmark.result.CpuStats;
import com.nelo2.benchmark.result.FsStats;
import com.nelo2.benchmark.result.Stats;
import com.nelo2.benchmark.result.VmStats;

public class JvmMonitor extends Abstractbenchmark {

	protected static long interval;
	final String SEP = "|";
	ExecutorService executor;
	private final Client client;
	private String result = "";
	ArrayList<NodesStatsResponse> responseList = new ArrayList<NodesStatsResponse>(20);

	private static AtomicBoolean stop = new AtomicBoolean(false);
	private long startTime = -1;

	public JvmMonitor(Settings settings, Client client) {
		this.settings = settings;
		this.client = client;
	}

	public boolean start() {
		if (!settings()) {
			return false;
		}
		executor = Executors.newFixedThreadPool(1);
		startTime = System.currentTimeMillis();
		executor.execute(new MonitorThread(responseList));
		return true;
	}

	private Map<String, List<Stats>> nodeStatsMap = new HashMap<String, List<Stats>>();

	private List<Stats> getStats(String nodeId, String nodeName) {

		List<Stats> stats = nodeStatsMap.get(nodeId);
		if (stats == null) {
			stats = new ArrayList<Stats>(2);
			stats.add(new VmStats(nodeId, nodeName));
			stats.add(new FsStats());
			stats.add(new CpuStats(nodeId, nodeName));

			nodeStatsMap.put(nodeId, stats);
		}
		return stats;
	}

	public void end() throws InterruptedException, IOException {
		stop.getAndSet(true);
		synchronized (responseList) {
			for (NodesStatsResponse response : responseList) {
				for (NodeStats nodeStats : response.getNodes()) {
					String id = nodeStats.getNode().getId();
					List<Stats> statsList = getStats(id, nodeStats.getNode().getName());
					for (Stats stats : statsList) {
						stats.state(nodeStats);
					}
				}
			}

		}

		// generate the HTML
		StringBuilder monitorHTML = new StringBuilder();

		NodesInfoRequestBuilder infoBuilder = client.admin().cluster()
				.prepareNodesInfo(nodeStatsMap.keySet().toArray(new String[] {})).all();
		NodesInfoResponse nodeInfoResponse = infoBuilder.execute().actionGet();
		Map<String, NodeInfo> nodeInfoMap = nodeInfoResponse.getNodesMap();

		monitorHTML.append("<div class=\"accordion\" id=\"charts\">");
		for (Entry<String, List<Stats>> nodeStats : nodeStatsMap.entrySet()) {
			String nodeId = nodeStats.getKey();

			NodeInfo info = nodeInfoMap.get(nodeId);
			String name = info.getNode().getName();

			monitorHTML
					.append("<div class=\"accordion-group\"><div class=\"accordion-heading\"><a class=\"accordion-toggle\" data-toggle=\"collapse\" data-parent=\"#chars\"")
					.append(" href=\"#").append(nodeId).append("_chart\" >").append(name)
					.append("</a></div><div id=\"").append(nodeId)
					.append("_chart\" class=\"accordion-body collapse in\"><div class=\"accordion-inner\">");

			/*String nodeInfoStr = getJMXNodeInfo(info);
			monitorHTML.append(nodeInfoStr);
			monitorHTML.append("<div>");*/
			for (Stats stats : nodeStats.getValue()) {
				String jvmHtml = stats.parse();
				monitorHTML.append(jvmHtml);
			}
			monitorHTML.append("</div>");
			monitorHTML.append("</div></div></div>");
		}
		monitorHTML.append("</div>");

		result = monitorHTML.toString().replace("%scripts%", "").replace("%divs%", "")
				.replaceAll("%interval%", interval + "").replaceAll("%datetime%", startTime + "");

		//		result = monitorHTML.toString();
	}

	private String getJMXNodeInfo(NodeInfo info) {
		StringBuilder output = new StringBuilder();
		output.append("<div class=\"accordion\" id=\"accordion_jmx\">");
		String address = info.getNetwork().getPrimaryInterface().address();

		JMXConnector conn = JmxManagementConnectionPools.getJMXConnector(address);
		try {
			MBeanServerConnection mbsc = conn.getMBeanServerConnection();
			RuntimeMXBean runtimeMxBean = JMX.newMXBeanProxy(mbsc,
					new ObjectName(ManagementFactory.RUNTIME_MXBEAN_NAME), RuntimeMXBean.class);
			List<String> jvmargs = runtimeMxBean.getInputArguments();
			BenchmarkSuit.appendAccordionGroup(output, "accordion_jmx", "jvmconf", "JVM configuration",
					jvmargs.toString());

			Map<String, String> pro = runtimeMxBean.getSystemProperties();
			StringBuilder properties = new StringBuilder();

			for (Entry<String, String> propertiesEntry : pro.entrySet()) {
				properties.append(propertiesEntry.getKey()).append("=").append(propertiesEntry.getValue())
						.append("<br>");
			}
			BenchmarkSuit.appendAccordionGroup(output, "accordion_jmx", "syspro", "System properties",
					properties.toString());

			OperatingSystemMXBean osMxBean = JMX.newMXBeanProxy(mbsc, new ObjectName(
					ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME), OperatingSystemMXBean.class);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		} finally {
			JmxManagementConnectionPools.close(conn);
		}
		output.append("</div>");

		return output.toString();
	}

	@Override
	public String getResult() {
		return result;
	}

	public void cleanup() {
		executor.shutdown();
		if (executor.isShutdown())
			client.close();
	}

	class MonitorThread implements Runnable {

		private ArrayList<NodesStatsResponse> responseList;

		public MonitorThread(ArrayList<NodesStatsResponse> responseList) {
			this.responseList = responseList;
		}

		public void run() {
			try {
				NodesStatsRequestBuilder builder = client.admin().cluster().prepareNodesStats();
				NodesStatsRequest request = builder.all().request();
				synchronized (responseList) {
					while (!stop.get()) {
						NodesStatsResponse response = client.admin().cluster().nodesStats(request).actionGet();
						responseList.add(response);
						Thread.sleep(interval);
					}
				}
			} catch (InterruptedException e) {
			}
		}
	}

	@Override
	public String name() {
		return "jvmmonitor";
	}

	@Override
	public boolean settings() {
		if (!super.settings()) {
			return false;
		}
		interval = SizeValue.parseSizeValue(componentSettings.get(".interval", "500")).singles();
		return true;
	}

	@Override
	public void benchmark() {
		if (!settings()) {
			return;
		}

	}

}
