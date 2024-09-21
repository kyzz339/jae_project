package com.springboot.project.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
	
	public Page<ProductDTO> findProductList(String title , Pageable pageable){
		
		Page<Product> productLists;
		
		if(title == null || title.isEmpty()) {
			productLists = productRepository.findAllByOrderByCreatedAtDesc(pageable);
		}else {
			productLists = productRepository.findByTitleOrderByCreatedAtDesc(title , pageable);
		}
		
		
		List<ProductDTO> productDTO = productLists.stream()
						.map(product -> ProductDTO.builder()
								.id(product.getId())
								.title(product.getTitle())
								.content(product.getContent())
								.price(product.getPrice())
								.stock(product.getStock())
								.image_Url(product.getImage_Url())
								.user_email(product.getUser_email())
								.createdAt(product.getCreatedAt())
								.build())
				.collect(Collectors.toList());
		
		return new PageImpl<>(productDTO , pageable , productLists.getTotalElements());
		
	}
	
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
	
	public ProductDTO findProductOne(Long id) {
		
		Product product = productRepository.findOneById(id);
		
		//상품 조회 안될 때 예외처리
		if(product == null) {
			throw new NoSuchElementException("해당 상품은 존재하지 않습니다.");
		}
		
		ProductDTO productDTO = ProductDTO.builder()
								.id(product.getId())
								.title(product.getTitle())
								.content(product.getContent())
								.price(product.getPrice())
								.stock(product.getStock())
								.image_Url(product.getImage_Url())
								.user_email(product.getUser_email())
								.createdAt(product.getCreatedAt())
								.updatedAt(product.getUpdatedAt())
								.build();
		
		return productDTO;
	}
	
	public ProductDTO updateProduct(ProductDTO productDTO , MultipartFile file) {
		
			LocalDateTime time = LocalDateTime.now();
			String file_url = null;
			
			Product updateProduct = Product.builder()
									.id(productDTO.getId())
									.title(productDTO.getTitle())
									.content(productDTO.getContent())
									.price(productDTO.getPrice())
									.stock(productDTO.getStock())
									.image_Url(productDTO.getImage_Url())
									.user_email(productDTO.getUser_email())
									.createdAt(productDTO.getCreatedAt())
									.updatedAt(productDTO.getUpdatedAt())
									.build();
									
			
			//파일 있을시 기존 파일 삭제 및 update 해야함!
			if(file != null && !file.isEmpty()) {
				if(file.getSize() > MAX_FILE_SIZE) {
					throw new IllegalArgumentException("파일 용량이 5MB를 넘을 수 없습니다.	");
				}
				
				String mimeType = file.getContentType();
				if(!mimeType.startsWith("image/")) {
					throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.");
				}
				
				if(productDTO.getImage_Url() != null) {
					
					String absoulutePath = productDTO.getImage_Url().substring("/uploads".length());
					//기존 파일 삭제
					Path oldPath = Paths.get(absoulutePath);
					
					try {
						Files.deleteIfExists(oldPath);
					}catch (Exception e) {
						throw new RuntimeException("파일 삭제를 실패하였습니다.");
					}
				}
				
				String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
				Path newPath = Paths.get(uploadDir + "/" + "productImage/" + productDTO.getUser_email() +"/"+time , fileName);
			
				try {
					Files.createDirectories(newPath.getParent());
					Files.copy(file.getInputStream(), newPath , StandardCopyOption.REPLACE_EXISTING);
					file_url = "/uploads/productImage/" + productDTO.getUser_email() + "/" + time + "/" + fileName;
					updateProduct.setImage_Url(file_url);
				}catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
		Product updatedProduct = productRepository.save(updateProduct);
		
		ProductDTO updatedProductDTO = ProductDTO.builder()
									.title(updatedProduct.getTitle())
									.content(updatedProduct.getContent())
									.price(updatedProduct.getPrice())
									.stock(updatedProduct.getStock())
									.image_Url(updatedProduct.getImage_Url())
									.user_email(updatedProduct.getUser_email())
									.createdAt(updatedProduct.getCreatedAt())
									.updatedAt(updatedProduct.getUpdatedAt())
									.build();
		return updatedProductDTO;
		
	}
	
	public ProductDTO deleteProduct(ProductDTO productDTO) {
		
		
		productRepository.deleteById(productDTO.getId());
		
		ProductDTO deletedProductDTO = ProductDTO.builder()
										.title(productDTO.getTitle())
										.content(productDTO.getContent())
										.price(productDTO.getPrice())
										.stock(productDTO.getStock())
										.image_Url(productDTO.getImage_Url())
										.user_email(productDTO.getUser_email())
										.createdAt(productDTO.getCreatedAt())
										.updatedAt(productDTO.getUpdatedAt())
										.build();
		
		return deletedProductDTO;
	}
	
}
