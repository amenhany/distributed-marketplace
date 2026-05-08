package org.team13.marketplace.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.team13.marketplace.model.User;
import org.team13.marketplace.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepo;

    public String login(String username, String password) {
        Optional<User> user = userRepo.findByUsername(username);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            // Generate a random session token
            String token = UUID.randomUUID().toString();
            user.get().setSessionToken(token);
            userRepo.save(user.get());
            return token;
        }
        return "AUTH_FAILED";
    }

    public boolean isValid(String token) {
        return userRepo.existsBySessionToken(token);
    }

    public Optional<User> getUserByToken(String token) {
        return userRepo.findBySessionToken(token);
    }
}
