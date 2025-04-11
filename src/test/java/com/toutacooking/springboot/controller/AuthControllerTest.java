package com.toutacooking.springboot.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toutacooking.springboot.entity.Role;
import com.toutacooking.springboot.entity.RoleEnum;
import com.toutacooking.springboot.entity.User;
import com.toutacooking.springboot.security.JwtUtils;
import com.toutacooking.springboot.security.payload.request.LoginRequest;
import com.toutacooking.springboot.security.payload.request.SignupRequest;
import com.toutacooking.springboot.service.RoleService;
import com.toutacooking.springboot.service.UserService;
import com.toutacooking.springboot.service.WelcomeMailSenderService;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testAuthenticateUser_success() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("johndoe");
        loginRequest.setPassword("PASSWORD123");

        Role mockRole = new Role();
        mockRole.setId(RoleEnum.USER.getId());
        mockRole.setLibelle(RoleEnum.USER.getLibelle());

        User mockUser = new User("johndoe", "John", "Doe", "john@doe.com", "encoded");
        mockUser.setId(1L);
        mockUser.setRole(mockRole);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mockUser);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("fake-jwt");

        mockMvc.perform(post("/api/auth/signin")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("fake-jwt"))
                .andExpect(jsonPath("$.userName").value("johndoe"));
    }

    @Test
    void testRegisterUser_success() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setUsername("newuser");
        request.setFirstname("New");
        request.setLastname("User");
        request.setEmail("new@user.com");
        request.setPassword("PASSWORD123");

        when(userService.existsByUsername("newuser")).thenReturn(false);
        when(userService.existsByEmail("new@user.com")).thenReturn(false);
        when(encoder.encode("PASSWORD123")).thenReturn("encodedpass");

        Role mockRole = new Role();
        mockRole.setId(RoleEnum.USER.getId());
        mockRole.setLibelle(RoleEnum.USER.getLibelle());

        when(roleService.findByLibelle(RoleEnum.USER.getLibelle()))
                .thenReturn(mockRole);

        mockMvc.perform(post("/api/auth/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    void testRegisterUser_usernameExists() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setUsername("existing");
        request.setFirstname("existingFirstName");
        request.setLastname("existingLastName");
        request.setEmail("existing@user.com");
        request.setPassword("TOPPASSWD456");

        when(userService.existsByUsername("existing")).thenReturn(true);

        mockMvc.perform(post("/api/auth/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Username is already taken"));
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public AuthenticationManager authenticationManager() {
            return Mockito.mock(AuthenticationManager.class);
        }

        @Bean
        public PasswordEncoder encoder() {
            return Mockito.mock(PasswordEncoder.class);
        }

        @Bean
        public JwtUtils jwtUtils() {
            return Mockito.mock(JwtUtils.class);
        }

        @Bean
        public WelcomeMailSenderService welcomeMailSenderService() {
            return Mockito.mock(WelcomeMailSenderService.class);
        }

        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }

        @Bean
        public RoleService roleService() {
            return Mockito.mock(RoleService.class);
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(csrf -> csrf.disable()); // Tu peux l'enlever si tu as bien les with(csrf())
            return http.build();
        }
    }
}
