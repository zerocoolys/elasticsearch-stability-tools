package com.nelo2.benchmark.core;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.StopWatch;

import com.nelo2.benchmark.AbstractEsBenchmark;
import com.nelo2.benchmark.utils.CommonUtils;

public class SearchWordEsBenchmark extends AbstractEsBenchmark {

	@Override
	public String benchmark() {
		if (!settings()) {
			return "";
		}
		StopWatch stopWatch = new StopWatch().start();
		Client client = CommonUtils.getClient();

		for (int j = 0; j < QUERY_COUNT; j++) {

			client.admin().indices().prepareClearCache()
					.setFieldDataCache(true).execute().actionGet();
			SearchRequest request = client.prepareSearch(indexs)
					.setTypes(types).request();

			request.source(source);
			client.search(request).actionGet();
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
		return "SearchWordRequest";
	}

}
