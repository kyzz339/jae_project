package com.springboot.project.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.springboot.project.data.dto.ProductDTO;
import com.springboot.project.data.entity.Product;
import com.springboot.project.repository.ProductRepository;
import com.springboot.project.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService{

	private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
	
	@Autowired
	ProductRepository productRepository;
	
	@Value("${uploadDir}")
	String uploadDir;
	
	public ProductDTO createProduct(ProductDTO productDTO , MultipartFile file) {
			
		String file_url = null;
		LocalDateTime time = LocalDateTime.now();
		if(file != null && !file.isEmpty()) {
			
			if(file.getSize() > MAX_FILE_SIZE) {
				throw new IllegalArgumentException("파일 용량이 5MB를 넘을 수 없습니다.	");
			}
			
			String mimeType = file.getContentType();
			if(!mimeType.startsWith("image/")) {
				throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.");
			}
			
			String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
			Path filePath = Paths.get(uploadDir + "/" + "productImage/" + productDTO.getUser_email() +"/"+time , fileName);
		
			try {
				Files.createDirectories(filePath.getParent());
				Files.copy(file.getInputStream(), filePath , StandardCopyOption.REPLACE_EXISTING);
				file_url = "/uploads/productImage/" + productDTO.getUser_email() + "/" + time + "/" + fileName;
			}catch (IOException e) {
				e.printStackTrace();
			}
				
		}
		
		Product product = Product.builder()
				.title(productDTO.getTitle())
				.content(productDTO.getContent())
				.price(productDTO.getPrice())
				.stock(productDTO.getStock())
				.image_Url(file_url)
				.user_email(productDTO.getUser_email())
				.createdAt(time)
				.build();
		
		productRepository.save(product);
		
		ProductDTO savedProductDTO =  ProductDTO.builder()
									.title(product.getTitle())
									.content(product.getContent())
									.price(product.getPrice())
									.stock(product.getStock())
									.image_Url(file_url)
									.user_email(product.getUser_email())
									.createdAt(product.getCreatedAt())
									.build();
		return savedProductDTO;
	}
	
}
