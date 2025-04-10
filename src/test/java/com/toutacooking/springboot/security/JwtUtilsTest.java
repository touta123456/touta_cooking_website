package com.toutacooking.springboot.security;

import com.toutacooking.springboot.entity.User;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    private final String secret = Base64.getEncoder().encodeToString("mySuperSecretKey1234567890123456".getBytes()); // 32+ chars for HMAC

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", secret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 3600000); // 1h
    }

    @Test
    void testGenerateJwtToken() {
        User user = new User();
        user.setUsername("johndoe");

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);

        String token = jwtUtils.generateJwtToken(auth);

        assertNotNull(token);
        assertTrue(token.startsWith("ey")); // Base64-encoded JWT
    }

    @Test
    void testGetUserNameFromJwtToken() {
        // Générer un token manuellement
        Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        String token = io.jsonwebtoken.Jwts.builder()
                .setSubject("johndoe")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(key)
                .compact();

        String username = jwtUtils.getUserNameFromJwtToken(token);
        assertEquals("johndoe", username);
    }

    @Test
    void testValidateJwtToken_valid() {
        Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        String token = io.jsonwebtoken.Jwts.builder()
                .setSubject("johndoe")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(key)
                .compact();

        assertTrue(jwtUtils.validateJwtToken(token));
    }

    @Test
    void testValidateJwtToken_expired() {
        Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        String token = io.jsonwebtoken.Jwts.builder()
                .setSubject("johndoe")
                .setIssuedAt(new Date(System.currentTimeMillis() - 2000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(key)
                .compact();

        assertFalse(jwtUtils.validateJwtToken(token));
    }

    @Test
    void testValidateJwtToken_malformed() {
        String malformedToken = "this.is.not.valid";

        assertFalse(jwtUtils.validateJwtToken(malformedToken));
    }
}
