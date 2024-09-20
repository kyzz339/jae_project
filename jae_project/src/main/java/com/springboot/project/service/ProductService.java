package com.springboot.project.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.springboot.project.data.dto.ProductDTO;

public interface ProductService {

	//public Page<ProductDTO> findProductList(String name);
	
	public ProductDTO createProduct(ProductDTO productDTO , MultipartFile file);
	
}
