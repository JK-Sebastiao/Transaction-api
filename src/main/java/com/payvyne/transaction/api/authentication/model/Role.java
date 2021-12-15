package com.payvyne.transaction.api.authentication.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Embeddable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Role implements GrantedAuthority {

    public static final String ADMIN = "ADMIN";
    public static final String MANAGER = "USER";
    public static final String EMPLOYEE = "USER";


    private String Authority;

}
