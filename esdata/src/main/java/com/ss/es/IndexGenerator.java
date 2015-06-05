package com.ss.es;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by dolphineor on 2015-6-2.
 * <p>
 * elasticsearch index 生成器
 */
public class IndexGenerator {
    private static final String ES_VISITOR_INDEX_PREFIX = "visitor-";

    public static List<String> createIndexes(int startOffset, int endOffset) {
        List<String> indexes = new ArrayList<>();
        LocalDate ld = LocalDate.now();

        for (int i = -endOffset; i < -startOffset + 1; i++)
            indexes.add(ES_VISITOR_INDEX_PREFIX + ld.minusDays(i));

        return indexes;
    }

    public static List<String> createIndexes(int size) {
        List<String> indexes = new ArrayList<>();
        Calendar c = Calendar.getInstance();

        for (int i = 0; i < size; i++)
            indexes.add(ES_VISITOR_INDEX_PREFIX + c.get(Calendar.YEAR) + "-" + getIndex(c.get(Calendar.MONTH) + 1) + "-" + getIndex(i + 10));
        return indexes;
    }

    public static String getIndex(int month) {
        return month < 10 ? "0" + month : "" + month;
    }

}
