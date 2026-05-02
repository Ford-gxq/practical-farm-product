package com.example.blog.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简单内存 Token 存储。
 *
 * 注意：服务重启后 token 会失效；多节点部署也不共享。
 * 生产项目建议改为 JWT 或 Redis 存储。
 */
public class TokenStore {
    private static final Map<String, Long> TOKEN_USER_MAP = new ConcurrentHashMap<>();

    public static void put(String token, Long userId) {
        TOKEN_USER_MAP.put(token, userId);
    }

    public static boolean isValid(String token) {
        return TOKEN_USER_MAP.containsKey(token);
    }

    public static void remove(String token) {
        TOKEN_USER_MAP.remove(token);
    }
}
