package org.rostik.andrusiv;

import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.PipelineOptions;

public interface JobOptions extends PipelineOptions {

    @Default.String("phrasal-client-372213")
    String getProjectName();

    void setProjectName(String value);

    @Default.String("mongodb+srv://metalrulez:11235813@cluster0.difgez5.mongodb.net")
    String getUri();

    void setUri(String value);

    @Default.String("testDb")
    String getInputDbName();

    void setInputDbName(String value);

    @Default.String("Person")
    String getInputCollectionId();

    void setInputCollectionId(String value);

    @Default.String("firestorePerson")
    String getOutputCollectionId();

    void setOutputCollectionId(String value);
}
