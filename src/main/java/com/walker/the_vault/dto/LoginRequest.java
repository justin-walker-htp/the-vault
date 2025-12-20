package com.walker.the_vault.dto;

import lombok.Data;

@Data // Generates Getters/Setters automatically
public class LoginRequest {
    private String username;
    private String password;
}
