package com.toutacooking.springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toutacooking.springboot.dto.CommentDTO;
import com.toutacooking.springboot.dto.RecipeDTO;
import com.toutacooking.springboot.dto.UserDTO;
import com.toutacooking.springboot.entity.User;
import com.toutacooking.springboot.security.ConnectedUserFinder;
import com.toutacooking.springboot.security.JwtUtils;
import com.toutacooking.springboot.service.CommentService;
import com.toutacooking.springboot.service.RecipeService;
import com.toutacooking.springboot.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecipesUserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(RecipesUserControllerTest.TestConfig.class)
class RecipesUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ConnectedUserFinder connectedUserFinder;

    @Autowired
    private ObjectMapper objectMapper;

    private RecipeDTO sampleRecipe;
    private CommentDTO sampleComment;

    @BeforeEach
    void setup() {
        sampleRecipe = new RecipeDTO();
        sampleRecipe.setId(1L);
        sampleRecipe.setTitle("Test Recipe");

        sampleComment = new CommentDTO();
        sampleComment.setContent("Great recipe!");
    }

    @Test
    void testGetMyRecipes() throws Exception {
        when(recipeService.findAll()).thenReturn(Collections.singletonList(sampleRecipe));

        mockMvc.perform(get("/api/recipes/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testGetRecipe_found() throws Exception {
        when(recipeService.findByIdWithComments(1L)).thenReturn(Optional.of(sampleRecipe));

        mockMvc.perform(get("/api/recipes/recipe/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testGetRecipe_notFound() throws Exception {
        when(recipeService.findByIdWithComments(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/recipes/recipe/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCommentRecipe() throws Exception {
        // Simuler l'utilisateur connecté
        User fakeUser = new User();
        fakeUser.setId(99L);
        when(connectedUserFinder.findRequestingUser(any(HttpServletRequest.class))).thenReturn(fakeUser);

        CommentDTO returnedComment = new CommentDTO();
        returnedComment.setId(10L);
        returnedComment.setContent("Great recipe!");
        returnedComment.setRecipe(sampleRecipe);
        UserDTO authorDTO = new UserDTO();
        authorDTO.setId(99L);
        returnedComment.setUser(authorDTO);

        when(commentService.save(any(CommentDTO.class))).thenReturn(returnedComment);

        mockMvc.perform(post("/api/recipes/comment/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleComment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.content").value("Great recipe!"));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public RecipeService recipeService() {
            return Mockito.mock(RecipeService.class);
        }

        @Bean
        public CommentService commentService() {
            return Mockito.mock(CommentService.class);
        }

        @Bean
        public ConnectedUserFinder connectedUserFinder() {
            return Mockito.mock(ConnectedUserFinder.class);
        }
        
        @Bean
        public JwtUtils jwtUtils() {
            return Mockito.mock(JwtUtils.class);
        }
        
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }

    }
}
