package com.ss.core;

import com.ss.tools.ArrayUtils;

import java.util.*;
import java.util.concurrent.*;

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
    private final BlockingQueue<MessageObject> queue;
    private final List<String> indexes;

    public DataHandler(int bulk, List<String> indexes) {
        this.queue = new LinkedBlockingQueue<>((int) (bulk * 1.2));
        this.indexes = indexes;
        init();
    }

    public void init() {
        for (int i = 0; i < indexes.size(); i++) {
            indexmap.put(indexes.get(i), ES_INDEX_START_COUNT + ES_INDEX_INCREMENT * i);
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

    public void offer(MessageObject mo) {
//        System.out.println(Thread.currentThread().getName() + "---offer---" + queue.size());
        queue.offer(mo);
    }

    /**
     * 初始化数组
     */
    public void initVisitorMap() {
//        System.out.println(Thread.currentThread().getName() + "。。开始创建访客数据");
//        System.out.println(indexmap);
//        System.out.println(indexes);
        String visitorIndex = indexes.get(0);
        for (int i = 0, _size = indexes.size(); i < _size; i++) {
            String tempIndex = indexes.get(RANDOM.nextInt(_size));
            if (indexmap.get(tempIndex) > 0) {
                visitorIndex = tempIndex;
                indexmap.put(tempIndex, indexmap.get(tempIndex) - CT_INIT_LENGTH);
                break;
            }
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
