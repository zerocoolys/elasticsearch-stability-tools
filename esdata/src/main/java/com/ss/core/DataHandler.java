package com.ss.core;

import com.ss.tools.ArrayUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ss.core.Constants.*;
import static com.ss.core.RandomDataReader.getIndexInfo;

/**
 * Created by dolphineor on 2015-5-28.
 */
public class DataHandler {

    private final ConcurrentHashMap<Integer, String> map = new ConcurrentHashMap<>();     // 用来模拟新老访客
    private final ConcurrentHashMap<String, Long> indexmap = new ConcurrentHashMap<>();     // 用来模拟每个index的数据量
    private final static Random RANDOM = new Random();
    private final Integer[] CT = {0, 0, 1, 1, 1, 1, 1, 1, 0, 0};
    private final LinkedBlockingQueue<MessageObject> queue;
    private final List<String> indexes;
    private AtomicInteger i = new AtomicInteger(0);

    public DataHandler(int bulk, List<String> indexes) {
        this.queue = new LinkedBlockingQueue<>((int) (bulk * 1.2));
        this.indexes = indexes;
        init();
    }

    public void init() {
        for (int i = 0; i < indexes.size(); i++) {
            indexmap.put(indexes.get(i), ES_INDEX_BASE_COUNT + ES_INDEX_INCREMENT * i);
        }
    }

    public MessageObject take() throws InterruptedException {
        return queue.take();
    }

    public boolean queueIsEmpty() {
        return queue.isEmpty();
    }

    public boolean mapIsEmpty() {
        return map.isEmpty();
    }

    public String removeMap() {
        return map.remove(map.size() - 1);
    }

    public void offer(MessageObject mo) throws InterruptedException {
//        System.out.println(Thread.currentThread().getName() + "---offer---" + queue.size());
//        System.out.println(queue.size());
        queue.put(mo);
    }

    /**
     * 初始化数组
     */
    public void initVisitorMap() {
//        System.out.println(Thread.currentThread().getName() + "。。开始创建访客数据");
//        System.out.println(indexmap);
//        System.out.println(indexes);
//        String visitorIndex = indexes.get(0);
        String visitorIndex = "";
        int i = 0;
        while (i < indexes.size()) {
            String tempIndex = indexes.get(i);
            if (indexmap.get(tempIndex) > 0) {
                visitorIndex = tempIndex;
                indexmap.put(tempIndex, indexmap.get(tempIndex) - CT_INIT_LENGTH);
                break;
            } else {
                i++;
            }
        }
        if ("".equals(visitorIndex)) {
//            System.out.println(queue);
            System.out.println(Thread.currentThread().getName() + "。。index数据创建完成");
            return;
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int b = 0;
        for (int _index = 0; _index < CT_INIT_LENGTH; _index++, b++) {
            if (b % 10 == 0) {
                ArrayUtils.randomSort(CT);
            }
            map.put(_index, CT[_index % 10] + "**" + visitorIndex);
        }
    }

}
