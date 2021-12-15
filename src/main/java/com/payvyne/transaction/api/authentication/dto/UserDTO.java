package com.payvyne.transaction.api.authentication.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private long id;
    private String username;
    private String fullName;
    private boolean enabled;
}
