package com.example.ceos.service;

import com.example.ceos.dto.UserDTO;
import com.example.ceos.dto.UserRegistrationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDTO registerUser(UserRegistrationDTO registrationDTO);
    UserDTO getUserById(Long id);
    UserDTO getUserByUsername(String username);
    Page<UserDTO> getAllUsers(Pageable pageable);
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    void changePassword(Long id, String oldPassword, String newPassword);
    void enableUser(Long id);
    void disableUser(Long id);
} 