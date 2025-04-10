package com.toutacooking.springboot.repository;

import static org.mockito.Mockito.verify;

import com.toutacooking.springboot.entity.Role;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class RoleRepositoryImplTest {

    private JpaRoleRepository jpaRoleRepository;
    private RoleRepositoryImpl roleRepository;

    @BeforeEach
    void setUp() {
        jpaRoleRepository = Mockito.mock(JpaRoleRepository.class);
        roleRepository = new RoleRepositoryImpl(jpaRoleRepository);
    }

    @Test
    void testSaveRole_shouldCallJpaRepositorySave() {
        // Given
        Role role = new Role();
        role.setId(1L);
        role.setLibelle("ADMIN");

        // When
        roleRepository.saveRole(role);

        // Then
        verify(jpaRoleRepository).save(role);
    }
}
