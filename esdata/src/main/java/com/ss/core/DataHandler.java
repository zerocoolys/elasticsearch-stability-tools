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
    private final BlockingQueue<MessageObject> queue;
    private final Integer[] CT = {0, 0, 1, 1, 1, 1, 1, 1, 0, 0};

    public DataHandler(int bulk) {
        this.queue = new LinkedBlockingQueue<>((int) (bulk * 1.2));
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

    public void offer(String name, MessageObject mo) {
//        System.out.println(name + "---offer---" + queue.size());
        queue.offer(mo);
    }

    /**
     * 初始化数组
     */
    public void initVisitorMap(String accessIndex) {
        int b = 0;
        for (int _index = 0; _index < CT_INIT_LENGTH; _index++, b++) {
            if (b % 10 == 0) {
                ArrayUtils.randomSort(CT);
            }
            map.put(_index, CT[_index % 10] + "**" + accessIndex);
        }
    }

}
