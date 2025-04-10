package com.toutacooking.springboot.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.toutacooking.springboot.entity.Role;

@Repository
class RoleRepositoryImpl implements RoleRepository {
	
	private final JpaRoleRepository roleRepository;
    
    public RoleRepositoryImpl(JpaRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void saveRole(Role role) {
    	this.roleRepository.save(role);
    }

}
