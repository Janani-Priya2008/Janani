package com.janani.jananimart.service;

import com.janani.jananimart.dao.CartDAO;
import com.janani.jananimart.dao.OrderDAO;
import com.janani.jananimart.dao.ProductDAO;
import com.janani.jananimart.model.CartItem;
import com.janani.jananimart.model.Order;
import com.janani.jananimart.model.OrderItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderService {
    private final DataSource dataSource;
    private final CartDAO cartDAO;
    private final ProductDAO productDAO;
    private final OrderDAO orderDAO;

    public OrderService(DataSource dataSource, CartDAO cartDAO, ProductDAO productDAO, OrderDAO orderDAO) {
        this.dataSource = dataSource;
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
        this.orderDAO = orderDAO;
    }

    /** Places an order from the user's cart. Mock payment: always "confirmed" on success. */
    public Order placeOrder(Long buyerId) {
        List<CartItem> cartItems = cartDAO.findByUser(buyerId);
        if (cartItems.isEmpty()) throw new IllegalStateException("Cart is empty");

        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            BigDecimal total = BigDecimal.ZERO;
            List<OrderItem> orderItems = new ArrayList<>();
            for (CartItem ci : cartItems) {
                boolean ok = productDAO.decrementStock(ci.getProductId(), ci.getQuantity(), conn);
                if (!ok) throw new IllegalStateException("Insufficient stock for product " + ci.getProductId());
                BigDecimal lineTotal = ci.getUnitPrice().multiply(BigDecimal.valueOf(ci.getQuantity()));
                total = total.add(lineTotal);
                orderItems.add(new OrderItem(ci.getProductId(), ci.getQuantity(), ci.getUnitPrice()));
            }

            Order order = new Order();
            order.setBuyerId(buyerId);
            order.setStatus("CONFIRMED"); // mock payment confirmation
            order.setTotalAmount(total);
            order.setItems(orderItems);

            orderDAO.placeOrder(order, conn);
            cartDAO.clearForUser(buyerId, conn);

            conn.commit();
            return order;
        } catch (SQLException | RuntimeException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new RuntimeException("Failed to place order: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    public List<Order> getBuyerOrders(Long buyerId) {
        return orderDAO.findByBuyer(buyerId);
    }

    public List<Order> getSellerOrders(Long sellerId) {
        return orderDAO.findBySeller(sellerId);
    }
}
