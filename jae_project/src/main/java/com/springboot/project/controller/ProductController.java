package com.springboot.project.controller;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.springboot.project.data.dto.ChatRoomDTO;
import com.springboot.project.data.dto.ProductDTO;
import com.springboot.project.data.entity.User;
import com.springboot.project.service.ChatRoomService;
import com.springboot.project.service.ProductService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "상품 관리 API", description = "상품 관련 CRUD 기능 제공")
@RestController
@RequestMapping("/api/product")
public class ProductController {

	private final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);
	private final ProductService productService;
	private final ChatRoomService chatRoomService;

	@Autowired
	public ProductController(ProductService productService , ChatRoomService chatRoomService) {
		this.productService = productService;
		this.chatRoomService = chatRoomService;
	}

	@GetMapping("/list")
	@ApiOperation(value = "상품 조회 리스트", notes = "상품 조회 리스트")
	public ResponseEntity<Page<ProductDTO>> findProductList(@RequestParam(required = false) String title,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "12") int size) {
		
		LOGGER.info("상품 조회 시작" , page , size);
		Pageable pageable = PageRequest.of(page, size);

		Page<ProductDTO> productList = productService.findProductList(title, pageable);
		LOGGER.info("상품 조회 완료 상품 갯수 : " ,productList.getTotalElements());
		
		return ResponseEntity.ok(productList);
	}

	@PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "상품등록", notes = "상품 등록")
	// 파일 업로드와 JSON 데이터 같은 복합적인 요청을 처리할 때 @RequestPart 사용
	public ResponseEntity<ProductDTO> CreateProduct(@RequestPart("product") ProductDTO productDTO,
			@RequestPart(value = "image", required = false) MultipartFile file) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getPrincipal();
		LOGGER.info("상품 등록 요청 이메일 :" , user.getEmail());

		productDTO.setUser_email(user.getEmail());
		
		ProductDTO createdProduct = productService.createProduct(productDTO, file);
		LOGGER.info("상품 등록 완료 상품 ID :" , createdProduct.getId());
		ChatRoomDTO chatRoomDTO = chatRoomService.createProductChatRoom(createdProduct , createdProduct.getTitle() + "채팅방");
		
		LOGGER.info("상품 관련 채팅방 생성 완료 채팅방 ID" , chatRoomDTO.getRoomId());
		return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);

	}

	@GetMapping(value = "/find/{id}")
	@ApiOperation(value = "상품조회", notes = "상품 조회")
	public ResponseEntity<ProductDTO> findProductOne(@PathVariable Long id) {

		LOGGER.info("상품 조회 요청 조회 ID :" , id);
		
		ProductDTO productDTO = productService.findProductOne(id);
		if (productDTO == null) {
			LOGGER.info("상품 조회 실패 상품 조회 ID :" ,id);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		LOGGER.info("상품 조회 성공 상품 조회 ID : ",id);
		return ResponseEntity.ok(productDTO);
	}

	@PostMapping("/delete/{id}")
	@ApiOperation(value = "상품 삭제", notes = "상품 삭제")
	public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long id) {

		LOGGER.info("상품 삭제 실행 상품ID :",id);
		ProductDTO existingProduct = productService.findProductOne(id);
		if (existingProduct == null) {
			LOGGER.info("상품 삭제 실패 , 상품 조회 실패 : 상품 id :" , id);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getPrincipal();
		if (!user.getEmail().equals(existingProduct.getUser_email())) {
			LOGGER.info("상품 삭제 실패 : 게시물 소유자 불일치 ",existingProduct.getUser_email());
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		ProductDTO productDTO = productService.deleteProduct(existingProduct);
		if (productDTO == null) {
			LOGGER.info("상품 삭제 실패 상품 ID: ", id);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		LOGGER.info("상품 삭제 성공 상품ID :",id);
		return ResponseEntity.ok(productDTO);

	}

	@PostMapping("/update")
	@ApiOperation(value = "상품 수정", notes = "상품 수정")
	public ResponseEntity<ProductDTO> updateProduct(@RequestPart("product") ProductDTO productDTO,
			@RequestPart(value = "image", required = false) MultipartFile file) {

		LOGGER.info("상품 수정 요청 상품 ID :",productDTO.getId());
		ProductDTO existingProductDTO = productService.findProductOne(productDTO.getId());

		if (existingProductDTO == null) {
			LOGGER.info("상품 조회 실패 상품ID :",productDTO.getId());
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		existingProductDTO.setTitle(productDTO.getTitle());
		existingProductDTO.setContent(productDTO.getContent());
		existingProductDTO.setPrice(productDTO.getPrice());
		existingProductDTO.setStock(productDTO.getStock());
		existingProductDTO.setUpdatedAt(LocalDateTime.now());

		ProductDTO updatedProductDTO = productService.updateProduct(existingProductDTO, file);
		LOGGER.info("상품 수정 성공 상품ID :",productDTO.getId());
		
		
		return ResponseEntity.ok(updatedProductDTO);

	}
	
	//상품 관련된 채팅방 입장 및 참여자 설정
	//상품 입잘
	
}
