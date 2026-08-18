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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;

@WebServlet("/api/v1/register")
public class RegisterServlet extends HttpServlet {

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
            String name = body.get("name").getAsString();
            String email = body.get("email").getAsString();
            String password = body.get("password").getAsString();
            String role = body.get("role").getAsString();

            User user = authService.register(name, email, password, role);
            user.setPasswordHash(null); // never expose hash
            JsonUtil.writeSuccess(resp, HttpServletResponse.SC_CREATED, user);
        } catch (IllegalArgumentException e) {
            JsonUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "VALIDATION_ERROR", e.getMessage());
        } catch (IllegalStateException e) {
            JsonUtil.writeError(resp, HttpServletResponse.SC_CONFLICT, "CONFLICT", e.getMessage());
        } catch (Exception e) {
            JsonUtil.writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SERVER_ERROR", "Registration failed");
        }
    }
}
