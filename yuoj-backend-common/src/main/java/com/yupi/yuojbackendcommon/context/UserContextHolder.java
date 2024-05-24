package com.yupi.yuojbackendcommon.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户上下文对象
 *
 * @author tangx
 */

public class UserContextHolder {

    private static final TransmittableThreadLocal<Map<String, String>> USER_HOLDER = new TransmittableThreadLocal<Map<String, String>>() {
        @Override
        protected Map<String, String> initialValue() {
            return new HashMap<>();
        }
    };

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static <T> void set(String key, T value) {
        try {
            USER_HOLDER.get().put(key, OBJECT_MAPPER.writeValueAsString(value));
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize value to JSON", e);
        }
    }

    public static String get(String key) {
        return USER_HOLDER.get().get(key);
    }

    public static <T> T get(String key, Class<T> clazz) {
        String value = USER_HOLDER.get().get(key);
        if (value == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(value, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert value to class " + clazz.getName(), e);
        }
    }

    public static void clear() {
        USER_HOLDER.remove();
    }
}
