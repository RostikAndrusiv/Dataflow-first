package org.rostik.andrusiv.util;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.rostik.andrusiv.model.PersonFirestore;
import org.rostik.andrusiv.model.PersonMongo;

@Mapper
public interface PersonMapper {
    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    PersonFirestore mapToFirestore(PersonMongo personMongo);

}
