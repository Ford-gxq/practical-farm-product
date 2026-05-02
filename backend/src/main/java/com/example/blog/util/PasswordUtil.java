package com.example.blog.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * 密码工具类。
 *
 * 为了减少学习成本，本项目使用 SHA-256 + 固定盐演示密码校验流程。
 * 生产环境建议使用 Spring Security 提供的 BCryptPasswordEncoder。
 */
public class PasswordUtil {
    public static String sha256(String rawPassword, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest((rawPassword + salt).getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }
}
