package com.springboot.project.data.dto;

import java.util.List;

import com.springboot.project.data.entity.ChatRoom;
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
public class ChatUserDTO {

	private Long id;
	private String email;
	private Long roomId;
	
}
