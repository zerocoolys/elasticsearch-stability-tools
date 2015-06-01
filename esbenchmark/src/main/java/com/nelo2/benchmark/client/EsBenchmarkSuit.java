package com.nelo2.benchmark.client;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;

import com.nelo2.benchmark.AbstractEsBenchmark;
import com.nelo2.benchmark.JvmMonitor;
import com.nelo2.benchmark.factory.EsBenchmarkFactory;
import com.nelo2.benchmark.utils.CommonUtils;

public class EsBenchmarkSuit {

	public static void main(String[] args) {
		ExecutorService service = Executors.newCachedThreadPool();
		final Settings settings = CommonUtils.getSetting();
		final Client  client = CommonUtils.getClient();
		service.execute(new Runnable(){
			public void run() {
				JvmMonitor monitor = null;
				List<AbstractEsBenchmark> tasks = EsBenchmarkFactory.getEsBenchmarkTasks();
				try {
					monitor = new JvmMonitor(settings, client);
					if (!monitor.start()) {
						return;
					}
					for(AbstractEsBenchmark task : tasks){
						task.benchmark();
					}
					
					monitor.end();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					cleanup(monitor);
				}
				
			}
			
		});

	}

	private static void cleanup(JvmMonitor monitor) {
		System.out.println(" output finished and cleanup..");
		monitor.cleanup();
		System.out.println(" cleanup success..");

	}
	
}
