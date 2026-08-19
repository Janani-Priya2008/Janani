package com.janani.jananimart.dao;

import com.janani.jananimart.model.User;
import java.util.Optional;

public interface UserDAO {
    User create(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
}
