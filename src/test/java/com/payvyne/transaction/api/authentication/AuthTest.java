package com.payvyne.transaction.api.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payvyne.transaction.api.authentication.dto.AuthDTO;
import com.payvyne.transaction.api.authentication.dto.NewUserDTO;
import com.payvyne.transaction.api.authentication.dto.UserDTO;
import com.payvyne.transaction.api.authentication.model.Role;
import com.payvyne.transaction.api.authentication.service.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthTest {

    private final UserService userService;
    private final MockMvc mockMvc;
    private final ObjectMapper mapper;

    @Autowired
    public AuthTest(UserService userService, MockMvc mockMvc, ObjectMapper mapper) {
        this.userService = userService;
        this.mockMvc = mockMvc;
        this.mapper = mapper;
    }

    private final String password = "password-test";

    @Test
    public void givenAuth_whenLogin_thenStatus200() throws Exception {

        UserDTO newUser = createUser("jan.kowalski@payvyne.com", password,Set.of("ADMIN"));
        AuthDTO authDTO = AuthDTO.builder()
                .username(newUser.getUsername())
                .password(password)
                .build();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(authDTO)))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.AUTHORIZATION))
                .andExpect(jsonPath("$.id", is((int)newUser.getId())));
    }

    @Test
    public void givenAuth_whenLoginWithWrongCredentials_thenStatus401() throws Exception {
        UserDTO newUser = createUser("jan.kowalski@payvyne.com", password,Set.of("ADMIN"));
        AuthDTO authDTO = AuthDTO.builder()
                .username(newUser.getUsername())
                .password(password+00)
                .build();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(authDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(header().doesNotExist(HttpHeaders.AUTHORIZATION));
    }

    @Test
    public void givenAuth_whenRegister_thenStatus201() throws Exception {
        NewUserDTO newUserDTO = getNewUserDTO("joanna.kowalski@payvyne.com", password,Set.of("MANAGER"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newUserDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", Matchers.notNullValue() ));

    }

    @Test
    public void givenAuth_whenRegisterWithDifferentPasswords_thenStatus400() throws Exception {
        NewUserDTO newUserDTO = getNewUserDTO("joanna2.kowalski@payvyne.com", password,Set.of("MANAGER"));
        newUserDTO.setRepeatPassword("different");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newUserDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Passwords don't match")));
    }

    @Test
    public void givenAuth_whenRegisterWithoutUsername_thenStatus400() throws Exception {
        NewUserDTO newUserDTO = getNewUserDTO(null, password,Set.of("MANAGER"));
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newUserDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Method argument validation failed")));
    }

    private UserDTO createUser(String username, String password, Set<String> roles) {
        NewUserDTO newUserDTO = getNewUserDTO(username, password, roles);
        return userService.upset(newUserDTO);
    }

    private NewUserDTO getNewUserDTO(String username, String password, Set<String> roles) {
        NewUserDTO newUserDTO = NewUserDTO.builder()
                .name("Jan")
                .surname("Kowalski")
                .username(username)
                .password(password)
                .repeatPassword(password)
                .authorities(roles.stream().map(Role::new).collect(Collectors.toSet()))
                .build();
        return newUserDTO;
    }


}
