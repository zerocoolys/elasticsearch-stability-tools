package com.nelo2.benchmark.factory;

import java.util.ArrayList;
import java.util.List;

import com.nelo2.benchmark.AbstractEsBenchmark;
import com.nelo2.benchmark.core.AccessEsBenchmark;
import com.nelo2.benchmark.core.DateHistogramEsBenchmark;
import com.nelo2.benchmark.core.Search30daysEsBenchmark;
import com.nelo2.benchmark.core.SearchEntrancepageEsBenchmark;
import com.nelo2.benchmark.core.SearchEquipmentEsBenchmark;
import com.nelo2.benchmark.core.SearchWordEsBenchmark;
import com.nelo2.benchmark.core.SourceExternallinksEsBenchmark;
import com.nelo2.benchmark.core.VisitorEquipmentEsBenchmark;

public class EsBenchmarkFactory {

	public static List<AbstractEsBenchmark> getEsBenchmarkTasks() {
		List<AbstractEsBenchmark> tasks = new ArrayList<AbstractEsBenchmark>();

		
		AccessEsBenchmark accessEsBenchmark = new AccessEsBenchmark();
		DateHistogramEsBenchmark dateHistogramEsBenchmark = new DateHistogramEsBenchmark();
		Search30daysEsBenchmark search30daysEsBenchmark = new Search30daysEsBenchmark();
		SearchEntrancepageEsBenchmark searchEntrancepageEsBenchmark = new SearchEntrancepageEsBenchmark();
		SearchEquipmentEsBenchmark searchEquipmentEsBenchmark = new SearchEquipmentEsBenchmark();
		SearchWordEsBenchmark searchWordEsBenchmark = new SearchWordEsBenchmark();
		SourceExternallinksEsBenchmark sourceExternallinksEsBenchmark = new SourceExternallinksEsBenchmark();
		VisitorEquipmentEsBenchmark visitorEquipmentEsBenchmark = new VisitorEquipmentEsBenchmark();

		
		tasks.add(accessEsBenchmark);
		tasks.add(dateHistogramEsBenchmark);
		tasks.add(search30daysEsBenchmark);
		tasks.add(searchEntrancepageEsBenchmark);
		tasks.add(searchEquipmentEsBenchmark);
		tasks.add(searchWordEsBenchmark);
		tasks.add(sourceExternallinksEsBenchmark);
		tasks.add(visitorEquipmentEsBenchmark);

		return tasks;
	}

}
