package com.ss.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by dolphineor on 2015-5-28.
 */
public class DataHandler {

    private final BlockingQueue<MessageObject> queue = new LinkedBlockingQueue<>();

}
