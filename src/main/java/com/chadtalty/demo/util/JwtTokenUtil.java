package com.chadtalty.demo.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTokenUtil {

    public static String generateToken(String username) throws Exception {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", "ROLE_USER");

        String privateKeyPath = System.getenv("JWT_PRIVATE_KEY_PATH");
        PrivateKey privateKey = getPrivateKey(privateKeyPath);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours validity
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    private static PrivateKey getPrivateKey(String filename) throws Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get(filename));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    public static void main(String[] args) {
        try {
            String jwtToken = generateToken("username");
            System.out.println("Bearer " + jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
