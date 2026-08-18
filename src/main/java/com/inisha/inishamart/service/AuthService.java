package com.inisha.inishamart.service;

import com.inisha.inishamart.dao.UserDAO;
import com.inisha.inishamart.model.User;
import com.inisha.inishamart.util.PasswordUtil;
import com.inisha.inishamart.util.ValidationUtil;

import java.time.LocalDateTime;
import java.util.Optional;

public class AuthService {

    private final UserDAO userDAO;

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User register(String name, String email, String password, String role) {
        if (ValidationUtil.isBlank(name)) throw new IllegalArgumentException("Name is required");
        if (!ValidationUtil.isValidEmail(email)) throw new IllegalArgumentException("Invalid email");
        if (ValidationUtil.isBlank(password) || password.length() < 8)
            throw new IllegalArgumentException("Password must be at least 8 characters");
        if (!role.equals("BUYER") && !role.equals("SELLER"))
            throw new IllegalArgumentException("Role must be BUYER or SELLER");
        if (userDAO.findByEmail(email).isPresent())
            throw new IllegalStateException("Email already registered");

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash(PasswordUtil.hash(password));
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        return userDAO.create(user);
    }

    public Optional<User> login(String email, String password) {
        Optional<User> userOpt = userDAO.findByEmail(email);
        if (userOpt.isEmpty()) return Optional.empty();
        User user = userOpt.get();
        if (!PasswordUtil.matches(password, user.getPasswordHash())) return Optional.empty();
        return Optional.of(user);
    }
}
