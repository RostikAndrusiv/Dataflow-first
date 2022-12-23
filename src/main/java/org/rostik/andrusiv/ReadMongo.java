package org.rostik.andrusiv;

import com.google.gson.Gson;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.mongodb.MongoDbIO;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadMongo {

    private Gson gson = new Gson();
    private static final Logger LOG = LoggerFactory.getLogger(ReadMongo.class);

    public static void main(String[] args) {

        CustomOptions options =
                PipelineOptionsFactory.fromArgs(args)
                        .withValidation()
                        .as(CustomOptions.class);

        runProductDetails(options);
    }

    public interface CustomOptions extends PipelineOptions {

        @Default.String("mongodb+srv://mongo:cNR2Q7ZtQ7WSDuBR@cluster0.klkb0yr.mongodb.net/mygrocerylist")
        String getUri();

        void setUri(String value);

        @Description("Path of the file to write to")
        @Default.String("C:\\Mentoring\\dataflow\\output_data\\outputMongo")
        String getOutputFile();

        void setOutputFile(String value);
    }

    static void runProductDetails(CustomOptions options) {

        Pipeline p = Pipeline.create(options);

        p.apply(MongoDbIO.read()
                        .withUri(options.getUri())
                        .withDatabase("mygrocerylist")
                        .withCollection("groceryitems"))
                .apply(ParDo.of(new ConvertToJson()))
                .apply(MapElements.via(new SimpleFunction<String, String>() {
                    @Override
                    public String apply(String input) {
                        System.out.println("MongoDoc: " + input);
                        LOG.info("MongoDoc: " + input);
                        return input;
                    }
                }))
                .apply("WriteSalesDetails", TextIO.write()
                        .to(options.getOutputFile())
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