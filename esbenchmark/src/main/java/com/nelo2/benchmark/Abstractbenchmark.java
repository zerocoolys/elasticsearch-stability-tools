package com.nelo2.benchmark;

import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;

public abstract class Abstractbenchmark {

	protected static String[] indexs;
	protected static String type;
	protected static String routing;
	protected Client client;
	protected Settings settings;
	protected Settings componentSettings;
	protected boolean DEBUG = false;
	protected Boolean matchsize;

	public abstract String name();

	public boolean settings() {
		componentSettings = this.settings.getByPrefix(name());

		indexs = this.settings.getAsArray("indexs");
		DEBUG = settings.getAsBoolean("debug", false);
		matchsize = settings.getAsBoolean("matchsize", false);
		return true;
	}

	public abstract void benchmark();

	protected static void log(String str) {
		Logging.log(str);
	}

	protected static void err(String str) {
		Logging.err(str);
	}

	protected void logRequest(ActionRequest request) {
		if (DEBUG) {
		}
	}

	public final String[] getIndexs() {
		if (indexs == null)
			indexs = new String[] {};
		return indexs;
	}

	public String getResult() {
		return "";
	}
}
