package com.chadtalty.demo.controller;

import com.chadtalty.demo.dto.PersonDTO;
import com.chadtalty.demo.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/persons")
@Tag(name = "Person Management", description = "Operations related to managing persons")
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping
    @Operation(summary = "Get all persons")
    public ResponseEntity<List<PersonDTO>> getAllPersons() {
        return ResponseEntity.ok(personService.getAllPersons());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get person by ID")
    public ResponseEntity<PersonDTO> getPersonById(@PathVariable Long id) {
        return ResponseEntity.ok(personService.getPersonDtoById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new person")
    public ResponseEntity<PersonDTO> createPerson(@RequestBody PersonDTO person) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personService.createPerson(person));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing person")
    public ResponseEntity<PersonDTO> updatePerson(@PathVariable Long id, @RequestBody PersonDTO updatedPerson) {
        return ResponseEntity.ok(personService.updatePerson(id, updatedPerson));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a person")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        personService.deletePerson(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
