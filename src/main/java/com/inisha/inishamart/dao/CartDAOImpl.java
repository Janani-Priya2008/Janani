package com.inisha.inishamart.dao;

import com.inisha.inishamart.model.CartItem;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CartDAOImpl implements CartDAO {

    private final DataSource dataSource;

    public CartDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public CartItem add(CartItem item) {
        String sql = "INSERT INTO cart_items (user_id, product_id, quantity) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, item.getUserId());
            ps.setLong(2, item.getProductId());
            ps.setInt(3, item.getQuantity());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) item.setId(rs.getLong(1));
            }
            return item;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add cart item", e);
        }
    }

    @Override
    public List<CartItem> findByUser(long userId) {
        String sql = "SELECT ci.id, ci.user_id, ci.product_id, ci.quantity, p.name, p.price " +
                "FROM cart_items ci JOIN products p ON ci.product_id = p.id WHERE ci.user_id = ?";
        List<CartItem> items = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartItem ci = new CartItem();
                    ci.setId(rs.getLong("id"));
                    ci.setUserId(rs.getLong("user_id"));
                    ci.setProductId(rs.getLong("product_id"));
                    ci.setQuantity(rs.getInt("quantity"));
                    ci.setProductName(rs.getString("name"));
                    ci.setUnitPrice(rs.getBigDecimal("price"));
                    items.add(ci);
                }
            }
            return items;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find cart items", e);
        }
    }

    @Override
    public Optional<CartItem> findByUserAndProduct(long userId, long productId) {
        String sql = "SELECT id, user_id, product_id, quantity FROM cart_items WHERE user_id = ? AND product_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CartItem ci = new CartItem();
                    ci.setId(rs.getLong("id"));
                    ci.setUserId(rs.getLong("user_id"));
                    ci.setProductId(rs.getLong("product_id"));
                    ci.setQuantity(rs.getInt("quantity"));
                    return Optional.of(ci);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to look up cart item", e);
        }
    }

    @Override
    public void updateQuantity(long cartItemId, int quantity) {
        String sql = "UPDATE cart_items SET quantity = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setLong(2, cartItemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update cart item", e);
        }
    }

    @Override
    public void remove(long cartItemId, long userId) {
        String sql = "DELETE FROM cart_items WHERE id = ? AND user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, cartItemId);
            ps.setLong(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove cart item", e);
        }
    }

    @Override
    public void clearForUser(long userId) {
        String sql = "DELETE FROM cart_items WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clear cart", e);
        }
    }
}
