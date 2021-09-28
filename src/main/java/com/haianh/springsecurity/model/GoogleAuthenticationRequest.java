package com.haianh.springsecurity.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GoogleAuthenticationRequest {
    private String token;
}
