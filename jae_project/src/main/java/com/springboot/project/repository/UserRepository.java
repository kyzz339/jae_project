package com.springboot.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.project.data.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User , String>{

	User findByEmail(String email);
	
}
