package com.springboot.project.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.springboot.project.data.entity.ChatMessage;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage , String>{

	public ChatMessage findByRoomId(int roomId);
	
	public List<ChatMessage> findAllByRoomId(int roomId);

}
