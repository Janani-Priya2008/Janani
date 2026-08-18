package com.inisha.inishamart.dao;

import com.inisha.inishamart.model.Order;
import java.sql.Connection;
import java.util.List;

public interface OrderDAO {
    Order createOrderWithItems(Connection conn, Order order) throws java.sql.SQLException;
    List<Order> findByBuyer(long buyerId);
}
