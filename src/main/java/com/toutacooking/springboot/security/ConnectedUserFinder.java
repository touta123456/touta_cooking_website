package com.toutacooking.springboot.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.toutacooking.springboot.entity.User;
import com.toutacooking.springboot.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class ConnectedUserFinder {
	
	private final JwtUtils jwtUtils;
    private final UserService userService;
    
	public ConnectedUserFinder(JwtUtils jwtUtils, UserService userService) {
		this.jwtUtils = jwtUtils;
		this.userService = userService;
	}
	
	public User findRequestingUser(HttpServletRequest request) {
		// 1. Retrieve JWT token in the header
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AccessDeniedException("Missing or invalid Authorization header");
        }
        
        // 2. Extract the token
        String jwt = authHeader.substring(7);
        String username = this.jwtUtils.getUserNameFromJwtToken(jwt);
        
        // 3. Retrive the user details
        User user = userService.loadUserByUsername(username);
        
        // 4. Validate the token
        if (!jwtUtils.validateJwtToken(jwt)) {
            throw new AccessDeniedException("Invalid or expired token");
        }

		return user;
	}
	
}
