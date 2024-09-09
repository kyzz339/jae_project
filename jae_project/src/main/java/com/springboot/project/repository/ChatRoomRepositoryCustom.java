package com.springboot.project.repository;

import java.util.List;
import com.springboot.project.data.entity.ChatRoom;

public interface ChatRoomRepositoryCustom {

	
	public List<ChatRoom> mychatRooms(String email);
	
}
