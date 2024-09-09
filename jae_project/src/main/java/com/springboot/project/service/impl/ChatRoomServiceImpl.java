package com.springboot.project.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.project.data.dto.ChatRoomDTO;
import com.springboot.project.data.dto.ChatUserDTO;
import com.springboot.project.data.entity.ChatRoom;
import com.springboot.project.data.entity.ChatUser;
import com.springboot.project.repository.ChatRoomRepository;
import com.springboot.project.repository.ChatUserRepository;
import com.springboot.project.service.ChatRoomService;

import lombok.extern.slf4j.Slf4j;

@Service
public class ChatRoomServiceImpl implements ChatRoomService{

	@Autowired
	ChatRoomRepository chatRoomRepository;
	
	@Autowired
	ChatUserRepository chatUserRepository;
	
	//본인 채팅방 찾기
	public List<ChatRoomDTO> findMyChatRoom(String email){
		//query DSL로 개인 이메일로 roomid 조회
		
		List<ChatRoom> chatRoom = chatRoomRepository.mychatRooms(email);
		List<ChatRoomDTO> chatRoomDTO = new ArrayList<ChatRoomDTO>();
		
		for(ChatRoom x : chatRoom) {
			ChatRoomDTO chatroomdto = ChatRoomDTO.builder()
									.roomId(x.getRoomId())
									.name(x.getName())
									.host(x.getHost())
									.build();
			chatRoomDTO.add(chatroomdto);
		}
		
		return chatRoomDTO;
	}
	
	//채팅방 생성
	public ChatRoomDTO createChatRoom(String roomName , String email) {
		
		ChatRoom chatroom = ChatRoom.builder()
				.name(roomName)
				.host(email)
				.build();
		
		chatRoomRepository.save(chatroom);
		
		ChatRoomDTO chatroomDTO = ChatRoomDTO.builder()
					.roomId(chatroom.getRoomId())
					.name(chatroom.getName())
					.build();
		
		ChatUser chatUser =  ChatUser.builder()
				.email(email) // jwt 로그인 본인 이메일로 변경 예정
				.chatRoom(chatroom)
				.build();
		chatUserRepository.save(chatUser);
		
		return chatroomDTO;
	}
	
	//채팅방 삭제
	public ChatRoomDTO deleteChatRoom(int roomId) {
		
		ChatRoom deletedChattRoom = chatRoomRepository.findByRoomId(roomId);
		ChatRoomDTO chatRoomDTO = ChatRoomDTO.builder()
								.roomId(deletedChattRoom.getRoomId())
								.name(deletedChattRoom.getName())
								.build();
		
		chatRoomRepository.deleteById(chatRoomDTO.getRoomId());
		
		return chatRoomDTO;
	}
	
	//채팅방 초대
	public ChatUserDTO inviteChatUser(int roomId , String email) {
		
		ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId);
		
		//유저중에 해당 email 가진 사람 있는지 확인
		
		ChatUser chatUser =	ChatUser.builder()
							.email(email)
							.chatRoom(chatRoom)
							.build();
		chatUserRepository.save(chatUser);
		
		ChatUserDTO chatUserDTO = ChatUserDTO.builder()
								.email(chatUser.getEmail())
								.roomId(chatUser.getChatRoom().getRoomId())
								.build();
		
		return chatUserDTO;
		
	}
	
	//채팅방 사용자 존재 확인
	public boolean isUserInRoom(String email ,int roomId) {
		
		boolean Chk = chatUserRepository.existsByEmailAndChatRoom_RoomId(email , roomId);
		
		return Chk;
		
	}
	
	//host 사용자 확인
	public String isHostChatRoom(int roomId) {
		
		ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId);
		
		String host = chatRoom.getHost();
		
		return host;
		
	}
	
}
