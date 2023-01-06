package org.rostik.andrusiv;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.gson.Gson;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.firestore.RpcQosOptions;
import org.apache.beam.sdk.io.mongodb.MongoDbIO;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.*;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class ReadWriteNew {
    private Gson gson = new Gson();
    private static final Logger LOG = LoggerFactory.getLogger(ReadWrite.class);

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

        @Default.String("blah")
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
            String uuid = UUID.randomUUID().toString();
            FSCollectionObject fsCollectionObject = new FSCollectionObject(uuid, context.element());
            receiver.output(fsCollectionObject);
//            }
        }
    }

    public static class FirestoreWriteDoFn<In> extends DoFn<In, Void> {

        private static final long serialVersionUID = 2L;
        private transient List<FSCollectionObject> mutations;


        public FirestoreWriteDoFn() {
        }

        @StartBundle
        public void setupBufferedMutator(StartBundleContext startBundleContext) throws IOException {
            this.mutations = new ArrayList<>();
        }

        @ProcessElement
        public synchronized void processElement(ProcessContext context) throws Exception {
            FSCollectionObject mutation = (FSCollectionObject) context.element();
            mutations.add(mutation);

            // Batch size set to 200, max size is 500
            if (mutations.size() >= 200) {
                flushBatch(context.getPipelineOptions());
            }
        }

        private synchronized void flushBatch(PipelineOptions pipelineOptions) throws Exception {

            List<FSCollectionObject> batchList;
            batchList = List.copyOf(mutations);

            mutations.removeAll(batchList);

            JobOptions options = pipelineOptions.as(JobOptions.class);

            // Create firestore instance
            FirestoreOptions firestoreOptions = FirestoreOptions
                    .getDefaultInstance().toBuilder()
                    .setProjectId(options.getProjectName())
                    .build();

            try (Firestore db = firestoreOptions.getService()) {

                // Create batch to commit documents
                WriteBatch batch = db.batch();

                for (FSCollectionObject doc : batchList) {
                    DocumentReference docRef = db.collection(options.getOutputCollectionId()).document(doc.getId());
                    batch.set(docRef, doc);
                }
                ApiFuture<List<WriteResult>> wr = batch.commit();
            } catch (Exception e) {
                LOG.info("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
            }
        }

        @FinishBundle
        public synchronized void finishBundle(PipelineOptions pipelineOptions) throws Exception {
            if (!mutations.isEmpty()) {
                flushBatch(pipelineOptions);
            }
        }
    }

    public static class FSCollectionObject implements Serializable {
        private String id;
        private String keyset;

        public FSCollectionObject() {
        }

        @Override
        public String toString() {
            return "FSCollectionObject{" +
                    "id='" + id + '\'' +
                    ", keyset='" + keyset + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FSCollectionObject that = (FSCollectionObject) o;
            return Objects.equals(id, that.id) && Objects.equals(keyset, that.keyset);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, keyset);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getKeyset() {
            return keyset;
        }

        public void setKeyset(String keyset) {
            this.keyset = keyset;
        }

        public FSCollectionObject(String id, String keyset) {
            this.id = id;
            this.keyset = keyset;
        }
    }
}
