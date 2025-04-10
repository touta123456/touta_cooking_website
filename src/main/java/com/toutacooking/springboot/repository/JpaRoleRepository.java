package com.toutacooking.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.toutacooking.springboot.entity.Role;

@Repository
public interface JpaRoleRepository extends JpaRepository<Role, Long> {

	Role findByLibelle(String libelle);
	
}