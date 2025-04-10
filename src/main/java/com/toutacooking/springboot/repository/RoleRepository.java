package com.toutacooking.springboot.repository;

import org.springframework.stereotype.Repository;

import com.toutacooking.springboot.entity.Role;

@Repository
public interface RoleRepository {

	public void saveRole(Role role);
	
}
