package com.springboot.project.controller;

import java.time.LocalDateTime;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.multipart.MultipartFile;

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

	private final Logger LOGGER = LoggerFactory.getLogger(ChatController.class);
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
			@RequestParam String type,
			@RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size
			) {
		
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    User user = (User) authentication.getPrincipal();
	    LOGGER.info("나의 채팅방 리스트 요청 이메일 : ",user.getEmail());
	    
		//페이징 추가합시다
	    Pageable pageable = PageRequest.of(page, size);
		Page<ChatRoomDTO> chatList = chatRoomService.findMyChatRoom(user.getEmail(), type , pageable);
		LOGGER.info("채팅방 리스트요청 성공 채팅방 갯수 :",chatList.getTotalElements());
		
		return ResponseEntity.ok(chatList);

	}

	// 채팅방 만들기
	@PostMapping("/rooms/create")
	@ApiOperation(value = "채팅방 만들기" ,notes = "채팅방 새로 만들기")
	public ResponseEntity<ChatRoomDTO> CreateRoom(@RequestBody ChatRoomDTO chatroomDTO) {
		
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    User user = (User) authentication.getPrincipal();
	    LOGGER.info("채팅방 생성요청 생성 요청 ID" ,user.getEmail());
	    
		ChatRoomDTO chatroom = chatRoomService.createChatRoom(chatroomDTO.getName(), user.getEmail());
		LOGGER.info("채팅방 생성 요청 성공 채팅방 ID",chatroom.getRoomId());
		
		return new ResponseEntity<>(chatroom , HttpStatus.CREATED);

	}

	// 채팅방 삭제
	@PostMapping("/rooms/delete")
	@ApiOperation(value = "채팅방 삭제" ,notes = "해당 채팅방 호스트만 사용 가능")
	public ResponseEntity<ChatRoomDTO> deleteChatRoom(@RequestBody ChatRoomDTO chatRoomDTO) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    User user = (User) authentication.getPrincipal();
	    
	    LOGGER.info("채팅방 생성요청 삭제 요청 ID" ,user.getEmail());
	    
	    if(chatRoomService.isChatRoom(chatRoomDTO.getRoomId()).getHost().equals(user.getEmail())) {
	    	ChatRoomDTO chatRoom = chatRoomService.deleteChatRoom(chatRoomDTO.getRoomId());
	    	LOGGER.info("채팅방 생성성공 채팅 ID" ,chatRoom.getRoomId());
			return ResponseEntity.ok(chatRoom);
	    }else {
	    	LOGGER.info("채팅방 생성실패 채팅 호스트 불일치 호스트 이메일 :" ,chatRoomDTO.getHost());
	    	throw new AccessDeniedException("host가 다른 사람입니다.");
	    }
	}
	
	//채팅방 입장
	@GetMapping("/rooms/{roomId}")
	@ApiOperation(value = "채팅방 입장 , 채팅방 채팅 내용 불러오기" , notes = "채팅방 입장")
	public ResponseEntity<?> enter(@PathVariable Long roomId
			,	@RequestParam(defaultValue = "0") int page,
				@RequestParam(defaultValue = "20") int size) {
		
		LOGGER.info("채팅방 입장 채팅방 ID :",roomId);
		// 사용자가 해당 채팅방에 포함되어 있는지 확인
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    User user = (User) authentication.getPrincipal();
	    
	    boolean isUserInRoom = chatRoomService.isUserInRoom(user.getEmail() , roomId);
	    if (!isUserInRoom) {
	    	//채팅방 타입에 따라서 아무나 들어갈 수 있는지 없는지 판단 해야함
	    	ChatRoomDTO checkedChatRoomDTO = chatRoomService.isChatRoom(roomId);
	    	
	    	if(checkedChatRoomDTO.getType().equals("product")) {
	    		
	        ChatUserDTO chatUserDTO = chatRoomService.inviteChatUser(roomId, user.getEmail());
	        
			ChatMessageDTO message = ChatMessageDTO.builder()
									.roomId(roomId)
									.content(user.getName() + "님이 입장하였습니다.")
									.sender("SYSTEM")
									.type("txt")
									.timestamp(LocalDateTime.now())
									.build();
									
			template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
			
			ChatMessage chatMessage = ChatMessage.builder()
									 .roomId(message.getRoomId())
									 .sender(message.getSender())
									 .content(message.getContent())
									 .type("txt")
									 .timestamp(message.getTimestamp())
									 .build();
			chatService.saveChat(chatMessage);
	    	}else {
	    		throw new AccessDeniedException("해당 채팅방에 접근할 수 없습니다.");
	    	}
	    }
		
		//채팅 내용 뿌려주기
	    //스크롤 위로 할 씨 추가적으로 fetch	
	    Pageable pageable = PageRequest.of(page, size );
	    
		Page<ChatMessageDTO> chatMessage = chatService.findChatMessageByroomId(roomId , pageable );
		return ResponseEntity.ok(chatMessage);
		
	}
	
	@PostMapping("/rooms/{roomId}/exit")
	@ApiOperation(value = "나의 채팅방 삭제(구독취소)", notes = "채팅방 삭제(구독취소)")
	public ResponseEntity<ChatUserDTO> exitChatRoom(@PathVariable Long roomId){
		
		LOGGER.info("채팅방 구독 취소 채팅방 ID :" , roomId);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    User user = (User) authentication.getPrincipal();
		
		ChatUserDTO chatUserDTO = chatRoomService.exitChatRoom(user.getEmail(), roomId);
		LOGGER.info("구독 취소 성공 채팅방 id : {}, 이메일 : {}" ,roomId , user.getEmail());
		
		return ResponseEntity.ok(chatUserDTO);
		
	}
	
	//메시지 전송
	@PostMapping("/rooms/{roomId}/messages")
	@ApiOperation(value = "메시지 전송 및 채팅 내용 저장" ,notes = "채팅 내용 저장")
	public ResponseEntity<ChatMessageDTO> message(@RequestBody ChatMessageDTO message) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getPrincipal();
		
		LOGGER.info("메시지 전송 요청 전송 요청자ID :",user.getEmail());
		
		ObjectId objectId = new ObjectId();
		message.setId(objectId.toString());
		message.setName(user.getName());
		message.setSender(user.getEmail());
		message.setTimestamp(LocalDateTime.now());
		
		ChatMessage chatMessage = ChatMessage.builder()
				.id(message.getId())
				.roomId(message.getRoomId())
				.sender(message.getSender())
				.name(message.getName())
				.type("txt")
				.content(message.getContent())
				.timestamp(message.getTimestamp())
				.build();
		
		ChatMessageDTO chatMessageDTO =  chatService.saveChat(chatMessage);
		LOGGER.info("메시지 저장 성공 메시지ID :" , chatMessage.getId());
		template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
		LOGGER.info("메시지 전송 성공 메시지ID :",chatMessage.getId());
		
		return ResponseEntity.ok(chatMessageDTO);
	}
	
	//채팅방 초대
	@PostMapping("/rooms/{roomId}/{email}/invite")
	@ApiOperation(value = "채팅방 초대 및 안내 메시지 전송" ,notes = "채팅방 초대")
	public ResponseEntity<?> inviteChatUser(@PathVariable Long roomId ,@PathVariable String email) {
		
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
								.type("txt")
								.timestamp(LocalDateTime.now())
								.build();
								
		template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
		
		ChatMessage chatMessage = ChatMessage.builder()
								 .roomId(message.getRoomId())
								 .sender(message.getSender())
								 .content(message.getContent())
								 .type("txt")
								 .timestamp(message.getTimestamp())
								 .build();
		chatService.saveChat(chatMessage);
		
		return ResponseEntity.ok(chatUserDTO);
		
	}
	
	//파일 업로드 추가
	@PostMapping("/upload/{roomId}/file")
	@ApiOperation(value = "파일 업로드" ,notes = "파일 업로드")
	public ResponseEntity<?> uploadfile(MultipartFile file , @PathVariable Long roomId) {
		LOGGER.info("파일 업로드 시작 파일 이름 :",file.getOriginalFilename());
		try {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		User user = (User) authentication.getPrincipal();
		ChatMessageDTO messageDTO = ChatMessageDTO.builder()
									.roomId(roomId)
									.sender(user.getEmail())
									.name(user.getName())
									.timestamp(LocalDateTime.now())
									.build();
		messageDTO = chatService.uploadFile(file , messageDTO);
		
		template.convertAndSend("/sub/chat/room/" + messageDTO.getRoomId(), messageDTO);
		
		ChatMessage chatMessage = ChatMessage.builder()
								.id(messageDTO.getId())
								.roomId(messageDTO.getRoomId())
								.sender(messageDTO.getSender())
								.name(messageDTO.getName())
								.content(messageDTO.getContent())
								.type(messageDTO.getType())
								.fileUrl(messageDTO.getFileUrl())
								.original_filename(messageDTO.getOriginal_filename())
								.timestamp(messageDTO.getTimestamp())
								.build();
		
		chatService.saveChat(chatMessage);
		
		return ResponseEntity.ok(messageDTO);
	}catch (IllegalArgumentException e) {
		LOGGER.info("파일 업로드 실패 :" , e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // 파일 크기 제한 등 예외 처리
    } catch (Exception e) {
    	LOGGER.info("파일 업로드 실패 : ", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 중 오류가 발생했습니다.");
    }
	}
	//파일 다운로드
	@GetMapping("{id}/download/{filename}")
	@ApiOperation(value = "파일 다운로드" , notes = "파일 다운로드")
	public ResponseEntity<Resource> downloadfile(@PathVariable String id , @PathVariable String filename){
		
		try {
		Resource resource = chatService.downloadFile(id, filename);
		
		String originalFileName = chatService.originalFileName(id);
		
		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalFileName + "\"")
                .body(resource);
		
		}catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // 잘못된 요청 처리
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // 서버 오류 처리
        }		
	}
	
	//채팅 취소 추가 정합성 떄문에 디비에서 지우지는 않고 취소 및 메시지 삭제 메시지로 수정예정
	@PostMapping("/delete/{id}")
	@ApiOperation(value = "메시지 삭제(취소)" , notes = "메시지 삭제(취소)")
	public ResponseEntity<ChatMessageDTO> deleteMessage(@PathVariable String id){
		
		ChatMessageDTO existingChatMessageDTO = chatService.findChatMessage(id);
		
		if(existingChatMessageDTO == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		existingChatMessageDTO.setContent("삭제된 메시지 입니다.");
		existingChatMessageDTO.setType("deleted");
		existingChatMessageDTO.setFileUrl(null);
		existingChatMessageDTO.setOriginal_filename(null);;
		
		template.convertAndSend("/sub/chat/room/" + existingChatMessageDTO.getRoomId(), existingChatMessageDTO);
		
		return ResponseEntity.ok(chatService.deleteMessage(existingChatMessageDTO));
		
	}
	
}
