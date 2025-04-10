package com.toutacooking.springboot.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

import com.toutacooking.springboot.entity.User;
import com.toutacooking.springboot.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

class ConnectedUserFinderTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ConnectedUserFinder connectedUserFinder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindRequestingUser_validToken_returnsUser() {
        String jwt = "valid.jwt.token";
        String username = "johndoe";
        User expectedUser = new User();
        expectedUser.setUsername(username);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtUtils.getUserNameFromJwtToken(jwt)).thenReturn(username);
        when(userService.loadUserByUsername(username)).thenReturn(expectedUser);
        when(jwtUtils.validateJwtToken(jwt)).thenReturn(true);

        User actualUser = connectedUserFinder.findRequestingUser(request);

        assertEquals(expectedUser, actualUser);
    }

    @Test
    void testFindRequestingUser_missingHeader_throwsException() {
        when(request.getHeader("Authorization")).thenReturn(null);

        assertThrows(AccessDeniedException.class, () -> {
            connectedUserFinder.findRequestingUser(request);
        });
    }

    @Test
    void testFindRequestingUser_invalidToken_throwsException() {
        String jwt = "invalid.jwt.token";
        String username = "johndoe";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtUtils.getUserNameFromJwtToken(jwt)).thenReturn(username);
        when(userService.loadUserByUsername(username)).thenReturn(new User());
        when(jwtUtils.validateJwtToken(jwt)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> {
            connectedUserFinder.findRequestingUser(request);
        });
    }
    
}
