package com.toutacooking.springboot.repository;

import com.toutacooking.springboot.entity.Recipe;
import com.toutacooking.springboot.entity.Role;
import com.toutacooking.springboot.entity.User;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class JpaRecipeRepositoryTest {

    @Autowired
    private JpaRecipeRepository recipeRepository;

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private JpaRoleRepository roleRepository;

    @Test
    void shouldFindRecipesByAuthor() {
        // Role creation
        Role role = new Role();
        role.setLibelle("ROLE_CHEF");
        role = roleRepository.save(role);

        // Author (a user) creation
        User author = new User();
        author.setFirstName("Gordon");
        author.setLastName("Ramsay");
        author.setUsername("gordonr");
        author.setEmail("gordon@example.com");
        author.setPassword("HELLSKITCHEN123");
        author.setRole(role);
        author = userRepository.save(author);

        // Recipe creation
        Recipe recipe = new Recipe();
        recipe.setTitle("Beef Wellington");
        recipe.setIngredients("A classic British dish.");
        recipe.setAuthor(author);
        recipeRepository.save(recipe);

        // Retrieve recipes by author
        List<Recipe> recipes = recipeRepository.findByAuthor(author);

        // Assertions
        assertThat(recipes).isNotEmpty();
        assertThat(recipes.get(0).getTitle()).isEqualTo("Beef Wellington");
        assertThat(recipes.get(0).getAuthor().getUsername()).isEqualTo("gordonr");
    }

    @Test
    void shouldReturnEmptyListWhenAuthorHasNoRecipes() {
        // Role creation
        Role role = new Role();
        role.setLibelle("ROLE_CHEF");
        role = roleRepository.save(role);

        // User creation
        User author = new User();
        author.setFirstName("Jamie");
        author.setLastName("Oliver");
        author.setUsername("jamieo");
        author.setEmail("jamie@example.com");
        author.setPassword("FAKECHEF321");
        author.setRole(role);
        author = userRepository.save(author);

        // Retrieve recipes by author (none ... empty list expected)
        List<Recipe> recipes = recipeRepository.findByAuthor(author);

        // Assertions
        assertThat(recipes).isEmpty();
    }
}
