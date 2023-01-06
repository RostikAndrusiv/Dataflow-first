package org.rostik.andrusiv;

import com.google.gson.Gson;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.mongodb.FindQuery;
import org.apache.beam.sdk.io.mongodb.MongoDbIO;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadMongoNew {

    private Gson gson = new Gson();
    private static final Logger LOG = LoggerFactory.getLogger(ReadMongoNew.class);

    public static void main(String[] args) {

        CustomOptions options =
                PipelineOptionsFactory.fromArgs(args)
                        .withValidation()
                        .as(CustomOptions.class);
        runProductDetails(options);
    }

    public interface CustomOptions extends PipelineOptions {

        @Default.String("mongodb://localhost:27017")
        String getUri();

        void setUri(String value);

        @Description("Path of the file to write to")
        @Default.String("C:\\Mentoring\\dataflow\\output_data\\outputMongo")
        String getOutputFile();

        void setOutputFile(String value);
    }

    static void runProductDetails(CustomOptions options) {
        String filterJson="{\n" +
                "     name: \"Whole Wheat Biscuit\"\n" +
                "}";

        Bson dc = BsonDocument.parse(filterJson);

        Pipeline p = Pipeline.create(options);

        p.apply(MongoDbIO.read()
                        .withUri(options.getUri())
                .withDatabase("mygrocerylist")
                .withCollection("groceryitems")
                .withQueryFn(FindQuery.create().withFilters(dc)))
                .apply(ParDo.of(new ConvertToJson()))

                .apply(MapElements.via(new SimpleFunction<String, String >() {
                    @Override
                    public String apply(String input) {
                        LOG.info("MongoDoc: " + input);
                        return input;
                    }
                }))

                .apply("WriteSalesDetails", TextIO.write()
                        .to(options.getOutputFile())
                        .withoutSharding()
                        .withShardNameTemplate("-SSS")
                        .withSuffix(".txt"));

        p.run().waitUntilFinish();
    }

    static class ConvertToJson extends DoFn<Document, String> {

        @ProcessElement
        public void processElement(@Element Document element, OutputReceiver<String> receiver) {
            String elementString = element.toJson();
            receiver.output(elementString);
        }
    }
}
