package com.toutacooking.springboot.repository;

import com.toutacooking.springboot.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;

class UserRepositoryImplTest {

    private JpaUserRepository jpaUserRepository;
    private UserRepositoryImpl userRepository;

    @BeforeEach
    void setUp() {
        jpaUserRepository = Mockito.mock(JpaUserRepository.class);
        userRepository = new UserRepositoryImpl(jpaUserRepository);
    }

    @Test
    void testSaveUser_shouldCallJpaRepositorySave() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setUsername("johndoe");
        user.setPassword("password123");

        // When
        userRepository.saveUser(user);

        // Then
        verify(jpaUserRepository).save(user);
    }
}
