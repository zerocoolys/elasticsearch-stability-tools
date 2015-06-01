package com.ss.tools;

import java.util.Random;

/**
 * Created by hydm on 2015/6/1.
 */
public final class ArrayUtils {
    private final static Random RANDOM = new Random();

    // 随机数组排序
    public static void randomSort(int[] array) {
        for (int index = array.length - 1; index >= 0; index--) {
            exchange(RANDOM.nextInt(index + 1), index, array);
        }
    }

    // 交换
    private static void exchange(int p1, int p2, int[] array) {
        array[p1] = array[p2] + (array[p2] = array[p1]) * 0;
    }
}
