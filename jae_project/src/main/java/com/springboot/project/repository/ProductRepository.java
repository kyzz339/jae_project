package com.springboot.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.project.data.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product , Integer>{

}
