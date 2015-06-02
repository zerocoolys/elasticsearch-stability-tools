package com.nelo2.benchmark.benchmarks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;

import com.nelo2.benchmark.Abstractbenchmark;
import com.nelo2.benchmark.result.TemplateFile;

public abstract class JsonFileBenchmark extends Abstractbenchmark {

	int QUERY_COUNT = 200;
	List<StatsResult> stats = new ArrayList<StatsResult>(QUERY_COUNT);

	protected String[] files;
	protected Integer WARNUP_COUNT;

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

	Map<String, String> fileSource = new HashMap<String, String>();

	@Override
	public void benchmark() {
		if (!settings())
			return;

		long totalQueryTime = 0;

		totalQueryTime = 0;

		for (String file : files) {
			String source = TemplateFile.readFile("./json/" + file + ".json");
			fileSource.put(file, source);
			client.admin().indices().prepareClearCache().setFieldDataCache(true).execute().actionGet();

			log(" (" + file + ") begin...");
			log("warn up...");
			for (int i = 0; i < WARNUP_COUNT; i++) {
				SearchRequest request = client.prepareSearch(getIndexs()).request();
				request.source(source);
				try {
					client.search(request).actionGet();
				} catch (Exception e) {
					continue;
				}
			}
			log("warn up end...");

			log("start benchmark (" + file + ")...");
			for (int j = 0; j < QUERY_COUNT; j++) {
				SearchRequest request = client.prepareSearch().request();
				request.source(source);
				try {
					SearchResponse searchResponse = client.search(request).actionGet();
					totalQueryTime += searchResponse.getTookInMillis();
				} catch (Exception e) {
					continue;
				}
			}
			log("benchmark (" + file + ") " + (totalQueryTime / QUERY_COUNT) + "ms");
			stats.add(new StatsResult(file, totalQueryTime));
			totalQueryTime = 0;
		}

	}

	@Override
	public String getResult() {
		StringBuilder result = new StringBuilder();

		result.append("<table class=\"table\"><thead><tr><th>name</th><th>size</th><th>total time</th><th>average time</th></tr></thead>");
		result.append("<tbody>");
		for (StatsResult stat : stats) {
			result.append("<tr>");
			String source = fileSource.get(stat.name);

			result.append("<td>")
					.append("<div class=\"accordion\" id=")
					.append(stat.name)
					.append("><div class=\"accordion-group\"><div class=\"accordion-heading\"><a class=\"accordion-toggle\" data-toggle=\"collapse\" data-parent=\"#")
					.append(stat.name).append("\" href=\"#").append(stat.name).append("_source\" >").append(stat.name)
					.append("</a></div><div id=\"").append(stat.name)
					.append("_source\" class=\"accordion-body collapse in\"><div class=\"accordion-inner\">")
					.append(source).append("</div></div></div></div>").append("</td>").append("<td>")
					.append(QUERY_COUNT).append("</td>").append("<td>").append(TimeValue.timeValueMillis(stat.took))
					.append("</td>").append("<td>").append((stat.took / QUERY_COUNT)).append("ms</td>");
			result.append("</tr>");
		}
		result.append("</tbody>");
		result.append("</table>");

		return result.toString();
	}
}
