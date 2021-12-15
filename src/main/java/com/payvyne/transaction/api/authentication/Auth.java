package com.payvyne.transaction.api.authentication;

import com.payvyne.transaction.api.authentication.dto.AuthDTO;
import com.payvyne.transaction.api.authentication.dto.NewUserDTO;
import com.payvyne.transaction.api.authentication.dto.UserDTO;
import com.payvyne.transaction.api.authentication.model.User;
import com.payvyne.transaction.api.authentication.service.UserService;
import com.payvyne.transaction.api.config.security.JwtTokenUtil;
import com.payvyne.transaction.api.util.Mapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "Auth")
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class Auth {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody @Valid AuthDTO authDTO){
        try {
            org.springframework.security.core.Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(authDTO.getUsername(), authDTO.getPassword()));

            User user = (User) authentication.getPrincipal();

            return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, jwtTokenUtil.generateAccessToken(user))
                    .body(Mapper.mapToUserDTO(user));
        } catch (BadCredentialsException ex){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody @Valid NewUserDTO newUserDTO){
        UserDTO userDTO = userService.create(newUserDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }
}
