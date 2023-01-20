package org.rostik.andrusiv;

import com.google.cloud.storage.*;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.mongodb.FindQuery;
import org.apache.beam.sdk.io.mongodb.MongoDbIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.*;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipelineWithSelectById {

    private static final Logger LOG = LoggerFactory.getLogger(PipelineWithSelectById.class);

    public static void main(String[] args) {
        JobOptions jobOptions =
                PipelineOptionsFactory.fromArgs(args)
                        .withValidation()
                        .as(JobOptions.class);
        runProductDetails(jobOptions);
    }

    static void runProductDetails(JobOptions options) {

        Bson filter = getBson(options);

        Pipeline p = Pipeline.create(options);
        p.apply(MongoDbIO.read()
                        .withUri(options.getUri())
                        .withDatabase(options.getInputDbName())
                        .withCollection(options.getInputCollectionId())
                        .withQueryFn(FindQuery.create().withFilters(filter))
                )
                .apply(MapElements.via(new SimpleFunction<Document, Document>() {
                    @Override
                    public Document apply(Document input) {
                        //                      LOG.info("MongoDoc: " + input);
                        return input;
                    }
                }))
                .apply(ParDo.of(new MapDocumentToMyDocument()));

        //FIXME commented to run and debug this locally
//        .apply(ParDo.of(new WriteMyDocument<>()));

        p.run().waitUntilFinish();
    }

    private static Bson getBson(JobOptions options) {
        StorageOptions storageOptions = StorageOptions
                .getDefaultInstance()
                .toBuilder()
                .setProjectId(options.getProjectName())
                .build();

        Storage storage = storageOptions.getService();


        Blob blob = storage.get(options.getIdBucket(), options.getIdFileName());
        String ids = new String(blob.getContent());

        String modifiedIds = ids.replaceAll("\\b(\\b([a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}+)\\b+)\\b", "\"$1\"");
        System.out.println(modifiedIds);

        String filterJson="{\"_id\" : {$in: [" + modifiedIds + "]}}";
        Bson filterBson = BsonDocument.parse(filterJson);
        return filterBson;
    }
}