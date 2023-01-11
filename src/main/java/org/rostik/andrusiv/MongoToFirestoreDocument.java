package org.rostik.andrusiv;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.beam.sdk.transforms.DoFn;
import org.bson.Document;
import org.rostik.andrusiv.model.PersonFirestore;
import org.rostik.andrusiv.model.PersonMongo;
import org.rostik.andrusiv.util.CustomLocalDateTimeDeserializer;
import org.rostik.andrusiv.util.PersonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class MongoToFirestoreDocument extends DoFn<Document, PersonFirestore> {

    private static final Logger LOG = LoggerFactory.getLogger(MongoToFirestoreDocument.class);
    private transient Gson gson;

    @StartBundle
    public void setup(StartBundleContext startBundleContext) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(LocalDateTime.class, new CustomLocalDateTimeDeserializer());
            this.gson =  gsonBuilder.setPrettyPrinting().create();
//        this.gson = new Gson();
    }

    @ProcessElement
    public void processElement(ProcessContext context, OutputReceiver<PersonFirestore> receiver) {
        Document doc = context.element();
        if (null == doc || doc.values().isEmpty()){
            return;
        }
        List<Class> classes = context.element().values().stream()
                .map(Object::getClass)
                .collect(Collectors.toList());
        LOG.info("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS" + classes);

        String personMongoJson = context.element().toJson();
        LOG.error(personMongoJson);
        LOG.error(gson.toString());

        try {
            PersonMongo personMongo = gson.fromJson(personMongoJson, PersonMongo.class);
            PersonFirestore personFirestore = PersonMapper.INSTANCE.mapToFirestore(personMongo);
            receiver.output(personFirestore);
        } catch (Exception e){
            e.printStackTrace();
            LOG.error("" + e.getMessage() + e.getCause());
        }
    }
}
