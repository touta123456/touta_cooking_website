package com.toutacooking.springboot.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.toutacooking.springboot.entity.Role;
import com.toutacooking.springboot.entity.User;

@DataJpaTest
class JpaUserRepositoryTest {

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private JpaRoleRepository roleRepository;
    
    @Test
    void shouldSaveAndFindUserByUsername() {
    	Role testRole = new Role();
    	testRole.setLibelle("ROLE_TEST");
    	this.roleRepository.save(testRole);
    	
        User user = new User();
        user.setFirstName("testFirstName");
        user.setLastName("testLastName");
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("TEST123456");
        user.setRole(testRole);

        userRepository.save(user);

        User found = userRepository.findByUsername("testuser");

        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldCheckIfEmailExists() {
    	Role testRole = new Role();
    	testRole.setLibelle("ROLE_TEST");
    	this.roleRepository.save(testRole);
    	
        User user = new User();
        user.setUsername("othertestuser");
        user.setFirstName("otherFirstName");
        user.setLastName("otherLastName");
        user.setEmail("exists@example.com");
        user.setPassword("PASS123654");
        user.setRole(testRole);

        userRepository.save(user);

        boolean exists = userRepository.existsByEmail("exists@example.com");

        assertThat(exists).isTrue();
    }
}
