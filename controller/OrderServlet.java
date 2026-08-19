package com.janani.jananimart.controller;

import com.janani.jananimart.dao.CartDAOImpl;
import com.janani.jananimart.dao.OrderDAOImpl;
import com.janani.jananimart.dao.ProductDAOImpl;
import com.janani.jananimart.model.Order;
import com.janani.jananimart.service.OrderService;
import com.janani.jananimart.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/v1/orders/*")
public class OrderServlet extends HttpServlet {
    private OrderService orderService;

    @Override
    public void init() {
        DataSource ds = (DataSource) getServletContext().getAttribute("dataSource");
        orderService = new OrderService(ds, new CartDAOImpl(ds), new ProductDAOImpl(ds), new OrderDAOImpl(ds));
    }

    // POST /api/v1/orders  -> place order from cart
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        Long userId = (Long) req.getSession().getAttribute("userId");
        try {
            Order order = orderService.placeOrder(userId);
            resp.setStatus(201);
            resp.getWriter().write(JsonUtil.envelope(true, order, null, null));
        } catch (IllegalStateException e) {
            resp.setStatus(400);
            resp.getWriter().write(JsonUtil.envelope(false, null, "VALIDATION_ERROR", e.getMessage()));
        } catch (RuntimeException e) {
            resp.setStatus(500);
            resp.getWriter().write(JsonUtil.envelope(false, null, "SERVER_ERROR", "Could not place order"));
        }
    }

    // GET /api/v1/orders?as=seller  -> buyer's own orders, or seller's incoming orders
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        Long userId = (Long) req.getSession().getAttribute("userId");
        String as = req.getParameter("as");
        List<Order> orders = "seller".equals(as)
            ? orderService.getSellerOrders(userId)
            : orderService.getBuyerOrders(userId);
        resp.getWriter().write(JsonUtil.envelope(true, orders, null, null));
    }
}
