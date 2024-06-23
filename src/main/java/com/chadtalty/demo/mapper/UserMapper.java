package com.chadtalty.demo.mapper;

import com.chadtalty.demo.dto.AddressDTO;
import com.chadtalty.demo.dto.UserDTO;
import com.chadtalty.demo.entity.Address;
import com.chadtalty.demo.entity.Person;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserDTO toDTO(Person person) {
        if (person == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setUsername(person.getUsername());
        dto.setEmail(person.getEmail());
        dto.setAddresses(person.getAddresses().stream().map(UserMapper::toDTO).collect(Collectors.toList()));

        return dto;
    }

    public static Person toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }

        Person person = new Person();
        person.setUsername(dto.getUsername());
        person.setEmail(dto.getEmail());
        person.setAddresses(
                dto.getAddresses().stream().map(UserMapper::toEntity).collect(Collectors.toList()));

        return person;
    }

    public static AddressDTO toDTO(Address address) {
        if (address == null) {
            return null;
        }

        AddressDTO dto = new AddressDTO();
        dto.setStreet(address.getStreet());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setZipCode(address.getZipCode());

        return dto;
    }

    public static Address toEntity(AddressDTO dto) {
        if (dto == null) {
            return null;
        }

        Address address = new Address();
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setZipCode(dto.getZipCode());

        return address;
    }
}
