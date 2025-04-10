package com.toutacooking.springboot.repository;

import com.toutacooking.springboot.entity.Comment;
import com.toutacooking.springboot.entity.Recipe;
import com.toutacooking.springboot.entity.Role;
import com.toutacooking.springboot.entity.User;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class JpaCommentRepositoryTest {

    @Autowired
    private JpaCommentRepository commentRepository;

    @Autowired
    private JpaRecipeRepository recipeRepository;

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private JpaRoleRepository roleRepository;

    @Test
    void shouldFindCommentsByRecipe() {
        // Création du rôle
        Role role = new Role();
        role.setLibelle("ROLE_USER");
        role = roleRepository.save(role);

        // Création de l'utilisateur
        User user = new User();
        user.setFirstName("Alice");
        user.setLastName("Doe");
        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setPassword("PASSWORD0123");
        user.setRole(role);
        user = userRepository.save(user);

        // Création de la recette
        Recipe recipe = new Recipe();
        recipe.setTitle("Chocolate Cake");
        recipe.setIngredients("A rich chocolate cake recipe.");
        recipe.setAuthor(user);
        recipe = recipeRepository.save(recipe);

        // Création d'un commentaire lié à cette recette
        Comment comment = new Comment();
        comment.setContent("Delicious!");
        comment.setRecipe(recipe);
        comment.setUser(user);
        commentRepository.save(comment);

        // Récupération des commentaires
        List<Comment> comments = commentRepository.findByRecipe(recipe);

        // Assertions
        assertThat(comments).isNotEmpty();
        assertThat(comments.get(0).getContent()).isEqualTo("Delicious!");
        assertThat(comments.get(0).getRecipe().getTitle()).isEqualTo("Chocolate Cake");
    }

    @Test
    void shouldReturnEmptyListWhenRecipeHasNoComments() {
        // Création du rôle
        Role role = new Role();
        role.setLibelle("ROLE_USER");
        role = roleRepository.save(role);

        // Création de l'utilisateur
        User user = new User();
        user.setFirstName("Bob");
        user.setLastName("Smith");
        user.setUsername("bob");
        user.setEmail("bob@example.com");
        user.setPassword("SECUREDPASS123");
        user.setRole(role);
        user = userRepository.save(user);

        // Création d'une recette sans commentaire
        Recipe recipe = new Recipe();
        recipe.setTitle("Pasta Carbonara");
        recipe.setIngredients("Classic Italian pasta.");
        recipe.setAuthor(user);
        recipe = recipeRepository.save(recipe);

        // Récupération des commentaires
        List<Comment> comments = commentRepository.findByRecipe(recipe);

        // Assertions
        assertThat(comments).isEmpty();
    }
}
