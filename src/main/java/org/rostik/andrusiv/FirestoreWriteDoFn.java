package org.rostik.andrusiv;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.WriteBatch;
import com.google.cloud.firestore.WriteResult;
import com.google.firestore.v1.Write;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.transforms.DoFn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FirestoreWriteDoFn<In> extends DoFn<In, Void> {

    private static final long serialVersionUID = 2L;
    private transient List<com.google.firestore.v1.Document> mutations;
    private transient Firestore db;

    public FirestoreWriteDoFn() {
    }

    @StartBundle
    public void setupBufferedMutator(StartBundleContext startBundleContext) throws IOException {
        this.mutations = new ArrayList<com.google.firestore.v1.Document>();
    }

    @ProcessElement
    public void processElement(ProcessContext context) throws Exception {
        com.google.firestore.v1.Document mutation = (com.google.firestore.v1.Document) context.element();
        mutations.add(mutation);
        // Batch size set to 200, could go up to 500
        if (mutations.size() >= 200) {
            flushBatch(context.getPipelineOptions());
        }
    }

    private void flushBatch(PipelineOptions pipelineOptions) throws Exception {

        JobOptions options = pipelineOptions.as(JobOptions.class);

        // Create firestore instance
        FirestoreOptions firestoreOptions = FirestoreOptions
                .getDefaultInstance().toBuilder()
                .setProjectId(options.getProjectName())
                .build();

        db = firestoreOptions.getService();

        // Create batch to commit documents
        WriteBatch batch = db.batch();

        for (com.google.firestore.v1.Document doc : mutations) {
            DocumentReference docRef = db.collection(options.getCollectionId()).document(doc.getName());
            batch.set(docRef, doc);
        }

        ApiFuture<List<WriteResult>> wr = batch.commit();
        if (wr.isDone()) return;
    }
    @FinishBundle
    public synchronized void finishBundle(FinishBundleContext context) throws Exception {
        if (mutations.size() > 0) {
            flushBatch(context.getPipelineOptions());
        }
        // close connection
        db.close();
    }
}
