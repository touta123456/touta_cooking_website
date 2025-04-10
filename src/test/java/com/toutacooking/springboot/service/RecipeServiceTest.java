package com.toutacooking.springboot.service;

import com.toutacooking.springboot.dto.RecipeDTO;
import com.toutacooking.springboot.dto.UserDTO;
import com.toutacooking.springboot.entity.Comment;
import com.toutacooking.springboot.entity.Recipe;
import com.toutacooking.springboot.entity.User;
import com.toutacooking.springboot.repository.JpaCommentRepository;
import com.toutacooking.springboot.repository.JpaRecipeRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RecipeServiceTest {

    @Mock
    private JpaRecipeRepository recipeRepository;

    @Mock
    private JpaCommentRepository commentRepository;

    @InjectMocks
    private RecipeService recipeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveRecipe() {
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setId(1L);
        recipeDTO.setTitle("Pasta");
        UserDTO authorDTO = new UserDTO();
        authorDTO.setId(1L);
        recipeDTO.setAuthor(authorDTO);

        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setTitle("Pasta");

        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);

        RecipeDTO saved = recipeService.save(recipeDTO);

        assertThat(saved.getTitle()).isEqualTo("Pasta");
        verify(recipeRepository, times(1)).save(any(Recipe.class));
    }

    @Test
    void testUpdateRecipe() {
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setId(1L);
        recipeDTO.setTitle("Updated title");
        
        UserDTO author = new UserDTO();
        author.setId(1L);
        recipeDTO.setAuthor(author);

        Recipe recipe = new Recipe();
        recipe.setId(1L);

        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);

        RecipeDTO updated = recipeService.update(1L, recipeDTO);

        assertThat(updated.getTitle()).isEqualTo("Updated title");
        verify(recipeRepository, times(1)).save(any(Recipe.class));
    }

    @Test
    void testDeleteRecipe() {
        recipeService.delete(1L);
        verify(recipeRepository, times(1)).deleteById(1L);
    }

    @Test
    void testFindAll() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setTitle("Tarte");
        recipe.setAuthor(new User());
        recipe.setComments(new ArrayList<>());

        when(recipeRepository.findAll()).thenReturn(List.of(recipe));

        List<RecipeDTO> recipes = recipeService.findAll();

        assertThat(recipes).hasSize(1);
        assertThat(recipes.get(0).getTitle()).isEqualTo("Tarte");
    }

    @Test
    void testFindById() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setTitle("Soupe");
        recipe.setAuthor(new User());
        recipe.setComments(new ArrayList<>());

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));

        Optional<RecipeDTO> found = recipeService.findById(1L);

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Soupe");
    }

    @Test
    void testFindByIdWithComments() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setTitle("Curry");
        recipe.setAuthor(new User());
        recipe.setComments(new ArrayList<>());

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("Great!");

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(commentRepository.findByRecipe(recipe)).thenReturn(List.of(comment));

        Optional<RecipeDTO> result = recipeService.findByIdWithComments(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getComments()).hasSize(1);
        assertThat(result.get().getComments().get(0).getContent()).isEqualTo("Great!");
    }

    @Test
    void testFindByAuthor() {
        User author = new User();
        author.setId(1L);

        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setTitle("Salade");
        recipe.setAuthor(author);
        recipe.setComments(new ArrayList<>());

        when(recipeRepository.findByAuthor(author)).thenReturn(List.of(recipe));

        List<RecipeDTO> result = recipeService.findByAuthor(author);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Salade");
    }
}
