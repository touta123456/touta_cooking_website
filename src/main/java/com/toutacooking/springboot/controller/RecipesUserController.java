package com.toutacooking.springboot.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toutacooking.springboot.dto.CommentDTO;
import com.toutacooking.springboot.dto.RecipeDTO;
import com.toutacooking.springboot.dto.UserDTO;
import com.toutacooking.springboot.entity.User;
import com.toutacooking.springboot.security.ConnectedUserFinder;
import com.toutacooking.springboot.service.CommentService;
import com.toutacooking.springboot.service.RecipeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/recipes")
@PreAuthorize("hasRole('USER')")
public class RecipesUserController {

	private final RecipeService recipeService;
    private final CommentService commentService;
    private final ConnectedUserFinder connectedUserFinder;
    
    public RecipesUserController(RecipeService recipeService, CommentService commentService, 
    		ConnectedUserFinder connectedUserFinder) {
		this.recipeService = recipeService;
		this.commentService = commentService;
		this.connectedUserFinder = connectedUserFinder;
	}


    @GetMapping("/all")
    public List<RecipeDTO> getAllRecipes() {
    	return recipeService.findAll();
    }
	
    @GetMapping("/recipe/{id}")
    ResponseEntity<Optional<RecipeDTO>> getRecipe(@PathVariable Long id) {
    	Optional<RecipeDTO> recipe = recipeService.findByIdWithComments(id);
        return recipe.map(response -> ResponseEntity.ok().body(recipe))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    

	@PostMapping("/comment/{id}")
    public ResponseEntity<CommentDTO> commentRecipe(@PathVariable Long id, @Valid @RequestBody CommentDTO comment, HttpServletRequest request) throws URISyntaxException {
	    
		// Author
		User author = this.connectedUserFinder.findRequestingUser(request);
	    UserDTO authorDTO = new UserDTO();
	    authorDTO.setId(author.getId());
	    comment.setUser(authorDTO);
	    
	    // Recipe
	    RecipeDTO recipeDTO = new RecipeDTO();
	    recipeDTO.setId(id);
	    comment.setRecipe(recipeDTO);
	    
	    // Save comment and response
		CommentDTO createdComment = this.commentService.save(comment);
        return ResponseEntity.created(new URI("/api/recipes/recipe/" + comment.getRecipe().getId()))
                .body(createdComment);
    }
}