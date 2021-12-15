package com.payvyne.transaction.api;

import com.payvyne.transaction.api.authentication.dto.NewUserDTO;
import com.payvyne.transaction.api.authentication.model.Role;
import com.payvyne.transaction.api.authentication.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@AllArgsConstructor
public class DatabaseInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private UserService userService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

            NewUserDTO newUserDTO = NewUserDTO.builder()
                    .name("Alan")
                    .surname("Turing")
                    .username("alan.turing@payvyne.com")
                    .password("admin-password")
                    .repeatPassword("admin-password")
                    .authorities(Set.of(new Role(Role.ADMIN)))
                    .build();
            userService.upset(newUserDTO);

    }
}
