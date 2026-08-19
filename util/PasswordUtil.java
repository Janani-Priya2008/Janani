package com.janani.jananimart.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    public static String hash(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt(12));
    }

    public static boolean verify(String plain, String hash) {
        return BCrypt.checkpw(plain, hash);
    }

    // quick way to generate a hash for seed.sql: run this main once
    public static void main(String[] args) {
        System.out.println(hash("password123"));
    }
}
