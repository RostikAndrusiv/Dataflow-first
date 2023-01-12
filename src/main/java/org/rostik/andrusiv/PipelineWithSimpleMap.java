package org.rostik.andrusiv;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.mongodb.MongoDbIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.*;
import org.bson.Document;
import org.rostik.andrusiv.model.PersonFirestore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipelineWithSimpleMap {

    private static final Logger LOG = LoggerFactory.getLogger(PipelineWithMapping.class);

    public static void main(String[] args) {
        JobOptions jobOptions =
                PipelineOptionsFactory.fromArgs(args)
                        .withValidation()
                        .as(JobOptions.class);

        runProductDetails(jobOptions);
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
                .apply(ParDo.of(new WriteMap<Document>()));

        p.run().waitUntilFinish();
    }
}
