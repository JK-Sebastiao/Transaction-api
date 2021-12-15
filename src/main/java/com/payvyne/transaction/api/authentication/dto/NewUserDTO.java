package com.payvyne.transaction.api.authentication.dto;

import com.payvyne.transaction.api.authentication.model.Role;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class NewUserDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String surname;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String repeatPassword;
    private Set<Role> authorities = new HashSet<>();
}
