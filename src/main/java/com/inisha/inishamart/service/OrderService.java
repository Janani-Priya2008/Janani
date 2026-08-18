package com.inisha.inishamart.service;

import com.inisha.inishamart.dao.CartDAO;
import com.inisha.inishamart.dao.OrderDAO;
import com.inisha.inishamart.dao.ProductDAO;
import com.inisha.inishamart.model.CartItem;
import com.inisha.inishamart.model.Order;
import com.inisha.inishamart.model.OrderItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderService {

    private final CartDAO cartDAO;
    private final ProductDAO productDAO;
    private final OrderDAO orderDAO;
    private final DataSource dataSource;

    public OrderService(CartDAO cartDAO, ProductDAO productDAO, OrderDAO orderDAO, DataSource dataSource) {
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
        this.orderDAO = orderDAO;
        this.dataSource = dataSource;
    }

    // Mock payment confirmation: no external gateway call, just a checkout step
    // that commits order + order_items + stock decrements as one transaction.
    public Order checkout(long buyerId) {
        List<CartItem> cartItems = cartDAO.findByUser(buyerId);
        if (cartItems.isEmpty()) throw new IllegalStateException("Cart is empty");

        Order order = new Order();
        order.setBuyerId(buyerId);
        order.setStatus("PENDING");

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : cartItems) {
            OrderItem oi = new OrderItem();
            oi.setProductId(ci.getProductId());
            oi.setQuantity(ci.getQuantity());
            oi.setUnitPrice(ci.getUnitPrice());
            orderItems.add(oi);
            total = total.add(ci.getUnitPrice().multiply(BigDecimal.valueOf(ci.getQuantity())));
        }
        order.setItems(orderItems);
        order.setTotalAmount(total);

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                for (CartItem ci : cartItems) {
                    productDAO.decrementStock(ci.getProductId(), ci.getQuantity());
                }
                orderDAO.createOrderWithItems(conn, order);
                cartDAO.clearForUser(buyerId);
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Checkout failed", e);
        }
        return order;
    }

    public List<Order> history(long buyerId) {
        return orderDAO.findByBuyer(buyerId);
    }
}
