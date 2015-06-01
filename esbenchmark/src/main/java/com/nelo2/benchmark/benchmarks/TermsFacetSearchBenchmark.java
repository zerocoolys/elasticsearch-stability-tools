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

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.search.facet.FacetBuilders.termsFacet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.SizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;

/**
 *
 */
public class TermsFacetSearchBenchmark extends JsonFileBenchmark {

	protected enum COND_TYPE {
		QUERY, FILTER, FILTERED
	}

	long COUNT = SizeValue.parseSizeValue("20k").singles();
	int QUERY_WARMUP = 50;
	int QUERY_COUNT = 500;
	String[] fields = null;
	String result = "";
	private Integer MAX_THREAD;
	private Integer THREAD_STEP;

	public TermsFacetSearchBenchmark(Settings settings, Client client) {
		this.settings = settings;
		this.client = client;
	}

	public static void main(String[] args) throws Exception {
		//		TermsFacetSearchBenchmark benchmark = new TermsFacetSearchBenchmark();
		//		benchmark.benchmark();
	}

	protected StatsResult terms(String name, String field, COND_TYPE type) {
		long totalQueryTime;

		client.admin().indices().prepareClearCache().setFieldDataCache(true).execute().actionGet();

		log("Warmup (" + name + ")" + QUERY_WARMUP + "...");
		SearchRequestBuilder builder = client.prepareSearch(getIndexs()).setSearchType(SearchType.COUNT);
		
		TermsFacetBuilder facetBuilders = termsFacet(field).field(field);
		builder.addFacet(facetBuilders);
		switch (type) {
		case QUERY:
			builder.setQuery(matchAllQuery()).addFacet(facetBuilders);
			break;
		case FILTER:
			break;
		case FILTERED:
			break;
		}
		SearchRequest request = builder.request();
		logRequest(request);

		for (int j = 0; j < QUERY_WARMUP; j++) {
			SearchResponse searchResponse = client.search(request).actionGet();
			if (j == 0) {
				log("Loading (" + field + "): took: " + searchResponse.getTook());
			}
			if (matchsize && searchResponse.getHits().totalHits() != COUNT) {
				err("mismatch on hits");
			}
		}
		log("Warmup (" + name + ") DONE");

		log("Running (" + name + ") " + QUERY_COUNT + "...");
		totalQueryTime = 0;
		for (int j = 0; j < QUERY_COUNT; j++) {
			SearchResponse searchResponse = client.prepareSearch().setSearchType(SearchType.COUNT)
					.setQuery(matchAllQuery()).setSize(0).addFacet(termsFacet(field).field(field)).execute()
					.actionGet();
			if (matchsize && searchResponse.getHits().totalHits() != COUNT) {
				err("mismatch on hits");
			}
			totalQueryTime += searchResponse.getTookInMillis();
		}
		log("Terms Facet (" + field + ") : " + (totalQueryTime / QUERY_COUNT) + "ms");
		return new StatsResult(name, totalQueryTime);
	}

	@Override
	public String name() {
		return "termsfacet";
	}

	@Override
	public boolean settings() {
		if (!super.settings()) {
			return false;
		}
		COUNT = SizeValue.parseSizeValue(settings.get("size")).singles();
		QUERY_COUNT = componentSettings.getAsInt(".query.count", settings.getAsInt("query.count", 500));
		QUERY_WARMUP = componentSettings.getAsInt(".warnup.count", settings.getAsInt("warnup.count", 500));

		fields = componentSettings.getAsArray(".fields");

		MAX_THREAD = componentSettings.getAsInt(".thread.max", settings.getAsInt("thread.max", 200));
		THREAD_STEP = componentSettings.getAsInt(".thread.step", settings.getAsInt("thread.step", 50));
		return true;
		
	}
	class BenchmarkThread implements Callable<Double>{

		private CyclicBarrier barrier;
		
		public BenchmarkThread(CyclicBarrier barrier){
			this.barrier = barrier;
		}
		public Double call() throws Exception {
			barrier.await();
			
			List<StatsResult> stats = new ArrayList<StatsResult>();
			for (String field : fields) {
				stats.add(terms(name() + " : terms_query_" + field, field, COND_TYPE.QUERY));
				stats.add(terms(name() + " : terms_filter_" + field, field, COND_TYPE.FILTER));
				stats.add(terms(name() + " : terms_filtered_" + field, field, COND_TYPE.FILTERED));
			}
			
			
			return null;
		}

		
	}
	@Override
	public void benchmark() {
		if (!settings())
			return;

		List<StatsResult> stats = new ArrayList<StatsResult>();
		for (String field : fields) {
			stats.add(terms(name() + " : terms_query_" + field, field, COND_TYPE.QUERY));
			stats.add(terms(name() + " : terms_filter_" + field, field, COND_TYPE.FILTER));
			stats.add(terms(name() + " : terms_filtered_" + field, field, COND_TYPE.FILTERED));
		}

		System.out.println("------------------ SUMMARY -------------------------------");
		System.out.format("%25s%10s%10s\n", "name", "took", "millis");

		StringBuilder result = new StringBuilder();
		result.append("<table class=\"table\"><tr><td>name</td><td>size</td><td>total time</td><td>average time</td></tr>");
		for (StatsResult stat : stats) {
			result.append("<tr>");
			result.append("<td>").append(stat.name).append("</td>").append("<td>").append(QUERY_COUNT).append("</td>")
					.append("<td>").append(TimeValue.timeValueMillis(stat.took)).append("</td>").append("<td>")
					.append((stat.took / QUERY_COUNT)).append("ms</td>");
			result.append("</tr>");
		}
		result.append("</table>");
		this.result = result.toString();
		System.out.println("------------------ SUMMARY -------------------------------");
	}

	public String getResult() {
		return result;
	}
}
