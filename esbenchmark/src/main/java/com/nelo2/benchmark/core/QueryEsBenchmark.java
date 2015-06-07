package com.nelo2.benchmark.core;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.StopWatch;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.nelo2.benchmark.AbstractEsBenchmark;
import com.nelo2.benchmark.utils.CommonUtils;

public class QueryEsBenchmark extends AbstractEsBenchmark {

	@Override
	public String benchmark() {
		StopWatch stopWatch = new StopWatch().start();
		Client client = CommonUtils.getClient();
		QueryBuilder query = null;
		for (int j = 0; j < QUERY_COUNT; j++) {
			query = QueryBuilders.matchQuery("loc", "http://www.best-ad.cn/");
			SearchResponse response = client.prepareSearch("access-2015-04-20")
					.setTypes("2").setSearchType(SearchType.DEFAULT)
					.setQuery(query).setFrom(0).setSize(10).execute()
					.actionGet();
			System.out.print(" The completed progress");
			if (j % 10 == 0) {
				System.out.print(String.format(",%d", j));
			}
		}
		stopWatch.stop().lastTaskTime();
		double result = CommonUtils.calculateTPS(stopWatch.totalTime()
				.getMillis(), QUERY_COUNT);

		System.out.println(".   " + name() + "  Indexing took " + stopWatch.totalTime() + ", TPS "
				+ result);

		return praseHtml(source, QUERY_COUNT, stopWatch.totalTime().toString(),
				result);
	}

	@Override
	public String name() {
		return "QueryEsBenchmark";
	}

}
