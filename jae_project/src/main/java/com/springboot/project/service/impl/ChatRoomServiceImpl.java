package com.springboot.project.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.springboot.project.data.dto.ChatRoomDTO;
import com.springboot.project.data.dto.ChatUserDTO;
import com.springboot.project.data.dto.ProductDTO;
import com.springboot.project.data.entity.ChatRoom;
import com.springboot.project.data.entity.ChatUser;
import com.springboot.project.data.entity.Product;
import com.springboot.project.repository.ChatRoomRepository;
import com.springboot.project.repository.ChatUserRepository;
import com.springboot.project.service.ChatRoomService;

@Service
public class ChatRoomServiceImpl implements ChatRoomService{

	@Autowired
	ChatRoomRepository chatRoomRepository;
	
	@Autowired
	ChatUserRepository chatUserRepository;
	
	//본인 채팅방 찾기
	public Page<ChatRoomDTO> findMyChatRoom(String email , Pageable pageable){
		//query DSL로 개인 이메일로 roomid 조회
		
		Page<ChatRoom> chatRooms = chatRoomRepository.mychatRooms(email , pageable);
		
		List<ChatRoomDTO> chatRoomDTO = chatRooms.stream()
		        .map(chatRoom -> ChatRoomDTO.builder()
		                .roomId(chatRoom.getRoomId())
		                .name(chatRoom.getName())
		                .host(chatRoom.getHost())
		                .build())
		            .collect(Collectors.toList());
		
		return new PageImpl<>(chatRoomDTO , pageable, chatRooms.getTotalElements());
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
	
	//채팅방 생성
		public ChatRoomDTO createProductChatRoom(ProductDTO productDTO , String roomName) {
			
			Product product = Product.builder()
							.id(productDTO.getId())
							.user_email(productDTO.getUser_email())
							.build();
							
			ChatRoom chatroom = ChatRoom.builder()
					.name(roomName)
					.host(productDTO.getUser_email())
					.product(product)
					.build();
			
			chatRoomRepository.save(chatroom);
			
			ChatRoomDTO chatroomDTO = ChatRoomDTO.builder()
						.roomId(chatroom.getRoomId())
						.name(chatroom.getName())
						.product(product)
						.build();
			
			ChatUser chatUser =  ChatUser.builder()
					.email(productDTO.getUser_email()) // jwt 로그인 본인 이메일로 변경 예정
					.chatRoom(chatroom)
					.build();
			chatUserRepository.save(chatUser);
			
			return chatroomDTO;
		}
	
	//채팅방 삭제
	public ChatRoomDTO deleteChatRoom(Long roomId) {
		
		ChatRoom deletedChattRoom = chatRoomRepository.findByRoomId(roomId);
		ChatRoomDTO chatRoomDTO = ChatRoomDTO.builder()
								.roomId(deletedChattRoom.getRoomId())
								.name(deletedChattRoom.getName())
								.build();
		
		chatRoomRepository.deleteById(chatRoomDTO.getRoomId());
		
		return chatRoomDTO;
	}
	
	//채팅방 초대
	public ChatUserDTO inviteChatUser(Long roomId , String email) {
		
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
	public boolean isUserInRoom(String email ,Long roomId) {
		
		boolean Chk = chatUserRepository.existsByEmailAndChatRoom_RoomId(email , roomId);
		
		return Chk;
		
	}
	
	//host 사용자 확인
	public String isHostChatRoom(Long roomId) {
		
		ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId);
		
		String host = chatRoom.getHost();
		
		return host;
		
	}
	
}
