package com.janani.jananimart.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.janani.jananimart.dao.UserDAOImpl;
import com.janani.jananimart.model.User;
import com.janani.jananimart.service.AuthService;
import com.janani.jananimart.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;

@WebServlet("/api/v1/auth/register")
public class RegisterServlet extends HttpServlet {
    private AuthService authService;

    @Override
    public void init() {
        DataSource ds = (DataSource) getServletContext().getAttribute("dataSource");
        authService = new AuthService(new UserDAOImpl(ds));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            JsonObject body = JsonParser.parseReader(req.getReader()).getAsJsonObject();
            String name = body.get("name").getAsString();
            String email = body.get("email").getAsString();
            String password = body.get("password").getAsString();
            String role = body.get("role").getAsString();

            User user = authService.register(name, email, password, role);
            user.setPasswordHash(null); // never return password hash
            resp.setStatus(201);
            resp.getWriter().write(JsonUtil.envelope(true, user, null, null));
        } catch (IllegalArgumentException | IllegalStateException e) {
            resp.setStatus(400);
            resp.getWriter().write(JsonUtil.envelope(false, null, "VALIDATION_ERROR", e.getMessage()));
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write(JsonUtil.envelope(false, null, "SERVER_ERROR", "Registration failed"));
        }
    }
}
