package com.toutacooking.springboot.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toutacooking.springboot.dto.RoleDTO;
import com.toutacooking.springboot.service.RoleService;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

	private final RoleService roleService;
    
    public RoleController(RoleService roleService) {
		this.roleService = roleService;
	}

    @GetMapping("/roles")
    public List<RoleDTO> getRoles() {
        return roleService.findAll();
    }
    
	
}
