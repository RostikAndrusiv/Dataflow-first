package org.rostik.andrusiv.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.rostik.andrusiv.model.PersonFirestore;
import org.rostik.andrusiv.model.PersonMongo;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Mapper
public interface PersonMapper {
    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);
    @Mapping(source = "dob", target = "dob", qualifiedBy = LocalDateTimeToDate.class)
    PersonFirestore mapToFirestore(PersonMongo personMongo);

    @LocalDateTimeToDate
    static Date LocalDateTimeToData(LocalDateTime localDateTime) {
        return java.util.Date
                .from(localDateTime.atZone(ZoneId.systemDefault())
                        .toInstant());
    }
}
