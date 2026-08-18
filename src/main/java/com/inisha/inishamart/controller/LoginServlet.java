package com.inisha.inishamart.controller;

import com.inisha.inishamart.dao.UserDAOImpl;
import com.inisha.inishamart.listener.DataSourceListener;
import com.inisha.inishamart.model.User;
import com.inisha.inishamart.service.AuthService;
import com.inisha.inishamart.util.JsonUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.Optional;

@WebServlet("/api/v1/login")
public class LoginServlet extends HttpServlet {

    private AuthService authService;

    @Override
    public void init() throws ServletException {
        DataSource ds = (DataSource) getServletContext().getAttribute(DataSourceListener.ATTR_NAME);
        authService = new AuthService(new UserDAOImpl(ds));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            JsonObject body = JsonParser.parseReader(req.getReader()).getAsJsonObject();
            String email = body.get("email").getAsString();
            String password = body.get("password").getAsString();

            Optional<User> userOpt = authService.login(email, password);
            if (userOpt.isEmpty()) {
                JsonUtil.writeError(resp, HttpServletResponse.SC_UNAUTHORIZED, "INVALID_CREDENTIALS", "Invalid email or password");
                return;
            }
            User user = userOpt.get();

            // Regenerate session ID on login to prevent session fixation.
            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) oldSession.invalidate();
            HttpSession session = req.getSession(true);
            session.setAttribute("userId", user.getId());
            session.setAttribute("role", user.getRole());
            session.setMaxInactiveInterval(30 * 60);

            user.setPasswordHash(null);
            JsonUtil.writeSuccess(resp, HttpServletResponse.SC_OK, user);
        } catch (Exception e) {
            JsonUtil.writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SERVER_ERROR", "Login failed");
        }
    }
}
