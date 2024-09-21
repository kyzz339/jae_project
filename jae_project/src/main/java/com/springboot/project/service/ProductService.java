package com.springboot.project.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.springboot.project.data.dto.ProductDTO;

public interface ProductService {

	public Page<ProductDTO> findProductList(String title , Pageable pageable);
	
	public ProductDTO createProduct(ProductDTO productDTO , MultipartFile file);
	
	public ProductDTO findProductOne(Long id);
	
	public ProductDTO updateProduct(ProductDTO productDTO , MultipartFile file);
	
	public ProductDTO deleteProduct(ProductDTO productDTO);
}
