package com.springboot.project.data.dto;

import java.time.LocalDateTime;

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
public class ChatMessageDTO {

	private String id;
	private int roomId;
	private String sender;
	private String name;
	private String content;
	private LocalDateTime timestamp;
	
}
