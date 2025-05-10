package com.example.ceos.controller;

import com.example.ceos.config.TestConfig;
import com.example.ceos.entity.User;
import com.example.ceos.repository.UserRepository;
import com.example.ceos.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        // 创建管理员用户
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setRole("ADMIN");
        userRepository.save(admin);

        // 创建普通用户
        User user = new User();
        user.setUsername("user");
        user.setEmail("user@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole("USER");
        userRepository.save(user);

        // 获取令牌
        adminToken = "Bearer " + tokenProvider.generateToken(admin);
        userToken = "Bearer " + tokenProvider.generateToken(user);
    }

    @Test
    void whenGetAllUsers_thenReturnsUserList() throws Exception {
        mockMvc.perform(get("/api/users")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void whenGetUserById_thenReturnsUser() throws Exception {
        User user = userRepository.findByUsername("user").orElseThrow();
        
        mockMvc.perform(get("/api/users/" + user.getId())
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"));
    }

    @Test
    void whenCreateUser_thenReturnsCreatedUser() throws Exception {
        Map<String, String> userData = new HashMap<>();
        userData.put("username", "newuser");
        userData.put("email", "new@example.com");
        userData.put("password", "password");
        userData.put("fullName", "New User");
        userData.put("role", "USER");

        mockMvc.perform(post("/api/users")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userData)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newuser"));
    }

    @Test
    void whenUpdateUser_thenReturnsUpdatedUser() throws Exception {
        User user = userRepository.findByUsername("user").orElseThrow();
        
        Map<String, String> updateData = new HashMap<>();
        updateData.put("fullName", "Updated Name");

        mockMvc.perform(put("/api/users/" + user.getId())
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Updated Name"));
    }

    @Test
    void whenDeleteUser_thenReturnsNoContent() throws Exception {
        User user = userRepository.findByUsername("user").orElseThrow();

        mockMvc.perform(delete("/api/users/" + user.getId())
                .header("Authorization", adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenNonAdminAccess_thenReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/users")
                .header("Authorization", userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenUnauthenticatedAccess_thenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }
} 