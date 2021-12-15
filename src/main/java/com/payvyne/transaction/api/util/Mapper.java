package com.payvyne.transaction.api.util;

import com.payvyne.transaction.api.authentication.model.User;
import com.payvyne.transaction.api.authentication.dto.UserDTO;

public class Mapper {

    public static UserDTO mapToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .fullName(String.format("%s %s", user.getName(), user.getSurname()))
                .username(user.getUsername())
                .enabled(user.isEnabled())
                .build();
    }
}
