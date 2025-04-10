package com.toutacooking.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.toutacooking.springboot.entity.Comment;
import com.toutacooking.springboot.entity.Recipe;

@Repository
public interface JpaCommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findByRecipe(Recipe recipe);
	
}