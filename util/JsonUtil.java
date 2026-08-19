package com.janani.jananimart.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtil {
    private static final Gson GSON = new GsonBuilder().create();

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    public static String envelope(boolean success, Object data, String errorCode, String errorMsg) {
        var map = new java.util.LinkedHashMap<String, Object>();
        map.put("success", success);
        map.put("data", data);
        if (errorCode != null) {
            var err = new java.util.LinkedHashMap<String, Object>();
            err.put("code", errorCode);
            err.put("message", errorMsg);
            map.put("error", err);
        } else {
            map.put("error", null);
        }
        return GSON.toJson(map);
    }
}
