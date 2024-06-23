package com.chadtalty.demo.mapper;

import com.chadtalty.demo.dto.PersonDTO;
import com.chadtalty.demo.entity.Person;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PersonMapper {

    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    PersonDTO toDto(Person person);

    @Mappings({
        @Mapping(target = "addresses", ignore = true),
        @Mapping(target = "createdAt", ignore = true),
        @Mapping(target = "createdBy", ignore = true),
        @Mapping(target = "createdDate", ignore = true),
        @Mapping(target = "lastModifiedBy", ignore = true),
        @Mapping(target = "lastModifiedDate", ignore = true),
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "orders", ignore = true),
        @Mapping(target = "password", ignore = true)
    })
    Person toEntity(PersonDTO personDTO);

    List<PersonDTO> toDtos(List<Person> persons);

    List<Person> toEntities(List<PersonDTO> personDTOs);
}
