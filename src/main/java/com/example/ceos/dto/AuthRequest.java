package com.example.ceos.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "认证请求")
public class AuthRequest {
    
    @Schema(description = "用户名", example = "admin")
    private String username;
    
    @Schema(description = "密码", example = "password")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
} 