package com.payvyne.transaction.api.authentication.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class AuthDTO {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
