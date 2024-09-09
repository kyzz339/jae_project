package com.springboot.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.project.data.entity.ChatRoom;
import java.util.List;


@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> , ChatRoomRepositoryCustom{
	
	public ChatRoom findByRoomId(int roomId); 
	
}
