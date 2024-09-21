package com.springboot.project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.project.data.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product , Long>{

	public Page<Product> findAllByOrderByCreatedAtDesc(Pageable pageable);
	
	public Page<Product> findByTitleOrderByCreatedAtDesc(String title , Pageable pageable);
	
	public Product findOneById(Long id);
}
