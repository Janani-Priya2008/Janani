package com.janani.jananimart.dao;

import com.janani.jananimart.model.Order;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface OrderDAO {
    Order placeOrder(Order order, Connection conn) throws SQLException;
    List<Order> findByBuyer(Long buyerId);
    List<Order> findBySeller(Long sellerId);
}
