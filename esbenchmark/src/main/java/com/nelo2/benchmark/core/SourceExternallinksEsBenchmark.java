package com.nelo2.benchmark.core;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.StopWatch;

import com.nelo2.benchmark.AbstractEsBenchmark;
import com.nelo2.benchmark.result.TemplateFile;
import com.nelo2.benchmark.utils.CommonUtils;

public class SourceExternallinksEsBenchmark extends AbstractEsBenchmark {

	@Override
	public String benchmark() {
		StopWatch stopWatch = new StopWatch().start();
		Client client = CommonUtils.getClient();

		// 索引
		String[] indexs = new String[] {
				"visitor-2015-05-26", "visitor-2015-05-27",
				"visitor-2015-05-28", "visitor-2015-05-29",
				"visitor-2015-05-30", "visitor-2015-05-31",
				"visitor-2015-06-01" };
		// 类型
		String[] types = new String[] { "1" };
		// 过滤
		String source = TemplateFile.readFile("./doc/source-externallinks-request");
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
		return "SourceExternallinksEsBenchmark";
	}

}
