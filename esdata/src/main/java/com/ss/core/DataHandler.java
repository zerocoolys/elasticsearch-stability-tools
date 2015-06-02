package com.ss.core;

import com.ss.tools.ArrayUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

import static com.ss.core.Constants.*;
import static com.ss.core.RandomDataReader.getIndexInfo;

/**
 * Created by dolphineor on 2015-5-28.
 */
public class DataHandler {

    private static final int THREAD_NUMBER = 2;

    private final ConcurrentHashMap<Integer, String> map = new ConcurrentHashMap<>();     // 用来模拟新老访客
    private final BlockingQueue<MessageObject> queue;
    private final ExecutorService executor;
    private final List<String> indexes;

    public DataHandler(List<String> indexes, int bulk) {
        this.executor = Executors.newFixedThreadPool(THREAD_NUMBER);
        this.queue = new LinkedBlockingQueue<>((int) (bulk * 1.2));
        this.indexes = indexes;
        run();
    }

    public MessageObject take() throws InterruptedException {
        return queue.take();
    }

    public boolean queueIsEmpty() {
        return queue.isEmpty();
    }

    public void create() {
        if (map.isEmpty()) {
            initVisitorMap();
        }

        MessageObject mo = new MessageObject();
        String temp = map.remove(map.size() - 1);
        mo.add(ES_INDEX, temp.substring(3));

        Map<String, Object> source = new HashMap<>();
        source.putAll(RandomDataReader.getIpInfo());
        mo.add(ES_CT, temp.substring(0, 1));
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
        try {
            queue.put(mo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 初始化数组
     */
    private void initVisitorMap() {
        String accessIndex = getIndexInfo();
        int[] ct = {0, 0, 1, 1, 1, 1, 1, 1, 0, 0};
        int b = 0;
        for (int _index = 0; _index < CT_INIT_LENGTH; _index++, b++) {
            if (b % 10 == 0) {
                ArrayUtils.randomSort(ct);
            }
            map.put(_index, ct[_index % 10] + "**" + accessIndex);
        }
//        System.out.println(accessIndex);
//        System.out.println(queue.size());
    }

    private void run() {
        for (int i = 0; i < THREAD_NUMBER; i++) {
            executor.execute(() -> {
                while (true) {
                    create();
                }
            });
        }

    }

    public void shutdown() {
        Objects.requireNonNull(executor);
        executor.shutdown();
    }
}
