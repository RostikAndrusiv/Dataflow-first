package org.rostik.andrusiv;

import com.google.cloud.firestore.*;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.transforms.DoFn;
import org.rostik.andrusiv.model.PersonFirestore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FirestoreWriteDoFn<In> extends DoFn<In, Void> {

    private static final Logger LOG = LoggerFactory.getLogger(PipelineWithMapping.class);

    private static final long serialVersionUID = 2L;
    private transient List<PersonFirestore> personList;

    private transient Firestore db;

    public FirestoreWriteDoFn() {
    }

    @StartBundle
    public void setup(StartBundleContext startBundleContext) throws IOException {

        PipelineOptions pipelineOptions = startBundleContext.getPipelineOptions();
        JobOptions options = pipelineOptions.as(JobOptions.class);
        FirestoreOptions firestoreOptions = FirestoreOptions
                .getDefaultInstance()
                .toBuilder()
                .setProjectId(options.getProjectName())
                .build();

        this.personList = new ArrayList<>();
        this.db = firestoreOptions.getService();
    }

    @ProcessElement
    public void processElement(ProcessContext context) throws Exception {
        PersonFirestore person = (PersonFirestore) context.element();
        personList.add(person);

        // Batch size set to 200, max size is 500
        if (personList.size() >= 200) {
            flushBatch(context.getPipelineOptions());
        }
    }

    private void flushBatch(PipelineOptions pipelineOptions) throws Exception {
        if (personList.isEmpty()) {
            return;
        }
        JobOptions options = pipelineOptions.as(JobOptions.class);
        List<PersonFirestore> processed = new ArrayList<>();
        // Create batch to commit documents
        WriteBatch batch = db.batch();

        for (PersonFirestore doc : personList) {
            DocumentReference docRef = db.collection(options.getOutputCollectionId()).document(doc.getId());
            batch.set(docRef, doc);
            processed.add(doc);
        }
        batch.commit();
        personList.removeAll(processed);
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
