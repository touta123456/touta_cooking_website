package com.toutacooking.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.toutacooking.springboot.entity.Recipe;
import com.toutacooking.springboot.entity.User;

@Repository
public interface JpaRecipeRepository extends JpaRepository<Recipe, Long> {

	List<Recipe> findByAuthor(User author);

}