package com.springboot.project.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	
	@GetMapping("/list")
	@ApiOperation(value ="상품 조회 리스트" ,notes = "상품 조회 리스트")
	public ResponseEntity<Page<ProductDTO>> findProductList(
											@RequestParam(required = false) String title,
											@RequestParam(defaultValue = "0") int page,
											@RequestParam(defaultValue = "10") int size){
		
		Pageable pageable = PageRequest.of(page, size);
		
		Page<ProductDTO> productList = productService.findProductList(title, pageable);
		
		return ResponseEntity.ok(productList);
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
	
	@GetMapping(value = "/find/{id}")
	@ApiOperation(value = "상품조회" , notes = "상품 조회")
	public ResponseEntity<ProductDTO> findProductOne(@PathVariable Long id){
		
		ProductDTO productDTO = productService.findProductOne(id);
		if(productDTO == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		return ResponseEntity.ok(productDTO);
	}
	
	@PostMapping("/delete/{id}")
	@ApiOperation(value = "상품 삭제" , notes = "상품 삭제")
	public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long id){
		
		ProductDTO existingProduct = productService.findProductOne(id);
		if(existingProduct == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    User user = (User) authentication.getPrincipal();
	    if(!user.getEmail().equals(existingProduct.getUser_email())) {
	    	return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	    }
		
		ProductDTO productDTO = productService.deleteProduct(existingProduct);
		if(productDTO == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(productDTO);
		
	}
	
	@PostMapping("/update")
	@ApiOperation(value = "상품 수정" , notes = "상품 수정")
	public ResponseEntity<ProductDTO> updateProduct(@RequestPart("product") ProductDTO productDTO ,
			 										@RequestPart(value = "image" , required = false) MultipartFile file){
		
		ProductDTO existingProductDTO = productService.findProductOne(productDTO.getId());
		
		if(existingProductDTO == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		existingProductDTO.setTitle(productDTO.getTitle());
		existingProductDTO.setContent(productDTO.getContent());
		existingProductDTO.setPrice(productDTO.getPrice());
		existingProductDTO.setStock(productDTO.getStock());
		existingProductDTO.setUpdatedAt(LocalDateTime.now());
		
		ProductDTO updatedProductDTO = productService.updateProduct(existingProductDTO, file);
		
		return ResponseEntity.ok(updatedProductDTO);
		
	}
}
