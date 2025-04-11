package com.toutacooking.springboot.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toutacooking.springboot.dto.UserDTO;
import com.toutacooking.springboot.entity.Role;
import com.toutacooking.springboot.entity.RoleEnum;
import com.toutacooking.springboot.entity.User;
import com.toutacooking.springboot.security.JwtUtils;
import com.toutacooking.springboot.security.payload.request.LoginRequest;
import com.toutacooking.springboot.security.payload.request.SignupRequest;
import com.toutacooking.springboot.security.payload.response.JwtResponse;
import com.toutacooking.springboot.security.payload.response.MessageResponse;
import com.toutacooking.springboot.service.RoleService;
import com.toutacooking.springboot.service.UserService;
import com.toutacooking.springboot.service.WelcomeMailSenderService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final Logger log = LoggerFactory.getLogger(AuthController.class);
	
	private final AuthenticationManager authenticationManager;
	private final UserService userService;
	private final RoleService roleService;
	private final PasswordEncoder encoder;
	private final JwtUtils jwtUtils;
	private final WelcomeMailSenderService welcomeMailSenderService;
	
	public AuthController(AuthenticationManager authenticationManager, UserService userService,
			RoleService roleService, PasswordEncoder encoder, JwtUtils jwtUtils,
			WelcomeMailSenderService welcomeMailSenderService) {
		this.authenticationManager = authenticationManager;
		this.userService = userService;
		this.roleService = roleService;
		this.encoder = encoder;
		this.jwtUtils = jwtUtils;
		this.welcomeMailSenderService = welcomeMailSenderService;
	}
	
	
	@PostMapping("/signin")
	public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, 
            HttpServletRequest request,
            HttpServletResponse response) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

	    SecurityContext context = SecurityContextHolder.createEmptyContext();
	    context.setAuthentication(authentication);
	    SecurityContextHolder.setContext(context);

		String jwt = jwtUtils.generateJwtToken(authentication);

		User userDetails = (User) authentication.getPrincipal(); 
		String role = userDetails.getAuthorities().iterator().next().getAuthority();

		return ResponseEntity.ok(
				new JwtResponse(
						jwt, 
						userDetails.getId(), 
						userDetails.getUsername(),
						userDetails.getFirstName(),
						userDetails.getLastName(),
						userDetails.getEmail(), 
						role));
		
	}

	@PostMapping("/signup")
	public ResponseEntity<Object> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userService.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(Map.of("message", "Error: Username is already taken"));
		}

		if (userService.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(Map.of("message", "Error: Email is already in use"));
		}

		// Create new user's account
		User user = new User(signUpRequest.getUsername(),
				signUpRequest.getFirstname(),
				signUpRequest.getLastname(),
				signUpRequest.getEmail(),
				encoder.encode(signUpRequest.getPassword()));

		String userRoleLibelle = RoleEnum.USER.getLibelle();
		Role userRole = this.roleService.findByLibelle(userRoleLibelle);
		user.setRole(userRole);
		UserDTO userDTO = this.userService.mapUserToDTO(user);
		userService.save(userDTO);
		welcomeMailSenderService.sendWelcomeEmail(user.getEmail());
		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));

	}
}