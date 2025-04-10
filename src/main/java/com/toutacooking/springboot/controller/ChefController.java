package com.toutacooking.springboot.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toutacooking.springboot.dto.RecipeDTO;
import com.toutacooking.springboot.dto.UserDTO;
import com.toutacooking.springboot.entity.User;
import com.toutacooking.springboot.security.ConnectedUserFinder;
import com.toutacooking.springboot.service.RecipeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/chef")
@PreAuthorize("hasRole('CHEF')")
public class ChefController {

	private final Logger log = LoggerFactory.getLogger(ChefController.class);
    private final RecipeService recipeService;
    private final ConnectedUserFinder connectedUserFinder;
    
    public ChefController(RecipeService recipeService, ConnectedUserFinder connectedUserFinder) {
		this.recipeService = recipeService;
		this.connectedUserFinder = connectedUserFinder;
	}

	@PostMapping("/recipe")
    public ResponseEntity<Object> createRecipe(@Valid @RequestBody RecipeDTO recipe, 
    		BindingResult result, HttpServletRequest request) throws URISyntaxException {
		log.info("Request to create recipe: {}", recipe);
	    if (result.hasErrors()) {
	        return ResponseEntity.badRequest().body(
	            result.getFieldErrors().stream()
	                .collect(Collectors.toMap(
	                    FieldError::getField,
	                    FieldError::getDefaultMessage
	                ))
	        );
	    }
	    
	    User author = this.connectedUserFinder.findRequestingUser(request);
	    UserDTO authorDTO = new UserDTO();
	    authorDTO.setId(author.getId());
	    recipe.setAuthor(authorDTO);
	    
		RecipeDTO createdRecipe = recipeService.save(recipe);
        return ResponseEntity.created(new URI("/api/chef/recipe/" + createdRecipe.getId()))
                .body(createdRecipe);
    }

    @PutMapping("/recipe/{id}")
    public ResponseEntity<Object> updateRecipe(@PathVariable Long id, @Valid @RequestBody RecipeDTO recipe, BindingResult result) {
    	log.info("Request to update recipe: {}", recipe);
	    if (result.hasErrors()) {
	        return ResponseEntity.badRequest().body(
	            result.getFieldErrors().stream()
	                .collect(Collectors.toMap(
	                    FieldError::getField,
	                    FieldError::getDefaultMessage
	                ))
	        );
	    }
	    RecipeDTO updatedRecipe = recipeService.update(id, recipe);
        return ResponseEntity.ok(updatedRecipe);
    }

    @DeleteMapping("/recipe/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        recipeService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/myrecipes")
    public List<RecipeDTO> getMyRecipes(HttpServletRequest request) {
    	User author = this.connectedUserFinder.findRequestingUser(request);
    	return recipeService.findByAuthor(author);
    }

    @GetMapping("/recipe/{id}")
    ResponseEntity<Object> getMyRecipe(@PathVariable Long id, HttpServletRequest request) {
    	
    	User requestingAuthor = this.connectedUserFinder.findRequestingUser(request);
    	Optional<RecipeDTO> recipe = recipeService.findById(id);
    	
    	if(recipe.isPresent() && recipe.get().getAuthor().getId().equals(requestingAuthor.getId())) {
            return ResponseEntity.ok().body(recipe.get());
    	}
    	else {
    		return ResponseEntity.badRequest().body("Not authorized to display this recipe");
    	}

    }
    

    
}