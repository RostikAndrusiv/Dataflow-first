package org.rostik.andrusiv;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firestore.v1.Value;
import lombok.*;
import lombok.ToString;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.firestore.RpcQosOptions;
import org.apache.beam.sdk.io.mongodb.MongoDbIO;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.*;
import org.bson.Document;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ReadWrite {
//
//    private static final FirestoreOptions FIRESTORE_OPTIONS = FirestoreOptions.getDefaultInstance();
//
//    private static final Logger LOG = LoggerFactory.getLogger(ReadWrite.class);
//
//    public static void main(String[] args) {
//
//        JobOptions jobOptions =
//                PipelineOptionsFactory.fromArgs(args)
//                        .withValidation()
//                        .as(JobOptions.class);
//
//        RpcQosOptions rpcQosOptions =
//                RpcQosOptions.newBuilder()
//                        .build();
//
//        runProductDetails(jobOptions, rpcQosOptions, "grocery-items");
//    }
//
//    public interface JobOptions extends PipelineOptions {
//
//        @Default.String("phrasal-client-372213")
//        String getProject();
//
//        void setProject(String value);
//        @Default.String("mongodb+srv://mongo:cNR2Q7ZtQ7WSDuBR@cluster0.klkb0yr.mongodb.net/mygrocerylist")
//        String getUri();
//
//        void setUri(String value);
//
//        @Default.String("grocery-items")
//        String getCollectionId();
//
//        void setCollectionId(String value);
//
//        @Default.String("mongodb://localhost:27017")
//        String getLocalUri();
//        void setLocalUri(String value);
//
//        @Description("Path of the file to write to")
//        @Default.String("C:\\Mentoring\\dataflow\\output_data\\outputMongo")
//        String getOutputFile();
//
//        void setOutputFile(String value);
//    }
//
//    static void runProductDetails(JobOptions options, RpcQosOptions rpcQosOptions, String collectionId) {
//
//        Pipeline p = Pipeline.create(options);
//
//        p.apply(MongoDbIO.read()
//                        .withUri(options.getUri())
//                        .withDatabase("mygrocerylist")
//                        .withCollection("groceryitems")
//                )
//                .apply(MapElements.via(new SimpleFunction<Document, Document>() {
//                    @Override
//                    public Document apply(Document input) {
//                        LOG.info("MongoDoc: " + input);
//                        return input;
//                    }
//                }))
//                .apply(ParDo.of(new ConvertToJson()))
//                .apply(ParDo.of(new JsonToFirestoreDocument()))
//                .apply(ParDo.of(new FirestoreWriteDoFn<com.google.firestore.v1.Document>()));
//
////        p.apply(Create.of(write1, write2))
////                .apply(FirestoreIO.v1().write().batchWrite().withRpcQosOptions(rpcQosOptions).build());
//
//
//        p.run().waitUntilFinish();
//    }
//
//    static class ConvertToJson extends DoFn<Document, String> {
//
//        @ProcessElement
//        public void processElement(@Element Document element, OutputReceiver<String> receiver) {
//            String elementString = element.toJson();
//            receiver.output(elementString);
//        }
//    }
//
//    static class JsonToFirestoreDocument extends DoFn<String, com.google.firestore.v1.Document> {
//
//        @ProcessElement
//        public void processElement(ProcessContext context, OutputReceiver<FSCollectionObject> receiver) {
//            JSONObject jsonObject = new JSONObject(context.element());
//            String id = (String) jsonObject.get("_id");
//            FSCollectionObject fsCollectionObject = new FSCollectionObject(id, context.element());
//            receiver.output(fsCollectionObject);
//        }
//    }
//
////    private static String createDocument(JobOptions jobOptions, String cityDocId) {
////        String documentPath =
////                String.format(
////                        "projects/%s/databases/%s/documents",
////                        FIRESTORE_OPTIONS.getProjectId(), FIRESTORE_OPTIONS.getDatabaseId());
////
////        return documentPath + "/" + jobOptions.getCollectionId() + "/" + cityDocId;
////    }
//
////    private static String createDocumentName(PipelineOptions pipelineOptions, String cityDocId) {
////        JobOptions options = pipelineOptions.as(JobOptions.class);
////        String documentPath =
////                String.format(
////                        "projects/%s/databases/%s/documents",
////                        FIRESTORE_OPTIONS.getProjectId(), FIRESTORE_OPTIONS.getDatabaseId());
////        return documentPath + "/" + options.getCollectionId() + "/" + cityDocId;
////    }
//    public static class FirestoreWriteDoFn<In> extends DoFn<In, Void> {
//
//        private static final long serialVersionUID = 2L;
//        private transient List<FSCollectionObject> mutations;
//        private transient Firestore db;
//
//        public FirestoreWriteDoFn() {
//
//        }
//
//        @StartBundle
//        public void setupBufferedMutator(StartBundleContext startBundleContext) throws IOException {
//            this.mutations = new ArrayList<FSCollectionObject>();
//        }
//
//        @ProcessElement
//        public void processElement(ProcessContext context) throws Exception {
//            PipelineOptions pipelineOptions = context.getPipelineOptions();
//            JobOptions options = pipelineOptions.as(JobOptions.class);
//
//            // Create firestore instance
//            FirestoreOptions firestoreOptions = FirestoreOptions
//                    .getDefaultInstance().toBuilder()
//                    .setProjectId(options.getProject())
//                    .build();
//
//            db = firestoreOptions.getService();
//
//
//            FSCollectionObject mutation = (FSCollectionObject) context.element();
//            mutations.add(mutation);
//            // Batch size set to 200, could go up to 500
//            if (mutations.size() >= 200) {
//                flushBatch();
//            }
//        }
//
//        private void flushBatch(JobOptions options, Firestore firestore) throws Exception {
//
//
//            // Create batch to commit documents
//
//            WriteBatch batch = db.batch();
//
//            for (FSCollectionObject doc : mutations) {
//                DocumentReference docRef = db.collection(options.getCollectionId()).document(doc.getId());
//                batch.set(docRef, doc);
//            }
//
//            ApiFuture<List<WriteResult>> wr = batch.commit();
//            if (wr.isDone()) {
//                db.close();
//            }
//        }
//        @FinishBundle
//        public synchronized void finishBundle(FinishBundleContext context) throws Exception {
//            if (mutations.size() > 0) {
//                flushBatch(context.getPipelineOptions());
//            }
//            // close connection
//            db.close();
//        }
//    }
//
//    public static class FSCollectionObject implements Serializable {
//        public FSCollectionObject() {
//        }
//
//        @Override
//        public String toString() {
//            return "FSCollectionObject{" +
//                    "id='" + id + '\'' +
//                    ", keyset='" + keyset + '\'' +
//                    '}';
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (o == null || getClass() != o.getClass()) return false;
//            FSCollectionObject that = (FSCollectionObject) o;
//            return Objects.equals(id, that.id) && Objects.equals(keyset, that.keyset);
//        }
//
//        @Override
//        public int hashCode() {
//            return Objects.hash(id, keyset);
//        }
//
//        public String getId() {
//            return id;
//        }
//
//        public void setId(String id) {
//            this.id = id;
//        }
//
//        public String getKeyset() {
//            return keyset;
//        }
//
//        public void setKeyset(String keyset) {
//            this.keyset = keyset;
//        }
//
//        public FSCollectionObject(String id, String keyset) {
//            this.id = id;
//            this.keyset = keyset;
//        }
//
//        private String id;
//        private String keyset;
//    }
}