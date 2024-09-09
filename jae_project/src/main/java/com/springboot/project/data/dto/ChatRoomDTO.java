package com.springboot.project.data.dto;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.OneToMany;

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
public class ChatRoomDTO {

	private int roomId;
	private String name;
	private String host;
    private List<ChatUser> chatUsers;
	
}
