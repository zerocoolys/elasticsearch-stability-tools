package com.ss;

import com.ss.core.DataHandler;
import com.ss.es.EsDataWriter;
import com.ss.es.EsPools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dolphineor on 2015-5-28.
 */
public class ESDataGeneratorMain {

    public static void main(String[] args) {
//        int bulk = Integer.valueOf(args[0]);
//        EsPools.setBulkRequestNumber(bulk);
//        List<String> indexes = new ArrayList<>();
        DataHandler dh = new DataHandler();

        new Thread(dh).start();

//        EsPools.getEsClient().forEach(client -> new EsDataWriter(client, indexes));

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}