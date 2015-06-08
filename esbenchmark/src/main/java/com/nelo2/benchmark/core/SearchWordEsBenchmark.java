package com.nelo2.benchmark.core;

import java.util.ArrayList;
import java.util.List;

import com.nelo2.benchmark.AbstractEsBenchmark;
import com.nelo2.benchmark.result.DSLStats;
import com.nelo2.benchmark.utils.CommonUtils;

public class SearchWordEsBenchmark extends AbstractEsBenchmark {

	@Override
	public String benchmark() {
		if(!settings()) {
			return "";
		}

		List<DSLStats> listDSL = new ArrayList<DSLStats>();
		
		listDSL.add(CommonUtils.exceuteSearch(QUERY_COUNT, name(), CommonUtils.getClient(), indexs, types, source));
		
		listDSL.add(CommonUtils.exceuteAfterSettingSearch(QUERY_COUNT, name(), CommonUtils.getAfterSettingClient(), indexs, types, source));

		return praseHtml(listDSL);
	}

	@Override
	public String name() {
		return "SearchWordRequest";
	}

}
