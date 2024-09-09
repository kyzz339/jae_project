package com.springboot.project.service;

import java.util.List;

import com.springboot.project.data.dto.ChatMessageDTO;
import com.springboot.project.data.entity.ChatMessage;

public interface ChatService {

	//채팅내용 저장
	public ChatMessageDTO saveChat(ChatMessage chatmessage);
	//채팅방 입장
	public ChatMessageDTO findRoomByroomId(int roomId);
	//채팅방 내용 fetch
	public List<ChatMessageDTO> findChatMessageByroomId(int roomId);
	//파일 업로드
	
}
