package com.toutacooking.springboot.repository;

import org.springframework.stereotype.Repository;

import com.toutacooking.springboot.entity.User;

@Repository
public interface UserRepository {
	
	void saveUser(User user);
	
}
