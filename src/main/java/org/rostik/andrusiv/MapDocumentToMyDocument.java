package org.rostik.andrusiv;

import org.apache.beam.sdk.transforms.DoFn;
import org.bson.Document;
import org.rostik.andrusiv.model.MyMap;

public class MapDocumentToMyDocument extends DoFn<Document, MyMap> {
    @ProcessElement
    public void processElement(ProcessContext context, OutputReceiver<MyMap> receiver) {
        Document doc = context.element();
        if (null == doc || doc.values().isEmpty()){
            return;
        }
        MyMap myMap = new MyMap(doc);
        receiver.output(myMap);

    }
}
