package com.nelo2.benchmark.benchmarks;

import static org.elasticsearch.search.facet.FacetBuilders.termsFacet;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;

public class TermsRangeFacetSearchBenchmark extends TermsFacetSearchBenchmark {

	public TermsRangeFacetSearchBenchmark(Settings settings, Client client) {
		super(settings, client);
	}

	private Long from;
	private Long to;
	private long step = -1;

	private long offset = 0;

	@Override
	public String name() {
		return "termsrangefacet";
	}

	@Override
	public boolean settings() {
		if (!super.settings()) {
			return false;
		}

		from = componentSettings.getAsLong(".from", (long) -1);
		to = componentSettings.getAsLong(".to", (long) -1);
		if (from == -1 || to == -2) {
			err("from and to properties are not setting correctly.");
			return false;
		}
		step = componentSettings.getAsLong(".step", (to - from) / QUERY_COUNT);

		if (fields.length == 0) {
			fields = settings.getByPrefix(super.name()).getAsArray(".fields");
		}
		return true;
	}

	@Override
	protected StatsResult terms(String name, String field, COND_TYPE type) {
		long totalQueryTime;

		client.admin().indices().prepareClearCache().setFieldDataCache(true).execute().actionGet();

		log("Warmup (" + name + ")" + QUERY_WARMUP + "...");
		SearchRequestBuilder builder = client.prepareSearch(getIndexs()).setSearchType(SearchType.COUNT);

		TermsFacetBuilder termsFacet = termsFacet(field).field(field);
		builder.addFacet(termsFacet);

		for (int j = 0; j < QUERY_WARMUP; j++) {
			switch (type) {
			case QUERY:
				builder.setQuery(QueryBuilders.rangeQuery("logTime").from(from + offset).to(to + offset));
				break;
			case FILTER:
				termsFacet.facetFilter(FilterBuilders.rangeFilter("logTime").from(from + offset).to(to + offset));
				break;
			case FILTERED:
				builder.setQuery(QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(),
						FilterBuilders.rangeFilter("logTime").from(from + offset).to(to + offset)));
				break;
			}
			SearchRequest request = builder.request();
			SearchResponse searchResponse = client.search(request).actionGet();

			if (j == 0) {
				log("Loading (" + field + "): took: " + searchResponse.getTook());
			}
			if (matchsize && searchResponse.getHits().totalHits() != COUNT) {
				err("mismatch on hits");
			}

			offset += step;
		}
		offset = 0l;
		log("Warmup (" + name + ") DONE");

		log("Running (" + name + ") " + QUERY_COUNT + "...");
		totalQueryTime = 0;

		for (int j = 0; j < QUERY_COUNT; j++) {

			switch (type) {
			case QUERY:
				builder.setQuery(QueryBuilders.rangeQuery("logTime").from(from + offset).to(to + offset));
				break;
			case FILTER:
				termsFacet.facetFilter(FilterBuilders.rangeFilter("logTime").from(from + offset).to(to + offset));
				break;
			case FILTERED:
				builder.setQuery(QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(),
						FilterBuilders.rangeFilter("logTime").from(from + offset).to(to + offset)));
				break;
			}
			SearchRequest request = builder.request();

			SearchResponse response = client.search(request).actionGet();

			if (matchsize && response.getHits().totalHits() != COUNT) {
				err("mismatch on hits");
			}
			totalQueryTime += response.getTookInMillis();
			offset += step;
		}
		log("Terms Facet (" + field + ") : " + (totalQueryTime / QUERY_COUNT) + "ms");
		return new StatsResult(name, totalQueryTime);
	}
}
