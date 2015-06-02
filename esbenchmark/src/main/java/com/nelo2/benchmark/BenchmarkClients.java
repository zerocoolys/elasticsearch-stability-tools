package com.nelo2.benchmark;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.PortsRange;
import org.elasticsearch.common.transport.TransportAddress;

public class BenchmarkClients implements Iterable<Client> {

	private Settings settings;
	private TransportClient client;

	private String port = "9300-9400";

	private List<Client> clients;
	private int length = -1;

	public BenchmarkClients(Settings settings) {
		this.settings = settings;
		init();
	}

	public int length() {
		return this.length;
	}

	private void init() {

		String[] clusterNames = settings.getAsArray("cluster.name");
		if (clusterNames == null || clusterNames.length == 0) {
			Logging.err(" cluster.name is empty. ");
			return;
		}

		String[] hosts = settings.getAsArray("hosts");
		if (hosts == null || hosts.length == 0) {
			Logging.err(" hosts is empty. ");
			return;
		}

		if (clusterNames.length != hosts.length) {
			Logging.err(" cluster.name and hosts items' length are not match.");
			return;
		}

		int i = 0;
		int length = clusterNames.length;
		clients = new ArrayList<Client>();

		for (; i < length; i++) {

			String cluster = clusterNames[i];
			String host = hosts[i];
			TransportAddress[] hostAddress = parseString(host);

			Settings temp = ImmutableSettings.settingsBuilder().put("cluster.name", cluster).build();
			Client client = new TransportClient(temp).addTransportAddresses(hostAddress);
			clients.add(client);
		}
		this.length = clients.size();
	}

	private TransportAddress[] parseString(String address) {
		int index = address.indexOf('[');
		if (index != -1) {
			String host = address.substring(0, index);
			Set<String> ports = Strings.commaDelimitedListToSet(address.substring(index + 1, address.indexOf(']')));
			List<TransportAddress> addresses = new ArrayList<TransportAddress>();
			for (String port : ports) {
				int[] iPorts = new PortsRange(port).ports();
				for (int iPort : iPorts) {
					addresses.add(new InetSocketTransportAddress(host, iPort));
				}
			}
			return addresses.toArray(new TransportAddress[addresses.size()]);
		} else {
			index = address.lastIndexOf(':');
			if (index == -1) {
				List<TransportAddress> addresses = new ArrayList<TransportAddress>();
				int[] iPorts = new PortsRange(this.port).ports();
				for (int iPort : iPorts) {
					addresses.add(new InetSocketTransportAddress(address, iPort));
				}
				return addresses.toArray(new TransportAddress[addresses.size()]);
			} else {
				String host = address.substring(0, index);
				int port = Integer.parseInt(address.substring(index + 1));
				return new TransportAddress[] { new InetSocketTransportAddress(host, port) };
			}
		}
	}

	public Iterator<Client> iterator() {
		return clients.iterator();
	}

}
