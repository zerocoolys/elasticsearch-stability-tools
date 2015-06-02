package com.nelo2.benchmark.benchmarks;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.StopWatch;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.SizeValue;
import org.elasticsearch.common.unit.TimeValue;

import com.nelo2.benchmark.Abstractbenchmark;
import com.nelo2.benchmark.utils.StringUtil;

public class IndexBenchmark extends Abstractbenchmark {

	static String indexName = "";
	static long SIZE = SizeValue.parseSizeValue("20k").singles();

	static int PROJECT_COUNT = 1200;
	static int BODY_COUNT = 1000;
	static int HOST_COUNT = 5000;
	static int WORD_COUNT = 10000;
	static long START_TIME = -1;
	static long END_TIME = Integer.MAX_VALUE;
	static int APP_COUNT = 4000;
	static int APP_NAME_SIZE = 10;

	static String[] LOG_LEVEL = { "DEBUG", "ERROR", "INFO", "WARNING", "FATAL" };
	static int LOG_LEVEL_COUNT = LOG_LEVEL.length;

	static int BATCH = 100;
	private double TPS = 0.0;
	private static boolean deleteIndex = true;
	private boolean enable = false;

	public IndexBenchmark(Settings settings, Client client) {
		this.settings = settings;
		this.client = client;
	}

	public static void main(String[] args) throws InterruptedException {
	}

	private static String getBody(String[] words, String[] temp) {
		StringBuilder sb = new StringBuilder(1000 + 100);
		sb.append(words[ThreadLocalRandom.current().nextInt(0, words.length)]);
		for (int i = 1; i < 100; i++) {
			sb.append(" ").append(words[ThreadLocalRandom.current().nextInt(0, words.length)]);
		}
		return sb.toString();
	}

	private Random randomTime;
	private Boolean bulk;
	private TimeValue TOTALTIME;

	@Override
	public String name() {
		return "index";
	}

	@Override
	public boolean settings() {
		super.settings();

		String size = settings.get("size", "20m");
		SIZE = SizeValue.parseSizeValue(size).singles();
		bulk = componentSettings.getAsBoolean("bulk", true);
		indexName = componentSettings.get(".name", "benchmark");
		Settings benchSettings = settings.getByPrefix(name());
		BATCH = benchSettings.getAsInt(".batch", 500);
		LOG_LEVEL = benchSettings.getAsArray(".log.levels");
		if (LOG_LEVEL == null || LOG_LEVEL.length == 0) {
			err(name() + ".log.levels is empty");
			return false;
		}
		LOG_LEVEL_COUNT = LOG_LEVEL.length;

		PROJECT_COUNT = benchSettings.getAsInt(".project.size", 1200);
		BODY_COUNT = benchSettings.getAsInt(".body.size", 2000);
		HOST_COUNT = benchSettings.getAsInt(".host.size", 5000);
		WORD_COUNT = benchSettings.getAsInt(".word.size", 10000);

		deleteIndex = benchSettings.getAsBoolean(".delete", false);

		END_TIME = SizeValue.parseSizeValue(benchSettings.get(".logtime.end", "36m")).singles();
		randomTime = new Random(END_TIME);

//		enable = componentSettings.getAsBoolean(".enable", false);
		enable = componentSettings.getAsBoolean(".enable", true);
		return true;
	}

	@Override
	public void benchmark() {
		if (!settings())
			return;
		if (!enable) {
			return;
		}
		String[] projectNames = new String[PROJECT_COUNT];

		for (int i = 0; i < projectNames.length; i++) {
			projectNames[i] = StringUtil.randomAlphabetic(15);
		}

		String[] words = new String[WORD_COUNT];
		for (int i = 0; i < WORD_COUNT; i++) {
			words[i] = StringUtil.random(10, true, false);
		}

		String[] bodys = new String[BODY_COUNT];
		for (int i = 0; i < bodys.length; i++) {
			bodys[i] = StringUtil.random(5000, true, true);
		}

		// random host lists
		String[] hosts = new String[HOST_COUNT];
		Random random = new Random();
		for (int i = 0; i < hosts.length; i++) {
			int a = random.nextInt(256);
			int b = random.nextInt(256);
			int c = random.nextInt(256);
			int d = random.nextInt(256);
			hosts[i] = a + "." + b + "." + c + "." + d;
		}

		log("data prepared finished");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			client.admin().indices().create(Requests.createIndexRequest(indexName)).actionGet();
		} catch (Exception e) {
			err(indexName + " already exists..");
			if (deleteIndex) {
				log("need delete the '" + indexName + "'");
				client.admin().indices().delete(Requests.deleteIndexRequest(indexName)).actionGet();
				log("deleted...");
			}
		}
		try {

			log("Indexing [" + SIZE + "] ...");
			if (bulk) {
				bulkRequest(projectNames, words, bodys, hosts);
			} else {
				singleRequest(projectNames, words, bodys, hosts);
			}

		} catch (Exception e) {
			err("Index already exists, ignoring indexing phase, waiting for green");
			ClusterHealthResponse clusterHealthResponse = client.admin().cluster().prepareHealth()
					.setWaitForGreenStatus().setTimeout("10m").execute().actionGet();
			if (clusterHealthResponse.isTimedOut()) {
				log("Timed out waiting for cluster health");
			}
		}
		client.admin().indices().prepareRefresh().execute().actionGet();
		SIZE = client.prepareCount().setQuery(matchAllQuery()).execute().actionGet().getCount();
		log("--> Number of docs in index: " + SIZE);
	}

	private void singleRequest(String[] projectNames, String[] words, String[] bodys, String[] hosts) {
		Random random = new Random();
		int count = 0;
		Map<String, Object> map = null;
		IndexRequest request = null;
		String[] temp = new String[WORD_COUNT];
		StopWatch stopWatch = new StopWatch().start();
		while (count < SIZE) {
			request = Requests.indexRequest();
			map = new HashMap<String, Object>(5);
			long start = System.currentTimeMillis();
			String projectName = projectNames[random.nextInt(projectNames.length)];
			String body = getBody(words, temp);
			map.put("projectName", projectName);
			map.put("logTime", randomTime.nextLong());
			map.put("logLevel", LOG_LEVEL[random.nextInt(LOG_LEVEL_COUNT)]);
			map.put("body", body);
			map.put("host", hosts[random.nextInt(HOST_COUNT)]);
			request.type(projectName).index(indexName).source(map);
			IndexResponse response = client.index(request).actionGet();
			count++;
			if ((count % (BATCH * 10)) == 0) {
				log("Indexed " + count + " took " + stopWatch.stop().lastTaskTime());
				stopWatch.start();
			}
			//			}
		}
		log("Indexing took " + stopWatch.totalTime() + ", TPS "
				+ (((double) (SIZE)) / stopWatch.totalTime().secondsFrac()));
	}

	private void bulkRequest(String[] projectNames, String[] words, String[] bodys, String[] hosts) {
		int count = 0;
		BulkRequestBuilder builder = client.prepareBulk();
		Random random = new Random();
		Map<String, Object> map = null;
		IndexRequest request = null;
		String[] temp = new String[WORD_COUNT];
		StopWatch stopWatch = new StopWatch().start();
		while (count < SIZE) {
			request = Requests.indexRequest();
			map = new HashMap<String, Object>(5);
			long start = System.currentTimeMillis();
			String projectName = projectNames[random.nextInt(projectNames.length)];
			String body = getBody(words, temp);
			map.put("projectName", projectName);
			map.put("logTime", randomTime.nextLong());
			map.put("logLevel", LOG_LEVEL[random.nextInt(LOG_LEVEL_COUNT)]);
			map.put("body", body);
			map.put("host", hosts[random.nextInt(HOST_COUNT)]);
			request.type(projectName).index(indexName).source(map);
			builder.add(request);
			count++;
			if (builder.numberOfActions() == BATCH) {
				BulkResponse response = builder.execute().actionGet();
				if (response.hasFailures()) {
					err("there are failed request!");
				}
				builder = client.prepareBulk();
				if ((count % (BATCH * 10)) == 0) {
					log("Indexed " + count + " took " + stopWatch.stop().lastTaskTime());
					stopWatch.start();
				}
			}
		}
		TPS = (((double) (SIZE)) / stopWatch.totalTime().secondsFrac());
		TOTALTIME = stopWatch.totalTime();
		log("Indexing took " + TOTALTIME + ", TPS " + TPS);
	}

	@Override
	public String getResult() {

		StringBuilder output = new StringBuilder();
		output.append("<table class=\"table\"><thead><tr><th>Size</th><th>Total Time</th><th>TPS</th></tr></thead>");
		if (enable) {
			output.append("<tr><td>" + SIZE + "</td><td>" + TOTALTIME + " (" + TOTALTIME.getMillis() + ")ms</td><td>"
					+ TPS + "</td></tr>");
		}
		output.append("</table>");
		return output.toString();
	}
}
