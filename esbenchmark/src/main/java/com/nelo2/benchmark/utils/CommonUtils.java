package com.nelo2.benchmark.utils;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class CommonUtils {
	
	private static Configuration  config ;
	private static Client client;
	private static Settings settings;
	
	static {
		try {
			config = new PropertiesConfiguration("config.properties");
			client = new TransportClient(getSetting()).addTransportAddress(new InetSocketTransportAddress(CommonUtils.readProperties("ip"), CommonUtils.readPropertiesAsInt("port")));

		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public static String  readProperties(String key){
		  return config.getString(key);
		  
	}
	public static int  readPropertiesAsInt(String key){
		  return config.getInt(key);
		  
	}
	
	public static Settings getSetting() {
		 settings = ImmutableSettings.settingsBuilder().put("cluster.name", CommonUtils.readProperties("cluster.name")).build();
         return settings;
	}
	
	public static Client getClient() {
//		client = new TransportClient(getSetting()).addTransportAddress(new InetSocketTransportAddress(CommonUtils.readProperties("ip"), CommonUtils.readPropertiesAsInt("port")));
		return client;
	}
	
	public static void main(String[] args) {
		/*System.out.println(CommonUtils.readProperties("ip"));
		Client client = getClient();
		System.out.println(client);
		client.close();*/
		
		final Settings settings = ImmutableSettings.settingsBuilder().loadFromClasspath("benchmark.yml").build();
		System.out.println(settings);
	}

}
