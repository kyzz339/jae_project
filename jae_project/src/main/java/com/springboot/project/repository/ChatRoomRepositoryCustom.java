package com.springboot.project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.springboot.project.data.entity.ChatRoom;

public interface ChatRoomRepositoryCustom {

	
	public Page<ChatRoom> mychatRooms(String email,String type , Pageable pageable);
	
}
