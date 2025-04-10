package com.toutacooking.springboot.service;

import com.toutacooking.springboot.dto.RoleDTO;
import com.toutacooking.springboot.entity.Role;
import com.toutacooking.springboot.entity.RoleEnum;
import com.toutacooking.springboot.repository.JpaRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleServiceTest {

    private JpaRoleRepository roleRepository;
    private RoleService roleService;

    @BeforeEach
    void setUp() {
        roleRepository = mock(JpaRoleRepository.class);
        roleService = new RoleService(roleRepository);
    }

    @Test
    void testSaveRole() {
        RoleEnum roleEnum = RoleEnum.ADMIN;

        Role savedRole = new Role();
        savedRole.setId(roleEnum.getId());
        savedRole.setLibelle(roleEnum.getLibelle());

        when(roleRepository.save(any(Role.class))).thenReturn(savedRole);

        Role result = roleService.save(roleEnum);

        assertNotNull(result);
        assertEquals(roleEnum.getId(), result.getId());
        assertEquals(roleEnum.getLibelle(), result.getLibelle());

        ArgumentCaptor<Role> roleCaptor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository).save(roleCaptor.capture());
        Role roleToSave = roleCaptor.getValue();
        assertEquals(roleEnum.getId(), roleToSave.getId());
        assertEquals(roleEnum.getLibelle(), roleToSave.getLibelle());
    }

    @Test
    void testFindAllRoles() {

        Role role1 = new Role();
        role1.setId(RoleEnum.ADMIN.getId());
        role1.setLibelle(RoleEnum.ADMIN.getLibelle());

        Role role2 = new Role();
        role2.setId(RoleEnum.CHEF.getId());
        role2.setLibelle(RoleEnum.CHEF.getLibelle());

        Role role3 = new Role();
        role3.setId(RoleEnum.USER.getId());
        role3.setLibelle(RoleEnum.USER.getLibelle());
        
        when(roleRepository.findAll()).thenReturn(Arrays.asList(role1, role2, role3));

        List<RoleDTO> result = roleService.findAll();

        assertEquals(3, result.size());

        RoleDTO dto1 = result.get(0);
        assertEquals(1L, dto1.getId());
        assertEquals("ADMIN", dto1.getLibelle());

        RoleDTO dto2 = result.get(1);
        assertEquals(2L, dto2.getId());
        assertEquals("CHEF", dto2.getLibelle());
        
        RoleDTO dto3 = result.get(2);
        assertEquals(3L, dto3.getId());
        assertEquals("USER", dto3.getLibelle());
    }

    @Test
    void testMapRoleToDTO() {
        Role role = new Role();
        role.setId(99L);
        role.setLibelle("TEST");

        RoleDTO dto = roleService.mapRoleToDTO(role);

        assertNotNull(dto);
        assertEquals(99L, dto.getId());
        assertEquals("TEST", dto.getLibelle());
    }
}
