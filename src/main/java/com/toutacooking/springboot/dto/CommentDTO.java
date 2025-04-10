package com.toutacooking.springboot.dto;

import java.time.LocalDateTime;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;

public class CommentDTO {

	@Id
    private Long id;
    
	@NotEmpty
    private String content;
    
    private UserDTO user;
    
    private RecipeDTO recipe;
	
    private LocalDateTime datePublication;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public UserDTO getUser() {
		return user;
	}

	public void setUser(UserDTO user) {
		this.user = user;
	}

	public RecipeDTO getRecipe() {
		return recipe;
	}

	public void setRecipe(RecipeDTO recipe) {
		this.recipe = recipe;
	}
	
    
	public LocalDateTime getDatePublication() {
		return datePublication;
	}

	public void setDatePublication(LocalDateTime datePublication) {
		this.datePublication = datePublication;
	}
}