package com.toutacooking.springboot.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class WebSecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenAccessingPublicEndpoint_thenPermitAll() throws Exception {
        mockMvc.perform(get("/api/public/ping"))
               .andExpect(status().isOk()); // suppose que "/" est accessible
    }

    @Test
    void whenAccessingProtectedEndpointWithoutAuth_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/recipes/all"))
               .andExpect(status().isUnauthorized()); // doit matcher une route protégée
    }
}
