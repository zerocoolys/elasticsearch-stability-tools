package com.ss.es;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dolphineor on 2015-6-2.
 */
public class IndexGenerator {
    private static final String ES_VISITOR_INDEX_PREFIX = "visitor-";

    public static List<String> createIndexes(int startOffset, int endOffset) {
        List<String> indexes = new ArrayList<>();
        LocalDate ld = LocalDate.now();

        for (int i = -endOffset; i < -startOffset + 1; i++) {
            indexes.add(ES_VISITOR_INDEX_PREFIX + ld.minusDays(i));
        }

        return indexes;
    }

}
