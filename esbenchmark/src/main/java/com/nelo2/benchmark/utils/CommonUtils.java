package com.nelo2.benchmark.utils;

import java.io.File;
import java.math.BigDecimal;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.StopWatch;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.nelo2.benchmark.result.DSLStats;

public class CommonUtils {

	private static Configuration config;
	private static Client client;
	private static Settings settings;
	private static String doc_path;
	private static Client afterSettingClient;

	static {
		try {
			doc_path = "." + File.separator + "doc" + File.separator;
			config = new PropertiesConfiguration("config.properties");
			client = new TransportClient(getSetting())
					.addTransportAddress(new InetSocketTransportAddress(
							CommonUtils.readProperties("ip"), CommonUtils
									.readPropertiesAsInt("port")));

			afterSettingClient = new TransportClient(getSetting())
					.addTransportAddress(new InetSocketTransportAddress(
							CommonUtils.readProperties("ip"), CommonUtils
									.readPropertiesAsInt("port")));

			// .addTransportAddress(
			// new InetSocketTransportAddress(CommonUtils
			// .readProperties("ip2"), CommonUtils
			// .readPropertiesAsInt("port")));

		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	public static String readProperties(String key) {
		return config.getString(key);

	}

	public static int readPropertiesAsInt(String key) {
		return config.getInt(key);

	}

	public static String getDoc_path() {
		return doc_path;
	}

	public static Settings getSetting() {
		// settings = ImmutableSettings.settingsBuilder().put("cluster.name",
		// CommonUtils.readProperties("cluster.name")).build();
		if (settings == null) {
			settings = ImmutableSettings.settingsBuilder()
					.loadFromClasspath("benchmark.yml").build();
		}
		return settings;
	}

	public static Client getClient() {
		// client = new TransportClient(getSetting()).addTransportAddress(new
		// InetSocketTransportAddress(CommonUtils.readProperties("ip"),
		// CommonUtils.readPropertiesAsInt("port")));
		return client;
	}

	public static DSLStats exceuteSearch(int count, String name, Client client,
			String[] indexs, String[] types, String source) {

		StopWatch stopWatch = new StopWatch().start();

		System.out.print(" The completed progress");
		for (int j = 0; j < count; j++) {

			client.admin().indices().prepareClearCache()
					.setFieldDataCache(true).execute().actionGet();
			SearchRequest request = client.prepareSearch(indexs)
					.setTypes(types).request();

			request.source(source);

			client.search(request).actionGet();
			if (j % 10 == 0) {
				System.out.print(String.format(",%d", j));
			}
		}
		stopWatch.stop().lastTaskTime();

		double result = CommonUtils.calculateTPS(stopWatch.totalTime()
				.getMillis(), count);

		System.out.println(".   " + name + "  Indexing took "
				+ stopWatch.totalTime() + ", TPS " + result);

		return new DSLStats(source, count, stopWatch.totalTime().toString(),
				result);

	}

	public static DSLStats exceuteAfterSettingSearch(int count, String name,
			Client client, String[] indexs, String[] types, String source) {
	

		client.admin().indices().prepareUpdateSettings(indexs)
				.setSettings(UpdateIndexSettingUtil.getSettingConfig());
		
		StopWatch stopWatch = new StopWatch().start();
		System.out.println("After Setting.");
		System.out.print("The completed progress");
		for (int j = 0; j < count; j++) {

			client.admin().indices().prepareClearCache()
					.setFieldDataCache(true).execute().actionGet();
			SearchRequest request = client.prepareSearch(indexs)
					.setTypes(types).request();

			request.source(source);

			client.search(request).actionGet();
			if (j % 10 == 0) {
				System.out.print(String.format(",%d", j));
			}
		}

		stopWatch.stop();

		double result = CommonUtils.calculateTPS(stopWatch.totalTime()
				.getMillis(), count);

		System.out.println(".   " + name + "  Indexing took "
				+ stopWatch.totalTime() + ", TPS " + result);

		return new DSLStats(source, count, stopWatch.totalTime().toString(),
				result);
	}

	public static Client getAfterSettingClient() {
		return afterSettingClient;
	}

	@SuppressWarnings("resource")
	public static Client getNewClient() {

		Client cl = new TransportClient(getSetting())
				.addTransportAddress(new InetSocketTransportAddress(CommonUtils
						.readProperties("ip"), CommonUtils
						.readPropertiesAsInt("port")));
		return cl;

	}

	public static Double calculateTPS(Long totalTime, int queryCount) {
		BigDecimal total = new BigDecimal(totalTime);
		double result = total.divide(new BigDecimal(queryCount)).doubleValue();

		return result;
	}

	public static void main(String[] args) {
		/*
		 * System.out.println(CommonUtils.readProperties("ip")); Client client =
		 * getClient(); System.out.println(client); client.close();
		 */

		final Settings settings = ImmutableSettings.settingsBuilder()
				.loadFromClasspath("benchmark.yml").build();

		System.out.println(settings);
	}

}
