package com.nelo2.benchmark.result;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class TemplateFile {

	private static String scriptContent;

	public static String getModuleHTML() {
		if (scriptContent == null) {
			scriptContent = readFile("./template/module.html");
		}
		return scriptContent;
	}

	private static String seriesContent;
	private static String templateContent;

	public static synchronized String readFile(String file) {
		InputStream is = new BufferedInputStream(TemplateFile.class.getClassLoader().getResourceAsStream(file));
		StringBuilder temp = new StringBuilder();
		byte[] array = new byte[512];
		try {
			int length = is.read(array);
			while (length != -1) {
				temp.append(new String(Arrays.copyOf(array, length)));
				length = is.read(array);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return temp.toString();
	}

	public static String getTemplateString() {
		if (templateContent == null) {
			
			templateContent = readFile("./template/template.html");
		}
		return templateContent;

	}

	public static synchronized String getSeriesString() {
		if (seriesContent == null) {
			seriesContent = readFile("./template/series.html");
		}
		return seriesContent;
	}

	public static void main(String[] args) {
		System.out.println(getSeriesString());
		System.out.println(getModuleHTML());
	}
}
