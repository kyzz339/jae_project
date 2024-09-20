package com.springboot.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
	
	@PostMapping(value =  "/create" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "상품등록" , notes = "상품 등록")
	//파일 업로드와 JSON 데이터 같은 복합적인 요청을 처리할 때 @RequestPart 사용
	public ResponseEntity<ProductDTO> CreateProduct(@RequestPart("product") ProductDTO productDTO ,
													 @RequestPart(value = "image" , required = false) MultipartFile file){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    User user = (User) authentication.getPrincipal();
	    
	    productDTO.setUser_email(user.getEmail());
	    
	    ProductDTO createdProduct = productService.createProduct(productDTO , file);
	    
	    return new ResponseEntity<>(createdProduct , HttpStatus.CREATED);
		
	}
	
}
