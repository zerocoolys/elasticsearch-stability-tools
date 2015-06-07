package com.ss.es;

import com.ss.core.Constants;
import com.ss.core.DataHandler;
import com.ss.core.MessageObject;
import com.ss.core.RandomDataReader;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by hydm on 2015/6/1.
 */
public class EsDataProducer implements Constants {

    private static final int THREAD_NUMBER = 1;

    private final ExecutorService executor;
    private DataHandler handler;
    private final ReentrantLock lock = new ReentrantLock();

    public EsDataProducer(DataHandler handler) {
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
        lock.lock();
        String temp;
        try {
            if (handler.mapIsEmpty()) {
                handler.initVisitorMap();
            }
            temp = handler.removeMap();

            if (temp == null) {
                return;
            }
        } finally {
            lock.unlock();
        }

//        System.out.println(Thread.currentThread().getName() + "--------" + temp);

        MessageObject mo = new MessageObject();
        mo.add(ES_INDEX, temp.substring(3));
        mo.add(ES_CT, temp.substring(0, 1));

        Map<String, Object> source = new HashMap<>();
        source.putAll(RandomDataReader.getIpInfo());
        source.forEach(mo::add);
        // 其它属性值数据设置

        Map<String, Object> loc_uTime = new HashMap<>();
        loc_uTime.putAll(RandomDataReader.getLocAndUTimeInfo(temp.substring(11)));
        loc_uTime.forEach(mo::add);

        // 操作系统以及设备终端
        Map<String, String> os_pm = new HashMap<>();
        os_pm.putAll(RandomDataReader.getOSAndPMInfo());
        os_pm.forEach(mo::add);

        Map<String, Object> rfType = new HashMap<>();
        rfType.putAll(RandomDataReader.getRfTypeInfo());
        rfType.forEach(mo::add);
        try {
            handler.offer(mo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        Objects.requireNonNull(executor);
        executor.shutdown();
    }

}
