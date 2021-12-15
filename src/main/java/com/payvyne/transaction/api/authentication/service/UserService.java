package com.payvyne.transaction.api.authentication.service;

import com.payvyne.transaction.api.authentication.dto.NewUserDTO;
import com.payvyne.transaction.api.authentication.dto.UserDTO;
import com.payvyne.transaction.api.authentication.model.User;
import com.payvyne.transaction.api.repository.UserRepository;
import com.payvyne.transaction.api.util.Mapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.util.HashSet;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUserByUsername(String username){
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(String.format("User with username - %s, not found", username))
        );
    }

    @Transactional
    public UserDTO create(NewUserDTO newUserDTO) {
        Optional<User> optionalUser = userRepository.findByUsername(newUserDTO.getUsername());
        if(optionalUser.isPresent()){
            throw  new ValidationException("There is an user with that username: "+ newUserDTO.getUsername());
        }

        if(!newUserDTO.getPassword().equals(newUserDTO.getRepeatPassword())){
            throw new ValidationException("Passwords don't match");
        }
        if(newUserDTO.getAuthorities() == null){
            newUserDTO.setAuthorities(new HashSet<>());
        }
        User user = User.builder()
                .name(newUserDTO.getName())
                .surname(newUserDTO.getSurname())
                .username(newUserDTO.getUsername())
                .password(passwordEncoder.encode(newUserDTO.getPassword()))
                .enabled(true)
                .authorities(newUserDTO.getAuthorities())
                .build();
        user = userRepository.save(user);
        return Mapper.mapToUserDTO(user);
    }

    @Transactional
    public UserDTO upset(NewUserDTO newUserDTO){
        Optional<User> optionalUser = userRepository.findByUsername(newUserDTO.getUsername());

        if(optionalUser.isEmpty()){
            return create(newUserDTO);
        } else {
            User user = optionalUser.get();
            user.setName(newUserDTO.getName());
            user.setSurname(newUserDTO.getSurname());
            user.setAuthorities(newUserDTO.getAuthorities());
            user = userRepository.save(user);
            return Mapper.mapToUserDTO(user);
        }
    }
}
