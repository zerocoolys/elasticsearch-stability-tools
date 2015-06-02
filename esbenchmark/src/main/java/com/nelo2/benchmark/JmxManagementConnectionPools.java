package com.nelo2.benchmark;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class JmxManagementConnectionPools {

	public static JMXConnector getJMXConnector(String ip) {
		int port = 9400;
		JMXConnector conn = null;
		JMXServiceURL jmxUrl = null;
	/*	while (jmxUrl == null && port != 9500) {*/
			try {
				jmxUrl = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + ip + ":" + 1099 + "/jmxrmi");
				conn = JMXConnectorFactory.connect(jmxUrl);
			} catch (MalformedURLException e) {
			} catch (IOException e) {
				e.printStackTrace();
			}
/*		}*/

		if (jmxUrl == null) {
			System.err.println("all port from 9400-9499 is not available!");
		}
		return conn;
	}

	public static void close(JMXConnector conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			conn = null;
		}
	}
}
