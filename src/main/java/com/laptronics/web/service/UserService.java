package com.laptronics.web.service;

import com.laptronics.web.dto.*;
import com.laptronics.web.entity.Role;
import com.laptronics.web.entity.User;
import com.laptronics.web.repository.RoleRepository;
import com.laptronics.web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordService passwordService;
    
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists!");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordService.encryptPassword(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setActive(true);
        
        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName("USER");
                    newRole.setDescription("Default user role");
                    return roleRepository.save(newRole);
                });
        
        user.getRoles().add(userRole);
        User savedUser = userRepository.save(user);
        
        return new AuthResponse(
            "User registered successfully!",
            convertToUserResponse(savedUser)
        );
    }
    
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsernameOrEmail(
                request.getUsernameOrEmail(), 
                request.getUsernameOrEmail()
            )
            .orElseThrow(() -> new RuntimeException("Invalid username or password!"));
        
        if (!user.getActive()) {
            throw new RuntimeException("Account is disabled!");
        }
        
        if (!passwordService.verifyPassword(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password!");
        }
        
        return new AuthResponse(
            "Login successful!",
            convertToUserResponse(user)
        );
    }
    
    public UserResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found!"));
        
        return convertToUserResponse(user);
    }
    
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found!"));
        
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        
        User updatedUser = userRepository.save(user);
        return convertToUserResponse(updatedUser);
    }
    
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
            .map(this::convertToUserResponse)
            .collect(Collectors.toList());
    }
    
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found!");
        }
        userRepository.deleteById(userId);
    }
    
    private UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setActive(user.getActive());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        
        response.setRoles(
            user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet())
        );
        
        return response;
    }
}
