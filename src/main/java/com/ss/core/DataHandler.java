package com.ss.core;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.StampedLock;

/**
 * Created by dolphineor on 2015-5-28.
 */
public class DataHandler implements Runnable {

    private static final int THREAD_NUMBER = Runtime.getRuntime().availableProcessors() * 2 + 1;
    private static final BlockingQueue<MessageObject> queue = new LinkedBlockingQueue<>();
    private static final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();     // 用来模拟新老访客

    private final StampedLock sLock = new StampedLock();


    public static MessageObject take() throws InterruptedException {
        return queue.take();
    }

    public void create() {
        long stamp = sLock.writeLock();
        try {
            Map<String, Object> source = new HashMap<>();
            source.putAll(RandomDataReader.getIpInfo());
            // 其它属性值数据设置

            MessageObject mo = new MessageObject();
            source.forEach(mo::add);
            queue.offer(mo);
        } finally {
            sLock.unlockWrite(stamp);
        }
    }

    @Override
    public void run() {

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUMBER);
        for (int i = 0; i < THREAD_NUMBER; i++) {
            executor.execute(() -> {
                while (true) {
                    this.create();
                }
            });

        }

    }
}
