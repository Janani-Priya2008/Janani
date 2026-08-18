package com.inisha.inishamart.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class JsonUtil {

    private static final Gson GSON = new GsonBuilder().create();

    private JsonUtil() {}

    public static void writeSuccess(HttpServletResponse resp, int status, Object data) throws IOException {
        Map<String, Object> envelope = new HashMap<>();
        envelope.put("success", true);
        envelope.put("data", data);
        envelope.put("error", null);
        write(resp, status, envelope);
    }

    public static void writeError(HttpServletResponse resp, int status, String code, String message) throws IOException {
        Map<String, Object> error = new HashMap<>();
        error.put("code", code);
        error.put("message", message);

        Map<String, Object> envelope = new HashMap<>();
        envelope.put("success", false);
        envelope.put("data", null);
        envelope.put("error", error);
        write(resp, status, envelope);
    }

    private static void write(HttpServletResponse resp, int status, Object body) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(GSON.toJson(body));
    }

    public static <T> T fromJson(java.io.Reader reader, Class<T> clazz) {
        return GSON.fromJson(reader, clazz);
    }
}
