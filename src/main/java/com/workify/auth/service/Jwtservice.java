package com.workify.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;

@Service
public class Jwtservice {
    private static final String SECRET_KEY = "27bc5821f84be3dca9696869c6248a4bbbf23e30906f1a6c7c2b79ce30e3c32a";
    public String extractusername(String token) {
        return null;
    }
    private Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                //it's the signature part of jwt
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
