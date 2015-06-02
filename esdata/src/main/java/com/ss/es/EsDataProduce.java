package com.ss.es;

import com.ss.core.Constants;
import com.ss.core.DataHandler;
import com.ss.core.MessageObject;
import com.ss.core.RandomDataReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hydm on 2015/6/1.
 */
public class EsDataProduce implements Constants {

    public static final int THREAD_NUMBER = 2;

    private final ExecutorService executor;

    private List<String> indexes;

    private DataHandler handler;

    public EsDataProduce(List<String> indexes, DataHandler handler) {
        this.indexes = indexes;
        this.handler = handler;
        this.executor = Executors.newFixedThreadPool(THREAD_NUMBER);
        produce();
    }

    private void produce() {
        for (int i = 0; i < THREAD_NUMBER; i++) {
            executor.execute(() -> {
                while (true) {
                    create();
                }
            });
        }
    }

    public void create() {
        if (handler.mapIsEmpty()) {
            handler.initVisitorMap(RandomDataReader.getIndexInfo(indexes));
        }

        MessageObject mo = new MessageObject();
        String temp = handler.removeMap();
        mo.add(ES_INDEX, temp.substring(8));
        mo.add(ES_CT, temp.substring(0, 1));

        Map<String, Object> source = new HashMap<>();
        source.putAll(RandomDataReader.getIpInfo());
        source.forEach(mo::add);
        // 其它属性值数据设置

        Map<String, Object> loc = new HashMap<>();
        loc.putAll(RandomDataReader.getLocInfo(mo.get(ES_INDEX)));
        loc.forEach(mo::add);

        Map<String, String> os = new HashMap<>();
        os.putAll(RandomDataReader.getOSInfo());
        os.forEach(mo::add);

        Map<String, String> pm = new HashMap<>();
        pm.putAll(RandomDataReader.getPMInfo());
        pm.forEach(mo::add);

        Map<String, Object> rfType = new HashMap<>();
        rfType.putAll(RandomDataReader.getRfTypeInfo());
        rfType.forEach(mo::add);
        handler.offer(Thread.currentThread().getName(), mo);
    }

    public void shutdown() {
        Objects.requireNonNull(executor);
        executor.shutdown();
    }

}
