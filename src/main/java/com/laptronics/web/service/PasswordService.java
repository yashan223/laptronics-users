package com.laptronics.web.service;

import org.springframework.stereotype.Service;

@Service
public class PasswordService {
    
    public String encryptPassword(String password) {
        return password;
    }
    
    public boolean verifyPassword(String plainPassword, String encryptedPassword) {
        return plainPassword.equals(encryptedPassword);
    }
}
