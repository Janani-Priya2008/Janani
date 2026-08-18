package com.inisha.inishamart.dao;

import com.inisha.inishamart.model.User;
import java.util.Optional;

public interface UserDAO {
    User create(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(long id);
}
