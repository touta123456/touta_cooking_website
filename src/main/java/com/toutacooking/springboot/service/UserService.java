package com.toutacooking.springboot.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toutacooking.springboot.dto.RoleDTO;
import com.toutacooking.springboot.dto.UserDTO;
import com.toutacooking.springboot.entity.Role;
import com.toutacooking.springboot.entity.User;
import com.toutacooking.springboot.repository.JpaUserRepository;

import jakarta.validation.Valid;

@Service
public class UserService implements UserDetailsService {

	private static final String ROLE_NOT_SET_WARNING = "User having id {} has no role set";
    private final Logger log = LoggerFactory.getLogger(UserService.class);
	private final JpaUserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

    public UserService(JpaUserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

    @Transactional
    public UserDTO save(UserDTO userDTO) {
    	User user = new User();
    	user.setId(userDTO.getId());
    	user.setEmail(userDTO.getEmail());
    	user.setFirstName(userDTO.getFirstName());
    	user.setLastName(userDTO.getLastName());
    	user.setUsername(userDTO.getUserName());
    	if(userDTO.getPassword()!=null) {
    		user.setPassword(userDTO.getPassword());
    	}
    	else { // default password
    		user.setPassword(this.passwordEncoder.encode(System.getenv("PWD_TOUTA_COOKING")));
    	}
    	
    	RoleDTO roleDTO = userDTO.getRole();
    	if(roleDTO!=null) {
	    	Role role = new Role();
	    	role.setId(roleDTO.getId());
	    	role.setLibelle(roleDTO.getLibelle());
	    	user.setRole(role);
    	}
    	else {
    		log.warn(ROLE_NOT_SET_WARNING, userDTO.getId());
    	}
    	
        userRepository.save(user);
        return userDTO;
    }

    @Transactional
	public UserDTO update(Long id, @Valid UserDTO userDTO) {
    	User user = new User();
    	user.setId(id);
    	user.setEmail(userDTO.getEmail());
    	user.setFirstName(userDTO.getFirstName());
    	user.setLastName(userDTO.getLastName());
    	user.setUsername(userDTO.getUserName());
    	user.setPassword(userDTO.getPassword());
    	
    	RoleDTO roleDTO = userDTO.getRole();
    	if(roleDTO!=null) {
	    	Role role = new Role();
	    	role.setId(roleDTO.getId());
	    	role.setLibelle(roleDTO.getLibelle());
	    	user.setRole(role);
    	}
    	else {
    		log.warn(ROLE_NOT_SET_WARNING, userDTO.getId());
    	}

		userRepository.save(user);
		return userDTO;
	}

    @Transactional
	public void delete(Long id) {
		this.userRepository.deleteById(id);
		
	}
    
	public List<UserDTO> findAll() {
		List<User> users = this.userRepository.findAll();
		return users.stream().map(this::mapUserToDTO).toList();
	}
	
    public UserDTO mapUserToDTO(User user) {
    	UserDTO userDTO = new UserDTO();
    	userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setUserName(user.getUsername());
        userDTO.setPassword(user.getPassword());
        
        Role role = user.getRole();
        if(role!=null) {
	    	RoleDTO roleDTO = new RoleDTO();
	    	roleDTO.setId(role.getId());
	    	roleDTO.setLibelle(role.getLibelle());
	    	userDTO.setRole(roleDTO);
        }
        else {
        	log.warn(ROLE_NOT_SET_WARNING, userDTO.getId());
        }
        return userDTO;
    }


	public Optional<UserDTO> findById(Long id) {
		Optional<User> user = this.userRepository.findById(id);
		return user.map(this::mapUserToDTO);
	}

	public Optional<User> findByUserEntityId(Long id) {
		return this.userRepository.findById(id);
	}
	
	public User findByEmail(String email) {
		return this.userRepository.findByEmail(email);
	}
	
	public User loadUserByUsername(String username) {
		return this.userRepository.findByUsername(username);
	}
    
}