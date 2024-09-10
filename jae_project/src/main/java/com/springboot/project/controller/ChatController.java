package com.springboot.project.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.project.data.dto.ChatMessageDTO;
import com.springboot.project.data.dto.ChatRoomDTO;
import com.springboot.project.data.dto.ChatUserDTO;
import com.springboot.project.data.entity.ChatMessage;
import com.springboot.project.data.entity.User;
import com.springboot.project.service.ChatRoomService;
import com.springboot.project.service.ChatService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

	private final SimpMessagingTemplate template;
	private final ChatService chatService;
	private final ChatRoomService chatRoomService;

	@Autowired
	public ChatController(SimpMessagingTemplate template, ChatService chatService, ChatRoomService chatRoomService) {
		this.template = template;
		this.chatService = chatService;
		this.chatRoomService = chatRoomService;
		
	}

	// 개인 채팅방 리스트
	@GetMapping("/rooms")
	@ApiOperation(value = "사용자가 포함된 채팅방 리스트" , notes = "사용자가 참가한 채팅방 리스트")
	public ResponseEntity<Page<ChatRoomDTO>> myChatRoomList(
			@RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size
			) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    User user = (User) authentication.getPrincipal();
		//페이징 추가합시다
	    Pageable pageable = PageRequest.of(page, size);
	    
		Page<ChatRoomDTO> chatList = chatRoomService.findMyChatRoom(user.getEmail() , pageable);
		
		return ResponseEntity.ok(chatList);

	}

	// 채팅방 만들기
	//@PostMapping("/chatRoomCreate")
	@PostMapping("/rooms/create")
	@ApiOperation(value = "채팅방 만들기" ,notes = "채팅방 새로 만들기")
	public ResponseEntity<ChatRoomDTO> CreateRoom(@RequestBody ChatRoomDTO chatroomDTO) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    User user = (User) authentication.getPrincipal();
		
		ChatRoomDTO chatroom = chatRoomService.createChatRoom(chatroomDTO.getName(), user.getEmail());

		return new ResponseEntity<>(chatroom , HttpStatus.CREATED);

	}

	// 채팅방 삭제
	//@PostMapping("/deleteChatRoom/{roomId}")
	@PostMapping("/rooms/delete")
	@ApiOperation(value = "채팅방 삭제" ,notes = "해당 채팅방 호스트만 사용 가능")
	public ResponseEntity<ChatRoomDTO> deleteChatRoom(@RequestBody ChatRoomDTO chatRoomDTO) {
		System.out.println(chatRoomDTO.getRoomId());
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    User user = (User) authentication.getPrincipal();
	    
	    if(chatRoomService.isHostChatRoom(chatRoomDTO.getRoomId()).equals(user.getEmail())) {
	    	ChatRoomDTO chatRoom = chatRoomService.deleteChatRoom(chatRoomDTO.getRoomId());

			return ResponseEntity.ok(chatRoom);
	    }else {
	    	
	    	throw new AccessDeniedException("host가 다른 사람입니다.");
	    }
	}
	
	//채팅방 입장
	//@GetMapping(value = "/enter/{roomId}")
	@GetMapping("/rooms/{roomId}")
	@ApiOperation(value = "채팅방 입장 , 채팅방 채팅 내용 불러오기" , notes = "채팅방 입장")
	public ResponseEntity<List<ChatMessageDTO>> enter(@PathVariable int roomId) {
		// 사용자가 해당 채팅방에 포함되어 있는지 확인
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    User user = (User) authentication.getPrincipal();
	    boolean isUserInRoom = chatRoomService.isUserInRoom(user.getEmail() , roomId);
	    if (!isUserInRoom) {
	        throw new AccessDeniedException("해당 채팅방에 접근할 수 없습니다.");
	    }
		
		//채팅 내용 뿌려주기
	    //스크롤 위로 할 씨 추가적으로 fetch	
		List<ChatMessageDTO> chatMessage = chatService.findChatMessageByroomId(roomId);
		
		return ResponseEntity.ok(chatMessage);
		
	}
	
	//메시지 전송
	//@PostMapping(value = "/message")
	@PostMapping("/rooms/{roomId}/messages")
	@ApiOperation(value = "메시지 전송 및 채팅 내용 저장" ,notes = "채팅 내용 저장")
	public ResponseEntity<Void> message(@RequestBody ChatMessageDTO message) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getPrincipal();
		
		message.setName(user.getName());
		message.setSender(user.getEmail());
		
		template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
		
		ChatMessage chatMessage = ChatMessage.builder()
				.roomId(message.getRoomId())
				.sender(message.getSender())
				.name(message.getName())
				.content(message.getContent())
				.timestamp(LocalDateTime.now()).build();
		
		chatService.saveChat(chatMessage);
		
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
	//채팅방 초대
	//@PostMapping("/invite/{roomId}/{email}")
	@PostMapping("/rooms/{roomId}/{email}/invite")
	@ApiOperation(value = "채팅방 초대 및 안내 메시지 전송" ,notes = "채팅방 초대")
	public ResponseEntity<?> inviteChatUser(@PathVariable int roomId ,@PathVariable String email) {
		
		//해당 아이디 회원으로 존재하는지 확인
		
		
		//Id 존재 확인 , 채팅방에 같은 아이디 있는지 확인
		boolean userExists = chatRoomService.isUserInRoom(email , roomId);
		if(userExists) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
									.body("이미 중복된 아이디가 있습니다.");
		}
		
		
		ChatUserDTO chatUserDTO = chatRoomService.inviteChatUser(roomId, email);
		
		ChatMessageDTO message = ChatMessageDTO.builder()
								.roomId(roomId)
								.content(email + "님이 입장하였습니다.")
								.sender("SYSTEM")
								.timestamp(LocalDateTime.now())
								.build();
								
		template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
		
		ChatMessage chatMessage = ChatMessage.builder()
								 .roomId(message.getRoomId())
								 .sender(message.getSender())
								 .content(message.getContent())
								 .timestamp(message.getTimestamp())
								 .build();
		chatService.saveChat(chatMessage);
		
		return ResponseEntity.ok(chatUserDTO);
		
	}
	
	//파일 업로드 추가 합시다
}
