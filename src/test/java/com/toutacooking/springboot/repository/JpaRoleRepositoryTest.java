package com.toutacooking.springboot.repository;

import com.toutacooking.springboot.entity.Role;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class JpaRoleRepositoryTest {

    @Autowired
    private JpaRoleRepository roleRepository;

    @Test
    void shouldSaveAndFindRoleByLibelle() {
        // Role creation
        Role role = new Role();
        role.setLibelle("ROLE_ADMIN");

        // Save in database
        roleRepository.save(role);

        // Retrieve role
        Role found = roleRepository.findByLibelle("ROLE_ADMIN");

        // Assertions
        assertThat(found).isNotNull();
        assertThat(found.getId()).isNotNull();
        assertThat(found.getLibelle()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void shouldReturnNullIfLibelleNotFound() {
        // Lets search a non exeisting role
        Role found = roleRepository.findByLibelle("ROLE_NON_EXISTENT");

        // Assert no role has been found
        assertThat(found).isNull();
    }
}
