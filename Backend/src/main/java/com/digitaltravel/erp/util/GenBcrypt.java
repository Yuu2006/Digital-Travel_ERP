package com.digitaltravel.erp.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenBcrypt {
    public static void main(String[] args) {
        String hash = new BCryptPasswordEncoder().encode("password");
        System.out.println(hash);
    }
}
