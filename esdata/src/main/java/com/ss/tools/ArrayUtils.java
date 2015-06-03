package com.ss.tools;

import java.util.Random;

/**
 * Created by hydm on 2015/6/1.
 */
public final class ArrayUtils {
    private final static Random RANDOM = new Random();

    /**
     * 打乱数组
     */
    public static void randomSort(Integer[] array) {
        for (int index = array.length - 1; index >= 0; index--) {
            exchange(RANDOM.nextInt(index + 1), index, array);
        }
    }

    // 交换数据
    private static void exchange(int p1, int p2, Integer[] array) {
        array[p1] = array[p2] + (array[p2] = array[p1]) * 0;
    }
}
