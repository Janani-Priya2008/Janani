package com.janani.jananimart.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.janani.jananimart.dao.UserDAOImpl;
import com.janani.jananimart.model.User;
import com.janani.jananimart.service.AuthService;
import com.janani.jananimart.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.Optional;

@WebServlet("/api/v1/auth/login")
public class LoginServlet extends HttpServlet {
    private AuthService authService;

    @Override
    public void init() {
        DataSource ds = (DataSource) getServletContext().getAttribute("dataSource");
        authService = new AuthService(new UserDAOImpl(ds));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        JsonObject body = JsonParser.parseReader(req.getReader()).getAsJsonObject();
        String email = body.get("email").getAsString();
        String password = body.get("password").getAsString();

        Optional<User> userOpt = authService.login(email, password);
        if (userOpt.isEmpty()) {
            resp.setStatus(401);
            resp.getWriter().write(JsonUtil.envelope(false, null, "INVALID_CREDENTIALS", "Email or password incorrect"));
            return;
        }

        User user = userOpt.get();

        // regenerate session id on login to prevent session fixation
        HttpSession oldSession = req.getSession(false);
        if (oldSession != null) oldSession.invalidate();
        HttpSession session = req.getSession(true);
        session.setAttribute("userId", user.getId());
        session.setAttribute("role", user.getRole());
        session.setMaxInactiveInterval(30 * 60);

        user.setPasswordHash(null);
        resp.getWriter().write(JsonUtil.envelope(true, user, null, null));
    }
}
