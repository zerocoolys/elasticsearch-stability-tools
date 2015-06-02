package com.nelo2.benchmark.core;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.StopWatch;

import com.nelo2.benchmark.AbstractEsBenchmark;
import com.nelo2.benchmark.result.TemplateFile;
import com.nelo2.benchmark.utils.CommonUtils;

public class SearchWordEsBenchmark extends AbstractEsBenchmark {

	@Override
	public String benchmark() {
		StopWatch stopWatch = new StopWatch().start();
		Client client = CommonUtils.getClient();

		// 索引
		String[] indexs = CommonUtils.getSetting().getAsArray(
				this.name() + ".indexs");
		// 类型
		String[] types = CommonUtils.getSetting().getAsArray(
				this.name() + ".types");
		// 过滤
		String source = TemplateFile.readFile(CommonUtils.getDoc_path()
				+ this.name() + ".json");

		for (int j = 0; j < QUERY_COUNT; j++) {

			client.admin().indices().prepareClearCache()
					.setFieldDataCache(true).execute().actionGet();
			SearchRequest request = client.prepareSearch(indexs)
					.setTypes(types).request();

			request.source(source);
			client.search(request).actionGet();

		}

		stopWatch.stop().lastTaskTime();
		System.out.println("Indexing took " + stopWatch.totalTime() + ", TPS "
				+ (stopWatch.totalTime().secondsFrac())
				/ ((double) (QUERY_COUNT)));

		return praseHtml(source, QUERY_COUNT, stopWatch.totalTime().toString(),
				(stopWatch.totalTime().secondsFrac())
						/ ((double) (QUERY_COUNT)));
	}

	@Override
	public String name() {
		return "SearchWordRequest";
	}

}
