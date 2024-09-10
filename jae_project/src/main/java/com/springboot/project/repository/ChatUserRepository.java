package com.springboot.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.project.data.entity.ChatUser;

@Repository
public interface ChatUserRepository extends JpaRepository<ChatUser, Long>{
	
	public List<ChatUser> findByEmail(String email);
	
	public boolean existsByEmailAndChatRoom_RoomId(String email, int roomId); 
	
	public ChatUser existsByChatRoom_RoomIdAndEmail(int roomId , String email);
	
}
