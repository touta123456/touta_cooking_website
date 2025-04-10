package com.toutacooking.springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toutacooking.springboot.dto.RoleDTO;
import com.toutacooking.springboot.dto.UserDTO;
import com.toutacooking.springboot.entity.RoleEnum;
import com.toutacooking.springboot.security.JwtUtils;
import com.toutacooking.springboot.service.UserService;
import com.toutacooking.springboot.service.WelcomeMailSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(UserControllerTest.TestConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private WelcomeMailSenderService welcomeMailSenderService;

    private UserDTO sampleUser;

    @BeforeEach
    void setUp() {
		UserDTO testUser = new UserDTO();
		testUser.setId(1L);
		testUser.setEmail("test@toutacookingwebsite.com");
		testUser.setPassword("TEST123456");
		testUser.setFirstName("TestFirstName");
		testUser.setLastName("TestLastName");
		testUser.setUserName("TestUserName");

		RoleDTO roleAdminDTO = new RoleDTO();
		roleAdminDTO.setId(RoleEnum.USER.getId());
		roleAdminDTO.setLibelle(RoleEnum.USER.getLibelle());
		testUser.setRole(roleAdminDTO);
		this.sampleUser = testUser;

    }
    
    @Test
    void testCreateUser() throws Exception {
        when(userService.save(any(UserDTO.class))).thenReturn(sampleUser);

        mockMvc.perform(post("/api/users/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));

        verify(welcomeMailSenderService).sendWelcomeEmail("test@toutacookingwebsite.com");
    }

    @Test
    void testUpdateUser() throws Exception {
        when(userService.update(eq(1L), any(UserDTO.class))).thenReturn(sampleUser);

        mockMvc.perform(put("/api/users/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testDeleteUser() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/api/users/user/1"))
                .andExpect(status().isNoContent());

        verify(userService).delete(1L);
    }

    @Test
    void testGetUsers() throws Exception {
        when(userService.findAll()).thenReturn(Arrays.asList(sampleUser));

        mockMvc.perform(get("/api/users/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testGetUser_found() throws Exception {
        when(userService.findById(1L)).thenReturn(Optional.of(sampleUser));

        mockMvc.perform(get("/api/users/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testGetUser_notFound() throws Exception {
        when(userService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/user/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateUser_validationError() throws Exception {
        UserDTO invalidUser = new UserDTO();
        invalidUser.setUserName("noemailuser");

        mockMvc.perform(post("/api/users/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists());
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }

        @Bean
        public WelcomeMailSenderService welcomeMailSenderService() {
            return Mockito.mock(WelcomeMailSenderService.class);
        }
        
        @Bean
        public JwtUtils jwtUtils() {
            return Mockito.mock(JwtUtils.class);
        }
    }
}
