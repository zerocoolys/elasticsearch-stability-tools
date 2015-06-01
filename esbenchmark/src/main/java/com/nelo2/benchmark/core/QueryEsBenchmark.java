package com.nelo2.benchmark.core;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.StopWatch;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.nelo2.benchmark.AbstractEsBenchmark;
import com.nelo2.benchmark.utils.CommonUtils;

public class QueryEsBenchmark extends AbstractEsBenchmark{
	int QUERY_COUNT = 200;
	@Override
	public void benchmark() {
//		long begintime = System.currentTimeMillis();
		StopWatch stopWatch = new StopWatch().start();
		Client client = CommonUtils.getClient();
		for (int j = 0; j < QUERY_COUNT; j++) {
		 QueryBuilder query = QueryBuilders.matchQuery("loc", "http://www.best-ad.cn/");
		 SearchResponse response = client.prepareSearch("access-2015-04-20").setTypes("2").setSearchType(SearchType.DEFAULT).setQuery(query).setFrom(0).setSize(10).execute() 
	                .actionGet(); 
		}
		stopWatch.stop().lastTaskTime();
		System.out.println("Indexing took " + stopWatch.totalTime() + ", TPS "+ (((double) (QUERY_COUNT)) / stopWatch.totalTime().secondsFrac()));
//		long endtime = System.currentTimeMillis();
		
	}

}
