package com.springboot.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.project.data.dto.ProductDTO;
import com.springboot.project.data.entity.User;
import com.springboot.project.service.ProductService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/product")
public class ProductController {

	private final ProductService productService;
	
	@Autowired
	public ProductController(ProductService productService) {
		this.productService = productService;
	}
	
	@PostMapping("/product/create")
	@ApiOperation(value = "상품등록" , notes = "상품 등록")
	public ResponseEntity<ProductDTO> CreateProduct(@RequestBody ProductDTO productDTO){
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    User user = (User) authentication.getPrincipal();
	    
	    productDTO.setUser_email(user.getEmail());
	    
	    ProductDTO createdProduct = productService.createProduct(productDTO);
	    
	    return new ResponseEntity<>(createdProduct , HttpStatus.CREATED);
		
	}
	
}
