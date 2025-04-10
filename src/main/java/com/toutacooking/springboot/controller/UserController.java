package com.toutacooking.springboot.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toutacooking.springboot.dto.UserDTO;
import com.toutacooking.springboot.service.UserService;
import com.toutacooking.springboot.service.WelcomeMailSenderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

	private final Logger log = LoggerFactory.getLogger(UserController.class);
    
	private final UserService userService;
	private final WelcomeMailSenderService welcomeMailSenderService;
    
    public UserController(UserService userService, WelcomeMailSenderService welcomeMailSenderService) {
		this.userService = userService;
		this.welcomeMailSenderService = welcomeMailSenderService;
	}

	@PostMapping("/user")
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDTO user, BindingResult result) throws URISyntaxException {
		log.info("Request to create user: {}", user);
	    if (result.hasErrors()) {
	        return ResponseEntity.badRequest().body(
	            result.getFieldErrors().stream()
	                .collect(Collectors.toMap(
	                    FieldError::getField,
	                    FieldError::getDefaultMessage
	                ))
	        );
	    }
		UserDTO createdUser = userService.save(user);
		welcomeMailSenderService.sendWelcomeEmail(user.getEmail());
		return ResponseEntity.created(new URI("/api/users/user/" + createdUser.getId()))
                .body(createdUser);
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO user, BindingResult result) {
    	log.info("Request to update user: {}", user);
	    if (result.hasErrors()) {
	        return ResponseEntity.badRequest().body(
	            result.getFieldErrors().stream()
	                .collect(Collectors.toMap(
	                    FieldError::getField,
	                    FieldError::getDefaultMessage
	                ))
	        );
	    }
	    UserDTO updatedUser = userService.update(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/users")
    public List<UserDTO> getUsers() {
        return userService.findAll();
    }
    
    @GetMapping("/user/{id}")
    ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        Optional<UserDTO> user = this.userService.findById(id);
        return user.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
}