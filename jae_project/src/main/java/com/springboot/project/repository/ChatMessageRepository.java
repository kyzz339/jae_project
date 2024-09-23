package com.springboot.project.repository;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.springboot.project.data.entity.ChatMessage;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage , String>{

	public ChatMessage findByRoomId(int roomId);
	
	public Page<ChatMessage> findAllByRoomId(int roomId , Pageable pageable);
	
	public ChatMessage findById(ObjectId objectId);
	
	public ChatMessage findOneById(String id);

}
