package com.nelo2.benchmark;

import org.elasticsearch.common.settings.Settings;

import com.nelo2.benchmark.result.TemplateFile;
import com.nelo2.benchmark.utils.CommonUtils;
import com.nelo2.benchmark.utils.IndexUtils;

/**
 * 
 * @author Administrator
 *
 */
public abstract class AbstractEsBenchmark {

	public int QUERY_COUNT = 10;

	public String[] indexs;

	public String[] types;

	public String source;

	protected Settings componentSettings;

	public abstract String name();

	public abstract String benchmark();

	public boolean settings() {
		componentSettings = CommonUtils.getSetting().getByPrefix(name());

		indexs = IndexUtils.calculateIndex(
				componentSettings.get(".indexPrdfixName"),
				componentSettings.getAsInt(".indexCount", 0));
		
		types = componentSettings.getAsArray(".types");
	
		source = TemplateFile.readFile(CommonUtils.getDoc_path()
						+ this.name() + ".json");
		return true;
	}

	public String praseHtml(String name, int size, String totalTime,
			double evarageTime) {
		StringBuffer bf = new StringBuffer();
		bf.append("<table class=\"table\">");
		bf.append("<thead><tr><th>name</th><th>size</th><th>total time</th><th>average time</th></tr></thead>");
		bf.append("<tbody>").append("<tr><td>").append(name).append("</td>")
				.append("<td>").append(size).append("</td>");
		bf.append("<td>").append(totalTime).append("</td>").append("<td>")
				.append(evarageTime).append("</td>");
		bf.append("</tr></tbody></table>");
		return bf.toString();
	}

}
