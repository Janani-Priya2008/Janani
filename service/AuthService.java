package com.janani.jananimart.service;

import com.janani.jananimart.dao.UserDAO;
import com.janani.jananimart.model.User;
import com.janani.jananimart.util.PasswordUtil;
import com.janani.jananimart.util.ValidationUtil;

import java.util.Optional;

public class AuthService {
    private final UserDAO userDAO;

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User register(String name, String email, String password, String role) {
        if (!ValidationUtil.isNonEmpty(name)) throw new IllegalArgumentException("Name is required");
        if (!ValidationUtil.isValidEmail(email)) throw new IllegalArgumentException("Invalid email");
        if (password == null || password.length() < 6) throw new IllegalArgumentException("Password too short");
        if (!role.equals("BUYER") && !role.equals("SELLER")) throw new IllegalArgumentException("Invalid role");
        if (userDAO.findByEmail(email).isPresent()) throw new IllegalStateException("Email already registered");

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash(PasswordUtil.hash(password));
        user.setRole(role);
        return userDAO.create(user);
    }

    public Optional<User> login(String email, String password) {
        Optional<User> userOpt = userDAO.findByEmail(email);
        if (userOpt.isEmpty()) return Optional.empty();
        User user = userOpt.get();
        if (!PasswordUtil.verify(password, user.getPasswordHash())) return Optional.empty();
        return Optional.of(user);
    }
}
