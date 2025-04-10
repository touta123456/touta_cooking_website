package com.toutacooking.springboot.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.toutacooking.springboot.entity.RoleEnum;
import com.toutacooking.springboot.entity.User;
import com.toutacooking.springboot.service.RoleService;
import com.toutacooking.springboot.service.UserService;

@SpringBootTest
class InitializerTest {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Test
    void initializer_shouldCreateAdminUserAndRoles() {
        // Check roles has been created
        assertThat(roleService.findAll())
            .extracting("libelle")
            .contains(RoleEnum.ADMIN.getLibelle(), RoleEnum.CHEF.getLibelle(), RoleEnum.USER.getLibelle());

        // Check the admin user has been created
        User admin = userService.findByEmail("admin@touta_cooking_website.com");
        assertThat(admin).isNotNull();
        assertThat(admin.getUsername()).isEqualTo("admin");
        assertThat(admin.getRole().getLibelle()).isEqualTo(RoleEnum.ADMIN.getLibelle());
    }
}
