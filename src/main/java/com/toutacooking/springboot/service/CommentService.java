package com.toutacooking.springboot.service;

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

@Service
public class CommentService {

    private final Logger log = LoggerFactory.getLogger(CommentService.class);
	private final JpaCommentRepository commentRepository;

    public CommentService(JpaCommentRepository commentRepository) {
		this.commentRepository = commentRepository;
	}


    @Transactional
    public CommentDTO save(CommentDTO commentDTO) {
    	Comment comment = new Comment();
    	comment.setId(commentDTO.getId());
    	comment.setContent(commentDTO.getContent());
    	
    	// Author
    	UserDTO authorDTO = commentDTO.getUser();
    	if(authorDTO!=null) {
	    	User author = new User();
	    	author.setId(commentDTO.getUser().getId());
	    	comment.setUser(author);
    	}
    	else {
    		log.warn("Comment having id {} has no author set", commentDTO.getId());
    	}

    	// Recipe
    	RecipeDTO recipeDTO = commentDTO.getRecipe();
    	if(recipeDTO!=null) {
	    	Recipe recipe = new Recipe();
	    	recipe.setId(commentDTO.getRecipe().getId());
	    	comment.setRecipe(recipe);
    	}
    	else {
    		log.warn("Comment having id {} is not linked to any recipe", commentDTO.getId());
    	}
    	

        this.commentRepository.save(comment);
        return commentDTO;
    }

    
}