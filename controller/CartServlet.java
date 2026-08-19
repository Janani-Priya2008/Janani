package com.janani.jananimart.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.janani.jananimart.dao.CartDAOImpl;
import com.janani.jananimart.dao.ProductDAOImpl;
import com.janani.jananimart.model.CartItem;
import com.janani.jananimart.service.CartService;
import com.janani.jananimart.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/v1/cart/*")
public class CartServlet extends HttpServlet {
    private CartService cartService;

    @Override
    public void init() {
        DataSource ds = (DataSource) getServletContext().getAttribute("dataSource");
        cartService = new CartService(new CartDAOImpl(ds), new ProductDAOImpl(ds));
    }

    // GET /api/v1/cart
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        Long userId = (Long) req.getSession().getAttribute("userId");
        List<CartItem> items = cartService.getCart(userId);
        resp.getWriter().write(JsonUtil.envelope(true, items, null, null));
    }

    // POST /api/v1/cart  { productId, quantity }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        Long userId = (Long) req.getSession().getAttribute("userId");
        try {
            JsonObject body = JsonParser.parseReader(req.getReader()).getAsJsonObject();
            Long productId = body.get("productId").getAsLong();
            int quantity = body.get("quantity").getAsInt();
            CartItem item = cartService.addToCart(userId, productId, quantity);
            resp.getWriter().write(JsonUtil.envelope(true, item, null, null));
        } catch (IllegalArgumentException | IllegalStateException e) {
            resp.setStatus(400);
            resp.getWriter().write(JsonUtil.envelope(false, null, "VALIDATION_ERROR", e.getMessage()));
        }
    }

    // PUT /api/v1/cart/{id}  { quantity }
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        Long userId = (Long) req.getSession().getAttribute("userId");
        Long cartItemId = Long.parseLong(req.getPathInfo().substring(1));
        JsonObject body = JsonParser.parseReader(req.getReader()).getAsJsonObject();
        int quantity = body.get("quantity").getAsInt();
        cartService.updateQuantity(userId, cartItemId, quantity);
        resp.getWriter().write(JsonUtil.envelope(true, null, null, null));
    }

    // DELETE /api/v1/cart/{id}
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        Long userId = (Long) req.getSession().getAttribute("userId");
        Long cartItemId = Long.parseLong(req.getPathInfo().substring(1));
        cartService.removeItem(userId, cartItemId);
        resp.getWriter().write(JsonUtil.envelope(true, null, null, null));
    }
}
