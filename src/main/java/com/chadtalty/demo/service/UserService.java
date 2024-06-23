package com.chadtalty.demo.service;

import com.chadtalty.demo.dto.UserDTO;
import com.chadtalty.demo.mapper.UserMapper;
import com.chadtalty.demo.repository.PersonRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PersonRepository repository;

    public List<UserDTO> getAllUsers() {
        return this.repository.findAll().stream().map(UserMapper::toDTO).collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        return UserMapper.toDTO(this.repository.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    public UserDTO createUser(UserDTO userDTO) {
        return UserMapper.toDTO(this.repository.save(UserMapper.toEntity(userDTO)));
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        return this.repository
                .findById(id)
                .map(person -> {
                    person.setUsername(userDTO.getUsername());
                    person.setEmail(userDTO.getEmail());
                    person.setAddresses(userDTO.getAddresses().stream()
                            .map(UserMapper::toEntity)
                            .collect(Collectors.toList()));
                    person = this.repository.save(person);
                    return UserMapper.toDTO(person);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteUser(Long id) {
        this.repository.deleteById(id);
    }
}
