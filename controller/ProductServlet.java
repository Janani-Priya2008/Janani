package com.janani.jananimart.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.janani.jananimart.dao.ProductDAOImpl;
import com.janani.jananimart.model.Product;
import com.janani.jananimart.service.ProductService;
import com.janani.jananimart.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@WebServlet("/api/v1/products/*")
public class ProductServlet extends HttpServlet {
    private ProductService productService;

    @Override
    public void init() {
        DataSource ds = (DataSource) getServletContext().getAttribute("dataSource");
        productService = new ProductService(new ProductDAOImpl(ds));
    }

    // GET /api/v1/products?keyword=...&category=...  OR  /api/v1/products/{id}
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();

        if (pathInfo != null && !pathInfo.equals("/")) {
            Long id = Long.parseLong(pathInfo.substring(1));
            Optional<Product> product = productService.getById(id);
            if (product.isEmpty()) {
                resp.setStatus(404);
                resp.getWriter().write(JsonUtil.envelope(false, null, "NOT_FOUND", "Product not found"));
                return;
            }
            resp.getWriter().write(JsonUtil.envelope(true, product.get(), null, null));
            return;
        }

        String keyword = req.getParameter("keyword");
        String category = req.getParameter("category");
        List<Product> results = productService.search(keyword, category);
        resp.getWriter().write(JsonUtil.envelope(true, results, null, null));
    }

    // POST /api/v1/products  (seller creates listing)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.setStatus(401);
            resp.getWriter().write(JsonUtil.envelope(false, null, "UNAUTHENTICATED", "Login required"));
            return;
        }
        if (!"SELLER".equals(session.getAttribute("role"))) {
            resp.setStatus(403);
            resp.getWriter().write(JsonUtil.envelope(false, null, "FORBIDDEN", "Sellers only"));
            return;
        }

        try {
            Long sellerId = (Long) session.getAttribute("userId");
            JsonObject body = JsonParser.parseReader(req.getReader()).getAsJsonObject();
            Product p = productService.createProduct(
                sellerId,
                body.get("name").getAsString(),
                body.has("description") ? body.get("description").getAsString() : null,
                new BigDecimal(body.get("price").getAsString()),
                body.get("stockQty").getAsInt(),
                body.has("category") ? body.get("category").getAsString() : null,
                body.has("imageUrl") ? body.get("imageUrl").getAsString() : null
            );
            resp.setStatus(201);
            resp.getWriter().write(JsonUtil.envelope(true, p, null, null));
        } catch (IllegalArgumentException e) {
            resp.setStatus(400);
            resp.getWriter().write(JsonUtil.envelope(false, null, "VALIDATION_ERROR", e.getMessage()));
        }
    }
              }
