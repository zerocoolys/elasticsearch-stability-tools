package com.ss.core;

import com.ss.tools.ArrayUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.StampedLock;

import static com.ss.core.Constants.CT_INIT_LENGTH;
import static com.ss.core.Constants.ES_INDEX;
import static com.ss.core.RandomDataReader.getIndexInfo;

/**
 * Created by dolphineor on 2015-5-28.
 */
public class DataHandler implements Runnable {

    public static final int THREAD_NUMBER = Runtime.getRuntime().availableProcessors() * 2 + 1;

    private static final BlockingQueue<MessageObject> queue = new LinkedBlockingQueue<>();
    private static final ConcurrentHashMap<Integer, String> map = new ConcurrentHashMap<>();     // 用来模拟新老访客

    private final StampedLock sLock = new StampedLock();

    private final StampedLock ssLock = new StampedLock();

    public static MessageObject take() throws InterruptedException {
        return queue.take();
    }

    public static boolean isEmpty() {
        return queue.isEmpty();
    }

    public void create() {
        long stamp = sLock.writeLock();
        if (map.isEmpty()) {
            initVisitorMap();
        }

        try {
            MessageObject mo = new MessageObject();
            String temp = map.remove(map.size() - 1);
            mo.add(ES_INDEX, temp.substring(3));

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
            queue.offer(mo);
        } finally {
            sLock.unlockWrite(stamp);
        }
    }

    /**
     * 初始化数组
     */
    private void initVisitorMap() {
        String accessIndex = getIndexInfo();
        int[] ct = {0, 0, 0, 0, 1, 1, 1, 1, 0, 0};
        int b = 0;
        for (int _index = 0; _index < CT_INIT_LENGTH; _index++, b++) {
            if (b % 10 == 0) {
                ArrayUtils.randomSort(ct);
            }
            map.put(_index, ct[_index % 10] + "**" + accessIndex);
        }
        System.out.println(accessIndex);
        System.out.println(queue.size());
    }

    @Override
    public void run() {

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUMBER);
        for (int i = 0; i < THREAD_NUMBER; i++) {
            executor.execute(() -> {
                while (true) {
                    create();
                }
            });
        }

    }
}
