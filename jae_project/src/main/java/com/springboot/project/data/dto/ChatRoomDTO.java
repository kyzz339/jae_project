package com.springboot.project.data.dto;

import java.util.List;

import com.springboot.project.data.entity.ChatUser;
import com.springboot.project.data.entity.Product;

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
public class ChatRoomDTO {

	private Long roomId;
	private String name;
	private String host;
	private Long product_id;
    private List<ChatUser> chatUsers;
	
}
