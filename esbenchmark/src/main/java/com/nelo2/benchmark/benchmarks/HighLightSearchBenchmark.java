/*
 * Licensed to Elastic Search and Shay Banon under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Elastic Search licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.nelo2.benchmark.benchmarks;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 *
 */
public class HighLightSearchBenchmark extends JsonFileBenchmark {

	int QUERY_COUNT = 200;
	List<StatsResult> stats = new ArrayList<StatsResult>(QUERY_COUNT);

	public HighLightSearchBenchmark(Settings settings, Client client) {
		this.settings = settings;
		this.client = client;
	}

	@Override
	public boolean settings() {
		if (!super.settings()) {
			return false;
		}
		files = componentSettings.getAsArray(".files");
		QUERY_COUNT = componentSettings.getAsInt(".query.count", settings.getAsInt("query.count", 500));
		WARNUP_COUNT = componentSettings.getAsInt(".warnup.count", settings.getAsInt("warnup.count", 500));
		return true;
	}

	@Override
	public String name() {
		return "highlightsearch";
	}

	@Override
	public void benchmark() {
		if (!settings())
			return;

		client.admin().indices().prepareClearCache("nelo2-log-2013-06-08").setFilterCache(true).setFieldDataCache(true)
				.execute().actionGet();
		
		QueryBuilder qb = QueryBuilders.constantScoreQuery(QueryBuilders.queryString(""));
		
		client.prepareSearch("nelo2-log-2013-06-08");

	}

	// @Override
	// public void benchmark() {
	// if (!settings())
	// return;
	//
	// long totalQueryTime = 0;
	//
	// totalQueryTime = 0;
	//
	// for (String file : files) {
	// String source = TemplateFile.readFile("./json/" + file + ".json");
	// client.admin().indices().prepareClearCache().setFieldDataCache(true).execute().actionGet();
	// log(" (" + file + ") begin...");
	// log("warn up...");
	// for (int i = 0; i < WARNUP_COUNT; i++) {
	// SearchRequest request = client.prepareSearch(getIndexs()).request();
	// request.source(source);
	// client.search(request).actionGet();
	// }
	// log("warn up end...");
	//
	// log("start facet benchmark (" + file + ")...");
	// for (int j = 0; j < QUERY_COUNT; j++) {
	// SearchRequest request = client.prepareSearch().request();
	// request.source(source);
	// SearchResponse searchResponse = client.search(request).actionGet();
	// totalQueryTime += searchResponse.getTookInMillis();
	// }
	// log(" facet query (" + file + ") " + (totalQueryTime / QUERY_COUNT) +
	// "ms");
	// stats.add(new StatsResult(file, totalQueryTime));
	// totalQueryTime = 0;
	// }
	//
	// }

	// @Override
	// public String getResult() {
	// StringBuilder result = new StringBuilder();
	// result.append("<table class=\"table\"><thead><tr><th>name</th><th>size</th><th>total time</th><th>average time</th></tr></thead>");
	// result.append("<tbody>");
	// for (StatsResult stat : stats) {
	// result.append("<tr>");
	// result.append("<td>").append(stat.name).append("</td>").append("<td>").append(QUERY_COUNT).append("</td>")
	// .append("<td>").append(TimeValue.timeValueMillis(stat.took)).append("</td>").append("<td>")
	// .append((stat.took / QUERY_COUNT)).append("ms</td>");
	// result.append("</tr>");
	// }
	// result.append("</tbody>");
	//
	// result.append("</table>");
	//
	// return result.toString();
	// }
}
