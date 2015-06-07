package com.ss;

import com.ss.core.DataHandler;
import com.ss.es.EsDataProducer;
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

//        int startOffset = Integer.valueOf(args[1]);
//        int endOffset = Integer.valueOf(args[2]);
//        List<String> indexes = IndexGenerator.createIndexes(startOffset, endOffset);

        int size = Integer.valueOf(args[1]);
        List<String> indexes = IndexGenerator.createIndexes(size);

        List<EsDataProducer> producers = new ArrayList<>();
        List<EsDataWriter> writers = new ArrayList<>();

        EsPools.getEsClient().forEach(client -> {
            DataHandler handler = new DataHandler(bulk, indexes);
            producers.add(new EsDataProducer(handler));
            writers.add(new EsDataWriter(client, handler));
        });


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            producers.forEach(EsDataProducer::shutdown);
            writers.forEach(EsDataWriter::shutdown);
        }));


        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }
}
