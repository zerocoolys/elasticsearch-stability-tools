package com.ss.es;

import com.ss.core.Constants;
import com.ss.core.MessageObject;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;

import static com.ss.core.DataHandler.isEmpty;
import static com.ss.core.DataHandler.take;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by dolphineor on 2015-5-29.
 */
public class EsDataWriter implements Constants {

    private final TransportClient client;
    private final List<String> indexes;

    public EsDataWriter(TransportClient client, List<String> indexes) {
        this.client = client;
        this.indexes = indexes;
        write();
    }

    private void write() {
        Executors.newSingleThreadExecutor().execute(() -> {
            BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
            while (true) {
                try {
                    Optional<MessageObject> optional = Optional.of(take());
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


                if (isEmpty() && bulkRequestBuilder.numberOfActions() > 0) {
                    bulkRequestBuilder.get();
                    bulkRequestBuilder = client.prepareBulk();
                    continue;
                }

                if (bulkRequestBuilder.numberOfActions() == EsPools.getBulkRequestNumber()) {
                    bulkRequestBuilder.get();
                    bulkRequestBuilder = client.prepareBulk();
                }
            }
        });
    }
}
