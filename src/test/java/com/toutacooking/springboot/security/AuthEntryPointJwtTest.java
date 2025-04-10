package com.toutacooking.springboot.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.AuthenticationException;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class AuthEntryPointJwtTest {

    private AuthEntryPointJwt authEntryPointJwt;

    @BeforeEach
    void setUp() {
        authEntryPointJwt = new AuthEntryPointJwt();
    }

    @Test
    void testCommence_setsUnauthorizedResponseWithJsonBody() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException exception = mock(AuthenticationException.class);

        when(request.getServletPath()).thenReturn("/api/test");
        when(exception.getMessage()).thenReturn("Authentication failed");

        // Simule un flux de sortie de la réponse
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ServletOutputStream servletOutputStream = new ServletOutputStream() {
            @Override
            public void write(int b) throws IOException {
                byteArrayOutputStream.write(b);
            }

            @Override
            public boolean isReady() {
                return true; // Pour simplifier, toujours prêt
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
                // No-op dans ce test
            }
        };

        when(response.getOutputStream()).thenReturn(servletOutputStream);

        // Appel de la méthode à tester
        authEntryPointJwt.commence(request, response, exception);

        verify(response).setContentType("application/json");
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // On vérifie le contenu JSON retourné
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> jsonResponse = mapper.readValue(byteArrayOutputStream.toByteArray(), Map.class);

        assertEquals(401, jsonResponse.get("status"));
        assertEquals("Unauthorized", jsonResponse.get("error"));
        assertEquals("Authentication failed", jsonResponse.get("message"));
        assertEquals("/api/test", jsonResponse.get("path"));
    }
}
