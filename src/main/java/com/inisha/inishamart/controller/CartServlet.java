package com.inisha.inishamart.controller;

import com.inisha.inishamart.dao.CartDAOImpl;
import com.inisha.inishamart.dao.ProductDAOImpl;
import com.inisha.inishamart.listener.DataSourceListener;
import com.inisha.inishamart.model.CartItem;
import com.inisha.inishamart.service.CartService;
import com.inisha.inishamart.util.JsonUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.servlet.ServletException;
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
    public void init() throws ServletException {
        DataSource ds = (DataSource) getServletContext().getAttribute(DataSourceListener.ATTR_NAME);
        cartService = new CartService(new CartDAOImpl(ds), new ProductDAOImpl(ds));
    }

    private long currentUserId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (Long) session.getAttribute("userId");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long userId = currentUserId(req);
        List<CartItem> items = cartService.getCart(userId);
        JsonUtil.writeSuccess(resp, HttpServletResponse.SC_OK, items);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            long userId = currentUserId(req);
            JsonObject body = JsonParser.parseReader(req.getReader()).getAsJsonObject();
            long productId = body.get("productId").getAsLong();
            int quantity = body.get("quantity").getAsInt();

            CartItem item = cartService.addItem(userId, productId, quantity);
            JsonUtil.writeSuccess(resp, HttpServletResponse.SC_CREATED, item);
        } catch (IllegalArgumentException e) {
            JsonUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "VALIDATION_ERROR", e.getMessage());
        } catch (IllegalStateException e) {
            JsonUtil.writeError(resp, HttpServletResponse.SC_CONFLICT, "CONFLICT", e.getMessage());
        } catch (Exception e) {
            JsonUtil.writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SERVER_ERROR", "Could not add to cart");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            long userId = currentUserId(req);
            long cartItemId = extractIdFromPath(req);
            JsonObject body = JsonParser.parseReader(req.getReader()).getAsJsonObject();
            int quantity = body.get("quantity").getAsInt();

            cartService.updateItem(userId, cartItemId, quantity);
            JsonUtil.writeSuccess(resp, HttpServletResponse.SC_OK, "updated");
        } catch (IllegalArgumentException e) {
            JsonUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "VALIDATION_ERROR", e.getMessage());
        } catch (Exception e) {
            JsonUtil.writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SERVER_ERROR", "Could not update cart item");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            long userId = currentUserId(req);
            long cartItemId = extractIdFromPath(req);
            cartService.removeItem(userId, cartItemId);
            JsonUtil.writeSuccess(resp, HttpServletResponse.SC_OK, "removed");
        } catch (Exception e) {
            JsonUtil.writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SERVER_ERROR", "Could not remove cart item");
        }
    }

    private long extractIdFromPath(HttpServletRequest req) {
        String pathInfo = req.getPathInfo(); // "/{id}"
        if (pathInfo == null || pathInfo.equals("/")) throw new IllegalArgumentException("Cart item id required");
        return Long.parseLong(pathInfo.substring(1));
    }
  }
