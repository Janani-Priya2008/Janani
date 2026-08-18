package com.inisha.inishamart.service;

import com.inisha.inishamart.dao.CartDAO;
import com.inisha.inishamart.dao.ProductDAO;
import com.inisha.inishamart.model.CartItem;
import com.inisha.inishamart.model.Product;

import java.util.List;
import java.util.Optional;

public class CartService {

    private final CartDAO cartDAO;
    private final ProductDAO productDAO;

    public CartService(CartDAO cartDAO, ProductDAO productDAO) {
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
    }

    public CartItem addItem(long userId, long productId, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        Product product = productDAO.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (product.getStockQty() < quantity)
            throw new IllegalStateException("Insufficient stock");

        Optional<CartItem> existing = cartDAO.findByUserAndProduct(userId, productId);
        if (existing.isPresent()) {
            CartItem item = existing.get();
            int newQty = item.getQuantity() + quantity;
            cartDAO.updateQuantity(item.getId(), newQty);
            item.setQuantity(newQty);
            return item;
        }
        CartItem item = new CartItem();
        item.setUserId(userId);
        item.setProductId(productId);
        item.setQuantity(quantity);
        return cartDAO.add(item);
    }

    public void updateItem(long userId, long cartItemId, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        cartDAO.updateQuantity(cartItemId, quantity);
    }

    public void removeItem(long userId, long cartItemId) {
        cartDAO.remove(cartItemId, userId);
    }

    public List<CartItem> getCart(long userId) {
        return cartDAO.findByUser(userId);
    }
}
