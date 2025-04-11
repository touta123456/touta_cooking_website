package com.toutacooking.springboot.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toutacooking.springboot.dto.RoleDTO;
import com.toutacooking.springboot.entity.Role;
import com.toutacooking.springboot.entity.RoleEnum;
import com.toutacooking.springboot.repository.JpaRoleRepository;


@Service
public class RoleService {

	private final JpaRoleRepository roleRepository;
	
    public RoleService(JpaRoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

    @Transactional
    public Role save(RoleEnum role) {
    	Role roleToSave = new Role();
    	roleToSave.setId(role.getId());
    	roleToSave.setLibelle(role.getLibelle());
    	return roleRepository.save(roleToSave);
    }

    @Transactional
    public List<RoleDTO> findAll() {
		List<Role> roles = this.roleRepository.findAll();
		return roles.stream().map(this::mapRoleToDTO).toList();
	}
	
    
    public RoleDTO mapRoleToDTO(Role role) {
	    RoleDTO roleDTO = new RoleDTO();
	    roleDTO.setId(role.getId());
	    roleDTO.setLibelle(role.getLibelle());
        return roleDTO;
    }

	public Role findByLibelle(String userRoleLibelle) {
		return this.roleRepository.findByLibelle(userRoleLibelle);
	}
	
}