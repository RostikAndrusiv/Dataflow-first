package org.rostik.andrusiv.util;

import com.google.cloud.Timestamp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.rostik.andrusiv.model.PersonFirestore;
import org.rostik.andrusiv.model.PersonMongo;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper
public interface PersonMapper {
    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);
    @Mapping(source = "dob", target = "dob", qualifiedBy = LocalDateTimeToTimestamp.class)
    PersonFirestore mapToFirestore(PersonMongo personMongo);

    @LocalDateTimeToTimestamp
    static Timestamp LocalDateTimeToTimestamp(LocalDateTime localDateTime) {
        java.util.Date from = java.util.Date
                .from(localDateTime.atZone(ZoneId.systemDefault())
                        .toInstant());
        return Timestamp.of(from);
    }
}
