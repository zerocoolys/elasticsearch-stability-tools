package com.ss.es;

import com.ss.core.Constants;
import com.ss.core.DataHandler;
import com.ss.core.MessageObject;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by dolphineor on 2015-5-29.
 */
public class EsDataWriter implements Constants {

    private static final int THREAD_NUMBER = Runtime.getRuntime().availableProcessors() * 2;

    private final TransportClient client;
    private final DataHandler handler;
    private final ExecutorService executor;

    public EsDataWriter(TransportClient client, DataHandler handler) {
        this.client = client;
        this.handler = handler;
        this.executor = Executors.newFixedThreadPool(THREAD_NUMBER);
        write();
    }

    private void write() {
        for (int i = 0; i < THREAD_NUMBER; i++) {
            executor.execute(() -> {
                BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
                while (true) {
                    try {
                        Optional<MessageObject> optional = Optional.of(handler.take());
                        if (!optional.isPresent())
                            continue;

                        Map<String, Object> requestMap = optional.get().getAttribute();
                        IndexRequestBuilder builder = client.prepareIndex();

                        XContentBuilder contentBuilder = jsonBuilder().startObject();

                        // 设置field
                        for (Map.Entry<String, Object> entry : requestMap.entrySet())
                            contentBuilder.field(entry.getKey(), entry.getValue());

                        contentBuilder.endObject();

                        builder.setIndex(requestMap.get(ES_INDEX).toString());
                        builder.setType(requestMap.get(ES_T).toString());
                        builder.setSource(contentBuilder);


                        bulkRequestBuilder.add(builder.request());
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }


                    if (handler.queueIsEmpty() && bulkRequestBuilder.numberOfActions() > 0) {
                        submitRequest(bulkRequestBuilder);
                        bulkRequestBuilder = client.prepareBulk();
                        continue;
                    }

                    if (bulkRequestBuilder.numberOfActions() == EsPools.getBulkRequestNumber()) {
                        submitRequest(bulkRequestBuilder);
                        bulkRequestBuilder = client.prepareBulk();
                    }
                }
            });
        }
    }

    private void submitRequest(BulkRequestBuilder bulkRequestBuilder) {
        BulkResponse responses = bulkRequestBuilder.get();
        if (responses.hasFailures()) {
            System.out.println("Failure: " + responses.buildFailureMessage());
        }
    }

    public void shutdown() {
        Objects.requireNonNull(executor);
        executor.shutdown();
    }

    class BulkRequestActionListener implements ActionListener<BulkResponse> {
        @Override
        public void onResponse(BulkResponse r) {
            if (r.hasFailures()) {
                System.out.println("Failure: " + r.buildFailureMessage());
            }
        }

        @Override
        public void onFailure(Throwable e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}
