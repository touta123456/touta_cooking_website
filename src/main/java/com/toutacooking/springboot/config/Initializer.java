package com.toutacooking.springboot.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.toutacooking.springboot.dto.RoleDTO;
import com.toutacooking.springboot.dto.UserDTO;
import com.toutacooking.springboot.entity.RoleEnum;
import com.toutacooking.springboot.service.RoleService;
import com.toutacooking.springboot.service.UserService;

@Component
public class Initializer implements CommandLineRunner {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    
	public Initializer(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.roleService = roleService;
		this.passwordEncoder = passwordEncoder;
	}
	   
	@Override
	@Transactional
	public void run(String... args) throws Exception {
		
		this.roleService.save(RoleEnum.ADMIN);
		this.roleService.save(RoleEnum.CHEF);
		this.roleService.save(RoleEnum.USER);
		
		UserDTO adminUser = new UserDTO();
		adminUser.setId(1L);
		adminUser.setEmail("admin@touta_cooking_website.com");
		adminUser.setPassword(this.passwordEncoder.encode(System.getenv("ADMIN_TOUTA_COOKING")));
		adminUser.setFirstName("Olivier");
		adminUser.setLastName("Touta");
		adminUser.setUserName("admin");
		
		
		RoleDTO roleAdminDTO = new RoleDTO();
		roleAdminDTO.setId(RoleEnum.ADMIN.getId());
		roleAdminDTO.setLibelle(RoleEnum.ADMIN.getLibelle());
		adminUser.setRole(roleAdminDTO);
		
		this.userService.save(adminUser);
		
	}

}
