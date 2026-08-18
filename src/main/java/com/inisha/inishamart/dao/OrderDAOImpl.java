package com.inisha.inishamart.dao;

import com.inisha.inishamart.model.Order;
import com.inisha.inishamart.model.OrderItem;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDAOImpl implements OrderDAO {

    private final DataSource dataSource;

    public OrderDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Runs inside a caller-managed transaction (see OrderService) so order + items
    // + stock decrements commit or roll back together.
    @Override
    public Order createOrderWithItems(Connection conn, Order order) throws SQLException {
        String orderSql = "INSERT INTO orders (buyer_id, status, total_amount, created_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, order.getBuyerId());
            ps.setString(2, order.getStatus());
            ps.setBigDecimal(3, order.getTotalAmount());
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) order.setId(rs.getLong(1));
            }
        }

        String itemSql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(itemSql)) {
            for (OrderItem item : order.getItems()) {
                ps.setLong(1, order.getId());
                ps.setLong(2, item.getProductId());
                ps.setInt(3, item.getQuantity());
                ps.setBigDecimal(4, item.getUnitPrice());
                ps.addBatch();
            }
            ps.executeBatch();
        }
        return order;
    }

    @Override
    public List<Order> findByBuyer(long buyerId) {
        String sql = "SELECT id, buyer_id, status, total_amount, created_at FROM orders WHERE buyer_id = ? ORDER BY created_at DESC";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, buyerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order o = new Order();
                    o.setId(rs.getLong("id"));
                    o.setBuyerId(rs.getLong("buyer_id"));
                    o.setStatus(rs.getString("status"));
                    o.setTotalAmount(rs.getBigDecimal("total_amount"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) o.setCreatedAt(ts.toLocalDateTime());
                    orders.add(o);
                }
            }
            return orders;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find orders", e);
        }
    }
                      }
