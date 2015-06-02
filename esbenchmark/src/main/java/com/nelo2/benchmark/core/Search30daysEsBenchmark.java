package com.nelo2.benchmark.core;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.StopWatch;

import com.nelo2.benchmark.AbstractEsBenchmark;
import com.nelo2.benchmark.result.TemplateFile;
import com.nelo2.benchmark.utils.CommonUtils;

public class Search30daysEsBenchmark extends AbstractEsBenchmark {

	@Override
	public String benchmark() {
		StopWatch stopWatch = new StopWatch().start();
		Client client = CommonUtils.getClient();

		// 索引
		String[] indexs = new String[] { "visitor-2015-05-03",
				"visitor-2015-05-04", "visitor-2015-05-05",
				"visitor-2015-05-06", "visitor-2015-05-07",
				"visitor-2015-05-08", "visitor-2015-05-09",
				"visitor-2015-05-10", "visitor-2015-05-11",
				"visitor-2015-05-12", "visitor-2015-05-13",
				"visitor-2015-05-14", "visitor-2015-05-15",
				"visitor-2015-05-16", "visitor-2015-05-17",
				"visitor-2015-05-18", "visitor-2015-05-19",
				"visitor-2015-05-20", "visitor-2015-05-21",
				"visitor-2015-05-22", "visitor-2015-05-23",
				"visitor-2015-05-24", "visitor-2015-05-25",
				"visitor-2015-05-26", "visitor-2015-05-27",
				"visitor-2015-05-28", "visitor-2015-05-29",
				"visitor-2015-05-30", "visitor-2015-05-31",
				"visitor-2015-06-01" };
		// 类型
		String[] types = new String[] { "1" };
		// 过滤
		String source = TemplateFile.readFile("./doc/search-30days-request");
		for (int j = 0; j < QUERY_COUNT; j++) {

			client.admin().indices().prepareClearCache()
					.setFieldDataCache(true).execute().actionGet();
			SearchRequest request = client.prepareSearch(indexs)
					.setTypes(types).request();

			request.source(source);
			client.search(request).actionGet();

		}

		stopWatch.stop().lastTaskTime();
		System.out.println("Indexing took "
				+ stopWatch.totalTime()
				+ ", TPS "
				+ (((double) (QUERY_COUNT)) / stopWatch.totalTime()
						.secondsFrac()));

		return praseHtml(
				source,
				QUERY_COUNT,
				stopWatch.totalTime().toString(),
				(((double) (QUERY_COUNT)) / stopWatch.totalTime().secondsFrac()));
	}

	@Override
	public String name() {
		return "Search30daysEsBenchmark";
	}

}
