package com.springboot.project.service;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.springboot.project.data.dto.ChatMessageDTO;
import com.springboot.project.data.entity.ChatMessage;

public interface ChatService {

	//채팅내용 저장
	public ChatMessageDTO saveChat(ChatMessage chatmessage);
	//채팅방 입장
	public ChatMessageDTO findRoomByroomId(int roomId);
	//채팅방 내용 fetch
	public Page<ChatMessageDTO> findChatMessageByroomId(int roomId , Pageable pageable);
	//파일 업로드
	public ChatMessageDTO uploadFile(MultipartFile file , ChatMessageDTO messageDTO);
	//파일 다운로드
	public Resource downloadFile(String objectId , String filename);
	//original file name
	public String originalFileName(String id);
	
}
