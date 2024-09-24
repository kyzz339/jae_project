package com.springboot.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.project.data.entity.ChatRoom;
import java.util.List;


@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> , ChatRoomRepositoryCustom{
	
	public ChatRoom findByRoomId(Long roomId); 
	
	public ChatRoom findByProductId(Long productId);
	
}
