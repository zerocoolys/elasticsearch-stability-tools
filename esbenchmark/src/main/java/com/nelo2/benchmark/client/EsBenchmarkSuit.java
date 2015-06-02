package com.nelo2.benchmark.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;

import com.nelo2.benchmark.AbstractEsBenchmark;
import com.nelo2.benchmark.JvmMonitor;
import com.nelo2.benchmark.factory.EsBenchmarkFactory;
import com.nelo2.benchmark.result.TemplateFile;
import com.nelo2.benchmark.utils.CommonUtils;

public class EsBenchmarkSuit {
	
	static Map<String, String> resultString = new TreeMap<String, String>();

	public static void main(String[] args) {
		ExecutorService service = Executors.newCachedThreadPool();
		final Settings settings = CommonUtils.getSetting();
		final Client  client = CommonUtils.getClient();
		service.execute(new Runnable(){
			public void run() {
				JvmMonitor monitor = null;
				List<AbstractEsBenchmark> tasks = EsBenchmarkFactory.getEsBenchmarkTasks();
				try {
					monitor = new JvmMonitor(settings, client);
					if (!monitor.start()) {
						return;
					}
					for(AbstractEsBenchmark task : tasks){
						String result =  task.benchmark();
						 resultString.put(task.name(), result);
					}
					monitor.end();
					
					StringBuilder output = new StringBuilder();
					output.append("<div id=\"modules\" class=\"accordion\">");
					Map<String, String> orders = new TreeMap<String, String>(settings.getAsMap());
					appendAccordionGroup(output, "modules", "monitor", "<h2>" + monitor.name() + "</h2>",
							monitor.getResult());

					outputHTML(output);
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					cleanup(monitor);
				}
				
			}
			
		});

	}

	
	private static void outputHTML(StringBuilder bodyHTML) {
		String filename = "result-" + DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime())
				+ ".html";
		filename = filename.replaceAll(":", "-").replaceAll(" ", "-");
		System.out.println("--> start to output the result:\n\t " + filename);

		for (Entry<String, String> entry : resultString.entrySet()) {
			appendAccordionGroup(bodyHTML, "modules", entry.getKey(), "<h2>" + entry.getKey() + "</h2>",
					entry.getValue());
		}
		bodyHTML.append("</div>");
		String templateHTML = TemplateFile.getTemplateString();
		templateHTML = templateHTML.replace("%module%", bodyHTML);
		File f = null;
		FileOutputStream fout = null;
		try {
			f = new File(filename);
			if (!f.exists()) {
				f.createNewFile();
			}
			fout = new FileOutputStream(f);
			fout.write(templateHTML.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fout = null;
			}
		}
	}
	
	private static void cleanup(JvmMonitor monitor) {
		System.out.println(" output finished and cleanup..");
		monitor.cleanup();
		System.out.println(" cleanup success..");

	}
	
	public static void appendAccordionGroup(StringBuilder output, String pid, String id, String title, String content) {
		output.append("<div class=\"accordion-group\">").append("<div class=\"accordion-heading\">")
				.append("<a class=\"accordion-toggle\" data-toggle=\"collapse\" data-parent=\"#").append(pid)
				.append("\" href=\"#").append(id).append("\">").append(title).append("</a></div>").append("<div id=\"")
				.append(id).append("\" class=\"accordion-body collapse in\"><div class=\"accordion-inner\">")
				.append(content).append("</div></div></div>");
	}
	
}
