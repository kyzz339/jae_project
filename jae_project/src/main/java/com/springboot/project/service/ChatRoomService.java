package com.springboot.project.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.springboot.project.data.dto.ChatRoomDTO;
import com.springboot.project.data.dto.ChatUserDTO;
import com.springboot.project.data.dto.ProductDTO;

public interface ChatRoomService {
	
	//나의 채팅방 찾기
	public Page<ChatRoomDTO> findMyChatRoom(String email , Pageable pageable);
	//채팅방 생성
	public ChatRoomDTO createChatRoom(String roomName , String userid);
	//상품관련 채팅방 생성
	public ChatRoomDTO createProductChatRoom(ProductDTO productDTO , String roomName);
	//채팅방 삭제
	public ChatRoomDTO deleteChatRoom(Long roomId);
	//채팅 상대 초대
	public ChatUserDTO inviteChatUser(Long roomId ,String email);
	//해당 사용자 채팅방 존재 확인
	public boolean isUserInRoom(String email ,Long roomId);
	//host 사용자 확인
	public String isHostChatRoom(Long roomId);
	
	
}
