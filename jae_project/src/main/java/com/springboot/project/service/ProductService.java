package com.springboot.project.service;

import org.springframework.data.domain.Page;

import com.springboot.project.data.dto.ProductDTO;

public interface ProductService {

	//public Page<ProductDTO> findProductList(String name);
	
	public ProductDTO createProduct(ProductDTO productDTO);
	
}
