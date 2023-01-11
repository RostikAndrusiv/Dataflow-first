package org.rostik.andrusiv.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

//class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {
//    @Override
//    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
//            throws JsonParseException {
//        return LocalDateTime.parse(json.getAsString(),
//                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX").withLocale(Locale.US));
//
//    }
//}

public class CustomLocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(json.getAsJsonObject().get("$date").toString()) / 1000),
                TimeZone.getDefault().toZoneId());

    }
}