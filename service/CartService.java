package com.janani.jananimart.service;

import com.janani.jananimart.dao.CartDAO;
import com.janani.jananimart.dao.ProductDAO;
import com.janani.jananimart.model.CartItem;
import com.janani.jananimart.model.Product;

import java.util.List;
import java.util.Optional;

public class CartService {
    private final CartDAO cartDAO;
    private final ProductDAO productDAO;

    public CartService(CartDAO cartDAO, ProductDAO productDAO) {
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
    }

    public CartItem addToCart(Long userId, Long productId, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        Optional<Product> product = productDAO.findById(productId);
        if (product.isEmpty()) throw new IllegalArgumentException("Product not found");
        if (product.get().getStockQty() < quantity) throw new IllegalStateException("Insufficient stock");
        return cartDAO.addOrUpdate(userId, productId, quantity);
    }

    public List<CartItem> getCart(Long userId) {
        return cartDAO.findByUser(userId);
    }

    public void updateQuantity(Long userId, Long cartItemId, int quantity) {
        if (quantity <= 0) {
            cartDAO.remove(cartItemId, userId);
        } else {
            cartDAO.updateQuantity(cartItemId, userId, quantity);
        }
    }

    public void removeItem(Long userId, Long cartItemId) {
        cartDAO.remove(cartItemId, userId);
    }
}
