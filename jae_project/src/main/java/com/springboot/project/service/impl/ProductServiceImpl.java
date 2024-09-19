package com.springboot.project.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.project.data.dto.ProductDTO;
import com.springboot.project.data.entity.Product;
import com.springboot.project.repository.ProductRepository;
import com.springboot.project.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService{

	@Autowired
	ProductRepository productRepository;
	
	//이미지 없음
	public ProductDTO createProduct(ProductDTO productDTO) {
		
		Product product = Product.builder()
						.name(productDTO.getName())
						.price(productDTO.getPrice())
						.stock(productDTO.getStock())
						.user_email(productDTO.getUser_email())
						.createdAt(LocalDateTime.now())
						.build();
		
		productRepository.save(product);
		
		ProductDTO savedProductDTO =  ProductDTO.builder()
									.name(product.getName())
									.price(product.getPrice())
									.stock(product.getStock())
									.user_email(product.getUser_email())
									.createdAt(product.getCreatedAt())
									.build();
		return savedProductDTO;
	}
	
}
