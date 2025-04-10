package com.toutacooking.springboot.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.toutacooking.springboot.entity.User;

@Repository
public class UserRepositoryImpl implements UserRepository {
	
	private final JpaUserRepository userRepository;
    
    public UserRepositoryImpl(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void saveUser(User user) {
    	this.userRepository.save(user);
    }

}
