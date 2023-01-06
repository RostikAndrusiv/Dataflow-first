package org.rostik.andrusiv;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.gson.Gson;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.mongodb.MongoDbIO;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.*;
import org.bson.Document;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class WriteFirestoreSimpleMap {
    private Gson gson = new Gson();
    private static final Logger LOG = LoggerFactory.getLogger(WriteFirestoreSimpleMap.class);

    public static void main(String[] args) {
        JobOptions jobOptions =
                PipelineOptionsFactory.fromArgs(args)
                        .withValidation()
                        .as(JobOptions.class);

        runProductDetails(jobOptions);
    }

    public interface JobOptions extends PipelineOptions {

        @Default.String("phrasal-client-372213")
        String getProjectName();

        void setProjectName(String value);

        @Default.String("mongodb+srv://mongo:cNR2Q7ZtQ7WSDuBR@cluster0.klkb0yr.mongodb.net/Product")
        String getUri();

        void setUri(String value);

        @Default.String("Product")
        String getInputDbName();

        void setInputDbName(String value);

        @Default.String("Products")
        String getInputCollectionId();

        void setInputCollectionId(String value);

        @Default.String("outputCollectionId")
        String getOutputCollectionId();

        void setOutputCollectionId(String value);
    }

    static void runProductDetails(JobOptions options) {

        Pipeline p = Pipeline.create(options);

        p.apply(MongoDbIO.read()
                        .withUri(options.getUri())
                        .withDatabase(options.getInputDbName())
                        .withCollection(options.getInputCollectionId())
                )
                .apply(MapElements.via(new SimpleFunction<Document, Document>() {
                    @Override
                    public Document apply(Document input) {
                        LOG.info("MongoDoc: " + input);
                        return input;
                    }
                }))
                .apply(ParDo.of(new ConvertToJson()))
                .apply(ParDo.of(new JsonToFirestoreDocument()))
                .apply(ParDo.of(new FirestoreWriteDoFn<FSCollectionObject>()));

        p.run().waitUntilFinish();
    }

    static class ConvertToJson extends DoFn<Document, String> {

        @ProcessElement
        public void processElement(@Element Document element, OutputReceiver<String> receiver) {
            String elementString = element.toJson();
            receiver.output(elementString);
        }
    }

    static class JsonToFirestoreDocument extends DoFn<String, FSCollectionObject> {

        @ProcessElement
        public void processElement(ProcessContext context, OutputReceiver<FSCollectionObject> receiver) {
//            if (null != context && null != context.element()) {
//                JSONObject jsonObject = new JSONObject(Objects.requireNonNull(context.element()));
//                if (null == jsonObject.get("_id")) {
//                    return;
//                }
//                String id = jsonObject.get("_id").toString();
            Map<String, Object> data = new HashMap<>();
            JSONObject jsonObject = new JSONObject(Objects.requireNonNull(context.element()));
            Set<String> strings = jsonObject.keySet();
            strings.forEach(key-> data.put(key, jsonObject.get(key).toString()));

            String uuid = UUID.randomUUID().toString();
            FSCollectionObject fsCollectionObject = new FSCollectionObject(uuid, data);
            receiver.output(fsCollectionObject);
//            }
        }
    }

    public static class FirestoreWriteDoFn<In> extends DoFn<In, Void> {

        private static final long serialVersionUID = 2L;
        private transient List<FSCollectionObject> mutations;

        private transient Firestore db;

        public FirestoreWriteDoFn() {
        }

        @StartBundle
        public void setupBufferedMutator(StartBundleContext startBundleContext) throws IOException {

            PipelineOptions pipelineOptions = startBundleContext.getPipelineOptions();
            JobOptions options = pipelineOptions.as(JobOptions.class);
            FirestoreOptions firestoreOptions = FirestoreOptions
                    .getDefaultInstance()
                    .toBuilder()
                    .setProjectId(options.getProjectName())
                    .build();

            this.mutations = new ArrayList<>();
            this.db = firestoreOptions.getService();
        }

        @ProcessElement
        public void processElement(ProcessContext context) throws Exception {
            FSCollectionObject mutation = (FSCollectionObject) context.element();
            mutations.add(mutation);

            // Batch size set to 200, max size is 500
            if (mutations.size() >= 200) {
                flushBatch(context.getPipelineOptions());
            }
        }

        private void flushBatch(PipelineOptions pipelineOptions) throws Exception {
            if (mutations.isEmpty()) {
                return;
            }
            JobOptions options = pipelineOptions.as(JobOptions.class);
            List<FSCollectionObject> processed = new ArrayList<>();
            // Create batch to commit documents
            WriteBatch batch = db.batch();

            for (FSCollectionObject doc : mutations) {
                DocumentReference docRef = db.collection(options.getOutputCollectionId()).document(doc.getId());
                batch.set(docRef, doc.getKeySet());
                processed.add(doc);
            }
            ApiFuture<List<WriteResult>> wr = batch.commit();
            mutations.removeAll(processed);
        }


        @FinishBundle
        public void finishBundle(FinishBundleContext context) throws Exception {
            flushBatch(context.getPipelineOptions());
            if (this.db != null) {
                db.close();
            }
        }

        @Teardown
        public void teardown() {
            try {
                if (this.db != null) {
                    this.db.close();
                }
            } catch (Exception e) {
                LOG.info("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
            }
        }
    }


    public static class FSCollectionObject implements Serializable {
        private String id;
        private Map<String, Object> keySet;

        public FSCollectionObject() {
        }

        public FSCollectionObject(String id, Map<String, Object> keySet) {
            this.id = id;
            this.keySet = keySet;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Map<String, Object> getKeySet() {
            return keySet;
        }

        public void setKeySet(Map<String, Object> keySet) {
            this.keySet = keySet;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FSCollectionObject that = (FSCollectionObject) o;
            return Objects.equals(id, that.id) && Objects.equals(keySet, that.keySet);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, keySet);
        }

        @Override
        public String toString() {
            return "FSCollectionObject{" +
                    "id='" + id + '\'' +
                    ", keySet=" + keySet +
                    '}';
        }
    }
}
