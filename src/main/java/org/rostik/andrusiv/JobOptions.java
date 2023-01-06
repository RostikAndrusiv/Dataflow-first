package org.rostik.andrusiv;

import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;

public interface JobOptions extends PipelineOptions {

    @Default.String("phrasal-client-372213")
    String getProjectName();

    void setProjectName(String value);
    @Default.String("mongodb+srv://mongo:cNR2Q7ZtQ7WSDuBR@cluster0.klkb0yr.mongodb.net/mygrocerylist")
    String getUri();

    void setUri(String value);

    @Default.String("grocery-items")
    String getCollectionId();

    void setCollectionId(String value);

    @Default.String("mongodb://localhost:27017")
    String getLocalUri();
    void setLocalUri(String value);

    @Description("Path of the file to write to")
    @Default.String("C:\\Mentoring\\dataflow\\output_data\\outputMongo")
    String getOutputFile();

    void setOutputFile(String value);
}
