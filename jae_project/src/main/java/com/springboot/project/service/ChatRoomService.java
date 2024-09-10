package com.springboot.project.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.springboot.project.data.dto.ChatRoomDTO;
import com.springboot.project.data.dto.ChatUserDTO;

public interface ChatRoomService {
	
	//나의 채팅방 찾기
	public Page<ChatRoomDTO> findMyChatRoom(String email , Pageable pageable);
	//채팅방 생성
	public ChatRoomDTO createChatRoom(String roomName , String userid);
	//채팅방 삭제
	public ChatRoomDTO deleteChatRoom(int roomId);
	//채팅 상대 초대
	public ChatUserDTO inviteChatUser(int roomId ,String email);
	//해당 사용자 채팅방 존재 확인
	public boolean isUserInRoom(String email ,int roomId);
	//host 사용자 확인
	public String isHostChatRoom(int roomId);
	
	
}
