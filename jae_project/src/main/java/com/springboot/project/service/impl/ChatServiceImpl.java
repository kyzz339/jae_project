package com.springboot.project.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.project.data.dto.ChatMessageDTO;
import com.springboot.project.data.entity.ChatMessage;
import com.springboot.project.repository.ChatMessageRepository;
import com.springboot.project.service.ChatService;

@Service
public class ChatServiceImpl implements ChatService{

	@Autowired
	ChatMessageRepository chatRepository;
	
	public ChatMessageDTO findRoomByroomId(int roomId) {
		
		ChatMessage chatMessage = chatRepository.findByRoomId(roomId);
		
		ChatMessageDTO chatmessageDTO = ChatMessageDTO.builder()
										.roomId(chatMessage.getRoomId())
										.sender(chatMessage.getSender())
										.name(chatMessage.getName())
										.content(chatMessage.getContent())
										.timestamp(chatMessage.getTimestamp())
										.build();
		
		
		return chatmessageDTO;
	}
	
	public ChatMessageDTO saveChat(ChatMessage chatmessage) {
		
		chatRepository.save(chatmessage);
		
		ChatMessageDTO chatMessageDTO = ChatMessageDTO.builder()
										.roomId(chatmessage.getRoomId())
										.sender(chatmessage.getSender())
										.name(chatmessage.getName())
										.content(chatmessage.getContent())
										.timestamp(chatmessage.getTimestamp())
										.build();
		
		return chatMessageDTO;
	}
	
	public List<ChatMessageDTO> findChatMessageByroomId(int roomId){
		
		List<ChatMessage> chatMessage = chatRepository.findAllByRoomId(roomId); 
		List<ChatMessageDTO> chatMessageDTO = new ArrayList<>();
		
		for(ChatMessage x : chatMessage) {
			ChatMessageDTO chatmessageDTO = ChatMessageDTO.builder()
											.roomId(x.getRoomId())
											.sender(x.getSender())
											.name(x.getName())
											.content(x.getContent())
											.timestamp(x.getTimestamp())
											.build();
			chatMessageDTO.add(chatmessageDTO);
		}
		
		return chatMessageDTO;
		
	}
	
}
