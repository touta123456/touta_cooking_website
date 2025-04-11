package com.toutacooking.springboot.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toutacooking.springboot.dto.CommentDTO;
import com.toutacooking.springboot.dto.RecipeDTO;
import com.toutacooking.springboot.dto.UserDTO;
import com.toutacooking.springboot.entity.Comment;
import com.toutacooking.springboot.entity.Recipe;
import com.toutacooking.springboot.entity.User;
import com.toutacooking.springboot.repository.JpaCommentRepository;
import com.toutacooking.springboot.repository.JpaRecipeRepository;

import jakarta.validation.Valid;

@Service
public class RecipeService {

    private final Logger log = LoggerFactory.getLogger(RecipeService.class);
    private static final String MISSING_AUTHOR_WARNING = "Recipe having id {} has no author set";
    
	private final JpaRecipeRepository recipeRepository;
	private final JpaCommentRepository commentRepository;

	
    public RecipeService(JpaRecipeRepository recipeRepository, JpaCommentRepository commentRepository) {
		this.recipeRepository = recipeRepository;
		this.commentRepository = commentRepository;
	}

    @Transactional
    public RecipeDTO save(RecipeDTO recipeDTO) {
    	Recipe recipe = new Recipe();
    	recipe.setId(recipeDTO.getId());
    	recipe.setTitle(recipeDTO.getTitle());
    	recipe.setIngredients(recipeDTO.getIngredients());
    	recipe.setKeywords(recipeDTO.getKeywords());
    	
    	// Author
    	UserDTO authorDTO = recipeDTO.getAuthor();
    	if(authorDTO!=null) {
	    	User author = new User();
	    	author.setId(recipeDTO.getAuthor().getId());
	    	recipe.setAuthor(author);
    	}
    	else {
    		log.warn(MISSING_AUTHOR_WARNING, recipeDTO.getId());
    	}
    	
    	// Lets save the recipe
        recipeRepository.save(recipe);
        return recipeDTO;
    }

    @Transactional
	public RecipeDTO update(Long id, @Valid RecipeDTO recipeDTO) {
    	Recipe recipe = new Recipe();
    	recipe.setId(id);
    	recipe.setTitle(recipeDTO.getTitle());
    	recipe.setIngredients(recipeDTO.getIngredients());
    	recipe.setKeywords(recipeDTO.getKeywords());
    	
    	// Author
    	UserDTO authorDTO = recipeDTO.getAuthor();
    	if(authorDTO!=null) {
	    	User author = new User();
	    	author.setId(recipeDTO.getAuthor().getId());
	    	recipe.setAuthor(author);
    	}
    	else {
    		log.warn(MISSING_AUTHOR_WARNING, recipeDTO.getId());
    	}

    	// Lets save the recipe
		recipeRepository.save(recipe);
		return recipeDTO;
	}

    @Transactional
	public void delete(Long id) {
		this.recipeRepository.deleteById(id);
	}

    @Transactional(readOnly = true)
	public List<RecipeDTO> findAll() {
		List<Recipe> recipes = this.recipeRepository.findAll();
		return recipes.stream().map(this::mapRecipeToDTO).toList();
	}
	
    private RecipeDTO mapRecipeToDTO(Recipe recipe) {
    	RecipeDTO recipeDTO = new RecipeDTO();
    	recipeDTO.setId(recipe.getId());
    	recipeDTO.setTitle(recipe.getTitle());
        recipeDTO.setIngredients(recipe.getIngredients());
        recipeDTO.setKeywords(recipe.getKeywords());
        
        // Author
        User author = recipe.getAuthor();
        if(author!=null) {
	    	UserDTO authorDTO = new UserDTO();
	    	authorDTO.setId(author.getId());
	    	authorDTO.setEmail(author.getEmail());
	    	authorDTO.setFirstName(author.getFirstName());
	    	authorDTO.setLastName(author.getLastName());
	    	authorDTO.setUserName(author.getUsername());
	    	recipeDTO.setAuthor(authorDTO);
    	}
    	else {
    		log.warn(MISSING_AUTHOR_WARNING, recipeDTO.getId());
    	}
        
        // Comments
        List<Comment> comments = recipe.getComments();
        List<CommentDTO> commentsDTO = comments.stream().map(c -> {
        	CommentDTO commentDTO = new CommentDTO();
        	commentDTO.setContent(c.getContent());
        	commentDTO.setId(c.getId());
        	commentDTO.setDatePublication(c.getDatePublication());
        	
        	// Author of the comment
        	User commentAuthor =c.getUser();
        	if(commentAuthor!=null) {
        		UserDTO commentAuthorDTO = new UserDTO();
        		commentAuthorDTO.setId(commentAuthor.getId());
        		commentAuthorDTO.setFirstName(commentAuthor.getFirstName());
        		commentAuthorDTO.setLastName(commentAuthor.getLastName());
        		commentDTO.setUser(commentAuthorDTO);
        	}
        	
        	return commentDTO;
        }).toList();
        recipeDTO.setComments(commentsDTO);
        
        return recipeDTO;
    }

    @Transactional(readOnly = true)
	public Optional<RecipeDTO> findById(Long id) {
		Optional<Recipe> recipe = this.recipeRepository.findById(id);
		return recipe.map(this::mapRecipeToDTO);
	}

    @Transactional(readOnly = true)
	public Optional<RecipeDTO> findByIdWithComments(Long id) {
	    return this.recipeRepository.findById(id).map(recipe -> {
	        List<Comment> comments = this.commentRepository.findByRecipe(recipe);
	        recipe.setComments(comments);
	        return mapRecipeToDTO(recipe);
	    });	
	}

    @Transactional(readOnly = true)
	public List<RecipeDTO> findByAuthor(User author) {
		List<Recipe> recipes = this.recipeRepository.findByAuthor(author);
		return recipes.stream().map(this::mapRecipeToDTO).toList();
	}

    
}