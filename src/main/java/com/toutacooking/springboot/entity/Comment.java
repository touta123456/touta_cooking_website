package com.toutacooking.springboot.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Entity
public class Comment implements Serializable{

	private static final long serialVersionUID = -6151394119727189474L;

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Size(max = 2000)
    @NotEmpty
    @Column(length = 2000, nullable = false)
    private String content;
    
    @ManyToOne
    private User user;
    
    @ManyToOne
    private Recipe recipe;
    
    @Column(name = "date_publication", nullable = false, updatable = false)
    private LocalDateTime datePublication;
    
    @PrePersist
    protected void onCreate() {
        this.datePublication = LocalDateTime.now();
    }
    
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Recipe getRecipe() {
		return recipe;
	}

	public void setRecipe(Recipe recipe) {
		this.recipe = recipe;
	}
	
    public LocalDateTime getDatePublication() {
        return datePublication;
    }

    protected void setDatePublication(LocalDateTime datePublication) {
        this.datePublication = datePublication;
    }

}