package com.toutacooking.springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toutacooking.springboot.dto.RecipeDTO;
import com.toutacooking.springboot.dto.UserDTO;
import com.toutacooking.springboot.entity.User;
import com.toutacooking.springboot.security.ConnectedUserFinder;
import com.toutacooking.springboot.security.JwtUtils;
import com.toutacooking.springboot.service.RecipeService;
import com.toutacooking.springboot.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChefController.class)
@AutoConfigureMockMvc(addFilters = false)
class ChefControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private ConnectedUserFinder connectedUserFinder;

    @Autowired
    private ObjectMapper objectMapper;

    private RecipeDTO sampleRecipe;
    private User fakeChef;

    @BeforeEach
    void setup() {
        sampleRecipe = new RecipeDTO();
        sampleRecipe.setId(1L);
        sampleRecipe.setTitle("Paella");
        sampleRecipe.setIngredients("Rice, chicken, etc");
        fakeChef = new User();
        fakeChef.setId(99L);
    }

    @Test
    void testCreateRecipe() throws Exception {
        when(connectedUserFinder.findRequestingUser(any(HttpServletRequest.class))).thenReturn(fakeChef);

        RecipeDTO created = new RecipeDTO();
        created.setId(1L);
        created.setTitle("Paella");
        created.setIngredients("Rice, chicken, etc");
        UserDTO authorDTO = new UserDTO();
        authorDTO.setId(99L);
        created.setAuthor(authorDTO);

        when(recipeService.save(any(RecipeDTO.class))).thenReturn(created);

        mockMvc.perform(post("/api/chef/recipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRecipe)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testUpdateRecipe() throws Exception {
        when(recipeService.update(eq(1L), any(RecipeDTO.class))).thenReturn(sampleRecipe);

        mockMvc.perform(put("/api/chef/recipe/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRecipe)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testDeleteRecipe() throws Exception {
        doNothing().when(recipeService).delete(1L);

        mockMvc.perform(delete("/api/chef/recipe/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetMyRecipes() throws Exception {
        when(connectedUserFinder.findRequestingUser(any(HttpServletRequest.class))).thenReturn(fakeChef);
        when(recipeService.findByAuthor(any(User.class))).thenReturn(Collections.singletonList(sampleRecipe));

        mockMvc.perform(get("/api/chef/myrecipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testGetMyRecipe_authorized() throws Exception {
        when(connectedUserFinder.findRequestingUser(any(HttpServletRequest.class))).thenReturn(fakeChef);
        RecipeDTO recipeWithAuthor = new RecipeDTO();
        recipeWithAuthor.setId(1L);
        UserDTO authorDTO = new UserDTO();
        authorDTO.setId(99L);
        recipeWithAuthor.setAuthor(authorDTO);

        when(recipeService.findById(1L)).thenReturn(Optional.of(recipeWithAuthor));

        mockMvc.perform(get("/api/chef/recipe/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testGetMyRecipe_notAuthorized() throws Exception {
        when(connectedUserFinder.findRequestingUser(any(HttpServletRequest.class))).thenReturn(fakeChef);
        RecipeDTO otherRecipe = new RecipeDTO();
        otherRecipe.setId(1L);
        UserDTO otherAuthor = new UserDTO();
        otherAuthor.setId(42L); // différent de fakeChef
        otherRecipe.setAuthor(otherAuthor);

        when(recipeService.findById(1L)).thenReturn(Optional.of(otherRecipe));

        mockMvc.perform(get("/api/chef/recipe/1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Not authorized to display this recipe"));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public RecipeService recipeService() {
            return Mockito.mock(RecipeService.class);
        }

        @Bean
        public ConnectedUserFinder connectedUserFinder() {
            return Mockito.mock(ConnectedUserFinder.class);
        }
        
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
        
        @Bean
        public JwtUtils jwtUtils() {
            return Mockito.mock(JwtUtils.class);
        }
    }
}
