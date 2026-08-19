package com.janani.jananimart.dao;

import com.janani.jananimart.model.CartItem;
import java.util.List;
import java.util.Optional;

public interface CartDAO {
    CartItem addOrUpdate(Long userId, Long productId, int quantity);
    List<CartItem> findByUser(Long userId);
    void updateQuantity(Long cartItemId, Long userId, int quantity);
    void remove(Long cartItemId, Long userId);
    void clearForUser(Long userId, java.sql.Connection conn) throws java.sql.SQLException;
    Optional<CartItem> findByUserAndProduct(Long userId, Long productId);
}
