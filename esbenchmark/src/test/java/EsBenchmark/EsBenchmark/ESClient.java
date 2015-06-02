package EsBenchmark.EsBenchmark;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.elasticsearch.action.admin.cluster.node.info.NodesInfoRequestBuilder;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse;
import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsRequest;
import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsRequestBuilder;
import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsResponse;
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsNodes.JvmStats;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.monitor.jvm.JvmInfo;
import org.elasticsearch.monitor.jvm.JvmStats.GarbageCollector;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import com.nelo2.benchmark.JvmMonitor;


public class ESClient {
	 private Client client;
	 
	 public void init() {


		    Settings settings = ImmutableSettings.settingsBuilder()   
		                    .put("cluster.name", "elasticsearch").build();
		    client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress("192.168.1.113", 9300));
	        /*client = new TransportClient().addTransportAddress(new InetSocketTransportAddress("182.92.227.79", 19200));*/
	    }
	 
	 public void close() {
	        client.close();
	 }
	 
	 public void createIndex() throws InterruptedException {
		   Random r = new Random();
	        for (int i = 0; i < 15; i++) {
	        	Business bs = new Business();
	        	/*bs.set_id(UUID.randomUUID().toString());*/
	        	bs.set_score(r.nextFloat());
	        	bs.set_type("2");
	        	bs.setLoc("http://www.best-ad.cn/");
	            client.prepareIndex("access-2015-04-20", "2").setSource(generateJson(bs))
	                    .execute().actionGet();
	            Thread.currentThread().sleep(2000);
	            System.out.println("add : "+bs.get_id());
	        }
	    }
	 
	 
	 private String generateJson(Business bs) {
	        String json = "";
	        try {
	            XContentBuilder contentBuilder = XContentFactory.jsonBuilder()
	                    .startObject();
	           /* contentBuilder.field("_id", bs.get_id() + "");*/
	            contentBuilder.field("_type", bs.get_type()+"");
	            contentBuilder.field("loc", bs.getLoc());
	            contentBuilder.field("_score", bs.get_score()+"");
	            json = contentBuilder.endObject().string();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return json;
	    }
	 
	 
	 
	 public Client getClient() {
		return client;
	}

	public void search() {
	      
	    }
	 
	public static void main(String[] args) {
		
		ESClient test = new ESClient();
		test.init();
		/*test.createIndex();
		test.close();*/
		
		Map<String, String> settingMap = new HashMap<String, String>();  
		settingMap.put("cluster.name", "elasticsearch");  
		/*settingMap.put("node.name", "geloin"); */
		
		Settings settings  = ImmutableSettings.settingsBuilder().put(settingMap).build();
		Client client = test.getClient();
		JvmMonitor monitor =new JvmMonitor(settings, client);
		if (!monitor.start()) {
			return;
		}
		
	  /*  QueryBuilder query = QueryBuilders.matchQuery("loc", "http://www.best-ad.cn/");
	    long start =  System.currentTimeMillis();
	    SearchResponse response = client.prepareSearch("access-2015-04-20").setTypes("2").setSearchType(SearchType.DEFAULT).setQuery(query).setFrom(0).setSize(10).execute() 
                .actionGet(); 
	    long end =  System.currentTimeMillis();
	    System.out.println("size="+10+"  totalTime = "+(end-start) + "  averageTime="+((end-start)/10) );
	    
	    NodesInfoResponse response1 = client.admin().cluster()
	    		.prepareNodesInfo()
	    		.setNetwork(true)
	    		.execute().actionGet();
	    
	    
	    JvmInfo jvminfo = response1.getNodes()[0].getJvm();
	    System.out.println("jvminfo:"+jvminfo);
	    
	    NodesStatsRequestBuilder builder = client.admin().cluster().prepareNodesStats();
		NodesStatsRequest request = builder.all().request();
		NodesStatsResponse response2 = client.admin().cluster().nodesStats(request).actionGet();
		
		org.elasticsearch.monitor.jvm.JvmStats jvmstats = response2.getNodes()[0].getJvm();
		for (GarbageCollector collector : jvmstats.gc()) {
			
			System.out.println("start: "+"");
		}
	    
                SearchHits shs = response.getHits();  
                for(SearchHit hit : shs){  
                	System.out.println(hit.sourceAsString());
                }
		System.out.println();*/
		
		try {
			test.createIndex();
			monitor.end();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		client.close();
		
	}
}
