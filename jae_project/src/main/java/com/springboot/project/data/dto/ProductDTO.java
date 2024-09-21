package com.springboot.project.data.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.springboot.project.data.entity.ChatUser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ProductDTO {

	private Long id;
	private String title;
	private String content;
	private Integer price;
	private Integer stock;
	private String image_Url;
	private String user_email;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private int chatRoomId;
	
}
