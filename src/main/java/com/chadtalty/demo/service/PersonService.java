package com.chadtalty.demo.service;

import com.chadtalty.demo.dto.PersonDTO;
import com.chadtalty.demo.entity.Person;
import com.chadtalty.demo.mapper.PersonMapper;
import com.chadtalty.demo.repository.PersonRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    private final PersonMapper personMapper;

    public List<PersonDTO> getAllPersons() {
        return personMapper.toDtos(personRepository.findAll());
    }

    public PersonDTO getPersonDtoById(Long id) {
        return personMapper.toDto(
                personRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Person not found.")));
    }

    public Person getPersonById(Long id) {
        return personRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Person not found."));
    }

    public PersonDTO createPerson(PersonDTO person) {
        return personMapper.toDto(personRepository.save(personMapper.toEntity(person)));
    }

    public PersonDTO updatePerson(Long id, PersonDTO updatedPerson) {
        return personMapper.toDto(personRepository
                .findById(id)
                .map(person -> {
                    person.setUsername(updatedPerson.getUsername());
                    person.setEmail(updatedPerson.getEmail());

                    return personRepository.save(person);
                })
                .orElseThrow(EntityNotFoundException::new));
    }

    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }

    public Person map(PersonDTO dto) {
        return personMapper.toEntity(dto);
    }
}
