package com.toutacooking.springboot.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.toutacooking.springboot.dto.RoleDTO;
import com.toutacooking.springboot.dto.UserDTO;
import com.toutacooking.springboot.entity.Role;
import com.toutacooking.springboot.entity.User;
import com.toutacooking.springboot.repository.JpaUserRepository;

class UserServiceTest {

    private JpaUserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setup() {
        userRepository = mock(JpaUserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void testSaveUser_WithPassword() {
        UserDTO userDTO = createUserDTO();
        userDTO.setPassword("myPassword");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDTO savedUser = userService.save(userDTO);

        assertThat(savedUser).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testSaveUser_WithDefaultPassword() {
        UserDTO userDTO = createUserDTO();
        userDTO.setPassword(null);

        when(passwordEncoder.encode("password")).thenReturn("encodedPwd");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.save(userDTO);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getPassword()).isEqualTo("encodedPwd");
    }

    @Test
    void testUpdateUser() {
        UserDTO userDTO = createUserDTO();
        userDTO.setPassword("newPassword");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDTO result = userService.update(1L, userDTO);

        assertThat(result).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testDeleteUser() {
        Long userId = 1L;
        userService.delete(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    void testFindAll() {
        User user = createUserEntity();
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDTO> result = userService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void testFindById_Found() {
        User user = createUserEntity();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<UserDTO> result = userService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getUserName()).isEqualTo("john_doe");
    }

    @Test
    void testFindById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<UserDTO> result = userService.findById(1L);

        assertThat(result).isNotPresent();
    }

    @Test
    void testFindByUserEntityId() {
        User user = createUserEntity();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUserEntityId(1L);

        assertThat(result).isPresent();
    }

    @Test
    void testFindByEmail() {
        User user = createUserEntity();
        when(userRepository.findByEmail("john@example.com")).thenReturn(user);

        User result = userService.findByEmail("john@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void testLoadUserByUsername() {
        User user = createUserEntity();
        when(userRepository.findByUsername("john_doe")).thenReturn(user);

        User result = userService.loadUserByUsername("john_doe");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("john_doe");
    }

    // Helpers
    private UserDTO createUserDTO() {
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(1L);
        roleDTO.setLibelle("ROLE_USER");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("john@example.com");
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setUserName("john_doe");
        userDTO.setRole(roleDTO);
        return userDTO;
    }

    private User createUserEntity() {
        Role role = new Role();
        role.setId(1L);
        role.setLibelle("ROLE_USER");

        User user = new User();
        user.setId(1L);
        user.setEmail("john@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("john_doe");
        user.setPassword("password");
        user.setRole(role);
        return user;
    }
}
