package com.ss;

import com.ss.core.DataHandler;
import com.ss.es.EsDataWriter;
import com.ss.es.EsPools;
import com.ss.es.IndexGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dolphineor on 2015-5-28.
 */
public class EsDataGeneratorMain {

    public static void main(String[] args) {
        int bulk = Integer.valueOf(args[0]);
        EsPools.setBulkRequestNumber(bulk);

        int startOffset = Integer.valueOf(args[1]);
        int endOffset = Integer.valueOf(args[2]);
        List<String> indexes = IndexGenerator.createIndexes(startOffset, endOffset);

        List<DataHandler> handlers = new ArrayList<>();
        List<EsDataWriter> writers = new ArrayList<>();

        EsPools.getEsClient().forEach(client -> {
            DataHandler handler = new DataHandler(indexes, bulk);
            handlers.add(handler);
            writers.add(new EsDataWriter(client, handler));
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            handlers.forEach(DataHandler::shutdown);
            writers.forEach(EsDataWriter::shutdown);
        }));

        System.exit(0);
    }
}
