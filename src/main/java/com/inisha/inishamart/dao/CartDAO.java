package com.inisha.inishamart.dao;

import com.inisha.inishamart.model.CartItem;
import java.util.List;
import java.util.Optional;

public interface CartDAO {
    CartItem add(CartItem item);
    List<CartItem> findByUser(long userId);
    Optional<CartItem> findByUserAndProduct(long userId, long productId);
    void updateQuantity(long cartItemId, int quantity);
    void remove(long cartItemId, long userId);
    void clearForUser(long userId);
}
