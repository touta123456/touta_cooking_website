package com.toutacooking.springboot.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

import com.toutacooking.springboot.dto.RoleDTO;
import com.toutacooking.springboot.entity.RoleEnum;
import com.toutacooking.springboot.security.JwtUtils;
import com.toutacooking.springboot.service.RoleService;
import com.toutacooking.springboot.service.UserService;

@WebMvcTest(RoleController.class)
@AutoConfigureMockMvc(addFilters = false)
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoleService roleService;

    @Test
    void testGetRoles() throws Exception {
        RoleDTO role1 = new RoleDTO();
        role1.setId(RoleEnum.ADMIN.getId());
        role1.setLibelle(RoleEnum.ADMIN.getLibelle());

        RoleDTO role2 = new RoleDTO();
        role2.setId(RoleEnum.CHEF.getId());
        role2.setLibelle(RoleEnum.CHEF.getLibelle());

        RoleDTO role3 = new RoleDTO();
        role3.setId(RoleEnum.USER.getId());
        role3.setLibelle(RoleEnum.USER.getLibelle());

        
        when(roleService.findAll()).thenReturn(Arrays.asList(role1, role2, role3));

        mockMvc.perform(get("/api/roles/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].libelle").value("ADMIN"))
                .andExpect(jsonPath("$[1].libelle").value("CHEF"))
                .andExpect(jsonPath("$[2].libelle").value("USER"));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public RoleService roleService() {
            return Mockito.mock(RoleService.class);
        }
        
        @Bean
        public JwtUtils jwtUtils() {
            return Mockito.mock(JwtUtils.class);
        }
        
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }
}
