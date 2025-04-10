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
        // Création d'un rôle
        Role role = new Role();
        role.setLibelle("ROLE_ADMIN");

        // Sauvegarde en base
        roleRepository.save(role);

        // Récupération avec la méthode à tester
        Role found = roleRepository.findByLibelle("ROLE_ADMIN");

        // Assertions
        assertThat(found).isNotNull();
        assertThat(found.getId()).isNotNull();
        assertThat(found.getLibelle()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void shouldReturnNullIfLibelleNotFound() {
        // On cherche un rôle qui n’existe pas
        Role found = roleRepository.findByLibelle("ROLE_NON_EXISTENT");

        // Vérification que rien n'est retourné
        assertThat(found).isNull();
    }
}
