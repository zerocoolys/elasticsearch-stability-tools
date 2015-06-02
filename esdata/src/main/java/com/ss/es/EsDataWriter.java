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
import java.util.List;
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

    private final TransportClient client;
    private final List<String> indexes;
    private final DataHandler handler;
    private final ExecutorService executor;

    public EsDataWriter(TransportClient client, List<String> indexes, DataHandler handler) {
        this.client = client;
        this.indexes = indexes;
        this.handler = handler;
        this.executor = Executors.newSingleThreadExecutor();
        write();
    }

    private void write() {
        executor.execute(() -> {
            BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
            while (true) {
                try {
                    Optional<MessageObject> optional = Optional.of(handler.take());
                    if (!optional.isPresent())
                        continue;

                    Map<String, Object> requestMap = optional.get().getAttribute();

                    for (String index : indexes) {
                        IndexRequestBuilder builder = client.prepareIndex();

                        XContentBuilder contentBuilder = jsonBuilder().startObject();

                        // 设置field
                        for (Map.Entry<String, Object> entry : requestMap.entrySet())
                            contentBuilder.field(entry.getKey(), entry.getValue());

                        contentBuilder.endObject();

                        builder.setIndex(index);
                        builder.setType(requestMap.get(ES_T).toString());
                        builder.setSource(contentBuilder);


                        bulkRequestBuilder.add(builder.request());
                    }
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }


                if (handler.queueIsEmpty() && bulkRequestBuilder.numberOfActions() > 0) {
                    bulkRequestBuilder.execute().addListener(new ActionListener<BulkResponse>() {
                        @Override
                        public void onResponse(BulkResponse bulkItemResponses) {
                            System.out.println(bulkItemResponses.buildFailureMessage());
                        }

                        @Override
                        public void onFailure(Throwable e) {
                            System.out.println(e.getMessage());
                        }
                    });
                    bulkRequestBuilder = client.prepareBulk();
                    continue;
                }

                if (bulkRequestBuilder.numberOfActions() == EsPools.getBulkRequestNumber()) {
                    bulkRequestBuilder.execute().addListener(new ActionListener<BulkResponse>() {
                        @Override
                        public void onResponse(BulkResponse bulkItemResponses) {
                            System.out.println(bulkItemResponses.buildFailureMessage());
                        }

                        @Override
                        public void onFailure(Throwable e) {
                            System.out.println(e.getMessage());
                        }
                    });
                    bulkRequestBuilder = client.prepareBulk();
                }
            }
        });
    }

    public void shutdown() {
        Objects.requireNonNull(executor);
        executor.shutdown();
    }
}
